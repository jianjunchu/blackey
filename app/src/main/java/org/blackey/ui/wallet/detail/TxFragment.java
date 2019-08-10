package org.blackey.ui.wallet.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletTxInfoBinding;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.TransactionInfo;
import com.m2049r.xmrwallet.model.Transfer;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.util.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import me.goldze.mvvmhabit.base.BaseFragment;

public class TxFragment extends BaseFragment<FragmentWalletTxInfoBinding,TxViewModel> {

    static public final String ARG_INFO = "info";

    private final SimpleDateFormat TS_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");


    TransactionInfo info = null;
    UserNotes userNotes = null;

    public TxFragment() {
        super();
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone(); //get the local time zone.
        TS_FORMATTER.setTimeZone(tz);
    }



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_tx_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle args = getArguments();
        TransactionInfo info = args.getParcelable(ARG_INFO);
        show(info);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.etTxNotes.setRawInputType(InputType.TYPE_CLASS_TEXT);

        binding.bTxNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.notes = null; // force reload on next view
                binding.bTxNotes.setEnabled(false);
                binding.etTxNotes.setEnabled(false);
                userNotes.setNote(binding.etTxNotes.getText().toString());
                activityCallback.onSetNote(info.hash, userNotes.txNotes);
            }
        });

        binding.tvTxXmrToKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.clipBoardCopy(getActivity(), getString(R.string.label_copy_xmrtokey), binding.tvTxXmrToKey.getText().toString());
                Toast.makeText(getActivity(), getString(R.string.message_copy_xmrtokey), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void show(TransactionInfo info) {
        if (info.txKey == null) {
            info.txKey = activityCallback.getTxKey(info.hash);
        }
        if (info.address == null) {
            info.address = activityCallback.getTxAddress(info.account, info.subaddress);
        }
        loadNotes(info);

        activityCallback.setSubtitle(getString(R.string.tx_title));

        binding.tvAccount.setText(getString(R.string.tx_account_formatted, info.account, info.subaddress));
        binding.tvAddress.setText(info.address);

        binding.tvTxTimestamp.setText(TS_FORMATTER.format(new Date(info.timestamp * 1000)));
        binding.tvTxId.setText(info.hash);
        binding.tvTxKey.setText(info.txKey.isEmpty() ? "-" : info.txKey);
        binding.tvTxPaymentId.setText(info.paymentId);
        if (info.isFailed) {
            binding.tvTxBlockheight.setText(getString(R.string.tx_failed));
        } else if (info.isPending) {
            binding.tvTxBlockheight.setText(getString(R.string.tx_pending));
        } else {
            binding.tvTxBlockheight.setText("" + info.blockheight);
        }
        String sign = (info.direction == TransactionInfo.Direction.Direction_In ? "+" : "-");

        long realAmount = info.amount;
        binding.tvTxAmount.setText(sign + Wallet.getDisplayAmount(realAmount));

        if ((info.fee > 0)) {
            String fee = Wallet.getDisplayAmount(info.fee);
            binding.tvTxFee.setText(getString(R.string.tx_list_fee, fee));
        } else {
            binding.tvTxFee.setText(null);
            binding.tvTxFee.setVisibility(View.GONE);
        }

        if (info.isFailed) {
            binding.tvTxAmount.setText(getString(R.string.tx_list_amount_failed, Wallet.getDisplayAmount(info.amount)));
            binding.tvTxFee.setText(getString(R.string.tx_list_failed_text));
            setTxColour(ContextCompat.getColor(getContext(), R.color.tx_failed));
        } else if (info.isPending) {
            setTxColour(ContextCompat.getColor(getContext(), R.color.tx_pending));
        } else if (info.direction == TransactionInfo.Direction.Direction_In) {
            setTxColour(ContextCompat.getColor(getContext(), R.color.tx_green));
        } else {
            setTxColour(ContextCompat.getColor(getContext(), R.color.tx_red));
        }
        Set<String> destinations = new HashSet<>();
        StringBuffer sb = new StringBuffer();
        StringBuffer dstSb = new StringBuffer();
        if (info.transfers != null) {
            boolean newline = false;
            for (Transfer transfer : info.transfers) {
                destinations.add(transfer.address);
                if (newline) {
                    sb.append("\n");
                } else {
                    newline = true;
                }
                sb.append("[").append(transfer.address.substring(0, 6)).append("] ");
                sb.append(Wallet.getDisplayAmount(transfer.amount));
            }
            newline = false;
            for (String dst : destinations) {
                if (newline) {
                    dstSb.append("\n");
                } else {
                    newline = true;
                }
                dstSb.append(dst);
            }
        } else {
            sb.append("-");
            dstSb.append(info.direction ==
                    TransactionInfo.Direction.Direction_In ?
                    activityCallback.getWalletSubaddress(info.account, info.subaddress) :
                    "-");
        }
        binding.tvTxTransfers.setText(sb.toString());
        binding.tvDestination.setText(dstSb.toString());
        this.info = info;
        showBtcInfo();
    }

    void loadNotes(TransactionInfo info) {
        if ((userNotes == null) || (info.notes == null)) {
            info.notes = activityCallback.getTxNotes(info.hash);
        }
        userNotes = new UserNotes(info.notes);
        binding.etTxNotes.setText(userNotes.note);
    }

    private void setTxColour(int clr) {
        binding.tvTxAmount.setTextColor(clr);
        binding.tvTxFee.setTextColor(clr);
    }

    void showBtcInfo() {
        if (userNotes.xmrtoKey != null) {
            binding.cvXmrTo.setVisibility(View.VISIBLE);
            binding.tvTxXmrToKey.setText(userNotes.xmrtoKey);
            binding.tvDestinationBtc.setText(userNotes.xmrtoDestination);
            binding.tvTxAmountBtc.setText(userNotes.xmrtoAmount + " BTC");
        } else {
            binding.cvXmrTo.setVisibility(View.GONE);
        }
    }

    Listener activityCallback;

    public interface Listener {

        String getWalletSubaddress(int accountIndex, int subaddressIndex);

        String getTxKey(String hash);

        String getTxNotes(String hash);

        String getTxAddress(int major, int minor);

        void onSetNote(String txId, String notes);


        void setSubtitle(String subtitle);


    }

    public void onNotesSet(boolean reload) {
        binding.bTxNotes.setEnabled(true);
        binding.etTxNotes.setEnabled(true);
        if (reload) {
            loadNotes(this.info);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TxFragment.Listener) {
            this.activityCallback = (TxFragment.Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }
}
