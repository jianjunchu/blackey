package org.blackey.ui.mine.certification;

import android.Manifest;
import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import org.blackey.R;
import org.blackey.entity.UserCertification;
import org.blackey.model.request.UserCertificationRequest;
import org.blackey.service.MineApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxtool.view.RxToast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;

public class UploadCredentialsViewModel extends ToolbarViewModel {

    public UploadCredentialsFragment fragment;

    public ImageView imageView;

    public File front;
    public File back;


    public UserCertificationRequest request = new UserCertificationRequest();

    public ObservableField<String> frontText = new ObservableField<>();
    public ObservableField<String> backText = new ObservableField<>();
    public ObservableField<Integer> frontVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> backVisibility = new ObservableField<>(View.VISIBLE);

    public ObservableField<Integer> hintFirstNameVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> hintLastNameVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> hintBirthDayVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> hintIdentityNumberVisibility = new ObservableField<>(View.GONE);

    public ObservableField<String> hintFirstName = new ObservableField<>("");
    public ObservableField<String> hintLastName = new ObservableField<>("");
    public ObservableField<String> hintBirthDay = new ObservableField<>("");
    public ObservableField<String> hintIdentityNumber = new ObservableField<>("");





    public UploadCredentialsViewModel(@NonNull Application application) {
        super(application);
    }

    public BindingCommand frontClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                imageView = (ImageView) view;
                //请求打开相机权限
                RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    RxPhotoTool.openCameraImage(fragment);
                                } else {
                                    RxToast.showToast(R.string.no_permission_camera);
                                }
                            }
                        });

            }
        });
    }

    public BindingCommand backClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                imageView = (ImageView) view;
                //请求打开相机权限
                RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    RxPhotoTool.openCameraImage(fragment);
                                } else {
                                    RxToast.showToast(R.string.no_permission_camera);
                                }
                            }
                        });
            }


        });
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar(int title) {

        String titleText = getApplication().getString(title);

        //初始化标题栏
        setTitleText(titleText);
        setRightIconVisible(View.GONE);

        frontText.set(getApplication().getString(R.string.front_of_id_card, titleText));
        backText.set(getApplication().getString(R.string.back_of_id_card, titleText));
    }



    public BindingCommand nextClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            hintFirstNameVisibility.set(View.GONE);
            hintLastNameVisibility.set(View.GONE);
            hintBirthDayVisibility.set(View.GONE);
            hintIdentityNumberVisibility.set(View.GONE);

            Map<String,Object> vs = new HashMap<>();


            if(TextUtils.isEmpty(request.getFirstName()) ){
                hintFirstNameVisibility.set(View.VISIBLE);
                hintFirstName.set(getApplication().getString(R.string.hint_first_name));
                return;
            }else{
                vs.put("firstName",request.getFirstName());
            }

            if(TextUtils.isEmpty(request.getLastName()) ){
                hintLastName.set(getApplication().getString(R.string.hint_last_name));
                hintLastNameVisibility.set(View.VISIBLE);
                return;
            }else{
                vs.put("lastName",request.getLastName());
            }
            if(TextUtils.isEmpty(request.getBirthDay()) ){
                hintBirthDay.set(getApplication().getString(R.string.hint_birth_day));
                hintBirthDayVisibility.set(View.VISIBLE);
                return;
            }else{
                vs.put("birthDay",request.getBirthDay());
            }

            if(TextUtils.isEmpty(request.getIdentityNumber())){
                hintIdentityNumber.set(getApplication().getString(R.string.hint_birth_day));
                hintIdentityNumberVisibility.set(View.VISIBLE);
                return;
            }else{
                vs.put("identityNumber",request.getIdentityNumber());
            }

            vs.put("identityCardType",request.getIdentityCardType()+"");
            vs.put("nationality",request.getNationality()+"");

            vs.put("back",back);
            vs.put("front",front);



            RetrofitClient.getInstance().create(MineApiService.class)
                    .addCertification(RetrofitClient.imageToMultipartBody(vs))
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                    .compose(RxUtils.schedulersTransformer()) //线程调度
                    .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                        }
                    })
                    .subscribe(new Consumer<BaseResponse<UserCertification>>() {
                        @Override
                        public void accept(BaseResponse<UserCertification> response) throws Exception {
                            //请求成功
                            if(response.isOk()){
                                startContainerActivity(CertificationResultFragment.class.getCanonicalName());
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


    });
}
