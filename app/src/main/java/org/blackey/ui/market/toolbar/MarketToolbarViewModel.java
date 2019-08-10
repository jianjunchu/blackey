package org.blackey.ui.market.toolbar;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;


/**
 * 市场
 */
public class MarketToolbarViewModel extends BaseViewModel {

    //标题文字
    public ObservableField<String> titleText = new ObservableField<>("");

    public MarketToolbarViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 设置标题
     *
     * @param text 标题文字
     */
    public void setTitleText(String text) {
        titleText.set(text);
    }


    /**
     * 右边文字的点击事件，子类可重写
     */
    public BindingCommand rightSearchOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

            }
        });
    }




    /**
     * 右边图标的点击事件，子类可重写
     */
    public BindingCommand rightAddOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {


            }
        });
    }

}
