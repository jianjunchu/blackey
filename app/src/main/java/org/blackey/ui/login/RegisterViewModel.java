package org.blackey.ui.login;

import android.app.Application;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.CountryOrRegion;
import org.blackey.model.request.PhoneRegisterRequest;
import org.blackey.service.AccountApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListActivity;
import org.blackey.ui.choose.country.ChooseCountryOrRegionListItemViewModel;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import java.util.Timer;
import java.util.TimerTask;

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


public class RegisterViewModel extends ToolbarViewModel {

    private static final String TOKEN_REGISTER_VIEW_MODEL_CHOOSE_COUNTRY = "token_register_view_model_choose_country";

    private int smsTime = 60;//发送短信间隔时间

    Timer timer = new Timer();

    public PhoneRegisterRequest request = new PhoneRegisterRequest();

    //国家/地区绑定
    public ObservableField<String> country = new ObservableField<>(getApplication().getString(R.string.CN)+" "+86);

    //获取验证码
    public ObservableField<String> getCaptcha = new ObservableField<>(getApplication().getString(R.string.get_captcha));

    public ObservableField<Boolean> readAgree = new ObservableField<>(false);

    //手机错误提示
    public ObservableField<String> mobileError = new ObservableField<>("");
    public ObservableField<String> captchaError = new ObservableField<>("");
    public ObservableField<String> passwordError = new ObservableField<>("");
    public ObservableField<String> readAgreeError = new ObservableField<>("");



    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.mobile_register));
    }

    public void initMessenger(){

        Messenger.getDefault().register(RxTool.getContext(), TOKEN_REGISTER_VIEW_MODEL_CHOOSE_COUNTRY, CountryOrRegion.class, new BindingConsumer<CountryOrRegion>() {
            @Override
            public void call(CountryOrRegion o) {
                request.setNationCode(o.getAreaCode()+"");
                country.set(o.getName()+" "+o.getAreaCode());
            }
        } );
    }



    //选择 国家/地区 点击事件
    public BindingCommand chooseCountryOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(ChooseCountryOrRegionListItemViewModel.TOKEN_CHOOSE_COUNTRY_REFRESH,TOKEN_REGISTER_VIEW_MODEL_CHOOSE_COUNTRY);

            startActivity(ChooseCountryOrRegionListActivity.class,bundle);
        }
    });

    //发送短信验证码点击事件
    public BindingCommand sendCaptchaOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            sendCaptcha();
        }
    });

    //注册点击事件
    public BindingCommand registerOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            register();
        }


    });



    public BindingCommand<Boolean> readAgreeOnCheckedChangedCommand = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(Boolean isChecked) {
            if(isChecked){
                readAgree.set(isChecked);
            }
        }
    });



    private void register() {
        mobileError.set("");
        captchaError.set("");
        passwordError.set("");
        readAgreeError.set("");

        if (TextUtils.isEmpty(request.getMobile())) {
            mobileError.set(getApplication().getString(R.string.hint_login_mobile));
            return;
        }

        if(TextUtils.isEmpty(request.getCaptcha())){
            captchaError.set(getApplication().getString(R.string.hint_login_captcha));
            return;
        }

        if(TextUtils.isEmpty(request.getPassword())){
            passwordError.set(getApplication().getString(R.string.hint_login_password));
            return;
        }
        if(!readAgree.get()){
            readAgreeError.set(getApplication().getString(R.string.hint_read_terms_service));
            return;
        }


        RetrofitClient.getInstance().create(AccountApiService.class)
                .phoneRegister(request)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Boolean>>() {
                    @Override
                    public void accept(BaseResponse<Boolean> response) throws Exception {
                        //请求成功
                        if (response.getCode() == 200) {
                            RxToast.success(getApplication().getString(R.string.registered_successfully));
                            finish();

                        } else if(response.getCode() == 200013){
                            mobileError.set(getApplication().getString(R.string.phone_number_is_exist));
                        } else if(200002 == response.getCode()){
                            captchaError.set(getApplication().getString(R.string.captcha_error));
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
                        //请求刷新完成收回
                        RxToast.error(throwable.message);
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回
                    }
                });

    }



    private void sendCaptcha() {

        if(smsTime<60){
            return;
        }

        if (TextUtils.isEmpty(request.getMobile())) {
            mobileError.set(getApplication().getString(R.string.hint_login_mobile));
            return;
        }

        mobileError.set("");
        RetrofitClient.getInstance().create(AccountApiService.class)
                .getRegisterCaptcha(request)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Object>>() {
                    @Override
                    public void accept(BaseResponse<Object> response) throws Exception {
                        //请求成功
                        if (response.getCode() == 200) {
                            timer.schedule(timerTask, 1000, 1000);       // timeTask
                            RxToast.success(getApplication().getString(R.string.send_captcha_success));

                        } else if(response.getCode() == 200013){
                            mobileError.set(getApplication().getString(R.string.phone_number_is_exist));
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
                        //请求刷新完成收回
                        RxToast.error(throwable.message);
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回
                    }
                });
    }



    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            smsTime--;
            getCaptcha.set(smsTime+"s");
            if(smsTime < 1){
                timer.cancel();
                getCaptcha.set(getApplication().getString(R.string.get_captcha));
                smsTime = 60;
            }


        }
    };
}
