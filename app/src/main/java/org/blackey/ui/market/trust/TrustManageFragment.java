package org.blackey.ui.market.trust;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketTrustManageBinding;
import org.blackey.ui.base.adapter.BaseFragmentPagerAdapter;
import org.blackey.ui.market.my.MyAdsListFragment;

import java.util.ArrayList;
import java.util.List;

import org.blackey.ui.base.BlackeyBaseFragment;

public class TrustManageFragment extends BlackeyBaseFragment<FragmentMarketTrustManageBinding,TrustManageViewModel> {

    private List<Fragment> mFragments;
    private List<String> titlePager;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_trust_manage;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        viewModel.initToolbar();
        mFragments = pagerFragment();
        titlePager = pagerTitleString();

        //设置Adapter
        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, titlePager);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
    }

    protected List<Fragment> pagerFragment() {
        List<Fragment> list = new ArrayList<>();

        list.add(new MyAdsListFragment(0,getActivity()));
        list.add(new MyAdsListFragment(1,getActivity()));

        return list;
    }

    protected List<String> pagerTitleString() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.mein_vertrauen));
        list.add(getString(R.string.trust_me));
        return list;
    }
}
