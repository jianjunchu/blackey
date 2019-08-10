package org.blackey.ui.complain;

import android.Manifest;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxui.view.dialog.RxDialogChooseImage;
import com.vondear.rxui.view.dialog.RxDialogScaleView;
import com.yalantis.ucrop.UCrop;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentComplainBinding;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.vondear.rxui.view.dialog.RxDialogChooseImage.LayoutType.TITLE;

public class ComplainFragment extends BaseFragment<FragmentComplainBinding,ComplainViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_complain;
    }



    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        viewModel.initToolbar();
    }



    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.showRxDialogScaleView.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

                ObservableField<String> observableField = (ObservableField<String>) sender;

                if(!TextUtils.isEmpty(observableField.get())){
                    RxDialogScaleView rxDialogScaleView = new RxDialogScaleView(getContext());
                    rxDialogScaleView.setImage(observableField.get(),false);
                    rxDialogScaleView.show();
                    viewModel.uc.showRxDialogScaleView.set("");
                }
            }
        });

        viewModel.uc.showRxDialogChooseImage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

                //请求打开相机权限
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.request(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {

                                    RxDialogChooseImage dialogChooseImage = new RxDialogChooseImage(ComplainFragment.this, TITLE);
                                    dialogChooseImage.show();
                                } else {
                                    ToastUtils.showShort("权限被拒绝");
                                }
                            }
                        });
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == getActivity().RESULT_OK) {

                    viewModel.addImage(RxPhotoTool.getImageAbsolutePath(getContext(),data.getData()));
                }

                break;
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == getActivity().RESULT_OK) {

                    viewModel.addImage(RxPhotoTool.getImageAbsolutePath(getContext(),RxPhotoTool.imageUriFromCamera) );
                }

                break;


            case UCrop.RESULT_ERROR://UCrop裁剪错误之后的处理
                final Throwable cropError = UCrop.getError(data);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
