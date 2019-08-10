package org.blackey.ui.orders.list;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentOrdersListBinding;
import org.blackey.ui.base.BlackeyBaseFragment;
import com.sinothk.switchTabView.style1.segement.SegmentView;

import java.util.Arrays;

/**
 * 订单列表
 */
public class OrdersListFragment extends BlackeyBaseFragment<FragmentOrdersListBinding, OrdersListViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_orders_list;
    }

    @Override
    public int initVariableId() {

        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.swipeRefresh = binding.swipeRefresh;

        binding.include.svSegement0.setTitles(Arrays.asList(getString(R.string.processing), getString(R.string.finished)));


        binding.swipeRefresh.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));
    }


    @Override
    public void onResume() {
        super.onResume();

        viewModel.finishRefreshing(0);
    }

    @Override
    public void initViewObservable() {

        binding.include.svSegement0.setOnSelectedListener(new SegmentView.OnSelectedListener() {
            @Override
            public void onSelected(int index) {
                switch (index){
                    case 0:
                        viewModel.finishRefreshing(0);
                        break;
                    case 1:
                        viewModel.finishRefreshing(1);
                        break;
                }
            }
        });

        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //结束刷新
                binding.swipeRefresh.setRefreshing(false);
            }
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadmore.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //结束刷新
                viewModel.observableList.remove(viewModel.footersItem);
            }
        });
    }


}
