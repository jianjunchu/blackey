package org.blackey.ui.market.search;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityMarketSearchBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

public class MarketSearchActivity extends BlackeyBaseActivity<ActivityMarketSearchBinding,MarketSearchViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_market_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        viewModel.initToolbar();
    }
}
