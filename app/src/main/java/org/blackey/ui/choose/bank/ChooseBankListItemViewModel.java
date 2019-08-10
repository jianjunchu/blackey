package org.blackey.ui.choose.bank;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.entity.Bank;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;

public class ChooseBankListItemViewModel extends ItemViewModel<ChooseBankListViewModel> {


    public static final String TOKEN_CHOOSE_BANK_REFRESH = "token_choose_bank_refresh";

    public ObservableField<Bank> entity = new ObservableField<>();

    public ChooseBankListItemViewModel(@NonNull ChooseBankListViewModel viewModel, Bank entity) {
        super(viewModel);
        this.entity.set(entity);
        //ImageView的占位图片，可以解决RecyclerView中图片错误问题
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Messenger.getDefault().send(entity.get(), TOKEN_CHOOSE_BANK_REFRESH);
            viewModel.finish();
        }
    });
}
