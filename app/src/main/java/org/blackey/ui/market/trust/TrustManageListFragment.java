package org.blackey.ui.market.trust;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketTrustManageListBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class TrustManageListFragment extends BlackeyBaseFragment<FragmentMarketTrustManageListBinding,TrustManageListViewModel> {

    private int index;

    public TrustManageListFragment() {
    }

    @SuppressLint("ValidFragment")
    public TrustManageListFragment(int index) {
        this();
        this.index = index;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_trust_manage_list;
    }



    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.swipeRefresh = binding.swipeRefresh;


        binding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));

        viewModel.requestNetWork(index);
    }
}
