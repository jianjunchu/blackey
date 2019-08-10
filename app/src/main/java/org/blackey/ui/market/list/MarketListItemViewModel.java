package org.blackey.ui.market.list;

import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.Advertise;
import org.blackey.ui.login.LoginActivity;
import org.blackey.ui.market.order.BuyXMRFragment;
import org.blackey.utils.ResourcesUtils;

import java.math.BigDecimal;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

/**
 *
 */

public class MarketListItemViewModel extends MultiItemViewModel<MarketListViewModel> {

    public ObservableField<Advertise> entity = new ObservableField<>();

    public ObservableField<String> priceText = new ObservableField<>();
    public ObservableField<String> inventoryText = new ObservableField<>();
    public ObservableField<String> minimumPurchaseRequirementsText = new ObservableField<>();

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

    public ObservableField<Integer> placeholderRes = new ObservableField<>();


    public MarketListItemViewModel(@NonNull MarketListViewModel viewModel) {
        super(viewModel);
    }

    public MarketListItemViewModel(@NonNull MarketListViewModel viewModel, Advertise entity) {
        super(viewModel);
        multiType = R.layout.item_market_list;
        placeholderRes.set(R.mipmap.icon_person);
        this.entity.set(entity);
        priceText.set(entity.getPrice().stripTrailingZeros().toPlainString());
        inventoryText.set(entity.getInventory().stripTrailingZeros().toPlainString());


        userCountTradesText.set(entity.getUserCountTrades()+"");
        userCountPraiseText.set(entity.getUserCountPraise()+"");
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
        BigDecimal min = price.multiply(entity.getMinLimit()).setScale(2,BigDecimal.ROUND_HALF_UP);

        minLimitText.set(min.toString());

        BigDecimal max = price.multiply(entity.getInventory()).setScale(2,BigDecimal.ROUND_HALF_UP);
        maxLimitText.set(max.toString());



    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            if(BlackeyApplication.getCurrent()==null){
                viewModel.startActivity(LoginActivity.class);
                return;
            }
            Bundle mBundle = new Bundle();
            mBundle.putLong(BuyXMRFragment.ADVERTISE_ID, entity.get().getAdId());
            viewModel.startContainerActivity(BuyXMRFragment.class.getCanonicalName(),mBundle);
        }
    });
}
