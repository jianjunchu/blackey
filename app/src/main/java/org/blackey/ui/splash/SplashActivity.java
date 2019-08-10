package org.blackey.ui.splash;


import android.Manifest;
import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.databinding.ActivitySplashBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.main.MainAcitvity;
import com.m2049r.xmrwallet.util.Helper;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxActivityTool;
import com.vondear.rxtool.RxAppTool;
import com.vondear.rxtool.RxBarTool;
import com.vondear.rxtool.view.RxToast;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.SPUtils;

public class SplashActivity extends BlackeyBaseActivity<ActivitySplashBinding,SplashViewModel> {


    private static final String VERSION_CODE = "versionCode";

    @Override
    public int initContentView(Bundle savedInstanceState) {
        //RxToast.success(getApplicationContext(), "这是一个提示成功的Toast!", Toast.LENGTH_SHORT, true).show();
        RxBarTool.hideStatusBar(this);
        return R.layout.activity_splash;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        JPushInterface.init(getApplicationContext());
        viewModel.requestNetWork();
        RxPermissions rxPermissions = new RxPermissions(SplashActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            BlackeyApplication.setWalletRoot(Helper.getWalletRoot(SplashActivity.this));
                            checkUpdate();

                        } else {
                            RxToast.error(getString(R.string.no_write_external_storage));
                        }
                    }
                });

        viewModel.initRpcNodel();
        viewModel.initSysConfig();
    }


    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkUpdate() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(4000);
                    toMain();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void toMain() {
        int appVersionCode = RxAppTool.getAppVersionCode(getApplicationContext());
        int saveVersionCode = SPUtils.getInstance().getInt(VERSION_CODE);
         if(appVersionCode != saveVersionCode){
             SPUtils.getInstance().put(VERSION_CODE,appVersionCode);
             RxActivityTool.skipActivityAndFinish(this, GuideActivity.class);
         }else{
             RxActivityTool.skipActivityAndFinish(this, MainAcitvity.class);
         }
    }
}
