package org.blackey.ui.webview;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.ui.base.viewmodel.ToolbarViewModel;

/**
 * 个人中心
 */
public class WebViewViewModel extends ToolbarViewModel {


    public WebViewViewModel(@NonNull Application application) {
        super(application);
    }


    public void initToolbar() {
        setRightIconVisible(View.GONE);
        setRightTextVisible(View.GONE);
    }
}
