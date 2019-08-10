package org.blackey.ui.choose.currency;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityChooseCurrencyListBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

/**
 * 货币字典选择器
 */
public class ChooseCurrencyListActivity extends BlackeyBaseActivity<ActivityChooseCurrencyListBinding, ChooseCurrencyListViewModel> {


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_currency_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        viewModel.initToolbar();
        viewModel.requestNetWork();


    }

    @Override
    public void initParam() {
        super.initParam();
    }
}
