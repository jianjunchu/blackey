package me.goldze.mvvmhabit.binding.viewadapter.radiobutton;

import android.databinding.BindingAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import me.goldze.mvvmhabit.binding.command.BindingCommand;

/**
 * Created by goldze on 2017/6/18.
 */
public class ViewAdapter {

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioButton radioButton, final BindingCommand<Boolean> bindingCommand) {
        radioButton.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bindingCommand.execute(isChecked);
            }

        });
    }
}
