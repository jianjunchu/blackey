package org.blackey.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.blackey.app.BlackeyApplication;
import org.blackey.ui.orders.detail.OrdersProcessingDetailFragment;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import me.goldze.mvvmhabit.base.ContainerActivity;

public class NotificationClickEventReceiver {

    private Context mContext;

    public NotificationClickEventReceiver(Context context) {
        mContext = context;
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this);
    }

    /**
     * 收到消息处理
     * @param notificationClickEvent 通知点击事件
     */
    public void onEvent(NotificationClickEvent notificationClickEvent) {
        if (null == notificationClickEvent) {
            return;
        }
        Message msg = notificationClickEvent.getMessage();
        if (msg != null) {
            String targetId = msg.getTargetID();
            String appKey = msg.getFromAppKey();



            ConversationType type = msg.getTargetType();
            Conversation conv;


            Intent notificationIntent = new Intent(mContext, ContainerActivity.class);

            String orderIdStr =   msg.getContent().getStringExtra(OrdersProcessingDetailFragment.ORDER_ID);

            if(!TextUtils.isEmpty(orderIdStr)){
                Bundle mBundle = new Bundle();
                mBundle.putLong(OrdersProcessingDetailFragment.ORDER_ID, Long.valueOf(orderIdStr));
                notificationIntent.putExtra(ContainerActivity.BUNDLE, mBundle);
            }

            notificationIntent.putExtra(ContainerActivity.FRAGMENT, OrdersProcessingDetailFragment.class.getCanonicalName());
            if (type == ConversationType.single) {
                conv = JMessageClient.getSingleConversation(targetId, appKey);
                notificationIntent.putExtra(BlackeyApplication.TARGET_ID, targetId);
                notificationIntent.putExtra(BlackeyApplication.TARGET_APP_KEY, appKey);
            } else {
                conv = JMessageClient.getGroupConversation(Long.parseLong(targetId));
                notificationIntent.putExtra(BlackeyApplication.GROUP_ID, Long.parseLong(targetId));
            }
            notificationIntent.putExtra(BlackeyApplication.CONV_TITLE, conv.getTitle());
            conv.resetUnreadCount();
//        notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.putExtra("fromGroup", false);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(notificationIntent);
        }
    }
}
