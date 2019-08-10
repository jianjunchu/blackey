package org.blackey.ui.market.sell;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import com.m2049r.xmrwallet.model.WalletManager;
import com.vondear.rxui.view.dialog.RxDialog;

import java.util.ArrayList;
import java.util.List;

public class SellConfirmationDialog extends RxDialog {

    private boolean isSync = false;

    private TextView mTradingValue;
    private TextView mTradingValueUnit;

    private TextView mTotalsold;
    private TextView mTotalsoldUnit;

    private TextView mGrossAmount;
    private TextView mGrossAmountUnit;
    private TextView mMinimumQuantity;
    private TextView mMinimumQuantityUnit;
    private TextView mMinimumAmount;
    private TextView mMinimumAmountUnit;

    private Spinner mSpinnerSelectWallet;

    private EditText mGeneratePassword;

    private Button mTvSure;
    private Button mTvCancel;

    private TextView mTvProgress;

    private ProgressBar mPbProgress;

    private List<String> names;


    public SellConfirmationDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public SellConfirmationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public SellConfirmationDialog(Context context) {
        super(context);
        initView();
    }

    public SellConfirmationDialog(Activity context) {
        super(context);
        initView();
    }

    public SellConfirmationDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }



    private void initView() {
        setCancelable(false);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sell_confirm, null);

        mTradingValue = (TextView) dialogView.findViewById(R.id.trading_value);
        mTradingValueUnit = (TextView) dialogView.findViewById(R.id.trading_value_unit);

        mTotalsold = (TextView) dialogView.findViewById(R.id.totalsold);
        mTotalsoldUnit = (TextView) dialogView.findViewById(R.id.totalsold_unit);

        mGrossAmount = (TextView) dialogView.findViewById(R.id.gross_amount);
        mGrossAmountUnit = (TextView) dialogView.findViewById(R.id.gross_amount_unit);
        mMinimumQuantity = (TextView) dialogView.findViewById(R.id.minimum_quantity);
        mMinimumQuantityUnit = (TextView) dialogView.findViewById(R.id.minimum_quantity_unit);
        mMinimumAmount = (TextView) dialogView.findViewById(R.id.minimum_amount);
        mMinimumAmountUnit = (TextView) dialogView.findViewById(R.id.minimum_amount_unit);
        mSpinnerSelectWallet = (Spinner) dialogView.findViewById(R.id.spinner_select_wallet);
        mGeneratePassword = (EditText) dialogView.findViewById(R.id.generate_password);

        mTvProgress = dialogView.findViewById(R.id.tvProgress);
        mPbProgress  = dialogView.findViewById(R.id.pbProgress);

        mTvSure = (Button) dialogView.findViewById(com.vondear.rxui.R.id.tv_sure);
        mTvCancel = (Button) dialogView.findViewById(com.vondear.rxui.R.id.tv_cancel);

        setContentView(dialogView);
    }
    private int syncProgress = -1;
    public void setProgress(final int n) {
        syncProgress = n;
        if (n > 100) {
            mPbProgress.setIndeterminate(true);
            mPbProgress.setVisibility(View.VISIBLE);
        } else if (n >= 0) {
            mPbProgress.setIndeterminate(false);
            mPbProgress.setProgress(n);
            mPbProgress.setVisibility(View.VISIBLE);
        } else { // <0
            mPbProgress.setVisibility(View.INVISIBLE);
        }
    }
    private String syncText = null;
    public void setProgress(final String text) {
        syncText = text;
        mTvProgress.setVisibility(View.VISIBLE);
        mTvProgress.setText(text);
    }

    public void initSpinnerData() {
        WalletManager mgr = WalletManager.getInstance();
        List<WalletManager.WalletInfo> walletInfos =
                mgr.findWallets(BlackeyApplication.getWalletRoot());

        names = new ArrayList<>();

        for(WalletManager.WalletInfo walletInfo:walletInfos){
            names.add(walletInfo.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.item_spinner_wallet, names);

        mSpinnerSelectWallet.setAdapter(spinnerAdapter);
    }

    public String getSpinnerData() {

       return names.get(mSpinnerSelectWallet.getSelectedItemPosition()) ;
    }


    public TextView getmTradingValue() {
        return mTradingValue;
    }

    public void setmTradingValue(TextView mTradingValue) {
        this.mTradingValue = mTradingValue;
    }

    public TextView getmTradingValueUnit() {
        return mTradingValueUnit;
    }

    public void setmTradingValueUnit(TextView mTradingValueUnit) {
        this.mTradingValueUnit = mTradingValueUnit;
    }

    public TextView getmGrossAmount() {
        return mGrossAmount;
    }

    public void setmGrossAmount(TextView mGrossAmount) {
        this.mGrossAmount = mGrossAmount;
    }

    public TextView getmGrossAmountUnit() {
        return mGrossAmountUnit;
    }

    public void setmGrossAmountUnit(TextView mGrossAmountUnit) {
        this.mGrossAmountUnit = mGrossAmountUnit;
    }

    public TextView getmMinimumQuantity() {
        return mMinimumQuantity;
    }

    public void setmMinimumQuantity(TextView mMinimumQuantity) {
        this.mMinimumQuantity = mMinimumQuantity;
    }

    public TextView getmMinimumQuantityUnit() {
        return mMinimumQuantityUnit;
    }

    public void setmMinimumQuantityUnit(TextView mMinimumQuantityUnit) {
        this.mMinimumQuantityUnit = mMinimumQuantityUnit;
    }

    public TextView getmMinimumAmount() {
        return mMinimumAmount;
    }

    public void setmMinimumAmount(TextView mMinimumAmount) {
        this.mMinimumAmount = mMinimumAmount;
    }

    public TextView getmMinimumAmountUnit() {
        return mMinimumAmountUnit;
    }

    public void setmMinimumAmountUnit(TextView mMinimumAmountUnit) {
        this.mMinimumAmountUnit = mMinimumAmountUnit;
    }

    public Spinner getmSpinnerSelectWallet() {
        return mSpinnerSelectWallet;
    }

    public void setmSpinnerSelectWallet(Spinner mSpinnerSelectWallet) {
        this.mSpinnerSelectWallet = mSpinnerSelectWallet;
    }

    public EditText getmGeneratePassword() {
        return mGeneratePassword;
    }

    public void setmGeneratePassword(EditText mGeneratePassword) {
        this.mGeneratePassword = mGeneratePassword;
    }

    public TextView getmTvSure() {
        return mTvSure;
    }

    public void setmTvSure(Button mTvSure) {
        this.mTvSure = mTvSure;
    }

    public TextView getmTvCancel() {
        return mTvCancel;
    }

    public void setmTvCancel(Button mTvCancel) {
        this.mTvCancel = mTvCancel;
    }

    public TextView getmTotalsold() {
        return mTotalsold;
    }

    public void setmTotalsold(TextView mTotalsold) {
        this.mTotalsold = mTotalsold;
    }

    public TextView getmTotalsoldUnit() {
        return mTotalsoldUnit;
    }

    public void setmTotalsoldUnit(TextView mTotalsoldUnit) {
        this.mTotalsoldUnit = mTotalsoldUnit;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }
}
