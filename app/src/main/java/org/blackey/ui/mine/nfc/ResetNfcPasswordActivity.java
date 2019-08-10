package org.blackey.ui.mine.nfc;

import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import com.vondear.rxtool.view.RxToast;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityResetNfcPasswordBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.base.NfcAnimationFragment;
import org.blackey.utils.nfc.TagUtil;
import org.blackey.utils.nfc.ThreeDES;

public class ResetNfcPasswordActivity extends BlackeyBaseActivity<ActivityResetNfcPasswordBinding,ResetNfcPasswordView> implements NfcAnimationFragment.Listener{
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_reset_nfc_password;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.originalPassword.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {

                ObservableField<String> field = (ObservableField<String>) sender;

                binding.tlOriginalPassword.setError(field.get());
            }
        });

        viewModel.uc.password.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableField<String> field = (ObservableField<String>) sender;
                binding.tlPassword.setError(field.get());
            }
        });

        viewModel.uc.confirmPassword.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ObservableField<String> field = (ObservableField<String>) sender;
                binding.tlConfirmPassword.setError(field.get());
            }
        });

        viewModel.uc.showNfcAnimation.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                NfcAnimationFragment.display(getSupportFragmentManager());
            }
        });

        viewModel.uc.removeNfcAnimation.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                NfcAnimationFragment.remove(getSupportFragmentManager());
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            try {

                boolean authenticated = false;

                TagUtil tagUtil = TagUtil.selectTag(intent, false);

                String originalPassword = ThreeDES.getPasswd(viewModel.originalPassword.get());
                String newPassword = ThreeDES.getPasswd(viewModel.password.get());

                try {
                   authenticated = tagUtil.authentication_internal(intent, originalPassword, false);

                }catch (Exception e) {
                    RxToast.error(getString(R.string.error_original_password));
                    e.printStackTrace();
                }

                if(authenticated){

                    try {
                        boolean result=  tagUtil.writeNewKey216SC(intent,newPassword,false);
                        if(!result) {
                            RxToast.showToast(R.string.write_new_key_failed);
                        }else{
                            RxToast.success(getString(R.string.write_new_key_success));
                            finish();
                        }
                    }catch (Exception e) {
                        RxToast.showToast(R.string.write_new_key_failed);
                        e.printStackTrace();
                    }

                }else{
                    RxToast.error(getString(R.string.error_original_password));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
