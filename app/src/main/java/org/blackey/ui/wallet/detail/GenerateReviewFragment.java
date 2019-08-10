package org.blackey.ui.wallet.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.m2049r.xmrwallet.ledger.Ledger;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.util.FingerprintHelper;
import com.m2049r.xmrwallet.util.Helper;
import com.m2049r.xmrwallet.util.KeyStoreHelper;
import com.m2049r.xmrwallet.util.MoneroThreadPoolExecutor;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletReviewBinding;
import org.blackey.ui.base.BlackeyBaseFragment;

import java.io.File;

import me.goldze.mvvmhabit.utils.KLog;
import timber.log.Timber;

public class GenerateReviewFragment extends BlackeyBaseFragment<FragmentWalletReviewBinding, GenerateReviewViewModel> {

    static final public String VIEW_TYPE_DETAILS = "details";
    static final public String VIEW_TYPE_ACCEPT = "accept";
    static final public String VIEW_TYPE_WALLET = "wallet";

    public static final String REQUEST_TYPE = "type";
    public static final String REQUEST_PATH = "path";
    public static final String REQUEST_PASSWORD = "password";

    private String walletPath;
    private String walletName;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_review;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        Bundle args = getArguments();
        type = args.getString(REQUEST_TYPE);
        walletPath = args.getString(REQUEST_PATH);
        localPassword = args.getString(REQUEST_PASSWORD);
        showDetails();


    }



    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.bAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptWallet();
            }
        });
        binding.bCopyViewKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyViewKey();
            }
        });
        binding.bCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyAddress();
            }
        });
    }

    void showDetails() {
        binding.tvWalletPassword.setText(null);
        new AsyncShow().executeOnExecutor(MoneroThreadPoolExecutor.MONERO_THREAD_POOL_EXECUTOR, walletPath);
    }

    void copyViewKey() {
        Helper.clipBoardCopy(getActivity(), getString(R.string.label_copy_viewkey), binding.tvWalletViewKey.getText().toString());
        Toast.makeText(getActivity(), getString(R.string.message_copy_viewkey), Toast.LENGTH_SHORT).show();
    }

    void copyAddress() {
        Helper.clipBoardCopy(getActivity(), getString(R.string.label_copy_address), binding.tvWalletAddress.getText().toString());
        Toast.makeText(getActivity(), getString(R.string.message_copy_address), Toast.LENGTH_SHORT).show();
    }

    void nocopy() {
        Toast.makeText(getActivity(), getString(R.string.message_nocopy), Toast.LENGTH_SHORT).show();
    }

    void showAdvancedInfo() {
        binding.llAdvancedInfo.setVisibility(View.VISIBLE);
        binding.bAdvancedInfo.setVisibility(View.GONE);
        binding.scrollview.post(new Runnable() {
            @Override
            public void run() {
                binding.scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    String type;

    private void acceptWallet() {
        binding.bAccept.setEnabled(false);
        getActivity().finish();
    }

    private class AsyncShow extends AsyncTask<String, Void, Boolean> {
        String name;
        String address;
        String seed;
        String viewKey;
        String spendKey;
        boolean isWatchOnly;
        Wallet.Status status;

        boolean dialogOpened = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
            if ((walletPath != null)
                    && (WalletManager.getInstance().queryWalletDevice(walletPath + ".keys", getPassword())
                    == Wallet.Device.Device_Ledger)
                    && (progressCallback != null)) {
                dialogOpened = true;
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length != 1) return false;
            String walletPath = params[0];

            Wallet wallet;
            boolean closeWallet;
            if (type.equals(GenerateReviewFragment.VIEW_TYPE_WALLET)) {
                wallet = GenerateReviewFragment.this.walletCallback.getWallet();
                closeWallet = false;
            } else {
                wallet = WalletManager.getInstance().openWallet(walletPath, getPassword());
                closeWallet = true;
            }
            name = wallet.getName();
            status = wallet.getStatus();
            if (status != Wallet.Status.Status_Ok) {
                Timber.e(wallet.getErrorString());
                if (closeWallet) wallet.close();
                return false;
            }

            address = wallet.getAddress();
            seed = wallet.getSeed();
            switch (wallet.getDeviceType()) {
                case Device_Ledger:
                    viewKey = Ledger.Key();
                    break;
                case Device_Software:
                    viewKey = wallet.getSecretViewKey();
                    break;
                default:
                    throw new IllegalStateException("Hardware backing not supported. At all!");
            }
            spendKey = isWatchOnly ? getActivity().getString(R.string.label_watchonly) : wallet.getSecretSpendKey();
            isWatchOnly = wallet.isWatchOnly();
            if (closeWallet) wallet.close();


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (dialogOpened)
                progressCallback.dismissProgressDialog();
            if (!isAdded()) return; // never mind
            walletName = name;
            if (result) {
                if (type.equals(GenerateReviewFragment.VIEW_TYPE_ACCEPT)) {
                    binding.bAccept.setVisibility(View.VISIBLE);
                    binding.bAccept.setEnabled(true);
                }
                binding.llPassword.setVisibility(View.VISIBLE);
                binding.tvWalletPassword.setText(getPassword());
                binding.tvWalletAddress.setText(address);
                if (!seed.isEmpty()) {
                    binding.llMnemonic.setVisibility(View.VISIBLE);
                    binding.tvWalletMnemonic.setText(seed);
                    KLog.e(seed);
                }
                boolean showAdvanced = false;
                if (isKeyValid(viewKey)) {
                    binding.llViewKey.setVisibility(View.VISIBLE);
                    binding.tvWalletViewKey.setText(viewKey);
                    showAdvanced = true;
                }
                if (isKeyValid(spendKey)) {
                    binding.llSpendKey.setVisibility(View.VISIBLE);
                    binding. tvWalletSpendKey.setText(spendKey);
                    showAdvanced = true;
                }
                if (showAdvanced) binding.bAdvancedInfo.setVisibility(View.VISIBLE);
                binding.bCopyAddress.setClickable(true);
                binding.bCopyAddress.setImageResource(R.drawable.ic_content_copy_black_24dp);
                //activityCallback.setTitle(name, getString(R.string.details_title));

                binding.bAdvancedInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAdvancedInfo();
                    }
                });
            } else {
                // TODO show proper error message and/or end the fragment?
                binding.tvWalletAddress.setText(status.toString());
                binding. tvWalletMnemonic.setText(status.toString());
                binding. tvWalletViewKey.setText(status.toString());
                binding.tvWalletSpendKey.setText(status.toString());
            }
            hideProgress();

            File walletFolder = Helper.getWalletRoot(getActivity());
            File walletFile = new File(walletFolder, walletName);
            Timber.d("New Wallet %s", walletFile.getAbsolutePath());
            walletFile.delete(); // when recovering wallets, the cache seems corrupt
        }
    }

    Listener activityCallback = null;
    ProgressListener progressCallback = null;
    AcceptListener acceptCallback = null;
    ListenerWithWallet walletCallback = null;
    PasswordChangedListener passwordCallback = null;

    public interface Listener {
        void setTitle(String title, String subtitle);

        void setToolbarButton(int type);
    }

    public interface ProgressListener {
        void showProgressDialog(int msgId);

        void showLedgerProgressDialog(int mode);

        void dismissProgressDialog();
    }


    public interface AcceptListener {
        void onAccept(String name, String password);
    }

    public interface ListenerWithWallet {
        Wallet getWallet();
    }

    public interface PasswordChangedListener {
        void onPasswordChanged(String newPassword);

        String getPassword();
    }

    private String localPassword = null;

    private String getPassword() {
        if (passwordCallback != null) return passwordCallback.getPassword();
        return localPassword;
    }

    private void setPassword(String password) {
        if (passwordCallback != null) passwordCallback.onPasswordChanged(password);
        else localPassword = password;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            this.activityCallback = (Listener) context;
        }
        if (context instanceof ProgressListener) {
            this.progressCallback = (ProgressListener) context;
        }
        if (context instanceof AcceptListener) {
            this.acceptCallback = (AcceptListener) context;
        }
        if (context instanceof ListenerWithWallet) {
            this.walletCallback = (ListenerWithWallet) context;
        }
        if (context instanceof PasswordChangedListener) {
            this.passwordCallback = (PasswordChangedListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume()");

    }

    public void showProgress() {
        binding.pbProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        binding.pbProgress.setVisibility(View.GONE);
    }

    boolean backOk() {
        return !type.equals(GenerateReviewFragment.VIEW_TYPE_ACCEPT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    boolean changeWalletPassword(String newPassword) {
        Wallet wallet;
        boolean closeWallet;
        if (type.equals(GenerateReviewFragment.VIEW_TYPE_WALLET)) {
            wallet = GenerateReviewFragment.this.walletCallback.getWallet();
            closeWallet = false;
        } else {
            wallet = WalletManager.getInstance().openWallet(walletPath, getPassword());
            closeWallet = true;
        }

        boolean ok = false;
        if (wallet.getStatus() == Wallet.Status.Status_Ok) {
            wallet.setPassword(newPassword);
            wallet.store();
            ok = true;
        } else {
            Timber.e(wallet.getErrorString());
        }
        if (closeWallet) wallet.close();
        return ok;
    }

    private class AsyncChangePassword extends AsyncTask<String, Void, Boolean> {
        String newPassword;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressCallback != null)
                progressCallback.showProgressDialog(R.string.changepw_progress);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params.length != 2) return false;
            final String userPassword = params[0];
            final boolean fingerPassValid = Boolean.valueOf(params[1]);
            newPassword = KeyStoreHelper.getCrazyPass(getActivity(), userPassword);
            final boolean success = changeWalletPassword(newPassword);
            if (success) {
                Context ctx = getActivity();
                if (ctx != null)
                    if (fingerPassValid) {
                        KeyStoreHelper.saveWalletUserPass(ctx, walletName, userPassword);
                    } else {
                        KeyStoreHelper.removeWalletUserPass(ctx, walletName);
                    }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if ((getActivity() == null) || getActivity().isDestroyed()) {
                return;
            }
            if (progressCallback != null)
                progressCallback.dismissProgressDialog();
            if (result) {
                Toast.makeText(getActivity(), getString(R.string.changepw_success), Toast.LENGTH_SHORT).show();
                setPassword(newPassword);
                showDetails();
            } else {
                Toast.makeText(getActivity(), getString(R.string.changepw_failed), Toast.LENGTH_LONG).show();
            }
        }
    }

    AlertDialog openDialog = null; // for preventing opening of multiple dialogs

    public AlertDialog createChangePasswordDialog() {
        if (openDialog != null) return null; // we are already open
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.prompt_changepw, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final TextInputLayout etPasswordA = promptsView.findViewById(R.id.etWalletPasswordA);
        etPasswordA.setHint(getString(R.string.prompt_changepw, walletName));

        final TextInputLayout etPasswordB = promptsView.findViewById(R.id.etWalletPasswordB);
        etPasswordB.setHint(getString(R.string.prompt_changepwB, walletName));

        LinearLayout llFingerprintAuth = promptsView.findViewById(R.id.llFingerprintAuth);
        final Switch swFingerprintAllowed = (Switch) llFingerprintAuth.getChildAt(0);
        if (FingerprintHelper.isDeviceSupported(getActivity())) {
            llFingerprintAuth.setVisibility(View.VISIBLE);

            swFingerprintAllowed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!swFingerprintAllowed.isChecked()) return;

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(Html.fromHtml(getString(R.string.generate_fingerprint_warn)))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.label_ok), null)
                            .setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    swFingerprintAllowed.setChecked(false);
                                }
                            })
                            .show();
                }
            });

            swFingerprintAllowed.setChecked(FingerprintHelper.isFingerPassValid(getActivity(), walletName));
        }

        etPasswordA.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etPasswordA.getError() != null) {
                    etPasswordA.setError(null);
                }
                if (etPasswordB.getError() != null) {
                    etPasswordB.setError(null);
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

        etPasswordB.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etPasswordA.getError() != null) {
                    etPasswordA.setError(null);
                }
                if (etPasswordB.getError() != null) {
                    etPasswordB.setError(null);
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

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.label_ok), null)
                .setNegativeButton(getString(R.string.label_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Helper.hideKeyboardAlways(getActivity());
                                dialog.cancel();
                                openDialog = null;
                            }
                        });

        openDialog = alertDialogBuilder.create();
        openDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPasswordA = etPasswordA.getEditText().getText().toString();
                        String newPasswordB = etPasswordB.getEditText().getText().toString();
                        // disallow empty passwords
                        if (newPasswordA.isEmpty()) {
                            etPasswordA.setError(getString(R.string.generate_empty_passwordB));
                        } else if (!newPasswordA.equals(newPasswordB)) {
                            etPasswordB.setError(getString(R.string.generate_bad_passwordB));
                        } else if (newPasswordA.equals(newPasswordB)) {
                            new AsyncChangePassword().execute(newPasswordA, Boolean.toString(swFingerprintAllowed.isChecked()));
                            Helper.hideKeyboardAlways(getActivity());
                            openDialog.dismiss();
                            openDialog = null;
                        }
                    }
                });
            }
        });

        // accept keyboard "ok"
        etPasswordB.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String newPasswordA = etPasswordA.getEditText().getText().toString();
                    String newPasswordB = etPasswordB.getEditText().getText().toString();
                    // disallow empty passwords
                    if (newPasswordA.isEmpty()) {
                        etPasswordA.setError(getString(R.string.generate_empty_passwordB));
                    } else if (!newPasswordA.equals(newPasswordB)) {
                        etPasswordB.setError(getString(R.string.generate_bad_passwordB));
                    } else if (newPasswordA.equals(newPasswordB)) {
                        new AsyncChangePassword().execute(newPasswordA, Boolean.toString(swFingerprintAllowed.isChecked()));
                        Helper.hideKeyboardAlways(getActivity());
                        openDialog.dismiss();
                        openDialog = null;
                    }
                    return true;
                }
                return false;
            }
        });
        //TODO BuildConfig.DEBUG
        // set FLAG_SECURE to prevent screenshots in Release Mode
        /*if (!(BuildConfig.DEBUG && BuildConfig.FLAVOR_type.equals("alpha"))) {
            openDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }*/

        return openDialog;
    }

    private boolean isKeyValid(String key) {
        return (key != null) && (key.length() == 64)
                && !key.equals("0000000000000000000000000000000000000000000000000000000000000000")
                && !key.toLowerCase().equals("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
        // ledger implmenetation returns the spend key as f's
    }
}
