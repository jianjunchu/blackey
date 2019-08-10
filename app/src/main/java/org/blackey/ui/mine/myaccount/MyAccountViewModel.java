package org.blackey.ui.mine.myaccount;

import android.app.Activity;
import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.UserPaymentMode;
import org.blackey.service.UserPaymentModeApiService;
import org.blackey.ui.base.viewmodel.AddToolbarViewModel;
import org.blackey.ui.mine.myaccount.form.MyAccountFormFragment;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class MyAccountViewModel extends AddToolbarViewModel {


    public SwipeRefreshLayout swipeRefresh;

    public ImageView ivGunther;


    //订单 给RecyclerView添加ObservableList
    public ObservableList<MyAccountListItemViewModel> observableList = new ObservableArrayList<>();
    //订单 给RecyclerView添加ItemBinding
    public ItemBinding<MyAccountListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_mine_my_account_list);
    //订单 给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象
    public final BindingRecyclerViewAdapter<MyAccountListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();

    public Activity activity;

    public MyAccountViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.my_account));
    }

    /**
     * 右边图标的点击事件，子类可重写
     */
    public BindingCommand rightAddOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                startContainerActivity(MyAccountFormFragment.class.getCanonicalName());

            }
        });
    }

    public void requestNetWork() {

        RetrofitClient.getInstance().create(UserPaymentModeApiService.class)
                .list()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<UserPaymentMode>>>() {
                    @Override
                    public void accept(BaseResponse<List<UserPaymentMode>> response) throws Exception {

                        //请求成功
                        if(response.isOk()){

                            for(UserPaymentMode userPaymentMode:response.getBody()){
                                MyAccountListItemViewModel itemViewModel = new MyAccountListItemViewModel(MyAccountViewModel.this, userPaymentMode);
                                //双向绑定动态添加Item
                                observableList.add(itemViewModel);
                            }

                        }

                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        //请求刷新完成收回

                    }
                });



    }


    public void finishRefreshing() {
        
        observableList.clear();
        requestNetWork();
    }
}
