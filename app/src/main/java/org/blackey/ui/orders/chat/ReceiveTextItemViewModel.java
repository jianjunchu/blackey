package org.blackey.ui.orders.chat;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import org.blackey.R;
import org.blackey.ui.orders.detail.OrdersProcessingDetailViewModel;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.base.MultiItemViewModel;

public class ReceiveTextItemViewModel extends MultiItemViewModel<OrdersProcessingDetailViewModel> {

    public ObservableField<Message> entity = new ObservableField<>();

    public ObservableField<String> contentText = new ObservableField<>();

    public  ObservableField<String> avatar = new ObservableField<>();


    public ReceiveTextItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel) {
        super(viewModel);
        multiType = R.layout.item_chat_receive_text;
    }

    public ReceiveTextItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel,Message entity,String avatar) {
        super(viewModel);
        this.entity.set(entity);
        this.avatar.set(avatar);
        TextContent textContent = (TextContent) entity.getContent();
        contentText.set(textContent.getText());

        multiType = R.layout.item_chat_receive_text;

    }
}
