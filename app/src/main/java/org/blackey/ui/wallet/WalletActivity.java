package org.blackey.ui.wallet;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityWalletBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.market.sell.ReleaseAdvertiseFragment;
import org.blackey.ui.wallet.detail.GenerateReviewFragment;
import org.blackey.ui.wallet.detail.TxFragment;
import org.blackey.ui.wallet.detail.WalletTransactionFragment;
import org.blackey.ui.wallet.receive.ReceiveFragment;
import org.blackey.ui.wallet.send.SendAddressWizardFragment;
import org.blackey.ui.wallet.send.SendFragment;
import com.m2049r.xmrwallet.OnBackPressedListener;
import com.m2049r.xmrwallet.OnUriScannedListener;
import com.m2049r.xmrwallet.data.BarcodeData;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.ledger.LedgerProgressDialog;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.TransactionInfo;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.service.WalletService;
import com.m2049r.xmrwallet.util.Helper;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.KLog;
import timber.log.Timber;

public class WalletActivity extends BlackeyBaseActivity<ActivityWalletBinding,WalletViewModel> implements WalletTransactionFragment.Listener,
        WalletService.Observer, SendFragment.Listener,
        GenerateReviewFragment.ListenerWithWallet,TxFragment.Listener,
        GenerateReviewFragment.Listener,
        GenerateReviewFragment.PasswordChangedListener,
        ScannerFragment.OnScannedListener, ReceiveFragment.Listener,
        ReleaseAdvertiseFragment.Listener,
        SendAddressWizardFragment.OnScanListener {

    public static final String REQUEST_ID = "id";
    public static final String REQUEST_PW = "pw";
    public static final String REQUEST_FINGERPRINT_USED = "fingerprint";
    public static final String REQUEST_STREETMODE = "streetmode";
    public static final String REQUEST_URI = "uri";

    private boolean needVerifyIdentity;
    private boolean requestStreetMode = false;

    private String password;

    private String uri = null;

    private long streetMode = 0;

    @Override
    public void onPasswordChanged(String newPassword) {
        password = newPassword;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setToolbarButton(int type) {

    }

    @Override
    public void setTitle(String title, String subtitle) {
        viewModel.setTitleText(title);
    }

    @Override
    public void setTitle(String title) {
        KLog.d("setTitle:%s.", title);
        viewModel.setTitleText(title);
    }

    @Override
    public void setSubtitle(String subtitle) {
        viewModel.setTitleText(subtitle);
    }

    private boolean synced = false;

    @Override
    public boolean isSynced() {
        return synced;
    }

    @Override
    public boolean isStreetMode() {
        return streetMode > 0;
    }

    private void enableStreetMode(boolean enable) {
        if (enable) {
            needVerifyIdentity = true;
            streetMode = getWallet().getDaemonBlockChainHeight();
        } else {
            streetMode = 0;
        }
        final WalletTransactionFragment walletFragment = (WalletTransactionFragment)
                getSupportFragmentManager().findFragmentByTag(WalletTransactionFragment.class.getName());
        if (walletFragment != null) walletFragment.resetDismissedTransactions();
        forceUpdate();
    }

    @Override
    public long getStreetModeHeight() {
        return streetMode;
    }

    @Override
    public boolean isWatchOnly() {
        return getWallet().isWatchOnly();
    }

    @Override
    public String getWalletSubaddress(int accountIndex, int subaddressIndex) {
        return getWallet().getSubaddress(accountIndex, subaddressIndex);
    }

    @Override
    public String getTxKey(String txId) {
        return getWallet().getTxKey(txId);
    }

    @Override
    public String getTxNotes(String txId) {
        return getWallet().getUserNote(txId);
    }

    @Override
    public String getTxAddress(int major, int minor) {
        return getWallet().getSubaddress(major, minor);
    }

    @Override
    public void onSetNote(String txId, String notes) {
        if (mIsBound) { // no point in talking to unbound service
            Intent intent = new Intent(getApplicationContext(), WalletService.class);
            intent.putExtra(WalletService.REQUEST, WalletService.REQUEST_CMD_SETNOTE);
            intent.putExtra(WalletService.REQUEST_CMD_SETNOTE_TX, txId);
            intent.putExtra(WalletService.REQUEST_CMD_SETNOTE_NOTES, notes);
            startService(intent);
            Timber.d("SET NOTE request sent");
        } else {
            Timber.e("Service not bound");
        }
    }

    @Override
    public void onSetNotes(final boolean success) {
        try {
            final TxFragment txFragment = (TxFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!success) {
                        Toast.makeText(WalletActivity.this, getString(R.string.tx_notes_set_failed), Toast.LENGTH_LONG).show();
                    }
                    txFragment.onNotesSet(success);
                }
            });
        } catch (ClassCastException ex) {
            // not in tx fragment
            Timber.d(ex.getLocalizedMessage());
            // never mind
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        KLog.d("onStart()");
    }

    private void startWalletService() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            acquireWakeLock();
            String walletId = extras.getString(REQUEST_ID);
            needVerifyIdentity = extras.getBoolean(REQUEST_FINGERPRINT_USED);
            // we can set the streetmode height AFTER opening the wallet
            requestStreetMode = extras.getBoolean(REQUEST_STREETMODE);
            password = extras.getString(REQUEST_PW);
            uri = extras.getString(REQUEST_URI);
            connectWalletService(walletId, password);
        } else {
            finish();
            //throw new IllegalStateException("No extras passed! Panic!");
        }
    }

    private void stopWalletService() {
        disconnectWalletService();
        releaseWakeLock();
    }

    @Override
    protected void onStop() {
        KLog.d("onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        KLog.d("onDestroy()");
        if ((mBoundService != null) && (getWallet() != null)) {
            saveWallet();
        }
        stopWalletService();

        super.onDestroy();
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_wallet;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public boolean hasWallet() {
        return haveWallet;
    }




    public void onWalletChangePassword() {
        try {
            GenerateReviewFragment detailsFragment = (GenerateReviewFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            AlertDialog dialog = detailsFragment.createChangePasswordDialog();
            if (dialog != null) {
                Helper.showKeyboard(dialog);
                dialog.show();
            }
        } catch (ClassCastException ex) {
            KLog.w("onWalletChangePassword() called, but no GenerateReviewFragment active");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KLog.d("onCreate()");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // activity restarted
            // we don't want that - finish it and fall back to previous activity
            finish();
            return;
        }

        Fragment fragment;
        Bundle data=getIntent().getExtras();
        if(data!=null && "ReleaseAdvertiseFragment".equalsIgnoreCase(data.getString("action"))){
            fragment = new ReleaseAdvertiseFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, ReleaseAdvertiseFragment.class.getName()).commit();
        }else{
            fragment = new WalletTransactionFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, WalletTransactionFragment.class.getName()).commit();

        }

        KLog.d("fragment added");

        startWalletService();
        KLog.d("onCreate() done.");
    }


    @Override
    public Wallet getWallet() {
        if (mBoundService == null) throw new IllegalStateException("WalletService not bound.");
        return mBoundService.getWallet();
    }

    private WalletService mBoundService = null;
    private boolean mIsBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((WalletService.WalletServiceBinder) service).getService();
            mBoundService.setObserver(WalletActivity.this);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String walletId = extras.getString(REQUEST_ID);
                if (walletId != null) {
                    setTitle(walletId, getString(R.string.status_wallet_connecting));
                }
            }
            updateProgress();
            KLog.d("CONNECTED");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            setTitle(getString(R.string.wallet_activity_name), getString(R.string.status_wallet_disconnected));
            KLog.d("DISCONNECTED");
        }
    };

    void connectWalletService(String walletName, String walletPassword) {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        Intent intent = new Intent(getApplicationContext(), WalletService.class);
        intent.putExtra(WalletService.REQUEST_WALLET, walletName);
        intent.putExtra(WalletService.REQUEST, WalletService.REQUEST_CMD_LOAD);
        intent.putExtra(WalletService.REQUEST_CMD_LOAD_PW, walletPassword);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        KLog.d("BOUND");
    }

    void disconnectWalletService() {
        if (mIsBound) {
            // Detach our existing connection.
            mBoundService.setObserver(null);
            unbindService(mConnection);
            mIsBound = false;
            KLog.d("UNBOUND");
        }
    }

    @Override
    protected void onPause() {
        KLog.d("onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d("onResume()");
    }

    public void saveWallet() {
        if (mIsBound) { // no point in talking to unbound service
            Intent intent = new Intent(getApplicationContext(), WalletService.class);
            intent.putExtra(WalletService.REQUEST, WalletService.REQUEST_CMD_STORE);
            startService(intent);
            KLog.d("STORE request sent");
        } else {
            KLog.e("Service not bound");
        }
    }

//////////////////////////////////////////
// WalletTransactionFragment.Listener
//////////////////////////////////////////



    @Override
    public boolean hasBoundService() {
        return mBoundService != null;
    }

    @Override
    public Wallet.ConnectionStatus getConnectionStatus() {
        return mBoundService.getConnectionStatus();
    }

    @Override
    public void startWalletService(String walletName, String walletPassword) {
        connectWalletService(walletName,walletPassword);
    }

    @Override
    public long getDaemonHeight() {
        return mBoundService.getDaemonHeight();
    }

    @Override
    public void onSendRequest() {
        replaceFragment(SendFragment.newInstance(uri), null, null);
        uri = null; // only use uri once
    }

    @Override
    public void onTxDetailsRequest(TransactionInfo info) {
        Bundle args = new Bundle();
        args.putParcelable(TxFragment.ARG_INFO, info);
        replaceFragment(new TxFragment(), null, args);
    }

    @Override
    public void forceUpdate() {
        try {
            onRefreshed(getWallet(), true);
        } catch (IllegalStateException ex) {
            KLog.e(ex.getLocalizedMessage());
        }
    }

///////////////////////////
// WalletService.Observer
///////////////////////////

    private int numAccounts = -1;

    // refresh and return true if successful
    @Override
    public boolean onRefreshed(final Wallet wallet, final boolean full) {
        KLog.d("onRefreshed()");

        try {
            final WalletTransactionFragment walletFragment = (WalletTransactionFragment)
                    getSupportFragmentManager().findFragmentByTag(WalletTransactionFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());

            if (wallet.isSynchronized()) {
                KLog.d("onRefreshed() synced");
                releaseWakeLock(RELEASE_WAKE_LOCK_DELAY); // the idea is to stay awake until synced
                if (!synced) { // first sync
                    onProgress(-1);
                    saveWallet(); // save on first sync
                    synced = true;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(walletFragment!=null){
                                walletFragment.onSynced();
                            }

                            if(advertiseFragment!=null){
                                advertiseFragment.onSynced();
                            }

                        }
                    });
                }
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    if(walletFragment!=null){
                        walletFragment.onRefreshed(wallet, full);
                    }

                    if(advertiseFragment!=null){
                        advertiseFragment.onRefreshed(wallet, full);
                    }

                }
            });
            return true;
        } catch (ClassCastException ex) {
            // not in wallet fragment (probably send monero)
            KLog.d(ex.getLocalizedMessage());
            // keep calm and carry on
        }
        return false;
    }

    @Override
    public void onWalletStored(final boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (success) {
                    Toast.makeText(WalletActivity.this, getString(R.string.status_wallet_unloaded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WalletActivity.this, getString(R.string.status_wallet_unload_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    boolean haveWallet = false;

    @Override
    public void onWalletOpen(final Wallet.Device device) {
        switch (device) {
            case Device_Ledger:
                runOnUiThread(new Runnable() {
                    public void run() {
                        showLedgerProgressDialog(LedgerProgressDialog.TYPE_RESTORE);
                    }
                });
        }
    }

    @Override
    public void onWalletStarted(final Wallet.ConnectionStatus connStatus) {
        runOnUiThread(new Runnable() {
            public void run() {
                dismissProgressDialog();
                switch (connStatus) {
                    case ConnectionStatus_Disconnected:
                        Toast.makeText(WalletActivity.this, getString(R.string.status_wallet_connect_failed), Toast.LENGTH_LONG).show();
                        break;
                    case ConnectionStatus_WrongVersion:
                        Toast.makeText(WalletActivity.this, getString(R.string.status_wallet_connect_wrongversion), Toast.LENGTH_LONG).show();
                        break;
                    case ConnectionStatus_Connected:
                        break;
                }
            }
        });
        if (connStatus != Wallet.ConnectionStatus.ConnectionStatus_Connected) {
            finish();
        } else {
            haveWallet = true;
            invalidateOptionsMenu();

            enableStreetMode(requestStreetMode);

            final WalletTransactionFragment walletFragment = (WalletTransactionFragment)
                    getSupportFragmentManager().findFragmentByTag(WalletTransactionFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());

            runOnUiThread(new Runnable() {
                public void run() {
                    if (walletFragment != null) {
                        walletFragment.onLoaded();
                    }

                    if (advertiseFragment != null) {
                        advertiseFragment.onLoaded();
                    }
                }
            });
        }
    }

    @Override
    public void onTransactionCreated(final String txTag, final PendingTransaction pendingTransaction) {
        try {


            final SendFragment sendFragment = (SendFragment)
                    getSupportFragmentManager().findFragmentByTag(SendFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());

            runOnUiThread(new Runnable() {
                public void run() {
                    dismissProgressDialog();
                    PendingTransaction.Status status = pendingTransaction.getStatus();
                    if (status != PendingTransaction.Status.Status_Ok) {
                        String errorText = pendingTransaction.getErrorString();
                        KLog.e(errorText);
                        getWallet().disposePendingTransaction();

                        if(sendFragment!=null){
                            sendFragment.onCreateTransactionFailed(errorText);
                        }
                        if(advertiseFragment!=null){
                            advertiseFragment.onCreateTransactionFailed(errorText);
                        }


                    } else {
                        if(sendFragment!=null)
                        sendFragment.onTransactionCreated(txTag, pendingTransaction);

                        if(advertiseFragment!=null)
                            advertiseFragment.onTransactionCreated(txTag, pendingTransaction);
                    }
                }
            });
        } catch (ClassCastException ex) {
            // not in spend fragment
            KLog.d(ex.getLocalizedMessage());
            // don't need the transaction any more
            getWallet().disposePendingTransaction();
        }
    }

    @Override
    public void onSendTransactionFailed(final String error) {
        try {
            final SendFragment sendFragment = (SendFragment)
                    getSupportFragmentManager().findFragmentByTag(SendFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());

            runOnUiThread(new Runnable() {
                public void run() {
                    if(sendFragment!=null){
                        sendFragment.onSendTransactionFailed(error);
                    }
                    if(advertiseFragment!=null){
                        advertiseFragment.onSendTransactionFailed(error);
                    }



                }
            });
        } catch (ClassCastException ex) {
            // not in spend fragment
            KLog.d(ex.getLocalizedMessage());
        }
    }

    @Override
    public void onTransactionSent(final String txId) {
        try {
            final SendFragment sendFragment = (SendFragment)
                    getSupportFragmentManager().findFragmentByTag(SendFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());
            runOnUiThread(new Runnable() {
                public void run() {
                    if(sendFragment!=null){
                        sendFragment.onTransactionSent(txId);
                    }
                    if(advertiseFragment!=null){
                        advertiseFragment.onTransactionSent(txId);
                    }

                }
            });
        } catch (ClassCastException ex) {
            // not in spend fragment
            KLog.d(ex.getLocalizedMessage());
        }
    }



    @Override
    public void onProgress(final String text) {
        try {
            final WalletTransactionFragment walletFragment = (WalletTransactionFragment)
                    getSupportFragmentManager().findFragmentByTag(WalletTransactionFragment.class.getName());

            final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                    getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());

            runOnUiThread(new Runnable() {
                public void run() {
                    if(walletFragment!=null){
                        walletFragment.setProgress(text);
                    }
                    if(advertiseFragment!=null){
                        advertiseFragment.setProgress(text);
                    }

                }
            });
        } catch (ClassCastException ex) {
            // not in wallet fragment (probably send monero)
            KLog.d(ex.getLocalizedMessage());
            // keep calm and carry on
        }
    }

    @Override
    public void onProgress(final int n) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    WalletTransactionFragment walletFragment = (WalletTransactionFragment)
                            getSupportFragmentManager().findFragmentByTag(WalletTransactionFragment.class.getName());

                    final ReleaseAdvertiseFragment advertiseFragment = (ReleaseAdvertiseFragment)
                            getSupportFragmentManager().findFragmentByTag(ReleaseAdvertiseFragment.class.getName());


                    if (walletFragment != null)
                        walletFragment.setProgress(n);

                    if (advertiseFragment != null)
                        advertiseFragment.setProgress(n);

                } catch (ClassCastException ex) {
                    // not in wallet fragment (probably send monero)
                    KLog.d(ex.getLocalizedMessage());
                    // keep calm and carry on
                }
            }
        });
    }

    private void updateProgress() {
        // TODO maybe show real state of WalletService (like "still closing previous wallet")
        if (hasBoundService()) {
            onProgress(mBoundService.getProgressText());
            onProgress(mBoundService.getProgressValue());
        }
    }

///////////////////////////
// SendFragment.Listener
///////////////////////////

    @Override
    public void onSend(UserNotes notes) {
        if (mIsBound) { // no point in talking to unbound service
            Intent intent = new Intent(getApplicationContext(), WalletService.class);
            intent.putExtra(WalletService.REQUEST, WalletService.REQUEST_CMD_SEND);
            intent.putExtra(WalletService.REQUEST_CMD_SEND_NOTES, notes.txNotes);
            startService(intent);
            KLog.d("SEND TX request sent");
        } else {
            KLog.e("Service not bound");
        }

    }



    @Override
    public void onPrepareSend(final String tag, final TxData txData) {
        if (mIsBound) { // no point in talking to unbound service
            Intent intent = new Intent(getApplicationContext(), WalletService.class);
            intent.putExtra(WalletService.REQUEST, WalletService.REQUEST_CMD_TX);
            intent.putExtra(WalletService.REQUEST_CMD_TX_DATA, txData);
            intent.putExtra(WalletService.REQUEST_CMD_TX_TAG, tag);
            startService(intent);
            KLog.d("CREATE TX request sent");
            if (getWallet().getDeviceType() == Wallet.Device.Device_Ledger)
                showLedgerProgressDialog(LedgerProgressDialog.TYPE_SEND);
        } else {
            KLog.e("Service not bound");
        }
    }


    public String getWalletName() {
        return getWallet().getName();
    }

    void popFragmentStack(String name) {
        if (name == null) {
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    void replaceFragment(Fragment newFragment, String stackName, Bundle extras) {
        if (extras != null) {
            newFragment.setArguments(extras);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment,Fragment.class.getName());
        transaction.addToBackStack(stackName);
        transaction.commit();
    }

    private void onWalletDetails() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        final Bundle extras = new Bundle();
                        extras.putString(GenerateReviewFragment.REQUEST_TYPE, GenerateReviewFragment.VIEW_TYPE_WALLET);

                        if (needVerifyIdentity) {
                            Helper.promptPassword(WalletActivity.this, getWallet().getName(), true, new Helper.PasswordAction() {
                                @Override
                                public void action(String walletName, String password, boolean fingerprintUsed) {
                                    replaceFragment(new GenerateReviewFragment(), null, extras);
                                    needVerifyIdentity = false;
                                }
                            });
                        } else {
                            replaceFragment(new GenerateReviewFragment(), null, extras);
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.details_alert_message))
                .setPositiveButton(getString(R.string.details_alert_yes), dialogClickListener)
                .setNegativeButton(getString(R.string.details_alert_no), dialogClickListener)
                .show();
    }



    @Override
    public void onDisposeRequest() {
        //TODO consider doing this through the WalletService to avoid concurrency issues
        getWallet().disposePendingTransaction();
    }

    private boolean startScanFragment = false;

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (startScanFragment) {
            startScanFragment();
            startScanFragment = false;
        }
    }

    private void startScanFragment() {
        Bundle extras = new Bundle();
        replaceFragment(new ScannerFragment(), null, extras);
    }

    /// QR scanner callbacks
    @Override
    public void onScan() {
        if (Helper.getCameraPermission(this)) {
            startScanFragment();
        } else {
            KLog.i("Waiting for permissions");
        }

    }

    @Override
    public boolean onScanned(String qrCode) {
        // #gurke
        BarcodeData bcData = BarcodeData.fromQrCode(qrCode);
        if (bcData != null) {
            popFragmentStack(null);
            KLog.d("AAA");
            onUriScanned(bcData);
            return true;
        } else {
            return false;
        }
    }

    OnUriScannedListener onUriScannedListener = null;

    @Override
    public void setOnUriScannedListener(OnUriScannedListener onUriScannedListener) {
        this.onUriScannedListener = onUriScannedListener;
    }

    @Override
    public void onUriScanned(BarcodeData barcodeData) {
        super.onUriScanned(barcodeData);
        boolean processed = false;
        if (onUriScannedListener != null) {
            processed = onUriScannedListener.onUriScanned(barcodeData);
        }
        if (!processed || (onUriScannedListener == null)) {
            Toast.makeText(this, getString(R.string.nfc_tag_read_what), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        KLog.d("onRequestPermissionsResult()");
        switch (requestCode) {
            case Helper.PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanFragment = true;
                } else {
                    String msg = getString(R.string.message_camera_not_permitted);
                    KLog.e(msg);
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onWalletReceive() {
        startReceive(getWallet().getAddress());
    }

    void startReceive(String address) {
        KLog.d("startReceive()");
        Bundle b = new Bundle();
        b.putString("address", address);
        b.putString("name", getWalletName());
        startReceiveFragment(b);
    }

    void startReceiveFragment(Bundle extras) {
        replaceFragment(new ReceiveFragment(), null, extras);
        KLog.d("ReceiveFragment placed");
    }

    @Override
    public long getTotalFunds() {
        return getWallet().getUnlockedBalance();
    }

    @Override
    public boolean verifyWalletPassword(String password) {
        String walletPassword = Helper.getWalletPassword(getApplicationContext(), getWalletName(), password);
        return walletPassword != null;
    }

    @Override
    public void onBackPressed() {

        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof OnBackPressedListener) {
            if (!((OnBackPressedListener) fragment).onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentDone() {
        popFragmentStack(null);
    }

    @Override
    public SharedPreferences getPrefs() {
        return getPreferences(Context.MODE_PRIVATE);
    }

    private List<Integer> accountIds = new ArrayList<>();

    // generate and cache unique ids for use in accounts list
    private int getAccountId(int accountIndex) {
        final int n = accountIds.size();
        for (int i = n; i <= accountIndex; i++) {
            accountIds.add(View.generateViewId());
        }
        return accountIds.get(accountIndex);
    }




}
