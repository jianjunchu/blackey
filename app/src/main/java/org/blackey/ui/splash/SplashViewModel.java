package org.blackey.ui.splash;

import android.app.Application;
import android.support.annotation.NonNull;

import com.vondear.rxtool.view.RxToast;

import org.blackey.app.BlackeyApplication;
import org.blackey.entity.CurrentUser;
import org.blackey.entity.SystemConfig;
import org.blackey.entity.SystemRpcNodeInfo;
import org.blackey.service.DictApiService;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.TokenManager;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;

public class SplashViewModel extends BaseViewModel {
    public SplashViewModel(@NonNull Application application) {
        super(application);
    }


    public void requestNetWork() {

        TokenManager.requestCurrentUser(this, new TokenManager.CurrentUserCallback() {
            @Override
            public void subscribe(CurrentUser user) {
                BlackeyApplication.setCurrent(user);
            }
        });

    }

    public void initRpcNodel() {
        RetrofitClient.getInstance().create(DictApiService.class)
                .getXmrNode()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<SystemRpcNodeInfo>>() {
                    @Override
                    public void accept(BaseResponse<SystemRpcNodeInfo> response) throws Exception {
                        KLog.e(response.getBody());
                        //请求成功
                        if(response.isOk()){
                            BlackeyApplication.rpcNodeInfo = response.getBody();
                        }

                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }

    public void initSysConfig() {
        RetrofitClient.getInstance().create(DictApiService.class)
                .getSysConfig()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<SystemConfig>>() {
                    @Override
                    public void accept(BaseResponse<SystemConfig> response) throws Exception {
                        KLog.e(response.getBody());
                        //请求成功
                        if(response.isOk()){
                            BlackeyApplication.setSystemConfig(response.getBody());
                        }

                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }
}
