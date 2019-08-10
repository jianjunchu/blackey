package org.blackey.ui.base.viewmodel;

import android.support.annotation.NonNull;

import org.blackey.R;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.MultiItemViewModel;

public class MultiRecycleFootersItemViewModel<VM extends BaseViewModel> extends MultiItemViewModel<VM> {


    public MultiRecycleFootersItemViewModel(@NonNull VM viewModel) {
        super(viewModel);
        multiType = R.layout.list_foot_loading;
    }


}
