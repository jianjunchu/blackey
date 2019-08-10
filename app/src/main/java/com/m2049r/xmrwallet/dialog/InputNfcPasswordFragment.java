package com.m2049r.xmrwallet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.lwjfork.code.CodeEditText;
import com.m2049r.xmrwallet.model.Wallet;

import org.blackey.R;
import org.blackey.utils.nfc.ThreeDES;

import me.goldze.mvvmhabit.utils.StringUtils;
import timber.log.Timber;

public class InputNfcPasswordFragment extends DialogFragment {

    private final static byte[] iv = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    private Wallet wallet;

    private Intent intent;

    static final String TAG = "InputNfcPasswordFragment";

    private CodeEditText codeInput;

    private TextInputLayout etWalletPassword;

    private Listener activityCallback;

    private boolean saveSeed;

    private CheckBox checkBoxSaveSeed;
    private boolean showCheckBoxSaveSeed = true;



    private static InputNfcPasswordFragment fragment = null;


    public static InputNfcPasswordFragment getInstance() {
        if(fragment ==null){
            fragment = new InputNfcPasswordFragment();
        }


        return fragment;
    }

    public static void display(FragmentManager supportFragmentManager, boolean b) {

        display(supportFragmentManager);
        getInstance().setShowCheckBoxSaveSeed(b);

    }

    @Override
    public void onResume() {
        super.onResume();
        activityCallback.enableNfc();
    }

    @Override
    public void onStop() {
        super.onStop();
        activityCallback.disableNfc();
        if(wallet!=null ){
            wallet.close();
        }
    }

    public static void display(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        InputNfcPasswordFragment.getInstance().show(ft, TAG);
    }

    public static void remove(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onAttach(Context context) {
        Timber.d("onAttach %s", context);
        super.onAttach(context);
        if (context instanceof Listener) {
            activityCallback = (Listener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_input_nfc_password, null);

        codeInput = view.findViewById(R.id.edit_underline);
        codeInput.setFocusable(true);
        codeInput.setFocusableInTouchMode(true);

        etWalletPassword = view.findViewById(R.id.etWalletPassword);
        checkBoxSaveSeed = view.findViewById(R.id.cb_save_seed);
        if(isShowCheckBoxSaveSeed()){
            checkBoxSaveSeed.setVisibility(View.VISIBLE);
            checkBoxSaveSeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    activityCallback.saveSeed(isChecked);
                }
            });
        }else {
            checkBoxSaveSeed.setVisibility(View.GONE);
        }




        ImageView nfcAnimationImage = (ImageView) view.findViewById(R.id.NfcAnimation);
        nfcAnimationImage.setImageResource(R.drawable.nfc_signal);
        AnimationDrawable nfcAnimation = (AnimationDrawable) nfcAnimationImage.getDrawable();
        nfcAnimation.setOneShot(false);
        nfcAnimation.start();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

    public boolean checkPassword(){
        etWalletPassword.setError(null);
        String passwd = codeInput.getText().toString();
        if(StringUtils.isEmpty(passwd)){
            etWalletPassword.setError("请输入密码");
            return  false;
        }

        return true;
    }

    public String getPasswd(){
        return ThreeDES.getPasswd(codeInput.getText().toString());
    }



    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getNfcBackupPassword(){
        return codeInput.getText().toString();
    }


    public boolean isSaveSeed() {
        return saveSeed;
    }

    public void setSaveSeed(boolean saveSeed) {
        this.saveSeed = saveSeed;
    }

    public interface Listener {

        void enableNfc();
        void disableNfc();
        void saveSeed(boolean save);

    }

    public CheckBox getCheckBoxSaveSeed() {
        return checkBoxSaveSeed;
    }

    public void setCheckBoxSaveSeed(CheckBox checkBoxSaveSeed) {
        this.checkBoxSaveSeed = checkBoxSaveSeed;
    }

    public boolean isShowCheckBoxSaveSeed() {
        return showCheckBoxSaveSeed;
    }

    public void setShowCheckBoxSaveSeed(boolean showCheckBoxSaveSeed) {
        this.showCheckBoxSaveSeed = showCheckBoxSaveSeed;
    }
}

