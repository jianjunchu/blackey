package org.blackey.ui.choose.img;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class ChooseImageListItemViewModel extends ItemViewModel<BaseViewModel> {

    private Listener callback;

    public interface Listener {

        void itemClick(String url);

        void deleteClick(ChooseImageListItemViewModel itemViewModel);

    }


    public ObservableField<String> url = new ObservableField<>();

    public ObservableField<Integer> deleteVisibility = new ObservableField<>(View.GONE);




    public ChooseImageListItemViewModel(@NonNull BaseViewModel viewModel,Listener callback) {
        super(viewModel);
        this.callback = callback;

    }

    public ChooseImageListItemViewModel(@NonNull BaseViewModel viewModel,Listener callback, String url) {
        super(viewModel);
        this.url.set(url);
        this.callback = callback;
        this.deleteVisibility.set(View.VISIBLE);
    }


    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            callback.itemClick(url.get());

        }
    });

    //删除
    public BindingCommand deleteClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            callback.deleteClick(ChooseImageListItemViewModel.this);
        }
    });




}
