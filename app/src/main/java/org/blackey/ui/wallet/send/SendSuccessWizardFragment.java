package org.blackey.ui.wallet.send;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletSendSuccessBinding;
import com.m2049r.xmrwallet.data.PendingTx;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.util.Helper;

import timber.log.Timber;

public class SendSuccessWizardFragment extends SendWizardFragment<FragmentWalletSendSuccessBinding,SendSuccessWizardViewModel> {


    public static SendSuccessWizardFragment newInstance(Listener listener) {
        SendSuccessWizardFragment instance = new SendSuccessWizardFragment();
        instance.setSendListener(listener);
        return instance;
    }

    Listener sendListener;

    public SendSuccessWizardFragment setSendListener(Listener listener) {
        this.sendListener = listener;
        return this;
    }

    interface Listener {
        TxData getTxData();

        PendingTx getCommittedTx();

        void enableDone();

        SendFragment.Mode getMode();

        SendFragment.Listener getActivityCallback();
    }
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_send_success;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        binding.bCopyTxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.clipBoardCopy(getActivity(), getString(R.string.label_send_txid), binding.tvTxId.getText().toString());
                Toast.makeText(getActivity(), getString(R.string.message_copy_txid), Toast.LENGTH_SHORT).show();
            }
        });

        binding.bCopyTxId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.clipBoardCopy(getActivity(), getString(R.string.label_send_txid), binding.tvTxId.getText().toString());
                Toast.makeText(getActivity(), getString(R.string.message_copy_txid), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onValidateFields() {
        return true;
    }

    @Override
    public void onPauseFragment() {
        super.onPauseFragment();
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        Timber.d("onResumeFragment()");
        Helper.hideKeyboard(getActivity());

        final TxData txData = sendListener.getTxData();
        binding.tvTxAddress.setText(txData.getDestinationAddress());
        String paymentId = txData.getPaymentId();
        if ((paymentId != null) && (!paymentId.isEmpty())) {
            binding.tvTxPaymentId.setText(txData.getPaymentId());
        } else {
            binding.tvTxPaymentId.setText("-");
        }

        final PendingTx committedTx = sendListener.getCommittedTx();
        if (committedTx != null) {
            binding.tvTxId.setText(committedTx.txId);
            binding.bCopyTxId.setEnabled(true);
            binding.bCopyTxId.setImageResource(R.drawable.ic_content_copy_black_24dp);

            if (sendListener.getActivityCallback().isStreetMode()
                    && (sendListener.getTxData().getAmount() == Wallet.SWEEP_ALL)) {
                binding.tvTxAmount.setText(getString(R.string.street_sweep_amount));
            } else {
                binding.tvTxAmount.setText(getString(R.string.send_amount, Helper.getDisplayAmount(committedTx.amount)));
            }
            binding.tvTxFee.setText(getString(R.string.send_fee, Helper.getDisplayAmount(committedTx.fee)));
        }
        sendListener.enableDone();
    }
}
