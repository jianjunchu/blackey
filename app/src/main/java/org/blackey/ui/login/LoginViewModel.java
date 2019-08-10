package org.blackey.ui.login;

import android.app.Application;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.authorization.Token;
import org.blackey.entity.CountryOrRegion;
import org.blackey.entity.CurrentUser;
import org.blackey.model.request.LoginRequest;
import org.blackey.service.AccountApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListActivity;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListItemViewModel;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.TokenManager;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class LoginViewModel extends ToolbarViewModel {

    public LoginRequest request = new LoginRequest();

    public static final String TOKEN_LOGIN_VIEW_MODEL_LOGIN = "token_login_view_model_login";
    public static final String TOKEN_LOGIN_VIEW_MODEL_CHOOSE_COUNTRY = "token_login_view_model_choose_country";

    //国家/地区绑定
    public ObservableField<String> country = new ObservableField<>(getApplication().getString(R.string.CN)+" "+86);

    //手机错误提示
    public TextInputLayout elMobile;
    public TextInputLayout elPasswd;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.login));
        setRightTextVisible(View.VISIBLE);
        setRightText(getApplication().getString(R.string.register));
    }

    public void initMessenger(){

        Messenger.getDefault().register(RxTool.getContext(), TOKEN_LOGIN_VIEW_MODEL_CHOOSE_COUNTRY, CountryOrRegion.class, new BindingConsumer<CountryOrRegion>() {
            @Override
            public void call(CountryOrRegion o) {
                request.setNationCode(o.getAreaCode()+"");
                country.set(o.getName()+" "+o.getAreaCode());
            }
        } );
    }

    @Override
    public BindingCommand rightTextOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

                startActivity(RegisterActivity.class);
            }
        });
    }

    //选择 国家/地区 点击事件
    public BindingCommand chooseCountryOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            Bundle bundle = new Bundle();
            bundle.putString(ChooseCountryOrRegionListItemViewModel.TOKEN_CHOOSE_COUNTRY_REFRESH,TOKEN_LOGIN_VIEW_MODEL_CHOOSE_COUNTRY);

            startActivity(ChooseCountryOrRegionListActivity.class,bundle);
        }
    });

    //忘记密码按钮的点击事件
    public BindingCommand forgetPasswordOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startActivity(ResetPasswordActivity.class);
        }
    });

    //登录按钮的点击事件
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            login();
        }
    });

    private void login() {
        elMobile.setError(null);
        elPasswd.setError(null);
        if (TextUtils.isEmpty(request.getMobile())) {
            elMobile.setError(getApplication().getString(R.string.hint_login_mobile));
            return;
        }
        if (TextUtils.isEmpty(request.getPassword())) {
            elPasswd.setError(getApplication().getString(R.string.hint_login_password));
            return;
        }

        RetrofitClient.getInstance().create(AccountApiService.class)
                .login(request)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))//界面关闭自动取消
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog("正在请求...");
                    }
                })
                .subscribe(new Consumer<BaseResponse<Token>>() {
                    @Override
                    public void accept(BaseResponse<Token> response) throws Exception {
                        //请求成功
                        if (response.getCode() == 200) {
                            Token token = response.getBody();
                            TokenManager.setToken(token);
                            RetrofitClient.refresh();
                            TokenManager.requestCurrentUser(LoginViewModel.this, new TokenManager.CurrentUserCallback() {
                                @Override
                                public void subscribe(CurrentUser user) {
                                    if(user !=null){
                                        //发送一个空消息
                                        Messenger.getDefault().sendNoMsg(LoginViewModel.TOKEN_LOGIN_VIEW_MODEL_LOGIN);
                                        finish();
                                    }
                                }
                            });

                        } else if(response.getCode() == 200001){
                            elPasswd.setError(getApplication().getString(R.string.error_invalid_username));
                        }else {
                            //code错误时也可以定义Observable回调到View层去处理
                            ToastUtils.showShort("数据错误");
                        }
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //关闭对话框
                        dismissDialog();
                        RxToast.error(throwable.message);
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        dismissDialog();

                    }
                });
    }
}
