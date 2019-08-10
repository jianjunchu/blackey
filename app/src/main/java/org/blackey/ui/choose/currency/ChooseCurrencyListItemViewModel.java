package org.blackey.ui.choose.currency;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.entity.Currency;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;

public class ChooseCurrencyListItemViewModel extends ItemViewModel<ChooseCurrencyListViewModel> {


    public static final String TOKEN_CHOOSE_CURRENCY_REFRESH = "token_choose_currency_refresh";

    public ObservableField<Currency> entity = new ObservableField<>();

    public ChooseCurrencyListItemViewModel(@NonNull ChooseCurrencyListViewModel viewModel, Currency entity) {
        super(viewModel);
        this.entity.set(entity);
        //ImageView的占位图片，可以解决RecyclerView中图片错误问题
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Messenger.getDefault().send(entity.get(), TOKEN_CHOOSE_CURRENCY_REFRESH);
            viewModel.finish();
        }
    });
}
