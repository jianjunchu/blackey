package org.blackey.ui.orders.chat;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ChatMultiItemViewModel;
import org.blackey.ui.orders.detail.OrdersProcessingDetailViewModel;

import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.utils.KLog;

public class SendTextItemViewModel extends ChatMultiItemViewModel {




    public SendTextItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel) {
        super(viewModel);
        multiType = R.layout.item_chat_send_text;
    }

    public SendTextItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel,Message entity,String avatar) {
        super(viewModel,entity,avatar);
    }

    public BindingCommand resendClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            sendMessage();
        }
    });

    public BindingCommand currentView = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            KLog.d(view);
            mSending = (ImageView) view;
        }
    });

}
