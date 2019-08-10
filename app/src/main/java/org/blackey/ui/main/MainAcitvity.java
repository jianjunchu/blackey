package org.blackey.ui.main;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.m2049r.xmrwallet.dialog.InputNfcPasswordFragment;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.util.Helper;
import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.databinding.ActivityMainBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.market.MarketFragment;
import org.blackey.ui.mine.MineFragment;
import org.blackey.ui.orders.list.OrdersListFragment;
import org.blackey.ui.wallet.list.WalletListFragment;
import org.blackey.ui.wallet.list.WalletListViewModel;
import org.blackey.utils.NfcBackupHandle;

import java.util.ArrayList;
import java.util.List;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import timber.log.Timber;

public class MainAcitvity extends BlackeyBaseActivity<ActivityMainBinding, MainViewModel> implements WalletListViewModel.Listener,InputNfcPasswordFragment.Listener{

    private List<Fragment> mFragments;



    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        //初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();
        //节点连接参数
        try {
            WalletManager.getInstance().setDaemon(BlackeyApplication.getNode());
        }catch (Exception e){
            e.printStackTrace();
        }


        if (Helper.getWritePermission(this)) {

        } else {
            Timber.i("Waiting for permissions");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            try {

                if(InputNfcPasswordFragment.getInstance().checkPassword()){
                    InputNfcPasswordFragment.getInstance().setIntent(intent);

                    new AsyncBackupToNFC2().execute();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void enableNfc() {
        super.enableNfc();
    }

    @Override
    public void disableNfc() {
        super.disableNfc();
    }

    @Override
    public void saveSeed(boolean save) {
        InputNfcPasswordFragment.getInstance().setSaveSeed(save);
    }

    private class AsyncBackupToNFC2 extends AsyncTask<Object, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Object... objects) {

            NfcBackupHandle nfcBackupHandle = null;
            String  passwd = InputNfcPasswordFragment.getInstance().getPasswd();
            Intent intent = InputNfcPasswordFragment.getInstance().getIntent();
            Wallet wallet = InputNfcPasswordFragment.getInstance().getWallet();
            boolean saveSeed  = InputNfcPasswordFragment.getInstance().isSaveSeed();
            try {
                nfcBackupHandle = new NfcBackupHandle(intent,wallet,passwd,saveSeed);
                return nfcBackupHandle.backupWalletToNFC();
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (isDestroyed()) {
                return;
            }
            dismissProgressDialog();
            if (!result) {
                RxToast.error(getString(R.string.backup_failed));
            }else{
                RxToast.success(getString(R.string.backup_success));
                InputNfcPasswordFragment.getInstance().dismiss();
            }
        }
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new MarketFragment());
        mFragments.add(new OrdersListFragment());
        mFragments.add(new WalletListFragment());
        mFragments.add(new MineFragment());
        //默认选中第一个
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, mFragments.get(0));
        transaction.commitAllowingStateLoss();
    }

    private void initBottomTab() {
        NavigationController navigationController = binding.pagerBottomTab.material()
                .addItem(R.mipmap.icon_market, getString(R.string.bottom_tab_market))
                .addItem(R.mipmap.icon_orders, getString(R.string.bottom_tab_orders))
                .addItem(R.mipmap.icon_wallet, getString(R.string.bottom_tab_wallet))
                .addItem(R.mipmap.icon_mine, getString(R.string.bottom_tab_mine))
                .setDefaultColor(ContextCompat.getColor(this, R.color.textColorVice))
                .build();
        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout, mFragments.get(index));
                transaction.commitAllowingStateLoss();
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }


    @Override
    public void onWalletBackupToNFC(String walletName, String password) {

        InputNfcPasswordFragment.display(getSupportFragmentManager(),true);

        String walletPath = Helper.getWalletFile(this, walletName).getAbsolutePath();

        Wallet wallet = WalletManager.getInstance().openWallet(walletPath, password);

        InputNfcPasswordFragment.getInstance().setWallet(wallet);

    }


}
