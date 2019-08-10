package me.goldze.mvvmhabit.binding.viewadapter.circleimageview;

import android.databinding.BindingAdapter;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import me.goldze.mvvmhabit.widget.CircleImageView;

public class ViewAdapter {

    @BindingAdapter(value = {"url", "placeholderRes"}, requireAll = false)
    public static void setImageUri(CircleImageView view, String url, int placeholderRes) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
            Glide.with(view.getContext())
                    .load(url)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .into(view);
        }
    }
}
