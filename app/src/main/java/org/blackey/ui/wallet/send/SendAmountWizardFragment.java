package org.blackey.ui.wallet.send;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletSendAmountBinding;
import com.m2049r.xmrwallet.data.BarcodeData;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.util.Helper;

import timber.log.Timber;

public class SendAmountWizardFragment extends SendWizardFragment<FragmentWalletSendAmountBinding,SendAmountWizardViewModel> {

    public static SendAmountWizardFragment newInstance(Listener listener) {
        SendAmountWizardFragment instance = new SendAmountWizardFragment();
        instance.setSendListener(listener);
        return instance;
    }

    Listener sendListener;

    public SendAmountWizardFragment setSendListener(Listener listener) {
        this.sendListener = listener;
        return this;
    }

    interface Listener {
        SendFragment.Listener getActivityCallback();

        TxData getTxData();

        BarcodeData popBarcodeData();
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_send_amount;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initViewObservable() {
        binding.ivSweep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweepAll(false);
            }
        });
        binding.ivSweep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweepAll(false);
            }
        });

        Helper.hideKeyboard(getActivity());
    }

    private boolean spendAllMode = false;

    private void sweepAll(boolean spendAllMode) {
        if (spendAllMode) {
            binding.ibSweep.setVisibility(View.INVISIBLE);
            binding.llAmount.setVisibility(View.GONE);
            binding.rlSweep.setVisibility(View.VISIBLE);
        } else {
            binding.ibSweep.setVisibility(View.VISIBLE);
            binding.llAmount.setVisibility(View.VISIBLE);
            binding.rlSweep.setVisibility(View.GONE);
        }
        this.spendAllMode = spendAllMode;
    }

    @Override
    public boolean onValidateFields() {
        if (spendAllMode) {
            if (sendListener != null) {
                sendListener.getTxData().setAmount(Wallet.SWEEP_ALL);
            }
        } else {
            if (!binding.evAmount.validate(maxFunds)) {
                return false;
            }

            if (sendListener != null) {
                String xmr = binding.evAmount.getAmount();
                if (xmr != null) {
                    sendListener.getTxData().setAmount(Wallet.getAmountFromString(xmr));
                } else {
                    sendListener.getTxData().setAmount(0L);
                }
            }
        }
        return true;
    }

    double maxFunds = 0;

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        Timber.d("onResumeFragment()");
        Helper.hideKeyboard(getActivity());
        final long funds = getTotalFunds();
        maxFunds = 1.0 * funds / 1000000000000L;
        if (!sendListener.getActivityCallback().isStreetMode()) {
            binding.tvFunds.setText(getString(R.string.send_available,
                    Wallet.getDisplayAmount(funds)));
        } else {
            binding.tvFunds.setText(getString(R.string.send_available,
                    getString(R.string.unknown_amount)));
        }
        // getAmount is null if exchange is in progress
        if ((binding.evAmount.getAmount() != null) && binding.evAmount.getAmount().isEmpty()) {
            final BarcodeData data = sendListener.popBarcodeData();
            if ((data != null) && (data.amount != null)) {
                binding.evAmount.setAmount(data.amount);
            }
        }
    }

    long getTotalFunds() {
        return sendListener.getActivityCallback().getTotalFunds();
    }
}
