package org.blackey.ui.choose.bank;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityChooseBankListBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

public class ChooseBankListActivity extends BlackeyBaseActivity<ActivityChooseBankListBinding, ChooseBankListViewModel> {


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_bank_list;
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
