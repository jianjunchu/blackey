package org.blackey.ui.choose.bank;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.Bank;
import org.blackey.service.DictApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ChooseBankListViewModel extends ToolbarViewModel {

    private static final String TAG = "ChooseBankListViewModel";


    //给RecyclerView添加ObservableList
    public ObservableList<ChooseBankListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ChooseBankListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_choose_bank_list);

    public final BindingRecyclerViewAdapter<ChooseBankListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();


    public ChooseBankListViewModel(@NonNull Application application) {
        super(application);
    }

    public void initToolbar() {
        setTitleText(getApplication().getString(R.string.choose_bank));
    }


    public void requestNetWork() {

        RetrofitClient.getInstance().create(DictApiService.class)
                .banks()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<Bank>>>() {
                    @Override
                    public void accept(BaseResponse<List<Bank>> response) throws Exception {
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

    private void initAndShowList(List<Bank> list) {
        for(Bank item : list){
            String name = ResourcesUtils.getCountryNameByCountryCode(RxTool.getContext(), item.getSwiftCode());
            KLog.i(TAG,name);
            if (TextUtils.isEmpty(name)) {
                name = item.getBankNameCn();
            }
            item.setBankName(name);
            ChooseBankListItemViewModel itemViewModel = new ChooseBankListItemViewModel(ChooseBankListViewModel.this, item);
            //双向绑定动态添加Item
            observableList.add(itemViewModel);
        }
    }


}
