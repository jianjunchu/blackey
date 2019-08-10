package org.blackey.ui.login;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityLoginBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

public class LoginActivity extends BlackeyBaseActivity<ActivityLoginBinding, LoginViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        viewModel.elMobile = binding.elMobile;
        viewModel.elPasswd = binding.elPassword;
        viewModel.request.setNationCode("86");
        viewModel.initToolbar();
        viewModel.initMessenger();
    }

}
