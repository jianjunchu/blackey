package org.blackey.ui.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;

public abstract class BlackeyBaseFragment<V extends ViewDataBinding, VM extends BaseViewModel>  extends BaseFragment<V,VM> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册sdk的event用于接收各种event事件
    }


}
