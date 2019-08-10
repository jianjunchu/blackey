package org.blackey.ui.mine;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.view.RxToast;
import com.vondear.rxui.view.dialog.RxDialogChooseImage;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.CurrentUser;
import org.blackey.service.MineApiService;
import org.blackey.ui.login.LoginActivity;
import org.blackey.ui.login.LoginViewModel;
import org.blackey.ui.market.my.MyAdsFragment;
import org.blackey.ui.mine.about.AboutFragment;
import org.blackey.ui.mine.certification.CertificationResultFragment;
import org.blackey.ui.mine.help.HelpFragment;
import org.blackey.ui.mine.myaccount.MyAccountFragment;
import org.blackey.ui.mine.myorders.MyOrdersFragment;
import org.blackey.ui.mine.nfc.ResetNfcPasswordActivity;
import org.blackey.ui.mine.security.SecurityCenterFragment;
import org.blackey.ui.mine.setting.SettingFragment;
import org.blackey.ui.mine.setting.SettingViewModel;
import org.blackey.ui.mine.share.ShareFragment;
import org.blackey.ui.mine.toolbar.MineToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.TokenManager;

import java.io.File;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.widget.CircleImageView;

import static com.vondear.rxui.view.dialog.RxDialogChooseImage.LayoutType.TITLE;


/**
 * 个人中心
 */
public class MineViewModel extends MineToolbarViewModel {

    public CircleImageView portraitImageView;

    public Activity mContext;

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.bottom_tab_mine));

        //注册一个空消息监听
        Messenger.getDefault().register(this, SettingViewModel.TOKEN_SETTING_VIEW_MODEL_LOGOUT, new BindingAction() {
            @Override
            public void call() {
                initData();
            }
        });

        //注册一个空消息监听
        Messenger.getDefault().register(this, LoginViewModel.TOKEN_LOGIN_VIEW_MODEL_LOGIN, new BindingAction() {
            @Override
            public void call() {
                requestNetWork();
            }
        });
    }



    /**
     * 邀请好友的点击事件
     */
    public final BindingCommand shareOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(ShareFragment.class.getCanonicalName());
        }
    });

    /**
     * 我的订单的点击事件
     */
    public final BindingCommand myOrdersOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(MyOrdersFragment.class.getCanonicalName());
        }
    });

    /**
     * 我的订单的点击事件
     */
    public final BindingCommand myAdOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            if(BlackeyApplication.getCurrent()==null){
                startActivity(LoginActivity.class);
                return;
            }
            startContainerActivity(MyAdsFragment.class.getCanonicalName());
        }
    });

    /**
     * 我的账号的点击事件
     */
    public final BindingCommand myAccountOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            if(BlackeyApplication.getCurrent()==null){
                startActivity(LoginActivity.class);
                return;
            }

            startContainerActivity(MyAccountFragment.class.getCanonicalName());
        }
    });

    /**
     * 安全中心的点击事件
     */
    public final BindingCommand securityCenterOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(SecurityCenterFragment.class.getCanonicalName());
        }
    });
    /**
     * 帮助按钮的点击事件
     */
    public final BindingCommand helpOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(HelpFragment.class.getCanonicalName());
        }
    });

    /**
     * 关于我们按钮的点击事件
     */
    public final BindingCommand aboutOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(AboutFragment.class.getCanonicalName());
        }
    });

    /**
     * 设置按钮的点击事件
     */
    public final BindingCommand settingOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(SettingFragment.class.getCanonicalName());
        }
    });

    public final BindingCommand resetNfcPasswordOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startActivity(ResetNfcPasswordActivity.class);
        }
    });





    /**
     * 实名认证的点击事件
     */
    public final BindingCommand certificationOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(CertificationResultFragment.class.getCanonicalName());
        }
    });

    /**
     * 右边文字的点击事件，子类可重写
     */
    @Override
    public BindingCommand loginOnClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                startActivity(LoginActivity.class);
            }
        });
    }

    /**
     * 右边文字的点击事件，子类可重写
     */
    @Override
    public BindingCommand chooseImageClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                if(BlackeyApplication.getCurrent()!=null){

                    //请求打开相机权限
                    RxPermissions rxPermissions = new RxPermissions((Activity) mContext);
                    rxPermissions.request(Manifest.permission.CAMERA)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        RxDialogChooseImage dialogChooseImage = new RxDialogChooseImage(mContext, TITLE);
                                        dialogChooseImage.show();
                                    } else {
                                        ToastUtils.showShort("权限被拒绝");
                                    }
                                }
                            });


                }else{
                    startActivity(LoginActivity.class);
                }
            }
        });
    }


    public void initData(){
        if(BlackeyApplication.getCurrent()!=null){
            noLoginVisibility.set(View.GONE);
            loginVisibility.set(View.VISIBLE);
            Glide.with(getApplication())
                    .load(BlackeyApplication.getCurrent().getPortrait())
                    .apply(new RequestOptions().error(R.mipmap.icon_person))
                    .into(portraitImageView);

            nickname.set(BlackeyApplication.getCurrent().getNickname());
            String testStr = getApplication().getResources().getString(R.string.number_trades);
            String result = String.format(testStr,
                    StringUtils.toString(BlackeyApplication.getCurrent().getTrades(),"0")
                    ,StringUtils.toString(BlackeyApplication.getCurrent().getCreditScore(),"0"));
            numberTrades.set(result);
        }else{
            noLoginVisibility.set(View.VISIBLE);
            loginVisibility.set(View.GONE);
            Glide.with(getApplication()).load(R.mipmap.icon_person).into(portraitImageView);
        }
    }

    public void requestNetWork() {
        initData();

        if(!TextUtils.isEmpty(TokenManager.getAccessToken())){

            TokenManager.requestCurrentUser(this, new TokenManager.CurrentUserCallback() {
                @Override
                public void subscribe(CurrentUser user) {
                    initData();
                }
            });
        }else{
            noLoginVisibility.set(View.VISIBLE);
            loginVisibility.set(View.GONE);
        }
    }

    public void requestUploadPhoto(File file) {

        RetrofitClient.getInstance().create(MineApiService.class)
                .uploadPhoto(RetrofitClient.imagesToMultipartBody("photo",file))
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<String>>() {
                    @Override
                    public void accept(BaseResponse<String> response) throws Exception {

                        //请求成功
                        if(response.isOk()){
                            Glide.with(getApplication())
                                    .load(response.getBody())
                                    .apply(new RequestOptions().error(R.mipmap.icon_person))
                                    .into(portraitImageView);
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
