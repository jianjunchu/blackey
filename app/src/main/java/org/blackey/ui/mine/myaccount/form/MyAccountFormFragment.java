package org.blackey.ui.mine.myaccount.form;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMineMyAccountFormBinding;
import org.blackey.entity.UserPaymentMode;
import org.blackey.model.request.UserPaymentModeRequest;
import org.blackey.ui.base.BlackeyBaseFragment;

public class MyAccountFormFragment extends BlackeyBaseFragment<FragmentMineMyAccountFormBinding,MyAccountFormViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_my_account_form;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.spinnerAccountType = binding.spinnerAccountType;
        viewModel.spinnerBankName = binding.spinnerBankName;
        viewModel.spinnerCurrency = binding.spinnerCurrency;

        Bundle mBundle = getArguments();
        if (mBundle != null) {
            UserPaymentMode entity = mBundle.getParcelable("entity");
            viewModel.request = new UserPaymentModeRequest(entity);
            viewModel.initToolbar(getActivity().getString(R.string.edit_account));
            binding.forPayment.setChecked(entity.getForPayment()!=null && entity.getForPayment() ==1);
            binding.forReceiving.setChecked(entity.getForCollection()!=null && entity.getForCollection() ==1);
        }else{
            viewModel.initToolbar(getActivity().getString(R.string.add_account));
            viewModel.request = new UserPaymentModeRequest();
        }

        viewModel.initCurrencyData();
        viewModel.initAccountTypeData();



    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

    }
}
