package org.blackey.ui.wallet.create;

import android.app.Application;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.util.Helper;
import com.m2049r.xmrwallet.util.MoneroThreadPoolExecutor;
import com.m2049r.xmrwallet.util.RestoreHeight;
import com.vondear.rxtool.view.RxToast;

import org.blackey.R;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.wallet.detail.GenerateReviewFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.utils.KLog;
import timber.log.Timber;

public class CreateNewWalletViewModel extends ToolbarViewModel {

    //钱包名称
    public ObservableField<String> walletName = new ObservableField<>("");

    //钱包名称错误提示
    public ObservableField<String> hintWalletName = new ObservableField<>("");

    //钱包名称长度提示
    public ObservableField<String> lengthWalletName = new ObservableField<>("0/20");

    //钱包密码
    public ObservableField<String> walletPasswd = new ObservableField<>("");

    //钱包密码错误提示
    public ObservableField<String> hintWalletPasswd = new ObservableField<>("");

    //助记词
    public ObservableField<String> mnemonicText = new ObservableField<>("");
    //助记词错误提示
    public ObservableField<String> hintMnemonic = new ObservableField<>("");

    public ObservableField<Integer> mnemonicVisibility = new ObservableField<>(View.GONE);

    //恢复高度
    public ObservableField<String> restoreheightText = new ObservableField<>("2019-05-01");

    //恢复高度错误提示
    public ObservableField<String> hintRestoreheight = new ObservableField<>("");

    public ObservableField<Integer> restoreheightVisibility = new ObservableField<>(View.GONE);

    public String  type;

    public BlackeyBaseActivity activity;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //开始从NFC恢复
        public ObservableBoolean enableNfc = new ObservableBoolean(false);
        //结束NFC
        public ObservableBoolean disableNfc = new ObservableBoolean(false);
    }


    public CreateNewWalletViewModel(@NonNull Application application) {
        super(application);
    }

    static final String MNEMONIC_LANGUAGE = "English"; // see mnemonics/electrum-words.cpp for more

    /**
     *
     */
    public BindingCommand createOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            if (!checkName()) return;

            KLog.e( walletPasswd.get());
            // create the real wallet password
            //String crazyPass = KeyStoreHelper.getCrazyPass(getApplication(), walletPasswd.get());

            //create wallet with original password(no crazy pass),


            if(CreateNewWalletActivity.TYPE_NEW.equalsIgnoreCase(type)){
                onGenerate();
            }else if(CreateNewWalletActivity.TYPE_SEED.equalsIgnoreCase(type)){
                onGenerate(mnemonicText.get());
            }else if(CreateNewWalletActivity.TYPE_NFC.equalsIgnoreCase(type)){
                uc.enableNfc.set(!uc.enableNfc.get());
            }

        }
    });

    String  getCrazyPass(){
        String crazyPass =  walletPasswd.get();

        KLog.e(crazyPass);

        return crazyPass;
    }

    private void onGenerate() {
        createWallet(walletName.get(), getCrazyPass(),
                new WalletCreator() {
                    public boolean createWallet(File aFile, String password) {
                        Wallet newWallet = WalletManager.getInstance()
                                .createWallet(aFile, password, MNEMONIC_LANGUAGE);
                        boolean success = (newWallet.getStatus() == Wallet.Status.Status_Ok);
                        if (!success) {
                            Timber.e(newWallet.getErrorString());
                            RxToast.error(newWallet.getErrorString());
                        }
                        newWallet.close();
                        return success;

                    }
                });
    }

    public void onGenerate(final String seed) {
        createWallet(walletName.get(), getCrazyPass(),
                new WalletCreator() {

                    @Override
                    public boolean createWallet(File aFile, String password) {
                        Wallet newWallet = WalletManager.getInstance()
                                .recoveryWallet(aFile, password, seed, getHeight());
                        boolean success = (newWallet.getStatus() == Wallet.Status.Status_Ok);
                        if (!success) {
                            Timber.e(newWallet.getErrorString());
                            RxToast.error(newWallet.getErrorString());
                        }
                        newWallet.close();
                        return success;
                    }
                });
    }



    public void onGenerate( final String address,final String viewKey,final String spendKey) {
        createWallet(walletName.get(), getCrazyPass(),
                new WalletCreator() {

                    @Override
                    public boolean createWallet(File aFile, String password) {
                        Wallet newWallet = WalletManager.getInstance()
                                .createWalletWithKeys(aFile, password, MNEMONIC_LANGUAGE, getHeight(),
                                        address, viewKey, spendKey);
                        boolean success = (newWallet.getStatus() == Wallet.Status.Status_Ok);
                        if (!success) {
                            Timber.e(newWallet.getErrorString());
                            RxToast.error(newWallet.getErrorString());
                        }

                        newWallet.close();
                        uc.disableNfc.set(false);
                        return success;
                    }
                });
    }



    private long getHeight() {
        long height = 0;

        String restoreHeight = restoreheightText.get().trim();
        if (restoreHeight.isEmpty()) return -1;
        try {
            // is it a date?
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            parser.setLenient(false);
            height = RestoreHeight.getInstance().getHeight(parser.parse(restoreHeight));
        } catch (ParseException ex) {
        }
        if (height <= 0)
            try {
                // is it a date without dashes?
                SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
                parser.setLenient(false);
                height = RestoreHeight.getInstance().getHeight(parser.parse(restoreHeight));
            } catch (ParseException ex) {
            }
        if (height <= 0)
            try {
                // or is it a height?
                height = Long.parseLong(restoreHeight);
            } catch (NumberFormatException ex) {
                return -1;
            }
        Timber.d("Using Restore Height = %d", height);
        return height;
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setRightIconVisible(View.GONE);
        setTitleText(getApplication().getString(R.string.generate_wallet_creating)+"-"+getType());
        if(CreateNewWalletActivity.TYPE_SEED.equalsIgnoreCase(type) ){
            mnemonicVisibility.set(View.VISIBLE);
            restoreheightVisibility.set(View.VISIBLE);
        }else if(CreateNewWalletActivity.TYPE_NFC.equalsIgnoreCase(type) ){
            restoreheightVisibility.set(View.VISIBLE);
        }


    }



    String getType() {
        switch (type) {

            case CreateNewWalletActivity.TYPE_NEW:
                return getApplication().getString(R.string.generate_wallet_type_new);
            case CreateNewWalletActivity.TYPE_SEED:
                return getApplication().getString(R.string.generate_wallet_type_seed);
            case CreateNewWalletActivity.TYPE_NFC:
                return "NFC";
            default:
                Timber.e("unknown type %s", type);
                return "?";
        }
    }

    private boolean checkName() {

        boolean ok = true;
        if (walletName.get().length() == 0) {
            hintWalletName.set(getApplication().getString(R.string.generate_wallet_name));
            ok = false;
        } else if (walletName.get().charAt(0) == '.') {
            hintWalletName.set(getApplication().getString(R.string.generate_wallet_dot));
            ok = false;
        } else {
            File walletFile = Helper.getWalletFile(getApplication(), walletName.get());
            if (WalletManager.getInstance().walletExists(walletFile)) {
                hintWalletName.set(getApplication().getString(R.string.generate_wallet_exists));
                ok = false;
            }
        }
        if (ok) {
            hintWalletName.set("");
        }
        return ok;
    }

    public void createWallet(final String name, final String password,
                             final WalletCreator walletCreator) {
        new AsyncCreateWallet(name, password, walletCreator)
                .executeOnExecutor(MoneroThreadPoolExecutor.MONERO_THREAD_POOL_EXECUTOR);
    }



    interface WalletCreator {
        boolean createWallet(File aFile, String password);

    }
    private class AsyncCreateWallet extends AsyncTask<Void, Void, Boolean> {
        final String walletName;
        final String walletPassword;
        final WalletCreator walletCreator;

        File newWalletFile;

        AsyncCreateWallet(final String name, final String password,
                          final WalletCreator walletCreator) {
            super();
            this.walletName = name;
            this.walletPassword = password;
            this.walletCreator = walletCreator;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
            activity.acquireWakeLock();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // check if the wallet we want to create already exists
            File walletFolder = Helper.getWalletRoot(getApplication());
            if (!walletFolder.isDirectory()) {
                Timber.e("Wallet dir " + walletFolder.getAbsolutePath() + "is not a directory");
                return false;
            }
            File cacheFile = new File(walletFolder, walletName);
            File keysFile = new File(walletFolder, walletName + ".keys");
            File addressFile = new File(walletFolder, walletName + ".address.txt");

            if (cacheFile.exists() || keysFile.exists() || addressFile.exists()) {
                Timber.e("Some wallet files already exist for %s", cacheFile.getAbsolutePath());
                return false;
            }

            newWalletFile = new File(walletFolder, walletName);
            boolean success = walletCreator.createWallet(newWalletFile, walletPassword);


            if (success) {
                return true;
            } else {
                Timber.e("Could not create new wallet in %s", newWalletFile.getAbsolutePath());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dismissDialog();
            super.onPostExecute(result);
            activity.releaseWakeLock(activity.RELEASE_WAKE_LOCK_DELAY);
            if (activity.isDestroyed()) {
                return;
            }
            if (result) {
                startDetails(newWalletFile, walletPassword, GenerateReviewFragment.VIEW_TYPE_ACCEPT);
            } else {
            }
        }
    }

    private void startDetails(File walletFile, String password, String type) {

        Bundle b = new Bundle();
        b.putString("path", walletFile.getAbsolutePath());
        b.putString("password", password);
        b.putString("type", type);

        startContainerActivity(GenerateReviewFragment.class.getCanonicalName(),b);

        finish();
    }

    public boolean checkMnemonic() {
        String seed = mnemonicText.get();
        boolean ok = (seed.split("\\s").length == 25); // 25 words
        if (!ok) {
            hintMnemonic.set(getApplication().getString(R.string.generate_check_mnemonic));
        } else {
            hintMnemonic.set("");
        }
        return ok;
    }
}
