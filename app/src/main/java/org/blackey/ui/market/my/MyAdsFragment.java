package org.blackey.ui.market.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketMyAdsBinding;
import org.blackey.ui.base.adapter.BaseFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import org.blackey.ui.base.BlackeyBaseFragment;

public class MyAdsFragment extends BlackeyBaseFragment<FragmentMarketMyAdsBinding,MyAdsViewModel> {


    private List<Fragment> mFragments;
    private List<String> titlePager;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_my_ads;
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
        list.add(getString(R.string.underway));
        list.add(getString(R.string.sold_out));
        return list;
    }
}
