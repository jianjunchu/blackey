package org.blackey.ui.choose.country;

import android.os.Bundle;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityChooseCountryListBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.widget.SideBar;

public class ChooseCountryOrRegionListActivity extends BlackeyBaseActivity<ActivityChooseCountryListBinding, ChooseCountryOrRegionListViewModel> {


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_country_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        Bundle bundle = getIntent().getExtras();
        viewModel.returnToken = bundle.getString(ChooseCountryOrRegionListItemViewModel.TOKEN_CHOOSE_COUNTRY_REFRESH);

        viewModel.setSortListView(binding.sortListView);
        viewModel.initToolbar();
        viewModel.requestNetWork();

        // 设置右侧触摸监听
        binding.sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                viewModel.dealSideBarItemSelected(s);
            }
        });

    }

    @Override
    public void initParam() {
        super.initParam();
    }
}
