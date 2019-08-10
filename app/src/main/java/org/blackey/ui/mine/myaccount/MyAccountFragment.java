package org.blackey.ui.mine.myaccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineMyAccountBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class MyAccountFragment extends BlackeyBaseFragment<FragmentMineMyAccountBinding,MyAccountViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_my_account;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.activity = getActivity();
        viewModel.ivGunther = binding.ivGunther;

        viewModel.initToolbar();


    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.finishRefreshing();
    }
}
