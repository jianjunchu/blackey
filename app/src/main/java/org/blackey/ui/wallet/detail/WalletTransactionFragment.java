package org.blackey.ui.wallet.detail;

import android.content.Context;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.m2049r.xmrwallet.model.TransactionInfo;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeApi;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeCallback;
import com.m2049r.xmrwallet.service.exchange.api.ExchangeRate;
import com.m2049r.xmrwallet.util.Helper;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentWalletTransactionBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class WalletTransactionFragment extends BaseFragment<FragmentWalletTransactionBinding, WalletTransactionViewModel> implements WalletTransactionViewModel.OnInteractionListener {

    Listener activityCallback;

    private NumberFormat formatter = NumberFormat.getInstance();

    private String walletTitle = null;

    private List<String> dismissedTransactions = new ArrayList<>();

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_wallet_transaction;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            this.activityCallback = (Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.d("onResume()");
        activityCallback.setTitle(walletTitle, walletSubtitle);
        //activityCallback.setToolbarButton(Toolbar.BUTTON_CLOSE); // TODO: Close button somewhere else
        setProgress(syncProgress);
        setProgress(syncText);
        showReceive();
        if (activityCallback.isSynced()) enableAccountsList(true);
    }

    @Override
    public void onPause() {
        enableAccountsList(false);
        super.onPause();
    }

    @Override
    public void onInteraction(View view, TransactionInfo infoItem) {
        activityCallback.onTxDetailsRequest(infoItem);
    }

    public interface DrawerLocker {
        void setDrawerEnabled(boolean enabled);
    }

    private void enableAccountsList(boolean enable) {
        if (activityCallback instanceof DrawerLocker) {
            ((DrawerLocker) activityCallback).setDrawerEnabled(enable);
        }
    }

    @Override
    public void initData() {

        viewModel.listener = this;
        if (activityCallback.isSynced()) {
            onSynced();
        }
        showBalance(Helper.getDisplayAmount(0));
        showUnconfirmed(0);
        activityCallback.forceUpdate();
    }

    @Override
    public void initViewObservable() {
        //监听下拉刷新完成
        viewModel.uc.finishReceive.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                activityCallback.onWalletReceive();
            }
        });
        //监听上拉加载完成
        viewModel.uc.finishSend.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                activityCallback.onSendRequest();
            }
        });

        binding.sCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refreshBalance();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing (yet?)
            }
        });
    }

    public void onSynced() {
        if (!activityCallback.isWatchOnly()) {
            binding.bSend.setVisibility(View.VISIBLE);
            binding.bSend.setEnabled(true);
        }
        if (isVisible()) enableAccountsList(true); //otherwise it is enabled in onResume()
    }

    void showBalance(String balance) {
        binding.tvBalance.setText(balance);
        if (!activityCallback.isStreetMode()) {
            binding.llBalance.setVisibility(View.VISIBLE);
            binding.tvStreetView.setVisibility(View.INVISIBLE);
        } else {
            binding.llBalance.setVisibility(View.INVISIBLE);
            binding.tvStreetView.setVisibility(View.VISIBLE);
        }
    }

    void showUnconfirmed(double unconfirmedAmount) {
        if (!activityCallback.isStreetMode()) {
            String unconfirmed = Helper.getFormattedAmount(unconfirmedAmount, true);
            binding.tvUnconfirmedAmount.setText(getResources().getString(R.string.xmr_unconfirmed_amount, unconfirmed));
        } else {
            binding.tvUnconfirmedAmount.setText(null);
        }
    }



    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private String syncText = null;

    public void setProgress(final String text) {
        KLog.e(text);
        syncText = text;
        binding.tvProgress.setText(text);
    }

    public void resetDismissedTransactions() {
        dismissedTransactions.clear();
    }

    private int syncProgress = -1;
    public void setProgress(final int n) {
        KLog.e(n);
        syncProgress = n;
        if (n > 100) {
            binding.pbProgress.setIndeterminate(true);
            binding.pbProgress.setVisibility(View.VISIBLE);
        } else if (n >= 0) {
            binding.pbProgress.setIndeterminate(false);
            binding.pbProgress.setProgress(n);
            binding.pbProgress.setVisibility(View.VISIBLE);
        } else { // <0
            binding.pbProgress.setVisibility(View.INVISIBLE);
        }
    }

    boolean walletLoaded = false;

    public void onLoaded() {
        walletLoaded = true;
        showReceive();
    }

    private void showReceive() {
        if (walletLoaded) {
            binding.bReceive.setVisibility(View.VISIBLE);
            binding.bReceive.setEnabled(true);
        }
    }

    public void onRefreshed(final Wallet wallet, final boolean full) {
        KLog.d("onRefreshed(%b)", full);
        if (full) {
            List<TransactionInfo> list = new ArrayList<>();
            final long streetHeight = activityCallback.getStreetModeHeight();
            KLog.d("StreetHeight=%d", streetHeight);
            for (TransactionInfo info : wallet.getHistory().getAll()) {
                KLog.d("TxHeight=%d", info.blockheight);
                if ((info.isPending || (info.blockheight >= streetHeight))
                        && !dismissedTransactions.contains(info.hash))
                    list.add(info);
            }
            viewModel.setInfos(list);

        }
        updateStatus(wallet);
    }

    private long firstBlock = 0;
    private String walletSubtitle = null;
    private long unlockedBalance = 0;
    private long balance = 0;
    private int accountIdx = -1;

    private void updateStatus(Wallet wallet) {
        if (!isAdded()) return;
        KLog.d("updateStatus()");

        balance = wallet.getBalance();
        unlockedBalance = wallet.getUnlockedBalance();
        refreshBalance();
        String sync = "";
        if (!activityCallback.hasBoundService())
            throw new IllegalStateException("WalletService not bound.");
        Wallet.ConnectionStatus daemonConnected = activityCallback.getConnectionStatus();
        if (daemonConnected == Wallet.ConnectionStatus.ConnectionStatus_Connected) {
            if (!wallet.isSynchronized()) {
                long daemonHeight = activityCallback.getDaemonHeight();
                long walletHeight = wallet.getBlockChainHeight();
                long n = daemonHeight - walletHeight;
                sync = getString(R.string.status_syncing) + " " + formatter.format(n) + " " + getString(R.string.status_remaining);
                if (firstBlock == 0) {
                    firstBlock = walletHeight;
                }
                int x = 100 - Math.round(100f * n / (1f * daemonHeight - firstBlock));
                if (x == 0) x = 101; // indeterminate
                setProgress(x);
                binding.ivSynced.setVisibility(View.GONE);
            } else {
                sync = getString(R.string.status_synced) + " " + formatter.format(wallet.getBlockChainHeight());
                binding.ivSynced.setVisibility(View.VISIBLE);
            }
        } else {
            sync = getString(R.string.status_wallet_connecting);
            setProgress(101);
        }
        setProgress(sync);
        // TODO show connected status somewhere
    }
    String balanceCurrency = "XMR";
    double balanceRate = 1.0;

    private final ExchangeApi exchangeApi = Helper.getExchangeApi();

    void refreshBalance() {
        double unconfirmedXmr = Double.parseDouble(Helper.getDisplayAmount(balance - unlockedBalance));
        showUnconfirmed(unconfirmedXmr);
        if (binding.sCurrency.getSelectedItemPosition() == 0) { // XMR
            double amountXmr = Double.parseDouble(Wallet.getDisplayAmount(unlockedBalance)); // assume this cannot fail!
            showBalance(Helper.getFormattedAmount(amountXmr, true));
        } else { // not XMR
            String currency = (String) binding.sCurrency.getSelectedItem();
            KLog.d(currency);
            if (!currency.equals(balanceCurrency) || (balanceRate <= 0)) {
                showExchanging();
                exchangeApi.queryExchangeRate(Helper.CRYPTO, currency,
                        new ExchangeCallback() {
                            @Override
                            public void onSuccess(final ExchangeRate exchangeRate) {
                                if (isAdded())
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            exchange(exchangeRate);
                                        }
                                    });
                            }

                            @Override
                            public void onError(final Exception e) {
                                KLog.e(e.getLocalizedMessage());
                                if (isAdded())
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            exchangeFailed();
                                        }
                                    });
                            }
                        });
            } else {
                updateBalance();
            }
        }
    }

    boolean isExchanging = false;

    void showExchanging() {
        isExchanging = true;
        binding.tvBalance.setVisibility(View.GONE);
        binding.flExchange.setVisibility(View.VISIBLE);
        binding.sCurrency.setEnabled(false);
    }

    void hideExchanging() {
        isExchanging = false;
        binding.tvBalance.setVisibility(View.VISIBLE);
        binding.flExchange.setVisibility(View.GONE);
        binding.sCurrency.setEnabled(true);
    }

    public void exchangeFailed() {
        binding.sCurrency.setSelection(0, true); // default to XMR
        double amountXmr = Double.parseDouble(Wallet.getDisplayAmount(unlockedBalance)); // assume this cannot fail!
        binding.tvBalance.setText(Helper.getFormattedAmount(amountXmr, true));
        hideExchanging();
    }

    void updateBalance() {
        if (isExchanging) return; // wait for exchange to finish - it will fire this itself then.
        // at this point selection is XMR in case of error
        String displayB;
        double amountA = Double.parseDouble(Wallet.getDisplayAmount(unlockedBalance)); // crash if this fails!
        if (!"XMR".equals(balanceCurrency)) { // not XMR
            double amountB = amountA * balanceRate;
            displayB = Helper.getFormattedAmount(amountB, false);
        } else { // XMR
            displayB = Helper.getFormattedAmount(amountA, true);
        }
        binding.tvBalance.setText(displayB);
    }

    public void exchange(final ExchangeRate exchangeRate) {
        hideExchanging();
        if (!"XMR".equals(exchangeRate.getBaseCurrency())) {
            KLog.e("Not XMR");
            binding.sCurrency.setSelection(0, true);
            balanceCurrency = "XMR";
            balanceRate = 1.0;
        } else {
            int spinnerPosition = ((ArrayAdapter) binding.sCurrency.getAdapter()).getPosition(exchangeRate.getQuoteCurrency());
            if (spinnerPosition < 0) { // requested currency not in list
                KLog.e("Requested currency not in list %s", exchangeRate.getQuoteCurrency());
                binding.sCurrency.setSelection(0, true);
            } else {
                binding.sCurrency.setSelection(spinnerPosition, true);
            }
            balanceCurrency = exchangeRate.getQuoteCurrency();
            balanceRate = exchangeRate.getRate();
        }
        updateBalance();
    }

    void setActivityTitle(Wallet wallet) {
        if (wallet == null) return;
        walletTitle = wallet.getName();
        String watchOnly = (wallet.isWatchOnly() ? getString(R.string.label_watchonly) : "");
        activityCallback.setTitle(walletTitle, "");
        KLog.d("wallet title is %s", walletTitle);
    }



    public interface Listener {


        boolean hasBoundService();

        void forceUpdate();

        Wallet.ConnectionStatus getConnectionStatus();

        long getDaemonHeight(); //mBoundService.getDaemonHeight();

        void onSendRequest();

        void onTxDetailsRequest(TransactionInfo info);

        boolean isSynced();

        boolean isStreetMode();

        long getStreetModeHeight();

        boolean isWatchOnly();

        String getTxKey(String txId);

        void onWalletReceive();

        boolean hasWallet();

        Wallet getWallet();

        void setToolbarButton(int type);

        void setTitle(String title, String subtitle);

        void setSubtitle(String subtitle);
    }
}
