package org.blackey.ui.mine.myorders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineMyOrderslBinding;
import com.sinothk.switchTabView.style1.segement.SegmentView;

import java.util.Arrays;

import org.blackey.ui.base.BlackeyBaseFragment;

public class MyOrdersFragment extends BlackeyBaseFragment<FragmentMineMyOrderslBinding,MyOrdersViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_my_ordersl;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.swipeRefresh = binding.swipeRefresh;

        binding.include.svSegement0.setTitles(Arrays.asList(getString(R.string.i_sell), getString(R.string.i_buy)));

        binding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));

        viewModel.finishRefreshing(1);

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        binding.include.svSegement0.setOnSelectedListener(new SegmentView.OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                switch (index){
                    case 0:
                        viewModel.finishRefreshing(1);
                        break;
                    case 1:
                        viewModel.finishRefreshing(2);
                        break;
                }
            }
        });
    }
}
