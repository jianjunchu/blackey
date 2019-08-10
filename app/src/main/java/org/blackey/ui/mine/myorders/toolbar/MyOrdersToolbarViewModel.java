package org.blackey.ui.mine.myorders.toolbar;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;


/**
 * 市场
 */
public class MyOrdersToolbarViewModel extends BaseViewModel {

    //标题文字

    public MyOrdersToolbarViewModel(@NonNull Application application) {
        super(application);
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


}
