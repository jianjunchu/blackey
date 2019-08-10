package org.blackey.utils;

import android.text.TextUtils;

import org.blackey.app.BlackeyApplication;
import org.blackey.authorization.Token;
import org.blackey.entity.CurrentUser;
import org.blackey.service.AccountApiService;
import com.vondear.rxtool.view.RxToast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;

public class TokenManager {

    private static SPUtils spUtils = SPUtils.getInstance("refresh_token");

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String DEFAULT_PASSWORD = "123456";

    public interface CurrentUserCallback{
        void subscribe(CurrentUser user);

    }

    public static String getAccessToken(){
       return   spUtils.getString(ACCESS_TOKEN);
    }
    public static void setAccessToken(String access_token){
        spUtils.put(ACCESS_TOKEN,access_token);
        
    }

    public static String getRefreshToken(){
        return   spUtils.getString(REFRESH_TOKEN);
    }
    public static void setRefreshToken(String refresh_token){
        spUtils.put(REFRESH_TOKEN,refresh_token);
    }

    public static void setToken(Token tokenInfo) {
        setAccessToken(tokenInfo.getAccess_token());
        setRefreshToken(tokenInfo.getRefresh_token());
    }


    public static void requestCurrentUser(BaseViewModel viewModel , final CurrentUserCallback callback){

        if(TextUtils.isEmpty(TokenManager.getAccessToken())){
            return;
        }
        RetrofitClient.getInstance().create(AccountApiService.class)
                .current()
                .compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<CurrentUser>>() {
                    @Override
                    public void accept(BaseResponse<CurrentUser> response) throws Exception {
                        KLog.e(response.getBody().getPortrait());
                        //请求成功
                        if(response.isOk()){
                            final CurrentUser user = response.getBody();
                            BlackeyApplication.setCurrent(user);

                            JMessageClient.register(user.getUserId() + "", DEFAULT_PASSWORD, new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    JMessageClient.login(user.getUserId()+"", DEFAULT_PASSWORD, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {

                                            UserInfo userInfo = JMessageClient.getMyInfo();
                                            userInfo.setNickname(user.getNickname());
                                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {

                                                }
                                            });
                                        }
                                    });
                                }
                            });



                            if(callback!=null){
                                callback.subscribe(user);
                            }
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
