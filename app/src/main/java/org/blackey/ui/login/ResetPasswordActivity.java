package org.blackey.ui.login;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityResetPasswordBinding;
import org.blackey.ui.base.BlackeyBaseActivity;

public class ResetPasswordActivity extends BlackeyBaseActivity<ActivityResetPasswordBinding,ResetPasswordViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_reset_password;
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
