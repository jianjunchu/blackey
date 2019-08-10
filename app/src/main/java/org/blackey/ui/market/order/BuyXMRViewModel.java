package org.blackey.ui.market.order;

import android.app.Application;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.model.WalletManager;
import com.m2049r.xmrwallet.util.Helper;
import com.vondear.rxtool.view.RxToast;

import org.blackey.R;
import org.blackey.entity.Advertise;
import org.blackey.entity.Order;
import org.blackey.model.request.OrderRequest;
import org.blackey.service.AdvertiseApiService;
import org.blackey.service.OrderApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.orders.detail.OrdersProcessingDetailFragment;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * 市场
 */
public class BuyXMRViewModel extends ToolbarViewModel {
    public ObservableField<Advertise> entity = new ObservableField<>();
    public Context context;

    public ObservableField<String> purchaseAmountText = new ObservableField<>();
    public ObservableField<String> purchaseQuantityText = new ObservableField<>();

    public ObservableField<String> priceText = new ObservableField<>();
    public ObservableField<String> totalQuantityText = new ObservableField<>();
    public ObservableField<String> minimumOrderQuantityText = new ObservableField<>();


    public ObservableField<String> leaveWordText = new ObservableField<>();

    public ObservableField<String> userCountTradesText = new ObservableField<>();

    public ObservableField<String> userCountPraiseText = new ObservableField<>();

    public ObservableField<String> minLimitText = new ObservableField<>();
    public ObservableField<String> maxLimitText = new ObservableField<>();

    public ObservableField<Integer> paymentModeId1Icon = new ObservableField<>();
    public ObservableField<Integer> paymentModeId2Icon = new ObservableField<>();
    public ObservableField<Integer> paymentModeId3Icon = new ObservableField<>();

    public ObservableField<Integer> paymentModeId1Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeId2Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeId3Visibility = new ObservableField<>(View.GONE);

    public ObservableField<String> limitErrorText = new ObservableField<>("");


    public BuyXMRViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.buy_xmr));
        setRightText(getApplication().getString(R.string.help));
        setRightTextVisible(View.VISIBLE);
        setRightIconVisible(View.GONE);

    }

    //登录按钮的点击事件
    public BindingCommand buyNowOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            limitErrorText.set("");

            if(TextUtils.isEmpty(purchaseQuantityText.get())){
                RxToast.showToast(R.string.hint_purchase_quantity);
                return;
            }

            if(TextUtils.isEmpty(purchaseAmountText.get())){
                RxToast.showToast(R.string.hint_purchase_quantity);
                return;
            }



            BigDecimal purchaseQuantity = new BigDecimal(purchaseQuantityText.get());
            //当剩余数量 > 最小交易数量
            if(entity.get().getInventory().compareTo(entity.get().getMinLimit()) >=0 ){
                if(purchaseQuantity.compareTo(entity.get().getMinLimit()) < 0){
                    limitErrorText.set(getApplication().getString(R.string.minimum_order_quantity) +": " + entity.get().getMinLimit().stripTrailingZeros().toPlainString());

                    return;
                }
            }else{ //当剩余数量 < 最小交易数量 则剩余数量为最小交易数量
                if(purchaseQuantity.compareTo(entity.get().getInventory()) < 0){
                    limitErrorText.set(getApplication().getString(R.string.minimum_order_quantity) +": " + entity.get().getInventory().stripTrailingZeros().toPlainString());
                    return;
                }
            }

            if(purchaseQuantity.compareTo(entity.get().getInventory()) > 0){
                limitErrorText.set(getApplication().getString(R.string.error_more_than));
                return;
            }


            final OrderConfirmationDialog dialog = new OrderConfirmationDialog(context);
            dialog.initSpinnerData();
            dialog.getmTvSure().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.promptPassword(context, dialog.getSpinnerData(), false,
                            new Helper.PasswordAction() {
                                @Override
                                public void action(String walletName, String password, boolean fingerprintUsed) {
                                    Wallet wallet = null;
                                    try {
                                        String path = Helper.getWalletFile(context, walletName).getAbsolutePath();
                                        wallet = WalletManager.getInstance().openWallet(path,password);
                                        String address =  wallet.getAddress();
                                        submit(address);
                                    }catch (Exception e){

                                    }finally {
                                        if(wallet!=null){
                                            wallet.close();
                                        }

                                    }




                                }
                            });

                    if(true){
                        return;
                    }


                }


            });
            dialog.getmTvCancel().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.getmBuyingPrice().setText(priceText.get());
            dialog.getmQurchaseQuantity().setText(purchaseQuantityText.get());
            dialog.getmPaymentAmount().setText(purchaseAmountText.get());
            dialog.show();
        }
    });

    private void submit(String address) {

        OrderRequest request = new OrderRequest();
        request.setAdId(entity.get().getAdId());
        request.setCurrencyId(entity.get().getCurrencyId());
        request.setTotalMoney(new BigDecimal(purchaseAmountText.get()));
        request.setQuantity(new BigDecimal(purchaseQuantityText.get()));
        request.setShippingAddress(address);
        request.setGoodsMoney(entity.get().getPrice());

        RetrofitClient.getInstance().create(OrderApiService.class)
                .create(request)
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
                        dismissDialog();
                        //请求成功
                        if(response.isOk()){

                            RxToast.showToast(R.string.create_order_succeed);

                            Bundle mBundle = new Bundle();
                            mBundle.putLong(OrdersProcessingDetailFragment.ORDER_ID, response.getBody().getOrderId());
                            mBundle.putInt(OrdersProcessingDetailFragment.ORDER_TYPE, Order.ORDER_TYPE_BUY);
                            startContainerActivity(OrdersProcessingDetailFragment.class.getCanonicalName(),mBundle);
                            finish();
                        }
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
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }


    public void initData() {
        leaveWordText.set(getApplication().getString(R.string.seller_message)+entity.get().getLeaveWord());
        priceText.set(entity.get().getPrice().stripTrailingZeros().toPlainString());
        totalQuantityText.set(entity.get().getInventory().stripTrailingZeros().toPlainString());
        minimumOrderQuantityText.set(entity.get().getMinLimit().stripTrailingZeros().toPlainString());

        userCountTradesText.set(entity.get().getUserCountTrades()+"");
        userCountPraiseText.set(entity.get().getUserCountPraise()+"");
        if(entity.get().getPaymentModeId1()!=null && !TextUtils.isEmpty(entity.get().getPaymentIcon1())){
            paymentModeId1Visibility.set(View.VISIBLE);
            paymentModeId1Icon.set(ResourcesUtils.getPaymentModeIcon(getApplication(),entity.get().getPaymentIcon1()));
        }
        if(entity.get().getPaymentModeId2()!=null && !TextUtils.isEmpty(entity.get().getPaymentIcon2())){
            paymentModeId2Visibility.set(View.VISIBLE);
            paymentModeId2Icon.set(ResourcesUtils.getPaymentModeIcon(getApplication(),entity.get().getPaymentIcon2()));
        }
        if(entity.get().getPaymentModeId3()!=null && !TextUtils.isEmpty(entity.get().getPaymentIcon3())){
            paymentModeId3Visibility.set(View.VISIBLE);
            paymentModeId3Icon.set(ResourcesUtils.getPaymentModeIcon(getApplication(),entity.get().getPaymentIcon3()));
        }

        BigDecimal price = entity.get().getPrice();
        BigDecimal min = price.multiply(entity.get().getMinLimit());
        minLimitText.set(min.stripTrailingZeros().toPlainString());

        BigDecimal max = price.multiply(entity.get().getTotalInventory());
        maxLimitText.set(max.stripTrailingZeros().toPlainString());
    }

    public void changedPurchaseAmountText(String val) {
        if(!TextUtils.isEmpty(val)){
            BigDecimal decimal = new BigDecimal(val);
            BigDecimal price = entity.get().getPrice();
            purchaseAmountText.set(decimal.multiply(price).stripTrailingZeros().toPlainString());
        }


    }

    public void changedPurchaseQuantityText(String val) {
        if(!TextUtils.isEmpty(val)){
            BigDecimal decimal = new BigDecimal(val);
            BigDecimal price = entity.get().getPrice();
            purchaseQuantityText.set(decimal.divide(price, 9, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        }
    }

    public void requestNetWork(Long adId) {
        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .get(adId)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Advertise>>() {
                    @Override
                    public void accept(BaseResponse<Advertise> response) throws Exception {
                        dismissDialog();
                        //请求成功
                        if(response.isOk()){
                            entity.set(response.getBody());
                            initData();
                        }
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
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }
}
