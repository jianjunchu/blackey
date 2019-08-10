package org.blackey.ui.market.search;

import android.app.Application;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;


public class MarketSearchViewModel extends ToolbarViewModel {

    public MarketSearchViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setLeftIconVisible(View.VISIBLE);
        setTitleText(getApplication().getString(R.string.title_search));
    }
}
