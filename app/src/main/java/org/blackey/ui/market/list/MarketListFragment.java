package org.blackey.ui.market.list;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketListBinding;
import org.blackey.entity.Currency;
import org.blackey.ui.base.BlackeyBaseFragment;

/**
 * 市场
 */
public class MarketListFragment extends BlackeyBaseFragment<FragmentMarketListBinding,MarketListViewModel> {


    private Currency currency;

    private int index;

    public MarketListFragment() {
    }

    @SuppressLint("ValidFragment")
    public MarketListFragment(int index,Currency currency) {
        this.currency = currency;
        this.index = index;
    }



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.currency = currency;

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.finishRefreshing();
    }
}
