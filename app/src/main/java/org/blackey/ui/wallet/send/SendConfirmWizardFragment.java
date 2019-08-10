package org.blackey.ui.wallet.send;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletSendConfirmBinding;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.util.Helper;

import timber.log.Timber;

public class SendConfirmWizardFragment extends SendWizardFragment<FragmentWalletSendConfirmBinding,SendConfirmWizardViewModel> implements SendConfirm  {

    public static SendConfirmWizardFragment newInstance(Listener listener) {
        SendConfirmWizardFragment instance = new SendConfirmWizardFragment();
        instance.setSendListener(listener);
        return instance;
    }

    Listener sendListener;

    public SendConfirmWizardFragment setSendListener(Listener listener) {
        this.sendListener = listener;
        return this;
    }

    interface Listener {
        SendFragment.Listener getActivityCallback();

        TxData getTxData();

        void commitTransaction();

        void disposeTransaction();

        SendFragment.Mode getMode();
    }



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_send_confirm;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        binding.bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("bSend.setOnClickListener");
                preSend();
            }
        });
    }

    boolean inProgress = false;

    public void hideProgress() {
        binding.llProgress.setVisibility(View.INVISIBLE);
        inProgress = false;
    }

    public void showProgress() {
        binding.llProgress.setVisibility(View.VISIBLE);
        inProgress = true;
    }

    PendingTransaction pendingTransaction = null;

    @Override
    // callback from wallet when PendingTransaction created
    public void transactionCreated(String txTag, PendingTransaction pendingTransaction) {
        // ignore txTag - the app flow ensures this is the correct tx
        // TODO: use the txTag
        hideProgress();
        if (isResumed) {
            this.pendingTransaction = pendingTransaction;
            refreshTransactionDetails();
        } else {
            sendListener.disposeTransaction();
        }
    }

    void send() {
        sendListener.commitTransaction();
        binding.pbProgressSend.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendFailed(String errorText) {
        binding.pbProgressSend.setVisibility(View.INVISIBLE);
        showAlert(getString(R.string.send_create_tx_error_title), errorText);
    }

    @Override
    public void createTransactionFailed(String errorText) {
        hideProgress();
        showAlert(getString(R.string.send_create_tx_error_title), errorText);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).
                setTitle(title).
                setMessage(message).
                create().
                show();
    }

    @Override
    public boolean onValidateFields() {
        return true;
    }

    private boolean isResumed = false;

    @Override
    public void onPauseFragment() {
        isResumed = false;
        pendingTransaction = null;
        sendListener.disposeTransaction();
        refreshTransactionDetails();
        super.onPauseFragment();
    }

    @Override
    public void onResumeFragment() {
        super.onResumeFragment();
        Timber.d("onResumeFragment()");
        Helper.hideKeyboard(getActivity());
        isResumed = true;

        final TxData txData = sendListener.getTxData();
        binding.tvTxAddress.setText(txData.getDestinationAddress());
        String paymentId = txData.getPaymentId();
        if ((paymentId != null) && (!paymentId.isEmpty())) {
            binding.tvTxPaymentId.setText(txData.getPaymentId());
        } else {
            binding.tvTxPaymentId.setText("-");
        }
        UserNotes notes = sendListener.getTxData().getUserNotes();
        if ((notes != null) && (!notes.note.isEmpty())) {
            binding.tvTxNotes.setText(notes.note);
        } else {
            binding.tvTxNotes.setText("-");
        }
        refreshTransactionDetails();
        if ((pendingTransaction == null) && (!inProgress)) {
            showProgress();
            prepareSend(txData);
        }
    }

    void refreshTransactionDetails() {
        Timber.d("refreshTransactionDetails()");
        if (pendingTransaction != null) {
            binding.llConfirmSend.setVisibility(View.VISIBLE);
            binding.tvTxFee.setText(Wallet.getDisplayAmount(pendingTransaction.getFee()));
            if (getActivityCallback().isStreetMode()
                    && (sendListener.getTxData().getAmount() == Wallet.SWEEP_ALL)) {
                binding.tvTxAmount.setText(getString(R.string.street_sweep_amount));
                binding.tvTxTotal.setText(getString(R.string.street_sweep_amount));
            } else {
                binding.tvTxAmount.setText(Wallet.getDisplayAmount(pendingTransaction.getAmount()));
                binding.tvTxTotal.setText(Wallet.getDisplayAmount(
                        pendingTransaction.getFee() + pendingTransaction.getAmount()));
            }
        } else {
            binding.llConfirmSend.setVisibility(View.GONE);
        }
    }

    public void preSend() {
        final Activity activity = getActivity();
        View promptsView = getLayoutInflater().inflate(R.layout.prompt_password, null);
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptsView);

        final TextInputLayout etPassword = promptsView.findViewById(R.id.etPassword);
        etPassword.setHint(getString(R.string.prompt_send_password));

        etPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etPassword.getError() != null) {
                    etPassword.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String pass = etPassword.getEditText().getText().toString();
                        if (getActivityCallback().verifyWalletPassword(pass)) {
                            dialog.dismiss();
                            Helper.hideKeyboardAlways(activity);
                            send();
                        } else {
                            etPassword.setError(getString(R.string.bad_password));
                        }
                    }
                })
                .setNegativeButton(getString(R.string.label_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Helper.hideKeyboardAlways(activity);
                                dialog.cancel();
                            }
                        });

        final android.app.AlertDialog passwordDialog = alertDialogBuilder.create();
        passwordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pass = etPassword.getEditText().getText().toString();
                        if (getActivityCallback().verifyWalletPassword(pass)) {
                            Helper.hideKeyboardAlways(activity);
                            passwordDialog.dismiss();
                            send();
                        } else {
                            etPassword.setError(getString(R.string.bad_password));
                        }
                    }
                });
            }
        });

        Helper.showKeyboard(passwordDialog);

        // accept keyboard "ok"
        etPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String pass = etPassword.getEditText().getText().toString();
                    if (getActivityCallback().verifyWalletPassword(pass)) {
                        Helper.hideKeyboardAlways(activity);
                        passwordDialog.dismiss();
                        send();
                    } else {
                        etPassword.setError(getString(R.string.bad_password));
                    }
                    return true;
                }
                return false;
            }
        });

        //TODO BuildConfig.DEBUG
        // set FLAG_SECURE to prevent screenshots in Release Mode
        /*if (!(BuildConfig.DEBUG && BuildConfig.FLAVOR_type.equals("alpha"))) {
            passwordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }*/

        passwordDialog.show();
    }

    // creates a pending transaction and calls us back with transactionCreated()
    // or createTransactionFailed()
    void prepareSend(TxData txData) {
        getActivityCallback().onPrepareSend(null, txData);
    }

    SendFragment.Listener getActivityCallback() {
        return sendListener.getActivityCallback();
    }
}
