package org.blackey.ui.wallet.detail;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.BR;
import org.blackey.R;
import com.m2049r.xmrwallet.model.TransactionInfo;

import java.util.Collections;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class WalletTransactionViewModel extends BaseViewModel {

    public  OnInteractionListener listener;

    //封装一个界面发生改变的观察者

    public interface OnInteractionListener {
        void onInteraction(View view, TransactionInfo item);
    }

    public UIChangeObservable uc = new UIChangeObservable();

    public WalletTransactionFragment.Listener activityCallback;

    public class UIChangeObservable {
        //下拉刷新完成
        public ObservableField finishReceive = new ObservableField();
        //上拉加载完成
        public ObservableField finishSend = new ObservableField();
    }

    //给RecyclerView添加ObservableList
    public ObservableList<WalletItemTransactionListViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<WalletItemTransactionListViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_wallet_transaction_list);
    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法，里面有你要的Item对应的binding对象
    public final BindingRecyclerViewAdapter<WalletItemTransactionListViewModel> adapter = new BindingRecyclerViewAdapter<>();

    public WalletTransactionViewModel(@NonNull Application application) {
        super(application);

    }


    public void setInfos(List<TransactionInfo> list) {
        observableList.clear();
        Collections.sort(list);
        for(TransactionInfo info : list){
            WalletItemTransactionListViewModel itemTransactionViewModel = new WalletItemTransactionListViewModel(this,info);
            observableList.add(itemTransactionViewModel);
        }
    }

    public BindingCommand receiveOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            uc.finishReceive.set("");
        }
    });

    public BindingCommand sendOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            uc.finishSend.set("");
        }
    });
}
