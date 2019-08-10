package org.blackey.ui.orders.list;

import android.databinding.ObservableField;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.vondear.rxtool.RxTool;

import org.blackey.R;
import org.blackey.entity.Order;
import org.blackey.ui.orders.detail.OrdersProcessingDetailFragment;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class OrdersListItemViewModel extends MultiItemViewModel<OrdersListViewModel> {

    Timer timer = new Timer();

    public ObservableField<String> mmfText = new ObservableField<>();

    public ObservableField<Integer> itemBackground = new ObservableField<>();
    public ObservableField<Order> entity = new ObservableField<>();
    public ObservableField<String> nameText = new ObservableField<>();
    public ObservableField<String> orderTypeText = new ObservableField<>();
    public ObservableField<String> orderStatusText = new ObservableField<>();

    public ObservableField<String> portrait = new ObservableField<>();
    public ObservableField<Integer> placeholderRes = new ObservableField<>();

    public ObservableField<String> goodsMoneyText = new ObservableField<>();
    public ObservableField<String> quantityText = new ObservableField<>();
    public ObservableField<String> totalMoneyText = new ObservableField<>();


    public OrdersListItemViewModel(@NonNull OrdersListViewModel viewModel) {
        super(viewModel);
    }

    public OrdersListItemViewModel(@NonNull final OrdersListViewModel viewModel, final Order entity) {
        super(viewModel);
        this.entity.set(entity);
        multiType = R.layout.item_order_list;
        placeholderRes.set(R.mipmap.icon_person);

        quantityText.set(entity.getQuantity().stripTrailingZeros().toString());
        goodsMoneyText.set(entity.getGoodsMoney()==null ? "0.00": entity.getGoodsMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        totalMoneyText.set(entity.getTotalMoney()==null ? "0.00": entity.getTotalMoney().setScale(2, BigDecimal.ROUND_HALF_UP).toString());



        if(entity.getOrderType() == Order.ORDER_TYPE_BUY){
            mmfText.set(RxTool.getContext().getString(R.string.ordrt_seller));
            orderTypeText.set(viewModel.getApplication().getString(R.string.order_type_buy));
            nameText.set(entity.getSellerNickname());
            portrait.set(entity.getSellerPortrait());
            itemBackground.set(Color.parseColor("#FF3E802F"));
        }else if(entity.getOrderType() == Order.ORDER_TYPE_SELL) {
            orderTypeText.set(viewModel.getApplication().getString(R.string.order_type_sell));
            mmfText.set(RxTool.getContext().getString(R.string.order_buyer));
            nameText.set(entity.getBuyerNickname());
            portrait.set(entity.getBuyerPortrait());
            itemBackground.set(Color.parseColor("#FFB23424"));
        }



        switch (entity.getOrderStatus()){
            case Order.ORDER_STATUS_UNPAID :

                timer.schedule(new TimerTask() {
                    public void run() {

                        long between = (entity.getCreateTime().getTime()+30*60*1000) - System.currentTimeMillis()  ;

                        if(between < 1){
                            timer.cancel();
                            orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_timeout));
                        }

                        long min = ((between / (60 * 1000)));

                        String minStr = min > 9 ? ""+min : "0"+min;

                        long s = (between / 1000 - min * 60);
                        String sStr = s > 9 ? ""+s : "0"+s;


                        orderStatusText.set(viewModel.getApplication().getString(R.string.order_status_unpaid) +" "+minStr+":"+sStr);
                    }
                }, 0 , 1000);


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
            if(entity.get().getIsClosed() == 0){
                Bundle mBundle = new Bundle();
                mBundle.putLong(OrdersProcessingDetailFragment.ORDER_ID, entity.get().getOrderId());
                mBundle.putInt(OrdersProcessingDetailFragment.ORDER_TYPE, entity.get().getOrderType());
                viewModel.startContainerActivity(OrdersProcessingDetailFragment.class.getCanonicalName(),mBundle);
            }

        }
    });


}
