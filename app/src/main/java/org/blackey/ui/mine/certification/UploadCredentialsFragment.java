package org.blackey.ui.mine.certification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineUploadCredentialBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.vondear.rxtool.RxPhotoTool;

import java.io.File;

import org.blackey.ui.base.BlackeyBaseFragment;

public class UploadCredentialsFragment extends BlackeyBaseFragment<FragmentMineUploadCredentialBinding,UploadCredentialsViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_upload_credential;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.fragment = this;

        Bundle mBundle = getArguments();
        viewModel.request = mBundle.getParcelable("request");
        viewModel.initToolbar(mBundle.getInt("title"));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case RxPhotoTool.GET_IMAGE_BY_CAMERA://选择照相机之后的处理
                if (resultCode == getActivity().RESULT_OK) {
                    /* data.getExtras().get("data");*/
//                    RxPhotoTool.cropImage(ActivityUser.this, RxPhotoTool.imageUriFromCamera);// 裁剪图片
                    //initUCrop(RxPhotoTool.imageUriFromCamera);
                   File file =  roadImageView(RxPhotoTool.imageUriFromCamera,viewModel.imageView);
                    if(viewModel.imageView.getId() == R.id.iv_front){
                        viewModel.frontVisibility.set(View.GONE);
                        viewModel.front = file;
                    }
                    if(viewModel.imageView.getId() == R.id.iv_back){
                        viewModel.backVisibility.set(View.GONE);
                        viewModel.back = file;
                    }
                }

                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //从Uri中加载图片 并将其转化成File文件返回
    private File roadImageView(Uri uri, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                //禁止Glide硬盘缓存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        Glide.with(getContext()).
                load(uri).
                apply(options).
                thumbnail(0.5f).
                into(imageView);

        return (new File(RxPhotoTool.getImageAbsolutePath(getContext(), uri)));
    }
}
