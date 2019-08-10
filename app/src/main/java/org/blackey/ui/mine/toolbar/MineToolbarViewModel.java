package org.blackey.ui.mine.toolbar;

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
public class MineToolbarViewModel extends BaseViewModel {


    public ObservableField<Integer> noLoginVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> loginVisibility = new ObservableField<>(View.GONE);
    public ObservableField<String>  nickname = new ObservableField<>("");
    public ObservableField<String>  numberTrades = new ObservableField<>("");


    //标题文字
    public MineToolbarViewModel(@NonNull Application application) {
        super(application);
    }

    public String portraitUrl = null;
    //标题文字
    public ObservableField<String> titleText = new ObservableField<>("");

    /**
     * 设置标题
     *
     * @param text 标题文字
     */
    public void setTitleText(String text) {
        titleText.set(text);
    }


    /**
     * 登录
     */
    public BindingCommand loginOnClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
            }
        });
    }

    /**
     * 选择照片
     */
    public BindingCommand chooseImageClickCommand() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
            }
        });
    }

}
