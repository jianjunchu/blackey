package org.blackey.ui.wallet.detail;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import org.blackey.R;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.TransactionInfo;
import com.m2049r.xmrwallet.util.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class WalletItemTransactionListViewModel extends ItemViewModel<WalletTransactionViewModel> {

    public ObservableField<TransactionInfo> entity = new ObservableField<>();

    public ObservableField<String> txAmountText = new ObservableField<>();
    public ObservableField<Integer> txAmountTextColor = new ObservableField<>(R.color.tx_red);

    public ObservableField<String> txFeeText = new ObservableField<>();
    public ObservableField<Integer> txFeeVisibility = new ObservableField<>(View.GONE);


    public ObservableField<Integer> ivTxTypeVisibility = new ObservableField<>(View.GONE);

    public ObservableField<String> txPaymentidText = new ObservableField<>("");
    public ObservableField<String> txDatetimeText = new ObservableField<>("");

    private final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public WalletItemTransactionListViewModel(@NonNull WalletTransactionViewModel viewModel, TransactionInfo entity) {
        super(viewModel);
        this.entity.set(entity);
        UserNotes userNotes = new UserNotes(entity.notes);
        if (userNotes.xmrtoKey != null) {
            ivTxTypeVisibility.set(View.VISIBLE);
        } else {
            ivTxTypeVisibility.set(View.GONE); // gives us more space for the amount
        }

        String displayAmount = Helper.getDisplayAmount(entity.amount, Helper.DISPLAY_DIGITS_INFO);

        if (entity.direction == TransactionInfo.Direction.Direction_Out) {
            txAmountText.set(viewModel.getApplication().getString(R.string.tx_list_amount_negative, displayAmount));
        } else {
            txAmountText.set(viewModel.getApplication().getString(R.string.tx_list_amount_positive, displayAmount));
        }

        if ((entity.fee > 0)) {
            String fee = Helper.getDisplayAmount(entity.fee, 5);
            txFeeText.set(viewModel.getApplication().getString(R.string.tx_list_fee, fee));
            txFeeVisibility.set(View.VISIBLE);
        } else {
            txFeeText.set("");
            txFeeVisibility.set(View.GONE);
        }

        if (entity.isFailed) {
            txAmountText.set(viewModel.getApplication().getString(R.string.tx_list_amount_failed, displayAmount));
            txFeeText.set(viewModel.getApplication().getString(R.string.tx_list_failed_text));
            txFeeVisibility.set(View.VISIBLE);
            txAmountTextColor.set(R.color.tx_failed);
        } else if (entity.isPending) {
            txAmountTextColor.set(R.color.tx_pending);
        } else if (entity.direction == TransactionInfo.Direction.Direction_In) {
            txAmountTextColor.set(R.color.tx_green);
        } else {
            txAmountTextColor.set(R.color.tx_red);
        }

        if ((userNotes.note.isEmpty())) {
            txPaymentidText.set(entity.paymentId.equals("0000000000000000") ?
                    (entity.subaddress != 0 ?
                            (viewModel.getApplication().getString(R.string.tx_subaddress, entity.subaddress)) :
                            "") :
                    entity.paymentId);
        } else {
            txPaymentidText.set(userNotes.note);
        }

        txDatetimeText.set(getDateTime(entity.timestamp));
    }



    private String getDateTime(long time) {
        return DATETIME_FORMATTER.format(new Date(time * 1000));
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            viewModel.listener.onInteraction(view,entity.get());
        }
    });
}
