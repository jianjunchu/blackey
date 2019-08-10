package org.blackey.ui.market.trust;

import android.app.Application;
import android.support.annotation.NonNull;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;

public class TrustManageViewModel extends ToolbarViewModel {


    public TrustManageViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.trust));

    }
}
