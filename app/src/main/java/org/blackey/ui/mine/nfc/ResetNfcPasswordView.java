package org.blackey.ui.mine.nfc;

import android.app.Application;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class ResetNfcPasswordView extends ToolbarViewModel {

    public ObservableField<String> originalPassword = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<String> confirmPassword = new ObservableField<>("");

    public ResetNfcPasswordView(@NonNull Application application) {
        super(application);
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public ObservableField<String> originalPassword = new ObservableField("");
        public ObservableField<String> password = new ObservableField("");
        public ObservableField<String> confirmPassword = new ObservableField("");
        public ObservableBoolean showNfcAnimation = new ObservableBoolean(false);
        public ObservableBoolean removeNfcAnimation = new ObservableBoolean(false);
    }


    public final BindingCommand resetNfcPasswordClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            if(TextUtils.isEmpty(originalPassword.get())){
                uc.originalPassword.set(getApplication().getString(R.string.hint_original_password));
                return;
            }else{
                uc.originalPassword.set("");
            }

            if(TextUtils.isEmpty(password.get())){
                uc.password.set(getApplication().getString(R.string.hint_password));
                return;
            }else{
                uc.password.set("");
            }

            if(TextUtils.isEmpty(confirmPassword.get())){
                uc.confirmPassword.set(getApplication().getString(R.string.hint_confirm_password));
                return;
            }else{
                uc.confirmPassword.set("");
            }

            if(!password.get().equals(confirmPassword.get())){
                uc.confirmPassword.set(getApplication().getString(R.string.hint_passwords_differ));
                return;
            }else{
                uc.confirmPassword.set("");
            }

            uc.showNfcAnimation.set(!uc.showNfcAnimation.get());
        }
    });
}
