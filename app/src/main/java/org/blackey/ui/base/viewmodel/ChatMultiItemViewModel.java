package org.blackey.ui.base.viewmodel;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import org.blackey.R;
import org.blackey.ui.orders.detail.OrdersProcessingDetailViewModel;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.MultiItemViewModel;

public class ChatMultiItemViewModel extends MultiItemViewModel<OrdersProcessingDetailViewModel> {

    public ObservableField<Message> entity = new ObservableField<>();

    public ImageView mSending;

    public  ObservableField<String> avatar = new ObservableField<>();

    public ObservableField<String> contentText = new ObservableField<>();

    public  ObservableField<Integer> sendingVisibility = new ObservableField<>(View.GONE);

    public ObservableField<Integer> resendVisibility = new ObservableField<>(View.GONE);



    public ChatMultiItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel) {
        super(viewModel);
    }

    public ChatMultiItemViewModel(@NonNull OrdersProcessingDetailViewModel viewModel,@NonNull Message entity,@NonNull String avatar) {
        super(viewModel);
        this.entity.set(entity);
        this.avatar.set(avatar);
        multiType = R.layout.item_chat_send_text;
        TextContent textContent = (TextContent) entity.getContent();
        contentText.set(textContent.getText());
    }

    public void sendMessage(){
        sendingVisibility.set(View.VISIBLE);
        entity.get().setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(final int status, String desc) {

                Observable.just("")
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                sendingVisibility.set(View.GONE);
                                if (status != 0) {
                                    resendVisibility.set(View.VISIBLE);
                                }
                            }
                        });


            }
        });
        JMessageClient.sendMessage(entity.get());
    }




}
