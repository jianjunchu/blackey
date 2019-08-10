package me.goldze.mvvmhabit.binding.viewadapter.textview;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

public class ViewAdapter {

    @BindingAdapter({"textColor"})
    public static void onRefreshCommand(TextView view, int resId) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), resId));

    }
}
