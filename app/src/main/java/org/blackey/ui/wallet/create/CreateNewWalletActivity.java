package org.blackey.ui.wallet.create;

import android.content.Intent;
import android.databinding.Observable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;

import org.blackey.R;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.ui.wallet.detail.GenerateReviewFragment;
import org.blackey.utils.NfcBackupHandle;
import com.m2049r.xmrwallet.dialog.InputNfcPasswordFragment;
import com.vondear.rxtool.view.RxToast;
import org.blackey.BR;
import org.blackey.databinding.ActivityWalletCreateBinding;
import org.blackey.exception.AuthenticationFailedException;
import org.blackey.exception.DataFormatException;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.utils.NfcBackupHandle;
import org.blackey.exception.AuthenticationFailedException;
import org.blackey.exception.DataFormatException;

public class CreateNewWalletActivity extends BlackeyBaseActivity<ActivityWalletCreateBinding,CreateNewWalletViewModel> implements InputNfcPasswordFragment.Listener{

    public static final String TYPE = "type";
    public static final String TYPE_NEW = "new";
    public static final String TYPE_SEED = "seed";
    public static final String TYPE_NFC = "nfc";


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_wallet_create;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {

        viewModel.activity = this;

        Bundle args = getIntent().getExtras();
        viewModel.type = args.getString(TYPE);
        if(CreateNewWalletActivity.TYPE_NFC.equalsIgnoreCase(viewModel.type)){
            enableNfc();
        }
        viewModel.initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onStop() {
        if(CreateNewWalletActivity.TYPE_NFC.equalsIgnoreCase(viewModel.type)){
            disableNfc();
        }
        super.onStop();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        if(TYPE_SEED.equalsIgnoreCase(viewModel.type)){
            binding.etWalletMnemonic.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        viewModel.checkMnemonic();
                    }
                }
            });
        }

        //监听下拉刷新完成
        viewModel.uc.enableNfc.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
               enableNfc();
                InputNfcPasswordFragment.display(getSupportFragmentManager(),false);

            }
        });

        viewModel.uc.disableNfc.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                disableNfc();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            try {

                if(InputNfcPasswordFragment.getInstance().checkPassword()){
                    InputNfcPasswordFragment.getInstance().setIntent(intent);

                    NfcBackupHandle nfcBackupHandle = null;
                    String  passwd = InputNfcPasswordFragment.getInstance().getPasswd();
                    try {
                        nfcBackupHandle = new NfcBackupHandle(intent,passwd);
                        String[] keys = nfcBackupHandle.readNfc();
                        if(keys !=null &&keys.length==4){
                            if(keys[3]!=null)
                                viewModel.onGenerate(keys[3]);
                            else
                                viewModel.onGenerate(keys[0],keys[1],keys[2]);
                        }else{
                            RxToast.error(getString(R.string.nfc_invalid));
                        }
                    } catch (AuthenticationFailedException e) {
                        RxToast.error(getString(R.string.nfc_invalid));
                        //RxToast.error(getString(R.string.restore_from_nfc_auth_failed));
                    }catch (DataFormatException e) {
                        RxToast.error(e.getMessage());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveSeed(boolean save) {
    }
}
