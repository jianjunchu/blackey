package org.blackey.ui.mine.help;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;

public class HelpViewModel extends ToolbarViewModel {


    public HelpViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.help_center));
        setRightIconVisible(View.GONE);
    }
}
