package org.blackey.ui.base.viewmodel;

import android.app.Application;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class AddToolbarViewModel extends BaseViewModel {

    //左边图标的观察者
    public ObservableInt leftIconVisibleObservable = new ObservableInt(View.VISIBLE);

    public AddToolbarViewModel(@NonNull Application application) {
        super(application);
    }

    //标题文字
    public ObservableField<String> titleText = new ObservableField<>("");

    public void setLeftIconVisible(int visibility) {
        this.leftIconVisibleObservable.set(visibility);
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
     * 返回按钮的点击事件
     */
    public final BindingCommand backOnClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            finish();
        }
    });


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
