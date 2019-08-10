package org.blackey.ui.orders.chat;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import org.blackey.ui.orders.detail.OrdersProcessingDetailViewModel;

import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.base.MultiItemViewModel;

public class ReceiveUnknownItemViewModel extends MultiItemViewModel<OrdersProcessingDetailViewModel> {

    public ObservableField<Message> entity = new ObservableField<>();

    public  ObservableField<String> avatar = new ObservableField<>();


    public ReceiveUnknownItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel) {
        super(viewModel);
    }

    public ReceiveUnknownItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel,Message entity,String avatar) {
        super(viewModel);
        this.entity.set(entity);
        this.avatar.set(avatar);

    }
}
