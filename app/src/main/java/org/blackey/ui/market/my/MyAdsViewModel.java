package org.blackey.ui.market.my;

import android.app.Application;
import android.support.annotation.NonNull;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;

public class MyAdsViewModel extends ToolbarViewModel {

    public MyAdsViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.my_ads));

    }
}
