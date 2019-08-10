package org.blackey.ui.splash;

import android.os.Bundle;
import android.widget.ImageView;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.ActivityGuideBinding;
import org.blackey.ui.base.BlackeyBaseActivity;
import org.blackey.ui.main.MainAcitvity;
import com.vondear.rxtool.RxActivityTool;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

public class GuideActivity extends BlackeyBaseActivity<ActivityGuideBinding,GuideViewModel> {



    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_guide;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        setListener();
        processLogic();
    }

    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        binding.bannerGuideForeground.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                RxActivityTool.skipActivityAndFinish(GuideActivity.this, MainAcitvity.class);
                finish();
            }
        });
    }

    private void processLogic() {
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        binding.bannerGuideBackground.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_background_1,
                R.drawable.uoko_guide_background_2,
                R.drawable.uoko_guide_background_3);

        binding.bannerGuideForeground.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_foreground_1,
                R.drawable.uoko_guide_foreground_2,
                R.drawable.uoko_guide_foreground_3);
    }
}
