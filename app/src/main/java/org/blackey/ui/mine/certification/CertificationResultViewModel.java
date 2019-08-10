package org.blackey.ui.mine.certification;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.UserCertification;
import org.blackey.service.MineApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;

public class CertificationResultViewModel extends ToolbarViewModel {

    public ObservableField<String> buttonText = new ObservableField<>(getApplication().getString(R.string.bottom_return));
    public ObservableField<String> passedText = new ObservableField<>("");
    public ObservableField<Integer> passedIcon = new ObservableField<>(R.mipmap.ic_kyc_passed);
    public ObservableField<Integer> passedImage = new ObservableField<>(R.mipmap.kyc_liveness);

    public CertificationResultViewModel(@NonNull Application application) {
        super(application);
    }

    public UserCertification userCertification;


    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.certification_result));
        setRightIconVisible(View.GONE);
    }

    public void initData() {

        requestNetWork();
    }

    /**
     * 网络请求方法，在ViewModel中调用，Retrofit+RxJava充当Repository，即可视为Model层
     */
    public void requestNetWork() {
        RetrofitClient.getInstance().create(MineApiService.class)
                .getCertification()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new Consumer<BaseResponse<UserCertification>>() {
                    @Override
                    public void accept(BaseResponse<UserCertification> response) throws Exception {
                        if(response.isOk()){
                            if(response.getBody() ==null){
                                startContainerActivity(SelectIdentityCardTypeFragment.class.getCanonicalName());
                                finish();
                            }else{
                                userCertification = response.getBody();
                                showData();

                            }
                        }else{
                            RxToast.error(response.getMessage());
                        }

                        dismissDialog();

                        KLog.e(response);
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        dismissDialog();
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        //关闭对话框
                        //请求刷新完成收回

                    }
                });


    }

    private void showData() {

        if(userCertification.getPassed() == UserCertification.PASSED_CHECK_PENDING){//待审核
            buttonText.set(getApplication().getString(R.string.bottom_return));
            passedText.set(getApplication().getString(R.string.passed_check_pending));
            passedIcon.set(R.mipmap.ic_kyc_pending);
            passedImage.set(R.mipmap.kyc_liveness);
        }else if(userCertification.getPassed() == UserCertification.PASSED_FAILED){
            buttonText.set(getApplication().getString(R.string.bottom_restart));
            passedText.set(getApplication().getString(R.string.passed_failed));
            passedIcon.set(R.mipmap.ic_kyc_failed);
            passedImage.set(R.mipmap.kyc_liveness_failed);
        }else if(userCertification.getPassed() == UserCertification.PASSED_APPROVE){
            buttonText.set(getApplication().getString(R.string.bottom_return));
            passedText.set(getApplication().getString(R.string.passed_approve));
            passedIcon.set(R.mipmap.ic_kyc_passed);
            passedImage.set(R.mipmap.kyc_passed);
        }
    }

    /**
     * 下一步
     * @return
     */
    public BindingCommand buttonClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

                if(userCertification ==null){
                    finish();
                }else if(userCertification.getPassed() == UserCertification.PASSED_CHECK_PENDING){//待审核
                    finish();
                }else if(userCertification.getPassed() == UserCertification.PASSED_FAILED){
                    startContainerActivity(SelectIdentityCardTypeFragment.class.getCanonicalName());
                    finish();

                }else if(userCertification.getPassed() == UserCertification.PASSED_APPROVE){
                    finish();
                }

            }
        });
    }
}
