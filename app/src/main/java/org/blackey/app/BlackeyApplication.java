package org.blackey.app;


import android.content.Context;
import android.content.res.Configuration;

import com.m2049r.xmrwallet.data.NodeInfo;
import com.m2049r.xmrwallet.model.NetworkType;
import com.m2049r.xmrwallet.util.LocaleHelper;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import org.blackey.BuildConfig;
import org.blackey.R;
import org.blackey.entity.CurrentUser;
import org.blackey.entity.SystemConfig;
import org.blackey.entity.SystemRpcNodeInfo;
import org.blackey.message.NotificationClickEventReceiver;
import org.blackey.ui.splash.SplashActivity;
import org.blackey.utils.ProperTies;

import java.io.File;
import java.net.UnknownHostException;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * Created by goldze on 2017/7/16.
 */

public class BlackeyApplication extends BaseApplication {

    public static final NetworkType NETWORK_TYPE = NetworkType.NetworkType_Mainnet;
    public static final int pageRows = 20;

    public static SystemRpcNodeInfo rpcNodeInfo = null;

    public static SystemConfig systemConfig = null;

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String GROUP_ID = "groupId";
    public static final String CONV_TITLE = "conv_title";

    private static CurrentUser current;

    private static File walletRoot;

    public static File getWalletRoot() {
        return walletRoot;
    }

    public static void setWalletRoot(File walletRoot) {
        BlackeyApplication.walletRoot = walletRoot;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LocaleHelper.setLocale(context, LocaleHelper.getLocale(context)));
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        LocaleHelper.updateSystemDefaultLocale(configuration.locale);
        LocaleHelper.setLocale(BlackeyApplication.this, LocaleHelper.getLocale(BlackeyApplication.this));
    }

    static public NetworkType getNetworkType() {
        //TODO BuildConfig.DEBUG
        return NETWORK_TYPE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RxTool.init(this);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JMessageClient.init(this,true);


        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

        //设置Notification的模式
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());

    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(SplashActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }



    public static CurrentUser getCurrent() {
        return current;
    }

    public static void setCurrent(CurrentUser current) {
        BlackeyApplication.current = current;
    }

    public static NodeInfo getNode() throws UnknownHostException {
        NodeInfo nodeInfo = new NodeInfo();
        if(rpcNodeInfo!=null){
            nodeInfo.setRpcPort(rpcNodeInfo.getPort());
            nodeInfo.setHost(rpcNodeInfo.getHostname());
            nodeInfo.setName("blackey");
            if(!StringUtils.isEmpty(rpcNodeInfo.getUsername())){
                nodeInfo.setUsername(rpcNodeInfo.getUsername());
                nodeInfo.setPassword(rpcNodeInfo.getPassword());
            }
        }else{
            nodeInfo = getDefaultNode();
            boolean test = nodeInfo.testRpcService();
            if(!test){
                RxToast.showToast(R.string.init_node_error);
                System.exit(0);
            }

        }
        KLog.i(nodeInfo.toString());
        return nodeInfo;


    }

    private static NodeInfo getDefaultNode() throws UnknownHostException {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setRpcPort(Integer.parseInt(ProperTies.getProperty("DefaultNodePort")));
        nodeInfo.setHost(ProperTies.getProperty("DefaultNodeHost"));
        nodeInfo.setName("Default");
        nodeInfo.setUsername(ProperTies.getProperty("DefaultNodeUsername"));
        nodeInfo.setPassword(ProperTies.getProperty("DefaultNodePassword"));
        return nodeInfo;
    }

    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();
    }

    public static SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public static void setSystemConfig(SystemConfig systemConfig) {
        BlackeyApplication.systemConfig = systemConfig;
    }
}
