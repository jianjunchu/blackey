package me.goldze.mvvmhabit.base;

import android.support.annotation.NonNull;

/**
 * Create Author：goldze
 * Create Date：2019/01/25
 * Description：RecycleView多布局ItemViewModel是封装
 */

public class MultiItemViewModel<VM extends BaseViewModel> extends ItemViewModel<VM> {
    protected int multiType;

    public int getItemType() {
        return multiType;
    }



    public MultiItemViewModel(@NonNull VM viewModel) {
        super(viewModel);
    }
}
