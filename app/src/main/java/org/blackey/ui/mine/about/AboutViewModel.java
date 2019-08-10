package org.blackey.ui.mine.about;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;

public class AboutViewModel extends ToolbarViewModel {


    public AboutViewModel(@NonNull Application application) {
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
