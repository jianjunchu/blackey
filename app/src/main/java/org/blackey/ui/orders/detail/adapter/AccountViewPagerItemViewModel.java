package org.blackey.ui.orders.detail.adapter;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.m2049r.xmrwallet.util.Helper;

import org.blackey.R;
import org.blackey.entity.PaymentMode;
import org.blackey.entity.UserPaymentMode;
import org.blackey.ui.orders.detail.BuyerOrderInfoViewModel;
import org.blackey.utils.ResourcesUtils;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;


public class AccountViewPagerItemViewModel  extends ItemViewModel<BaseViewModel> {


    public ObservableField<UserPaymentMode> entity = new ObservableField<>();
    public ObservableField<String> paymentModeNameText = new ObservableField<>();
    public ObservableField<Integer> imageViewSrc = new ObservableField<>();

    public AccountViewPagerItemViewModel(@NonNull BuyerOrderInfoViewModel viewModel) {
        super(viewModel);
    }

    public PaymentMode paymentMode;

    public AccountViewPagerItemViewModel(@NonNull BaseViewModel viewModel, UserPaymentMode entity) {
        super(viewModel);
        this.entity.set(entity);
        imageViewSrc.set(ResourcesUtils.getPaymentModeIcon(viewModel.getApplication(),entity.getPaymentModeIcon()));
        if("BANKACCOUNT".equalsIgnoreCase(entity.getPaymentModeCode())){
            paymentModeNameText.set(ResourcesUtils.getBankName(viewModel.getApplication(),entity.getBankSwiftCode()));
        }else{
            paymentModeNameText.set(ResourcesUtils.getPaymentName(viewModel.getApplication(),entity.getPaymentModeCode()));

        }
    }


    public BindingCommand bAccountNameClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            Helper.clipBoardCopy(view.getContext(), viewModel.getApplication().getString(R.string.account_name), entity.get().getAccountName());
            Toast.makeText(view.getContext(), viewModel.getApplication().getString(R.string.message_copy_account_name), Toast.LENGTH_SHORT).show();
        }
    });

    public BindingCommand bCopyAccountNoClickCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            Helper.clipBoardCopy(view.getContext(), viewModel.getApplication().getString(R.string.account_no), entity.get().getAccountNo());
            Toast.makeText(view.getContext(), viewModel.getApplication().getString(R.string.message_copy_account_no), Toast.LENGTH_SHORT).show();
        }
    });

}
