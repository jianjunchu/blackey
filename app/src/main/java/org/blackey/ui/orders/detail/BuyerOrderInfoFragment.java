package org.blackey.ui.orders.detail;

import android.arch.lifecycle.Observer;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentBuyerOrderInfoBinding;
import org.blackey.entity.PaymentMode;
import org.blackey.ui.base.BlackeyBaseFragment;
import org.blackey.ui.orders.detail.adapter.AccountViewPagerBindingAdapter;

import me.goldze.mvvmhabit.utils.ToastUtils;

public class BuyerOrderInfoFragment extends BlackeyBaseFragment<FragmentBuyerOrderInfoBinding, BuyerOrderInfoViewModel> {

    public static final String ORDER_ID = "orderId";

    private ImageView imageView;
    private ImageView[] imageViews;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_buyer_order_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.activity = getActivity();
        Bundle mBundle = getArguments();
        Long orderId = mBundle.getLong(ORDER_ID);
        viewModel.initToolbar();
        viewModel.requestNetWork(orderId);
        //给ViewPager设置adapter
        binding.setAdapter(new AccountViewPagerBindingAdapter());



    }








    @Override
    public void initViewObservable() {
        viewModel.itemClickEvent.observe(this, new Observer<PaymentMode>() {
            @Override
            public void onChanged(@Nullable PaymentMode text) {
                ToastUtils.showShort("position：" + text);
            }
        });

        viewModel.uc.initImageViews.addOnPropertyChangedCallback(new ObservableInt.OnPropertyChangedCallback() {

            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableInt observableInt = (ObservableInt) sender;
                //有多少张图就有多少个点点
                imageViews = new ImageView[observableInt.get()];
                for(int i =0;i<imageViews.length;i++) {
                    imageView = new ImageView(getContext());
                    LinearLayout.LayoutParams  ll =  new LinearLayout.LayoutParams(20, 20);
                    ll.setMargins(16,0,16,0);
                    imageView.setLayoutParams(ll);
                    imageViews[i] = imageView;

                    //默认第一张图显示为选中状态
                    if (i == 0) {
                        imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
                    } else {
                        imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    }

                    binding.viewGroup.addView(imageViews[i]);
                }
            }
        });

        viewModel.uc.pageSelectedCommand.addOnPropertyChangedCallback(new ObservableInt.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableInt observableInt = (ObservableInt) sender;
                for(int i=0;i<imageViews.length;i++){
                    imageViews[observableInt.get()].setBackgroundResource(R.drawable.page_indicator_focused);
                    if (observableInt.get() != i) {
                        imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                    }
                }

            }
        });
    }
}
