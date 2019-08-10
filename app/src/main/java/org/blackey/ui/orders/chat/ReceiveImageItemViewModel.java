package org.blackey.ui.orders.chat;

import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.vondear.rxui.view.dialog.RxDialogScaleView;

import org.blackey.R;
import org.blackey.ui.orders.detail.OrdersProcessingDetailViewModel;

import java.io.File;

import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.utils.KLog;

public class ReceiveImageItemViewModel extends MultiItemViewModel<OrdersProcessingDetailViewModel> {

    public ObservableField<Message> entity = new ObservableField<>();

    public  ObservableField<String> avatar = new ObservableField<>();

    public  ObservableField<String> path = new ObservableField<>();

    public RxDialogScaleView rxDialogScaleView;
    public ReceiveImageItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel) {
        super(viewModel);
        multiType = R.layout.item_chat_receive_image;
    }

    public ReceiveImageItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel,Message entity,String avatar) {
        super(viewModel);
        this.entity.set(entity);
        this.avatar.set(avatar);
        multiType = R.layout.item_chat_receive_image;

        ImageContent imageContent = (ImageContent) entity.getContent();

        path.set(imageContent.getLocalThumbnailPath());
        KLog.i(imageContent.getLocalPath());

        rxDialogScaleView = new RxDialogScaleView(viewModel.activity);

        imageContent.downloadOriginImage(entity,new DownloadCompletionCallback(){
            @Override
            public void onComplete(int i, String s, File file) {
                Uri uri = Uri.fromFile(file);
                rxDialogScaleView.setImage(uri);
            }
        });

    }

    public BindingCommand showScaleView = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            rxDialogScaleView.show();
        }
    });
}