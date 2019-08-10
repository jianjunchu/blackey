package org.blackey.ui.market.trust;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class TrustManageListItemViewModel extends MultiItemViewModel<TrustManageListViewModel> {

    public ObservableField<String> entity = new ObservableField<>();

    public TrustManageListItemViewModel(@NonNull TrustManageListViewModel viewModel) {
        super(viewModel);
    }

    public TrustManageListItemViewModel(@NonNull TrustManageListViewModel viewModel, String entity) {
        super(viewModel);
        multiType = R.layout.item_market_trust_manage_list;
        this.entity.set(entity);

    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑

        }
    });

}
