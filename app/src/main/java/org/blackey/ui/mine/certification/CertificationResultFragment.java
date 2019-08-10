package org.blackey.ui.mine.certification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineCertificationResultBinding;

import org.blackey.ui.base.BlackeyBaseFragment;

public class CertificationResultFragment extends BlackeyBaseFragment<FragmentMineCertificationResultBinding,CertificationResultViewModel> {


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_certification_result;
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
