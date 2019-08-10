package org.blackey.ui.login;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityRegisterBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

public class RegisterActivity extends BlackeyBaseActivity<ActivityRegisterBinding,RegisterViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_register;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.request.setNationCode("86");
        viewModel.initToolbar();
        viewModel.initMessenger();
    }
}
