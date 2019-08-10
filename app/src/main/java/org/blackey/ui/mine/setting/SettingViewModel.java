package org.blackey.ui.mine.setting;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.TokenManager;
import com.vondear.rxtool.view.RxToast;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;

public class SettingViewModel extends ToolbarViewModel {

    public static final String TOKEN_SETTING_VIEW_MODEL_LOGOUT = "TOKEN_SETTING_VIEW_MODEL_LOGOUT";
    //手机错误提示
    public ObservableField<Integer> logoutVisibility = new ObservableField<>(View.GONE);

    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.setting));
        setRightIconVisible(View.GONE);
    }
    public void  initData(){

        if(BlackeyApplication.getCurrent()==null){
            logoutVisibility.set(View.GONE);
        }else{
            logoutVisibility.set(View.VISIBLE);
        }
    }

    //选择 国家/地区 点击事件
    public BindingCommand logoutOnClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            logoutVisibility.set(View.GONE);
            BlackeyApplication.setCurrent(null);
            TokenManager.setRefreshToken("");
            TokenManager.setAccessToken("");
            RetrofitClient.refresh();
            RxToast.success(getApplication().getString(R.string.logout_success));
            //发送一个空消息
            Messenger.getDefault().sendNoMsg(SettingViewModel.TOKEN_SETTING_VIEW_MODEL_LOGOUT);
        }
    });

    @Override
    public void onResume() {
        initData();
    }
}
