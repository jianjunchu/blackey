package org.blackey.ui.market.order;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import com.m2049r.xmrwallet.model.WalletManager;
import com.vondear.rxui.view.dialog.RxDialog;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationDialog extends RxDialog {

    private TextView mBuyingPrice;
    private TextView mQurchaseQuantity;
    private TextView mPaymentAmount;

    private TextView mTvSure;
    private TextView mTvCancel;

    private Spinner mSpinnerGatheringWallet;

    private List<String> names;


    public OrderConfirmationDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public OrderConfirmationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public OrderConfirmationDialog(Context context) {
        super(context);
        initView();
    }

    public OrderConfirmationDialog(Activity context) {
        super(context);
        initView();
    }

    public OrderConfirmationDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_confirm, null);
        mBuyingPrice = (TextView) dialogView.findViewById(R.id.buying_price);
        mQurchaseQuantity = (TextView) dialogView.findViewById(R.id.purchase_quantity);
        mPaymentAmount = (TextView) dialogView.findViewById(R.id.payment_amount);
        mTvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        mTvCancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        mSpinnerGatheringWallet = dialogView.findViewById(R.id.spinner_select_wallet);
        setContentView(dialogView);
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

        mSpinnerGatheringWallet.setAdapter(spinnerAdapter);
    }

    public String getSpinnerData() {

        return names.get(mSpinnerGatheringWallet.getSelectedItemPosition()) ;
    }


    public TextView getmBuyingPrice() {
        return mBuyingPrice;
    }

    public void setmBuyingPrice(TextView mBuyingPrice) {
        this.mBuyingPrice = mBuyingPrice;
    }

    public TextView getmQurchaseQuantity() {
        return mQurchaseQuantity;
    }

    public void setmQurchaseQuantity(TextView mQurchaseQuantity) {
        this.mQurchaseQuantity = mQurchaseQuantity;
    }

    public TextView getmPaymentAmount() {
        return mPaymentAmount;
    }

    public void setmPaymentAmount(TextView mPaymentAmount) {
        this.mPaymentAmount = mPaymentAmount;
    }

    public TextView getmTvSure() {
        return mTvSure;
    }

    public void setmTvSure(TextView mTvSure) {
        this.mTvSure = mTvSure;
    }

    public TextView getmTvCancel() {
        return mTvCancel;
    }

    public void setmTvCancel(TextView mTvCancel) {
        this.mTvCancel = mTvCancel;
    }

    public Spinner getmSpinnerGatheringWallet() {
        return mSpinnerGatheringWallet;
    }

    public void setmSpinnerGatheringWallet(Spinner mSpinnerGatheringWallet) {
        this.mSpinnerGatheringWallet = mSpinnerGatheringWallet;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }
}
