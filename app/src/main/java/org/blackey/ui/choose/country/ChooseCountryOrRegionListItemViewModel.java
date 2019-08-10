package org.blackey.ui.choose.country;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.entity.CountryOrRegion;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;

public class ChooseCountryOrRegionListItemViewModel extends ItemViewModel<ChooseCountryOrRegionListViewModel> {

    public static final String TOKEN_CHOOSE_COUNTRY_REFRESH = "token_choose_country_refresh";

    public ObservableField<CountryOrRegion> entity = new ObservableField<>();

    public ChooseCountryOrRegionListItemViewModel(@NonNull ChooseCountryOrRegionListViewModel viewModel, CountryOrRegion entity) {
        super(viewModel);
        this.entity.set(entity);
        //ImageView的占位图片，可以解决RecyclerView中图片错误问题
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Messenger.getDefault().send(entity.get(), viewModel.returnToken);
            viewModel.finish();
        }
    });


}
