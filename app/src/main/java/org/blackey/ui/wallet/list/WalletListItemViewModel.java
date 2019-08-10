package org.blackey.ui.wallet.list;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import org.blackey.R;
import com.m2049r.xmrwallet.model.WalletManager;

import java.net.UnknownHostException;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;

public class WalletListItemViewModel extends ItemViewModel<WalletListViewModel> {


    public ObservableField<WalletManager.WalletInfo> entity = new ObservableField<>();

    public WalletListItemViewModel(@NonNull WalletListViewModel viewModel) {
        super(viewModel);
    }

    public WalletListItemViewModel(@NonNull WalletListViewModel viewModel, WalletManager.WalletInfo entity) {
        super(viewModel);
        this.entity.set(entity);
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {

            try {
                viewModel.onWalletSelected(entity.get().getName(),false);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    });

    public BindingCommand optionsClick = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(View view) {
            if (viewModel.popupOpen) return;

            //creating a popup menu
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            //inflating menu from xml resource
            popup.inflate(R.menu.list_context_menu);
            viewModel.popupOpen = true;
            //adding click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (viewModel.listener != null) {
                        return viewModel.listener.onContextInteraction(item, entity.get());
                    }
                    return false;
                }
            });

            //displaying the popup
            popup.show();
            popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    viewModel.popupOpen = false;
                }
            });

        }
    });
}
