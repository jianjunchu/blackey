package org.blackey.ui.choose.paymentMode;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityChooseCurrencyListBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

/**
 * 货币字典选择器
 */
public class ChoosePaymentModeListActivity extends BlackeyBaseActivity<ActivityChooseCurrencyListBinding, ChoosePaymentModeListViewModel> {


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_payment_mode_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.activity = this;
        viewModel.initToolbar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ChoosePaymentModeListViewModel.selected.clear();
        viewModel.requestNetWork();
    }

    @Override
    public void initParam() {
        super.initParam();
    }
}
