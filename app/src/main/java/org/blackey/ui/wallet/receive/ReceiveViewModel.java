package org.blackey.ui.wallet.receive;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.ui.base.viewmodel.ToolbarViewModel;

public class ReceiveViewModel extends ToolbarViewModel {


    public ReceiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void initToolbar(String walletName) {
        //初始化标题栏
        setRightIconVisible(View.GONE);
        setTitleText(walletName);

    }

}
