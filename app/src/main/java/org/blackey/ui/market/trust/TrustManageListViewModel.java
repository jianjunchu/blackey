package org.blackey.ui.market.trust;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.ui.base.viewmodel.MultiRecycleFootersItemViewModel;
import com.vondear.rxtool.view.RxToast;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class TrustManageListViewModel extends BaseViewModel {


    public TrustManageListViewModel(@NonNull Application application) {
        super(application);
    }

    public SwipeRefreshLayout swipeRefresh;

    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();

    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局

            if (R.layout.item_market_trust_manage_list == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_market_trust_manage_list);
            } else if (R.layout.list_foot_loading == item.getItemType()) {
                //设置左布局
                itemBinding.set(BR.viewModel, R.layout.list_foot_loading);
            }
        }
    });

    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法
    public final BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();


    /**
     * 网络请求方法，在ViewModel中调用，Retrofit+RxJava充当Repository，即可视为Model层
     */
    public void requestNetWork(int index) {
        for(int i = 0 ; i< (index+1)*10 ; i++){
            TrustManageListItemViewModel itemViewModel = new TrustManageListItemViewModel(TrustManageListViewModel.this, "");
            //双向绑定动态添加Item
            observableList.add(itemViewModel);
        }
    }

    //上拉加载
    public BindingCommand<Integer> onLoadMoreCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer isChecked) {


            if(isLoadMoreing()){
                return;
            }else{
                setLoadMoreing(true);
                MultiRecycleFootersItemViewModel footersItem = new MultiRecycleFootersItemViewModel(TrustManageListViewModel.this);
                //双向绑定动态添加Item
                observableList.add(footersItem);
            }


        }
    });

    /**
     * 刷新事件
     */
    public BindingCommand swipeOnRefresh() {
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RxToast.success("1");


                    }
                }, 10);
            }
        });
    }
}
