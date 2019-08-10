package org.blackey.ui.orders.detail;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.Order;
import org.blackey.entity.PaymentMode;
import org.blackey.entity.UserPaymentMode;
import org.blackey.service.AdvertiseApiService;
import org.blackey.service.OrderApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.complain.ComplainFragment;
import org.blackey.ui.orders.detail.adapter.AccountViewPagerItemViewModel;
import org.blackey.utils.DateUtils;
import org.blackey.utils.RetrofitClient;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class BuyerOrderInfoViewModel extends ToolbarViewModel {


    public ObservableField<Order> entity = new ObservableField<>();

    public ObservableField<String> contactNumber = new ObservableField<>();

    public ObservableField<String> rightButtonText = new ObservableField<>();

    public ObservableField<Integer> rightButtonBackground = new ObservableField<>();

    public ObservableField<String> goodsMoneyText = new ObservableField<>();
    public ObservableField<String> quantityText = new ObservableField<>();
    public ObservableField<String> totalMoneyText = new ObservableField<>();
    public ObservableField<String> commissionRateText = new ObservableField<>();
    public ObservableField<String> getQuantityText = new ObservableField<>();
    public ObservableField<String> createTimeText = new ObservableField<>();

    public ObservableField<String> textBuyerMind = new ObservableField<>();


    public SingleLiveEvent<PaymentMode> itemClickEvent = new SingleLiveEvent<>();

    public Activity activity;


    public BuyerOrderInfoViewModel(@NonNull Application application) {
        super(application);
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public static class UIChangeObservable {
        //下拉刷新完成
        public ObservableInt initImageViews = new ObservableInt();
        //上拉加载完成
        public ObservableInt pageSelectedCommand = new ObservableInt();
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.order_info));

    }

    //给ViewPager添加ObservableList
    public ObservableList<AccountViewPagerItemViewModel> items = new ObservableArrayList<>();
    //给ViewPager添加ItemBinding
    public ItemBinding<AccountViewPagerItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_account_viewpager);

    //ViewPager切换监听
    public BindingCommand<Integer> onPageSelectedCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer index) {
            uc.pageSelectedCommand.set(index);
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

                            

                            initViewPager(entity.get().getAdId());
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
        quantityText.set(entity.get().getQuantity().stripTrailingZeros().toString());
        goodsMoneyText.set(entity.get().getGoodsMoney()==null ? "0.00": entity.get().getGoodsMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        totalMoneyText.set(entity.get().getTotalMoney()==null ? "0.00": entity.get().getTotalMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        commissionRateText.set(entity.get().getCommissionRate()==null? "0" : entity.get().getCommissionRate().stripTrailingZeros().toPlainString());
        getQuantityText.set(entity.get().getGetQuantity()==null? "0" : entity.get().getGetQuantity().stripTrailingZeros().toPlainString());

        contactNumber.set("+"+entity.get().getSellerNationCode() + entity.get().getSellerMobile());


        String totalMoneyStr = totalMoneyText.get()+" " +entity.get().getCurrencyName();

        textBuyerMind.set(getApplication().getString(R.string.text_buyer_mind,totalMoneyStr));


        if(entity.get().getOrderStatus() == Order.ORDER_STATUS_UNPAID){

            rightButtonText.set(getApplication().getString(R.string.buyer_order_status_unpaid));

        }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_PREPAID){
            rightButtonText.set(getApplication().getString(R.string.buyer_status_prepaid));

        }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_COMPLETE){
            rightButtonText.set(getApplication().getString(R.string.order_status_complete));
        }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_CANCEL){
            rightButtonText.set(getApplication().getString(R.string.order_status_cancel));
        }else if(entity.get().getOrderStatus() == Order.ORDER_STATUS_TIMEOUT){
            rightButtonText.set(getApplication().getString(R.string.order_status_timeout));
        }


        createTimeText.set(DateUtils.toYmdHms(entity.get().getCreateTime()));
    }

    private void initViewPager(Long adId) {

        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .getAdPaymentMode(adId)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<UserPaymentMode>>>() {
                    @Override
                    public void accept(BaseResponse<List<UserPaymentMode>> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            for(UserPaymentMode paymentMode : response.getBody()){
                                AccountViewPagerItemViewModel itemViewModel = new AccountViewPagerItemViewModel(BuyerOrderInfoViewModel.this, paymentMode);
                                items.add(itemViewModel);


                            }
                            uc.initImageViews.set(response.getBody().size());
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


    public BindingCommand rightButtonClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startContainerActivity(ComplainFragment.class.getCanonicalName());
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


}
