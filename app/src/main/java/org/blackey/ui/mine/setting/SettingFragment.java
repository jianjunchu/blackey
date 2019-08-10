package org.blackey.ui.mine.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineSettingBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class SettingFragment extends BlackeyBaseFragment<FragmentMineSettingBinding,SettingViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_setting;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.initToolbar();
        viewModel.initData();
    }


}
