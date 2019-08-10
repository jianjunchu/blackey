package org.blackey.ui.wallet.list;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.ui.base.viewmodel.AddToolbarViewModel;
import org.blackey.ui.market.popupwindowMenu.PopUpMenuBean;
import org.blackey.ui.market.popupwindowMenu.PopupWindowMenuUtil;
import org.blackey.ui.wallet.WalletActivity;
import org.blackey.ui.wallet.create.CreateNewWalletActivity;
import org.blackey.ui.wallet.receive.ReceiveFragment;
import com.m2049r.xmrwallet.data.Node;
import com.m2049r.xmrwallet.data.NodeInfo;
import com.m2049r.xmrwallet.ledger.Ledger;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.service.WalletService;
import com.m2049r.xmrwallet.util.Helper;
import com.vondear.rxtool.view.RxToast;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.utils.KLog;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 钱包管理
 */
public class WalletListViewModel extends AddToolbarViewModel {


    public View spinnerview;

    public SwipeRefreshLayout swipeRefresh;

    public ImageView ivGunther;

    public Activity context;

    public boolean popupOpen = false;


    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public WalletListFragment.OnInteractionListener listener;


    private static class BindingViewHolder extends RecyclerView.ViewHolder {
        public BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
        }
    }

    public class UIChangeObservable {
        //下拉刷新完成
        public ObservableBoolean finishRefreshing = new ObservableBoolean(false);
    }

    public WalletListViewModel(@NonNull Application application) {
        super(application);
    }



    //给RecyclerView添加ObservableList
    public ObservableList<WalletListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<WalletListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_wallet_list);
    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象
    public final BindingRecyclerViewAdapter<WalletListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();



    public void requestNetWork() {

        observableList.clear();
        Observable.just("")
                .map(new Func1<String, List<WalletManager.WalletInfo>>() {
                    @Override
                    public List<WalletManager.WalletInfo> call(String s) {
                        WalletManager mgr = WalletManager.getInstance();
                        List<WalletManager.WalletInfo> walletInfos =
                                mgr.findWallets(BlackeyApplication.getWalletRoot());

                        return walletInfos;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<WalletManager.WalletInfo>>() {
                    @Override
                    public void call(List<WalletManager.WalletInfo> walletInfos) {
                        if(walletInfos.isEmpty()){
                            ivGunther.setVisibility(View.VISIBLE);
                            ivGunther.setImageResource(R.mipmap.gunther_desaturated);
                        }else{
                            ivGunther.setVisibility(View.GONE);
                        }

                        for(WalletManager.WalletInfo walletInfo : walletInfos){

                            WalletListItemViewModel itemViewModel = new WalletListItemViewModel(WalletListViewModel.this, walletInfo);
                            //双向绑定动态添加Item
                            observableList.add(itemViewModel);
                        }

                        uc.finishRefreshing.set(!uc.finishRefreshing.get());
                    }
                });







    }



    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.title_wallet));
        setLeftIconVisible(View.GONE);
    }

    /**
     * 刷新事件
     */
    public BindingCommand swipeOnRefresh() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestNetWork();

                    }
                }, 10);
            }
        });
    }

    /**
     * 添加按钮点击事件
     */
    @Override
    public BindingCommand rightAddOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

                final ArrayList<PopUpMenuBean> menuList = new ArrayList<PopUpMenuBean>();

                PopUpMenuBean popUpMenuBean = new PopUpMenuBean();
                popUpMenuBean.setImgResId(R.mipmap.ic_new_wallet);
                popUpMenuBean.setItemStr(getApplication().getString(R.string.create_new_wallet));
                menuList.add(popUpMenuBean);


                PopUpMenuBean popUpMenuBean1 = new PopUpMenuBean();
                popUpMenuBean1.setImgResId(R.mipmap.ic_restore_wallet);
                popUpMenuBean1.setItemStr(getApplication().getString(R.string.restore_wallet));
                menuList.add(popUpMenuBean1);

                PopUpMenuBean popUpMenuBean2 = new PopUpMenuBean();
                popUpMenuBean2.setImgResId(R.mipmap.ic_restore_25_wallet);
                popUpMenuBean2.setItemStr(getApplication().getString(R.string.restore_25_wallet));
                menuList.add(popUpMenuBean2);



                PopupWindowMenuUtil.showPopupWindows(getApplication().getApplicationContext(), spinnerview, menuList, new PopupWindowMenuUtil.OnListItemClickLitener() {
                    @Override
                    public void onListItemClick(int position) {
                        //如果position == -1，预留位，用来标明是点击弹出框外面的区域
                        if (position != -1) {

                            if (menuList.get(position).getImgResId() == R.mipmap.ic_new_wallet) {

                                Bundle extras = new Bundle();
                                extras.putString(CreateNewWalletActivity.TYPE, CreateNewWalletActivity.TYPE_NEW);
                                startActivity(CreateNewWalletActivity.class,extras);

                            } else if (menuList.get(position).getImgResId() == R.mipmap.ic_restore_wallet) {

                                Bundle extras = new Bundle();
                                extras.putString(CreateNewWalletActivity.TYPE, CreateNewWalletActivity.TYPE_NFC);
                                startActivity(CreateNewWalletActivity.class,extras);

                            }else if (menuList.get(position).getImgResId() == R.mipmap.ic_restore_25_wallet) {
                                Bundle extras = new Bundle();
                                extras.putString(CreateNewWalletActivity.TYPE, CreateNewWalletActivity.TYPE_SEED);
                                startActivity(CreateNewWalletActivity.class,extras);
                            }
                        }
                    }
                });

            }
        });
    }





    private void showReceive(@NonNull String walletName) {
        Timber.d("receive for wallet .%s.", walletName);
        if (checkServiceRunning()) return;
        final File walletFile = Helper.getWalletFile(context, walletName);
        if (WalletManager.getInstance().walletExists(walletFile)) {
            Helper.promptPassword(context, walletName, false, new Helper.PasswordAction() {
                @Override
                public void action(String walletName, String password, boolean fingerprintUsed) {
                    startReceive(walletFile, password);
                }
            });
        } else { // this cannot really happen as we prefilter choices
            RxToast.error(getApplication().getString(R.string.bad_wallet));
        }
    }


    void startReceive(File walletFile, String password) {
        Timber.d("startReceive()");
        Bundle b = new Bundle();
        b.putString("path", walletFile.getAbsolutePath());
        b.putString("password", password);
        startContainerActivity(ReceiveFragment.class.getCanonicalName(),b);
    }



    boolean checkServiceRunning() {
        if (WalletService.Running) {
            RxToast.error(getApplication().getString(R.string.service_busy));
            return true;
        } else {
            return false;
        }
    }
    public boolean onWalletSelected(String walletName, boolean streetmode) throws UnknownHostException {

        if (checkServiceRunning()) return false;
        try {
            new AsyncOpenWallet(walletName, BlackeyApplication.getNode(), streetmode).execute();
        } catch (IllegalArgumentException ex) {
            Timber.e(ex.getLocalizedMessage());
            RxToast.showToast(ex.getLocalizedMessage());
            return false;
        }
        return true;
    }


    // an AsyncTask which tests the node before trying to open the wallet
    private class AsyncOpenWallet extends AsyncTask<Void, Void, Boolean> {
        final static int OK = 0;
        final static int TIMEOUT = 1;
        final static int INVALID = 2;
        final static int IOEX = 3;

        private final String walletName;
        private final NodeInfo node;
        private final boolean streetmode;

        AsyncOpenWallet(String walletName, NodeInfo node, boolean streetmode) {
            this.walletName = walletName;
            this.node = node;
            this.streetmode = streetmode;
        }
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            showDialog(context.getString(R.string.loading));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            KLog.d("checking %s", node.getAddress());
            KLog.i(node.testRpcService());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dismissDialog();
            if (context.isDestroyed()) {
                return;
            }
            if (result) {
                Timber.d("selected wallet is .%s.", node.getName());
                // now it's getting real, onValidateFields if wallet exists
                promptAndStart(walletName, node, streetmode);
            } else {
                if (node.getResponseCode() == 0) { // IOException
                    RxToast.showToast(R.string.status_wallet_node_invalid);
                } else { // connected but broken
                    RxToast.showToast(R.string.status_wallet_connect_ioex);
                }
            }
        }


    }

    void promptAndStart(String walletName, Node node, final boolean streetmode) {
        File walletFile = Helper.getWalletFile(context, walletName);
        if (WalletManager.getInstance().walletExists(walletFile)) {
            Helper.promptPassword(context, walletName, false,
                    new Helper.PasswordAction() {
                        @Override
                        public void action(String walletName, String password, boolean fingerprintUsed) {
                            if (checkDevice(walletName, password))
                                startWallet(walletName, password, fingerprintUsed, streetmode);
                        }
                    });
        } else { // this cannot really happen as we prefilter choices
            RxToast.showToast(R.string.bad_wallet);
        }
    }

    boolean checkDevice(String walletName, String password) {
        String keyPath = new File(Helper.getWalletRoot(context),
                walletName + ".keys").getAbsolutePath();
        // check if we need connected hardware
        Wallet.Device device =
                WalletManager.getInstance().queryWalletDevice(keyPath, password);
        switch (device) {
            case Device_Ledger:
                if (!Ledger.isConnected()) {
                    RxToast.showToast(R.string.open_wallet_ledger_missing);
                } else {
                    return true;
                }
                break;
            default:
                // device could be undefined meaning the password is wrong
                // this gets dealt with later
                return true;
        }
        return false;
    }

    private String uri = null;

    void startWallet(String walletName, String walletPassword,
                     boolean fingerprintUsed, boolean streetmode) {
        Timber.d("startWallet()");
        Intent intent = new Intent(context, WalletActivity.class);
        intent.putExtra(WalletActivity.REQUEST_ID, walletName);
        intent.putExtra(WalletActivity.REQUEST_PW, walletPassword);
        intent.putExtra(WalletActivity.REQUEST_FINGERPRINT_USED, fingerprintUsed);
        intent.putExtra(WalletActivity.REQUEST_STREETMODE, streetmode);
        if (uri != null) {
            intent.putExtra(WalletActivity.REQUEST_URI, uri);
            uri = null; // use only once
        }
        context.startActivity(intent);
    }

    public interface Listener {


        void onWalletBackupToNFC(String walletName, String password);



    }
}
