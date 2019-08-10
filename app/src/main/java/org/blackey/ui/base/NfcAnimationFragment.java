package org.blackey.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.blackey.R;

import timber.log.Timber;

public class NfcAnimationFragment extends DialogFragment {

    static final String TAG = "NfcAnimationFragment";

    private Listener activityCallback;


    private static NfcAnimationFragment fragment = null;


    public static NfcAnimationFragment getInstance() {
        if(fragment ==null){
            fragment = new NfcAnimationFragment();
        }


        return fragment;
    }

    public static void display(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        NfcAnimationFragment.getInstance().show(ft, TAG);
    }

    public static void remove(FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_nfc_animation, null);


        ImageView nfcAnimationImage = (ImageView) view.findViewById(R.id.NfcAnimation);
        nfcAnimationImage.setImageResource(R.drawable.nfc_signal);
        AnimationDrawable nfcAnimation = (AnimationDrawable) nfcAnimationImage.getDrawable();
        nfcAnimation.setOneShot(false);
        nfcAnimation.start();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
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


    public interface Listener {

        void enableNfc();
        void disableNfc();
    }

}
