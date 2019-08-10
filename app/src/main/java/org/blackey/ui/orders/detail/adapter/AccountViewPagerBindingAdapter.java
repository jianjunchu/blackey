package org.blackey.ui.orders.detail.adapter;

import android.databinding.ViewDataBinding;
import android.view.ViewGroup;

import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

public class AccountViewPagerBindingAdapter extends BindingViewPagerAdapter<AccountViewPagerItemViewModel> {

    @Override
    public void onBindBinding(final ViewDataBinding binding, int variableId, int layoutRes, final int position, AccountViewPagerItemViewModel item) {
        super.onBindBinding(binding, variableId, layoutRes, position, item);
        //这里可以强转成ViewPagerItemViewModel对应的ViewDataBinding，

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
