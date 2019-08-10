package com.sinothk.switchTabView.style1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.sinothk.switchTabView.R;

/**
 * TabTextView
 * Created by D on 2017/8/25.
 */
public class TabTextView extends View implements TabView {
    private int width;
    private int height;

    private Paint paint;
    private String text = "title";
    private float textHeight;

    /**
     * define
     */
    private static float textSize;//title文字大小
    private static int textColor;//title文字颜色
    private static int textColorFocus;//title文字颜色
    private int padding;//title文字左右预留间距

    public TabTextView(Context context) {
        this(context, null);
    }

    public TabTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        textSize = UIUtil.sp2px(context, 15);//textSize == 0 ? UIUtil.dip2px(context, 15) : textSize;

        textColor = ContextCompat.getColor(context, R.color.lib_ui_common_color_text);
        textColorFocus = ContextCompat.getColor(context, R.color.lib_ui_common_color_accent);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setColor(textColor);

        textHeight = UIUtil.getTextHeight(paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = width / 2f;
        float y = height / 2f + textHeight / 2f;
        canvas.drawText(text, x, y, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = UIUtil.getTextWidth(text, paint) + padding * 2;
        }
        height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public static void setTextColor(int tc, int tcFocus) {
        textColor = tc;
        textColorFocus = tcFocus;
    }

    @Override
    public void setPadding(int padding) {
        this.padding = padding;
    }

    @Override
    public void setNumber(String text, int visibility) {

    }

    @Override
    public void notifyData(boolean focus) {
//
//        if (focus) {
//            this.paint.setColor(focus ? textColorFocus : textColor);
//            tvTitle.setTextColor(textColorFocus);
//        } else {
//            this.paint.setColor(focus ? textColorFocus : textColor);
//            tvTitle.setTextColor(textColor);
//        }

        this.paint.setColor(focus ? textColorFocus : textColor);
        invalidate();
    }

    @Override
    public void onScroll(float factor) {

    }
}
