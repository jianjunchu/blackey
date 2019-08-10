package org.blackey.ui.market.my;

import android.app.Activity;
import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.Advertise;
import org.blackey.service.AdvertiseApiService;
import org.blackey.ui.base.viewmodel.MultiRecycleFootersItemViewModel;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.PageResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class MyAdsListViewModel extends BaseViewModel {

    public Activity activity;

    public int status;

    private int page = 1;

    MultiRecycleFootersItemViewModel footersItem = new MultiRecycleFootersItemViewModel(MyAdsListViewModel.this);


    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();


    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局

            if (R.layout.item_market_my_out_ad_list == item.getItemType()) {

                itemBinding.set(BR.viewModel, R.layout.item_market_my_out_ad_list);

            } else if (R.layout.item_market_my_proceed_ad_list == item.getItemType()) {
                //设置左布局
                itemBinding.set(BR.viewModel, R.layout.item_market_my_proceed_ad_list);

            }else if (R.layout.list_foot_loading == item.getItemType()) {

                //设置左布局
                itemBinding.set(BR.viewModel, R.layout.list_foot_loading);
            }
        }
    });

    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法
    public final BindingRecyclerViewAdapter<MultiItemViewModel> adapter = new BindingRecyclerViewAdapter<>();

    public MyAdsListViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 网络请求方法，在ViewModel中调用，Retrofit+RxJava充当Repository，即可视为Model层
     */
    public void requestNetWork(int page) {
        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .my_list(page, BlackeyApplication.pageRows,status)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<BaseResponse<PageResponse<Advertise>>>() {
                    @Override
                    public void accept(BaseResponse<PageResponse<Advertise>> response) throws Exception {
                        observableList.remove(footersItem);
                        //请求成功
                        if(response.isOk() && response.getBody().getList()!=null){
                            if(status == 0){
                                for(Advertise advertise : response.getBody().getList()){
                                    MyProceedAdListItemViewModel itemViewModel = new MyProceedAdListItemViewModel(MyAdsListViewModel.this, advertise);
                                    //双向绑定动态添加Item
                                    observableList.add(itemViewModel);
                                }
                            }
                            if(status == 1){
                                for(Advertise advertise : response.getBody().getList()){
                                    MyOutAdListItemViewModel itemViewModel = new MyOutAdListItemViewModel(MyAdsListViewModel.this, advertise);
                                    //双向绑定动态添加Item
                                    observableList.add(itemViewModel);
                                }
                            }


                        }
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        observableList.remove(footersItem);
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        observableList.remove(footersItem);
                        //关闭对话框
                        //请求刷新完成收回

                    }
                });


    }

    public void   finishRefreshing(){
        page  = 1;
        observableList.clear();
        requestNetWork(page);

    }

    public void finishLoadmore(){
        page++;
        requestNetWork(page);
    }
    //上拉加载
    public BindingCommand<Integer> onLoadMoreCommand = new BindingCommand<>(new BindingConsumer<Integer>() {
        @Override
        public void call(Integer isChecked) {


            if(observableList.contains(footersItem)){
                return;
            }else{
                //双向绑定动态添加Item
                observableList.add(footersItem);
                finishLoadmore();
            }


        }
    });


}
