package org.blackey.ui.market.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketMyadsListBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class MyAdsListFragment extends BlackeyBaseFragment<FragmentMarketMyadsListBinding,MyAdsListViewModel> {

    private int status;

    private Activity activity;

    public MyAdsListFragment() {
    }



    @SuppressLint("ValidFragment")
    public MyAdsListFragment(int status, Activity activity) {
        super();
        this.status = status;
        this.activity = activity;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_myads_list;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.activity = activity;
        viewModel.status = status;

        viewModel.finishRefreshing();
    }
}
