package org.blackey.ui.mine.certification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineSelectIdentityCardTypeBinding;

import org.blackey.ui.base.BlackeyBaseFragment;


public class SelectIdentityCardTypeFragment extends BlackeyBaseFragment<FragmentMineSelectIdentityCardTypeBinding, SelectIdentityCardTypeViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_select_identity_card_type;
    }



    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.initToolbar();
        viewModel.initData();
        viewModel.initMessenger();
    }
}
