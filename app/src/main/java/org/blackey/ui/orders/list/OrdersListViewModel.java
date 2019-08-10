package org.blackey.ui.orders.list;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.Order;
import org.blackey.service.OrderApiService;
import org.blackey.ui.base.viewmodel.MultiRecycleFootersItemViewModel;
import org.blackey.ui.login.LoginActivity;
import org.blackey.ui.orders.toolbar.OrdersToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.PageResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

/**
 * 订单列表
 */
public class OrdersListViewModel extends OrdersToolbarViewModel {

    public int page = 1;

    public ObservableField<Integer> loginVisibility = new ObservableField<>(View.GONE);

    public SwipeRefreshLayout swipeRefresh;
    private int isClosed;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public static class UIChangeObservable {
        //下拉刷新完成
        public ObservableBoolean finishRefreshing = new ObservableBoolean(false);
        //上拉加载完成
        public ObservableBoolean finishLoadmore = new ObservableBoolean(false);
    }

    public OrdersListViewModel(@NonNull Application application) {
        super(application);
    }

    MultiRecycleFootersItemViewModel footersItem = new MultiRecycleFootersItemViewModel(OrdersListViewModel.this);

    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();

    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局

            if (R.layout.item_order_list == item.getItemType()) {
                itemBinding.set(BR.viewModel, R.layout.item_order_list);
            } else if (R.layout.list_foot_loading == item.getItemType()) {
                //设置左布局
                itemBinding.set(BR.viewModel, R.layout.list_foot_loading);
            }
        }
    });
    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法
    public final BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();


    public void requestNetWork() {

        RetrofitClient.getInstance().create(OrderApiService.class)
                .myOrderList(page, BlackeyApplication.pageRows,isClosed,0)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<PageResponse<Order>>>() {
                    @Override
                    public void accept(BaseResponse<PageResponse<Order>> response) throws Exception {
                        observableList.remove(footersItem);
                        //请求成功
                        if(response.isOk()){

                            for(Order entity : response.getBody().getList()){
                                OrdersListItemViewModel itemViewModel = new OrdersListItemViewModel(OrdersListViewModel.this, entity);
                                //双向绑定动态添加Item
                                observableList.add(itemViewModel);
                            }
                        }

                        dismissDialog();
                        uc.finishRefreshing.set(!uc.finishRefreshing.get());
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        dismissDialog();
                        observableList.remove(footersItem);
                        uc.finishRefreshing.set(!uc.finishRefreshing.get());
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dismissDialog();
                        observableList.remove(footersItem);
                        uc.finishRefreshing.set(!uc.finishRefreshing.get());
                        //关闭对话框
                        //请求刷新完成收回

                    }
                });


    }

    public void finishRefreshing(int isClosed) {
        if(BlackeyApplication.getCurrent()!=null){
            loginVisibility.set(View.GONE);
            this.isClosed = isClosed;
            page = 1;
            observableList.clear();
            requestNetWork();
        }else{
            loginVisibility.set(View.VISIBLE);
        }


    }

    public void finishLoadmore(){
        page++;
        requestNetWork();
    }


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
                        finishRefreshing(isClosed);

                    }
                }, 10);
            }
        });
    }

    //上拉加载
    public BindingCommand<Integer> onLoadMoreCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer isChecked) {

            if(!observableList.contains(footersItem)){
                //双向绑定动态添加Item
                observableList.add(footersItem);
                finishLoadmore();
            }


        }
    });

    //登录按钮的点击事件
    public BindingCommand loginOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            startActivity(LoginActivity.class);
        }
    });
}
