package org.blackey.ui.choose.paymentMode;

import android.app.Activity;
import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.UserPaymentMode;
import org.blackey.service.UserPaymentModeApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.market.sell.ReleaseAdvertiseViewModel;
import org.blackey.ui.mine.myaccount.form.MyAccountFormFragment;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ChoosePaymentModeListViewModel extends ToolbarViewModel {

    private static final String TAG = "ChoosePaymentModeListViewModel";


    public static List<ChoosePaymentModeListItemViewModel> selected = new ArrayList<>();



    //给RecyclerView添加ObservableList
    public ObservableList<ChoosePaymentModeListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ChoosePaymentModeListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_choose_payment_mode_list);

    public final BindingRecyclerViewAdapter<ChoosePaymentModeListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();
    public Activity activity;


    public ChoosePaymentModeListViewModel(@NonNull Application application) {
        super(application);
    }

    public void initToolbar() {
        setTitleText(getApplication().getString(R.string.choose_payment_mode));
        setRightTextVisible(View.VISIBLE);
        setRightText(getApplication().getString(R.string.send_confirm_title));
        selected = new ArrayList<>();
    }


    /**
     * 右边文字的点击事件，子类可重写
     */
    public BindingCommand rightTextOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                Messenger.getDefault().sendNoMsg(ReleaseAdvertiseViewModel.TOKEN_SELECT_PAYMENT_MODE);
                finish();
            }
        });
    }

    public void requestNetWork() {
        observableList.clear();
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

                            initAndShowList(response.getBody());

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

    private void initAndShowList(List<UserPaymentMode> list) {
        for(UserPaymentMode item : list){

            ChoosePaymentModeListItemViewModel itemViewModel = new ChoosePaymentModeListItemViewModel(ChoosePaymentModeListViewModel.this, item);
            //双向绑定动态添加Item
            observableList.add(itemViewModel);
        }
    }

    public boolean selected(ChoosePaymentModeListItemViewModel itemViewModel) {

        if(selected.contains(itemViewModel)){
            selected.remove(itemViewModel);
            return false;
        }else{
            if(selected.size()>2){
                return false;
            }
            selected.add(itemViewModel);
            return true;
        }
    }

    public void remove(ChoosePaymentModeListItemViewModel itemViewModel) {
        selected.remove(itemViewModel);
    }



    /**
     * 底部添加
     */
    public BindingCommand addClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                startContainerActivity(MyAccountFormFragment.class.getCanonicalName());
            }
        });
    }
}
