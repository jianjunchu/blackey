package org.blackey.ui.mine.myorders;

import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.entity.Order;
import org.blackey.ui.orders.detail.OrdersProcessingDetailFragment;

import java.math.BigDecimal;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class MyOrderListItemViewModel extends MultiItemViewModel<MyOrdersViewModel> {

    public ObservableField<Order> entity = new ObservableField<>();
    public ObservableField<String> nameText = new ObservableField<>();
    public ObservableField<String> orderStatusText = new ObservableField<>();

    public ObservableField<String> portrait = new ObservableField<>();
    public ObservableField<Integer> placeholderRes = new ObservableField<>();

    public ObservableField<String> goodsMoneyText = new ObservableField<>();
    public ObservableField<String> quantityText = new ObservableField<>();
    public ObservableField<String> totalMoneyText = new ObservableField<>();


    public MyOrderListItemViewModel(@NonNull MyOrdersViewModel viewModel) {
        super(viewModel);
    }

    public MyOrderListItemViewModel(@NonNull MyOrdersViewModel viewModel, Order entity) {
        super(viewModel);
        this.entity.set(entity);
        multiType = R.layout.item_mine_my_order_list;
        placeholderRes.set(R.mipmap.icon_person);

        quantityText.set(entity.getQuantity().stripTrailingZeros().toString());
        goodsMoneyText.set(entity.getGoodsMoney()==null ? "0.00": entity.getGoodsMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        totalMoneyText.set(entity.getTotalMoney()==null ? "0.00": entity.getTotalMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());



        if(entity.getOrderType() == Order.ORDER_TYPE_BUY){
            nameText.set(entity.getSellerNickname());
            portrait.set(entity.getSellerPortrait());
        }else if(entity.getOrderType() == Order.ORDER_TYPE_SELL) {
            nameText.set(entity.getBuyerNickname());
            portrait.set(entity.getBuyerPortrait());

        }else{
            nameText.set(entity.getBuyerNickname());
            portrait.set(entity.getBuyerPortrait());
        }




        switch (entity.getOrderStatus()){
            case Order.ORDER_STATUS_UNPAID :
                orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_unpaid));
                break;
            case Order.ORDER_STATUS_PREPAID :
                orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_prepaid));
                break;
            case Order.ORDER_STATUS_COMPLETE :
                orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_complete));
                break;
            case Order.ORDER_STATUS_CANCEL :
                orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_cancel));
                break;
            case Order.ORDER_STATUS_TIMEOUT :
                orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_timeout));
                break;
        }

    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            if(entity.get().getOrderStatus() == Order.ORDER_STATUS_UNPAID || entity.get().getOrderStatus() == Order.ORDER_STATUS_PREPAID){
                Bundle mBundle = new Bundle();
                mBundle.putLong(OrdersProcessingDetailFragment.ORDER_ID, entity.get().getOrderId());
                viewModel.startContainerActivity(OrdersProcessingDetailFragment.class.getCanonicalName(),mBundle);
            }
        }
    });
}
