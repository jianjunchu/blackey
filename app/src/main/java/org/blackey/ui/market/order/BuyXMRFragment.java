package org.blackey.ui.market.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketBuyXmrBinding;
import org.blackey.ui.base.BlackeyBaseFragment;

import me.goldze.mvvmhabit.utils.KLog;

public class BuyXMRFragment extends BlackeyBaseFragment<FragmentMarketBuyXmrBinding,BuyXMRViewModel> {

    public static final String ADVERTISE_ID = "ADVERTISE_ID";

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market_buy_xmr;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    TextWatcher purchaseAmount =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            KLog.e(s.toString());
            viewModel.changedPurchaseQuantityText( s.toString());
        }
    };


    TextWatcher purchaseQuantity = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            KLog.e(s.toString());
            viewModel.changedPurchaseAmountText(binding.purchaseQuantity.getText().toString());
        }
    };
    @Override
    public void initData() {
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            Long adId = mBundle.getLong(ADVERTISE_ID);
            viewModel.requestNetWork(adId);
        }

        viewModel.context = getContext();
        //初始化标题

        viewModel.initToolbar();

        binding.purchaseAmount.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.llTradingLimits.setVisibility(View.VISIBLE);
                    binding.purchaseAmount.addTextChangedListener(purchaseAmount);

                } else {
                    // 此处为失去焦点时的处理内容
                    binding.llTradingLimits.setVisibility(View.INVISIBLE);
                    binding.purchaseAmount.removeTextChangedListener(purchaseAmount);
                }
            }
        });

        binding.purchaseQuantity.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.llTradingLimits.setVisibility(View.VISIBLE);
                    // 此处为得到焦点时的处理内容
                    binding.purchaseQuantity.addTextChangedListener(purchaseQuantity);
                } else {
                    // 此处为失去焦点时的处理内容
                    binding.llTradingLimits.setVisibility(View.INVISIBLE);
                    binding.purchaseQuantity.removeTextChangedListener(purchaseQuantity);
                }
            }
        });




    }
}
