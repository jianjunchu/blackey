package org.blackey.ui.orders.detail;

import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxui.view.dialog.RxDialogChooseImage;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentOrderProcessingDetailBinding;
import org.blackey.ui.base.BlackeyBaseFragment;

import java.io.File;
import java.io.FileNotFoundException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.utils.KLog;

import static com.vondear.rxui.view.dialog.RxDialogChooseImage.LayoutType.TITLE;

public class OrdersProcessingDetailFragment extends BlackeyBaseFragment<FragmentOrderProcessingDetailBinding,OrdersProcessingDetailViewModel> {

    public static final String ORDER_ID = "orderId";

    public static final String ORDER_TYPE = "orderType";

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_order_processing_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.activity = getActivity();
        JMessageClient.registerEventReceiver(this);
        Bundle mBundle = getArguments();
        Long orderId = mBundle.getLong(ORDER_ID);
        int orderType =  mBundle.getInt(ORDER_TYPE);
        viewModel.orderType = orderType;
        viewModel.requestNetWork(orderId);
    }



    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();
        KLog.e(message);
        viewModel.messageEvent(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RxPhotoTool.GET_IMAGE_FROM_PHONE://选择相册之后的处理
                if (resultCode == getActivity().RESULT_OK) {
//                    RxPhotoTool.cropImage(ActivityUser.this, );// 裁剪图片
                    //initUCrop(data.getData());
                    File file = new File(RxPhotoTool.getImageAbsolutePath(getContext(), data.getData()));
                    try {
                        viewModel.sendImage(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == getActivity().RESULT_OK) {
                    /* data.getExtras().get("data");*/
//                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// 裁剪图片
                    //initUCrop(RxPhotoTool.imageUriFromCamera);
                    File file = new File(RxPhotoTool.getImageAbsolutePath(getContext(), RxPhotoTool.imageUriFromCamera));
                    try {
                        viewModel.sendImage(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        binding.etMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String key =  binding.etMessage.getText().toString().trim();
                    if (TextUtils.isEmpty(key)) {

                        return true;
                    }

                    //  这里记得一定要将键盘隐藏了
                    RxKeyboardTool.hideSoftInput(getActivity());
                    return true;
                }
                return false;
            }
        });

        viewModel.uc.showRxDialogChooseImage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                RxDialogChooseImage dialogChooseImage = new RxDialogChooseImage(OrdersProcessingDetailFragment.this, TITLE);
                dialogChooseImage.show();
            }
        });
    }
}
