package org.blackey.ui.choose.currency;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.Currency;
import org.blackey.service.DictApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ChooseCurrencyListViewModel extends ToolbarViewModel {

    private static final String TAG = "ChoosePaymentModeListViewModel";


    //给RecyclerView添加ObservableList
    public ObservableList<ChooseCurrencyListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ChooseCurrencyListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_choose_currency_list);

    public final BindingRecyclerViewAdapter<ChooseCurrencyListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();


    public ChooseCurrencyListViewModel(@NonNull Application application) {
        super(application);
    }

    public void initToolbar() {
        setTitleText(getApplication().getString(R.string.choose_currency));
    }


    public void requestNetWork() {

        RetrofitClient.getInstance().create(DictApiService.class)
                .currencys()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<Currency>>>() {
                    @Override
                    public void accept(BaseResponse<List<Currency>> response) throws Exception {
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

    private void initAndShowList(List<Currency> list) {
        for(Currency item : list){

            ChooseCurrencyListItemViewModel itemViewModel = new ChooseCurrencyListItemViewModel(ChooseCurrencyListViewModel.this, item);
            //双向绑定动态添加Item
            observableList.add(itemViewModel);
        }
    }


}
