package org.blackey.ui.market.my;

import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.Advertise;
import org.blackey.service.AdvertiseApiService;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.math.BigDecimal;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;

public class MyOutAdListItemViewModel extends MultiItemViewModel<MyAdsListViewModel> {


    public ObservableField<Advertise> entity = new ObservableField<>();

    public ObservableField<String> priceText = new ObservableField<>();

    public ObservableField<String> userCountTradesText = new ObservableField<>();

    public ObservableField<String> totalInventory = new ObservableField<>();

    public ObservableField<String> minLimitText = new ObservableField<>();
    public ObservableField<String> maxLimitText = new ObservableField<>();

    public ObservableField<Integer> paymentModeId1Icon = new ObservableField<>();
    public ObservableField<Integer> paymentModeId2Icon = new ObservableField<>();
    public ObservableField<Integer> paymentModeId3Icon = new ObservableField<>();

    public ObservableField<Integer> paymentModeId1Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeId2Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeId3Visibility = new ObservableField<>(View.GONE);

    public ObservableField<Integer> placeholderRes = new ObservableField<>();

    public MyOutAdListItemViewModel(@NonNull MyAdsListViewModel viewModel) {
        super(viewModel);
    }

    public MyOutAdListItemViewModel(@NonNull MyAdsListViewModel viewModel, Advertise entity) {
        super(viewModel);
        multiType = R.layout.item_market_my_out_ad_list;
        this.entity.set(entity);

        placeholderRes.set(R.mipmap.icon_person);

        priceText.set(entity.getPrice().stripTrailingZeros().toPlainString());
        userCountTradesText.set(entity.getUserCountTrades()+"");
        totalInventory.set(entity.getTotalInventory()+"");
        if(entity.getPaymentModeId1()!=null && !TextUtils.isEmpty(entity.getPaymentIcon1())){
            paymentModeId1Visibility.set(View.VISIBLE);
            paymentModeId1Icon.set(ResourcesUtils.getPaymentModeIcon(viewModel.getApplication(),entity.getPaymentIcon1()));
        }
        if(entity.getPaymentModeId2()!=null && !TextUtils.isEmpty(entity.getPaymentIcon2())){
            paymentModeId2Visibility.set(View.VISIBLE);
            paymentModeId2Icon.set(ResourcesUtils.getPaymentModeIcon(viewModel.getApplication(),entity.getPaymentIcon2()));
        }
        if(entity.getPaymentModeId3()!=null && !TextUtils.isEmpty(entity.getPaymentIcon3())){
            paymentModeId3Visibility.set(View.VISIBLE);
            paymentModeId3Icon.set(ResourcesUtils.getPaymentModeIcon(viewModel.getApplication(),entity.getPaymentIcon3()));
        }

        BigDecimal price = entity.getPrice();
        BigDecimal min = price.multiply(entity.getMinLimit());
        minLimitText.set(min.stripTrailingZeros().toPlainString());

        BigDecimal max = price.multiply(entity.getTotalInventory());
        maxLimitText.set(max.stripTrailingZeros().toPlainString());

    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
        }
    });

    //条目的点击事件
    public BindingCommand deleteClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            showNormalDialog();
        }
    });

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(viewModel.activity);
        normalDialog.setTitle(viewModel.getApplication().getString(R.string.delete_confirmation));
        normalDialog.setMessage(viewModel.getApplication().getString(R.string.hint_delete_ad));
        normalDialog.setPositiveButton(viewModel.getApplication().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestDelete();
                    }
                });
        normalDialog.setNegativeButton(viewModel.getApplication().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void requestDelete() {
        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .delete(entity.get().getAdId())
                .compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        viewModel.showDialog();
                    }
                })
                .subscribe(new Consumer<BaseResponse<Integer>>() {
                    @Override
                    public void accept(BaseResponse<Integer> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            viewModel.finishRefreshing();
                        }
                        viewModel.dismissDialog();
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        viewModel.dismissDialog();
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                        //关闭对话框
                        viewModel.dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }

}
