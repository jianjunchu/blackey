package org.blackey.ui.mine.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineSecurityCenterBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class SecurityCenterFragment extends BlackeyBaseFragment<FragmentMineSecurityCenterBinding,SecurityCenterViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_security_center;
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
