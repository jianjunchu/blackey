package org.blackey.ui.market.sell;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.m2049r.xmrwallet.data.PendingTx;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.Wallet;
import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketReleaseAdvertiseBinding;

import java.text.NumberFormat;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;
import timber.log.Timber;

/**
 * 市场
 */
public class ReleaseAdvertiseFragment extends BaseFragment<FragmentMarketReleaseAdvertiseBinding, ReleaseAdvertiseViewModel> {


    Listener listenerCallback = null;

    private NumberFormat formatter = NumberFormat.getInstance();

    public void resetDismissedTransactions() {
    }

    public void setProgress(String text) {
        KLog.e(text);
        if(viewModel.dialog!=null){
            viewModel.dialog.setProgress(text);
        }
    }

    public void setProgress(final int n) {
        KLog.e(n);
        if(viewModel.dialog!=null){
            viewModel.dialog.setProgress(n);
        }
    }

    public void onSynced() {

    }

    public void onRefreshed(Wallet wallet, boolean full) {
        updateStatus(wallet);
    }

    public void onLoaded() {
    }

    public void onCreateTransactionFailed(String errorText) {
        showAlert(getString(R.string.send_create_tx_error_title), errorText);
    }

    PendingTx pendingTx;

    public PendingTx getPendingTx() {
        return pendingTx;
    }

    public void onTransactionCreated(String txTag, PendingTransaction pendingTransaction) {
        showDialog(getString(R.string.loading));
        pendingTx = new PendingTx(pendingTransaction);
        listenerCallback.onSend(viewModel.txData.getUserNotes());
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).
                setTitle(title).
                setMessage(message).
                create().
                show();
    }

    public void onTransactionSent(String txId) {
        String txkey = listenerCallback.getWallet().getTxKey(txId);

        viewModel.onTransactionSent(txId,txkey);

    }

    public void onSendTransactionFailed(String error) {
        RxToast.error(getString(R.string.status_transaction_failed, error));
    }

    public interface Listener {

        void onSend(UserNotes notes);

        void onPrepareSend(final String tag, final TxData txData);

        void setTitle(String title);

        boolean hasBoundService();

        Wallet getWallet();


        long getDaemonHeight(); //mBoundService.getDaemonHeight();

        Wallet.ConnectionStatus getConnectionStatus();

        void startWalletService(String walletName, String walletPassword);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listenerCallback = (Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_release_advertise;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        viewModel.listenerCallback = listenerCallback;
        viewModel.activity = getActivity();
        //初始化标题
        listenerCallback.setTitle(getString(R.string.publish));
        viewModel.initData();

    }

    private long firstBlock = 0;

    private void updateStatus(Wallet wallet) {
        if (!isAdded()) return;
        viewModel.dialog.setSync(true);

        Timber.d("updateStatus()");

        long balance = wallet.getBalance();
        viewModel.unlockedBalance = wallet.getUnlockedBalance();
        String sync = "";
        if (!listenerCallback.hasBoundService())
            throw new IllegalStateException("WalletService not bound.");
        Wallet.ConnectionStatus daemonConnected = listenerCallback.getConnectionStatus();
        if (daemonConnected == Wallet.ConnectionStatus.ConnectionStatus_Connected) {
            long daemonHeight = listenerCallback.getDaemonHeight();
            if (!wallet.isSynchronized()) {
                long n = daemonHeight - wallet.getBlockChainHeight();
                sync = getString(R.string.status_syncing) + " " + formatter.format(n) + " " + getString(R.string.status_remaining);
                if (firstBlock == 0) {
                    firstBlock = wallet.getBlockChainHeight();
                }
                int x = 100 - Math.round(100f * n / (1f * daemonHeight - firstBlock));
                if (x == 0) x = 101; // indeterminate
                setProgress(x);
            } else {
                sync = getString(R.string.status_synced) + " " + formatter.format(wallet.getBlockChainHeight());
                showDialog(getString(R.string.loading));

                if(wallet.getUnlockedBalance() < viewModel.advertise.getTotalInventory().longValue()){

                    RxToast.showToast(R.string.not_sufficient_funds);
                    dismissDialog();
                    viewModel.dialog.dismiss();
                    return;
                }else{
                    listenerCallback.onPrepareSend(viewModel.advertise.getAdId().toString(),viewModel.txData);
                }

            }
        } else {
            sync = getString(R.string.status_wallet_connecting);
            setProgress(101);
        }
        setProgress(sync);
        // TODO show connected status somewhere
    }


}
