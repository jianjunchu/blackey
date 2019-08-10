package org.blackey.ui.login;

import android.app.Application;
import android.support.annotation.NonNull;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;


public class ResetPasswordViewModel extends ToolbarViewModel {

    public ResetPasswordViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.reset_password));
    }
}
