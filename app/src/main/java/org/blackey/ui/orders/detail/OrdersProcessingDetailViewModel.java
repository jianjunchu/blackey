package org.blackey.ui.orders.detail;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.Order;
import org.blackey.service.OrderApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.login.LoginActivity;
import org.blackey.ui.orders.chat.ReceiveImageItemViewModel;
import org.blackey.ui.orders.chat.ReceiveTextItemViewModel;
import org.blackey.ui.orders.chat.SendImageItemViewModel;
import org.blackey.ui.orders.chat.SendTextItemViewModel;
import org.blackey.utils.RetrofitClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class OrdersProcessingDetailViewModel extends ToolbarViewModel {


    public Integer orderType;


    Timer timer = new Timer();

    public ObservableField<Order> entity = new ObservableField<>();

    public ObservableField<String> messageText = new ObservableField<>("");



    public ObservableField<Integer> cancelVisibility = new ObservableField<>(View.VISIBLE);

    public ObservableField<Integer> remindVisibility = new ObservableField<>(View.GONE);

    public ObservableField<Integer> paymentVisibility = new ObservableField<>(View.GONE);

    public ObservableField<Integer> preceiptVisibility = new ObservableField<>(View.GONE);



    public ObservableField<String> orderStatusText = new ObservableField<>();

    public ObservableField<String> totalMoneyText = new ObservableField<>();

    private Conversation mConv;

    public Animation mSendingAnim;




    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();

    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局

            if (R.layout.item_chat_receive_file == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_file);
            } else if (R.layout.item_chat_receive_image == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_image);
            }else if (R.layout.item_chat_receive_location == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_location);
            }else if (R.layout.item_chat_receive_text == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_text);
            }else if (R.layout.item_chat_receive_video == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_video);
            }else if (R.layout.item_chat_receive_voice == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_receive_voice);
            }else if (R.layout.item_chat_send_file == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_file);
            }else if (R.layout.item_chat_send_image == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_image);
            }else if (R.layout.item_chat_send_location == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_location);
            }else if (R.layout.item_chat_send_text == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_text);
            }else if (R.layout.item_chat_send_video == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_video);
            }else if (R.layout.item_chat_send_voice == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_chat_send_voice);
            }
        }
    });
    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法
    public final BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public Activity activity;

    public OrdersProcessingDetailViewModel(@NonNull Application application) {
        super(application);
    }


    public BindingCommand hideRemindOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            remindVisibility.set(View.GONE);
        }
    });



    public void requestNetWork(Long orderId) {
        RetrofitClient.getInstance().create(OrderApiService.class)
                .get(orderId)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Order>>() {
                    @Override
                    public void accept(BaseResponse<Order> response) throws Exception {
                        //请求成功
                        if(response.isOk()){

                            entity.set(response.getBody());
                            initData();
                        }
                        dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }

    private void initData() {

        mSendingAnim = AnimationUtils.loadAnimation(activity, R.anim.jmui_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        mSendingAnim.setInterpolator(lin);
        totalMoneyText.set(entity.get().getTotalMoney()==null ? "0.00": entity.get().getTotalMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        if(entity.get()!=null){

            Long userId = orderType == Order.ORDER_TYPE_SELL ? entity.get().getBuyerId() : entity.get().getSellerId();

            mConv = JMessageClient.getSingleConversation(userId.toString());
            if (mConv == null) {
                mConv = Conversation.createSingleConversation(userId.toString());
            }

            switch (entity.get().getOrderStatus()){
                case Order.ORDER_STATUS_UNPAID :

                    timer.schedule(new TimerTask() {
                        public void run() {

                            long between = (entity.get().getCreateTime().getTime()+30*60*1000) - System.currentTimeMillis()  ;

                            if(between < 1){
                                timer.cancel();
                                orderStatusText.set(getApplication().getString(R.string.order_status_timeout));
                            }

                            long min = ((between / (60 * 1000)));

                            String minStr = min > 9 ? ""+min : "0"+min;

                            long s = (between / 1000 - min * 60);
                            String sStr = s > 9 ? ""+s : "0"+s;
                            orderStatusText.set(getApplication().getString(R.string.order_status_unpaid) +" "+minStr+":"+sStr);

                        }
                    }, 0 , 1000);


                    break;
                case Order.ORDER_STATUS_PREPAID :
                    orderStatusText.set(getApplication().getString(R.string.order_status_prepaid));
                    break;
                case Order.ORDER_STATUS_COMPLETE :
                    orderStatusText.set(getApplication().getString(R.string.order_status_complete));
                    break;
                case Order.ORDER_STATUS_CANCEL :
                    orderStatusText.set(getApplication().getString(R.string.order_status_cancel));
                    break;
                case Order.ORDER_STATUS_TIMEOUT :
                    orderStatusText.set(getApplication().getString(R.string.order_status_timeout));
                    break;
            }


            //出售订单
            if(Order.ORDER_TYPE_SELL.equals(orderType) || (orderType ==null && entity.get().isSellOrder()) ){

                cancelVisibility.set(View.GONE);

                setTitleText(getApplication().getString(R.string.sell_order,entity.get().getBuyerNickname()));
                if(entity.get().getOrderStatus() == Order.ORDER_STATUS_UNPAID){
                    paymentVisibility.set(View.INVISIBLE);
                }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_PREPAID){
                    preceiptVisibility.set(View.VISIBLE);
                }else{

                    paymentVisibility.set(View.INVISIBLE);
                    preceiptVisibility.set(View.INVISIBLE);
                }

                //购买订单
            }else if(Order.ORDER_TYPE_BUY.equals(orderType) || (orderType ==null &&entity.get().isBuyOrder()) ){
                setTitleText(getApplication().getString(R.string.buy_order,entity.get().getSellerNickname()));
                if(entity.get().getOrderStatus() == Order.ORDER_STATUS_UNPAID){
                    paymentVisibility.set(View.VISIBLE);
                }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_PREPAID){
                    paymentVisibility.set(View.INVISIBLE);
                }else{
                    cancelVisibility.set(View.GONE);
                    paymentVisibility.set(View.INVISIBLE);
                    preceiptVisibility.set(View.INVISIBLE);
                }
            }

        }

        List<Message> list =  mConv.getAllMessage();

        for(Message message : list){

            String extra =  message.getContent().getStringExtra(OrdersProcessingDetailFragment.ORDER_ID);

            String portrait = orderType == Order.ORDER_TYPE_SELL ? entity.get().getBuyerPortrait() : entity.get().getSellerPortrait();

            if(extra!=null && extra.equals(entity.get().getOrderId().toString()) ){
                if(message.getContentType().equals(ContentType.text) && message.getDirect() == MessageDirect.send){
                    SendTextItemViewModel itemViewModel = new SendTextItemViewModel(OrdersProcessingDetailViewModel.this,message, BlackeyApplication.getCurrent().getPortrait());
                    observableList.add(itemViewModel);
                }else if(message.getContentType().equals(ContentType.text) && message.getDirect() != MessageDirect.send){
                    ReceiveTextItemViewModel itemViewModel = new ReceiveTextItemViewModel(OrdersProcessingDetailViewModel.this,message, portrait);
                    observableList.add(itemViewModel);
                }else if(message.getContentType().equals(ContentType.image) && message.getDirect() == MessageDirect.send){
                    SendImageItemViewModel itemViewModel = new SendImageItemViewModel(OrdersProcessingDetailViewModel.this,message, BlackeyApplication.getCurrent().getPortrait());
                    observableList.add(itemViewModel);
                }else if(message.getContentType().equals(ContentType.image) && message.getDirect() != MessageDirect.send){
                    ReceiveImageItemViewModel itemViewModel = new ReceiveImageItemViewModel(OrdersProcessingDetailViewModel.this,message, portrait);
                    observableList.add(itemViewModel);
                }

            }
        }
    }

    public BindingCommand paymentClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            confirmPayment();
        }
    });

    public BindingCommand orderNoClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            Bundle mBundle = new Bundle();
            if(orderType !=null && orderType == Order.ORDER_TYPE_SELL ){
                mBundle.putLong(BuyerOrderInfoFragment.ORDER_ID, entity.get().getOrderId());
                startContainerActivity(SellerOrderInfoFragment.class.getCanonicalName(),mBundle);
            }else if(orderType !=null && orderType == Order.ORDER_TYPE_BUY ){
                mBundle.putLong(BuyerOrderInfoFragment.ORDER_ID, entity.get().getOrderId());
                startContainerActivity(BuyerOrderInfoFragment.class.getCanonicalName(),mBundle);
            }else{
                mBundle.putLong(BuyerOrderInfoFragment.ORDER_ID, entity.get().getOrderId());
                startContainerActivity(BuyerOrderInfoFragment.class.getCanonicalName(),mBundle);
            }

        }
    });

    public BindingCommand cancelClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            cancelOrderConfirm();
        }
    });

    private void cancelOrderConfirm() {

        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(activity);
        normalDialog.setTitle(getApplication().getString(R.string.title_confirm_cancel));
        normalDialog.setPositiveButton(getApplication().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestCancelOrder();
                    }
                });
        normalDialog.setNegativeButton(getApplication().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void requestCancelOrder() {

        RetrofitClient.getInstance().create(OrderApiService.class)
                .cancel(entity.get().getOrderId())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Order>>() {
                    @Override
                    public void accept(BaseResponse<Order> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            RxToast.success(getApplication().getString(R.string.success));
                            entity.set(response.getBody());
                            initData();
                        }
                        dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }

    private void confirmPayment() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(activity);
        normalDialog.setTitle(getApplication().getString(R.string.title_confirm_payment));
        normalDialog.setPositiveButton(getApplication().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestConfirmPayment();
                    }
                });
        normalDialog.setNegativeButton(getApplication().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void requestConfirmPayment() {

        RetrofitClient.getInstance().create(OrderApiService.class)
                .confirmPayment(entity.get().getOrderId())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Order>>() {
                    @Override
                    public void accept(BaseResponse<Order> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            RxToast.success(getApplication().getString(R.string.success));
                            entity.set(response.getBody());
                            initData();
                        }
                        dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }

    public BindingCommand preceiptClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            confirmReceipt();
        }
    });

    private void confirmReceipt() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(activity);
        normalDialog.setTitle(getApplication().getString(R.string.title_confirm_receipt));
        normalDialog.setPositiveButton(getApplication().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestConfirmReceipt();
                    }
                });
        normalDialog.setNegativeButton(getApplication().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void requestConfirmReceipt() {

        RetrofitClient.getInstance().create(OrderApiService.class)
                .confirmReceipt(entity.get().getOrderId())
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Order>>() {
                    @Override
                    public void accept(BaseResponse<Order> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            RxToast.success(getApplication().getString(R.string.success));
                            entity.set(response.getBody());
                            initData();
                        }else if(response.getCode() == 201001){
                            RxToast.error(getApplication().getString(R.string.error_processing_previous));
                        }else {
                            RxToast.error(response.getMessage());
                        }
                        dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });
    }


    public void messageEvent(final Message message) {

        final String portrait = orderType == Order.ORDER_TYPE_SELL ? entity.get().getBuyerPortrait() : entity.get().getSellerPortrait();

        Observable.just("")
                .delay(1, TimeUnit.SECONDS) //延迟3秒
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider()))//界面关闭自动取消
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                        switch (message.getContentType()) {
                            case text:
                                //处理文字消息
                                ReceiveTextItemViewModel textItemViewModel = new ReceiveTextItemViewModel(OrdersProcessingDetailViewModel.this,message,portrait);
                                observableList.add(textItemViewModel);
                                break;
                            case image:
                                //处理图片消息
                                ReceiveImageItemViewModel imageItemViewModel = new ReceiveImageItemViewModel(OrdersProcessingDetailViewModel.this,message, portrait);
                                observableList.add(imageItemViewModel);
                                break;
                        }


                    }
                });


    }

    public BindingCommand sendTextClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {


            TextContent textContent = new TextContent(messageText.get());
            textContent.setStringExtra(OrdersProcessingDetailFragment.ORDER_ID,entity.get().getOrderId().toString());

            final Message textMessage =  mConv.createSendMessage(textContent);

            messageText.set("");
            RxKeyboardTool.hideSoftInput(activity);

            Observable.just("")
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {

                            SendTextItemViewModel itemViewModel = new SendTextItemViewModel(OrdersProcessingDetailViewModel.this,textMessage, BlackeyApplication.getCurrent().getPortrait());
                            observableList.add(itemViewModel);
                            itemViewModel.sendMessage();
                        }
                    });
        }

    });

    public void sendImage (File image) throws FileNotFoundException {


            ImageContent textContent = new ImageContent(image);
            textContent.setStringExtra(OrdersProcessingDetailFragment.ORDER_ID,entity.get().getOrderId().toString());

            final Message textMessage =  mConv.createSendMessage(textContent);

            messageText.set("");
            RxKeyboardTool.hideSoftInput(activity);

            Observable.just("")
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {

                            SendImageItemViewModel itemViewModel = new SendImageItemViewModel(OrdersProcessingDetailViewModel.this,textMessage, BlackeyApplication.getCurrent().getPortrait());
                            observableList.add(itemViewModel);
                            itemViewModel.sendMessage();
                        }
                    });
        }


    public BindingCommand chooseImageClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                if(BlackeyApplication.getCurrent()!=null){

                    //请求打开相机权限
                    RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.request(Manifest.permission.CAMERA)
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        uc.showRxDialogChooseImage.set(aBoolean);

                                    } else {
                                        ToastUtils.showShort("权限被拒绝");
                                    }
                                }
                            });


                }else{
                    startActivity(LoginActivity.class);
                }
            }
        });
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {

        public ObservableBoolean showRxDialogChooseImage = new ObservableBoolean(false);
    }
}
