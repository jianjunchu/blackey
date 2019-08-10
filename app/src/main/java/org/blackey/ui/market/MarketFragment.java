package org.blackey.ui.market;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.databinding.FragmentMarketBinding;
import org.blackey.entity.Banner;
import org.blackey.entity.Currency;
import org.blackey.ui.base.BlackeyBaseFragment;
import org.blackey.ui.base.adapter.BaseFragmentPagerAdapter;
import org.blackey.ui.market.list.MarketListFragment;
import org.blackey.ui.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * 市场
 */
public class MarketFragment extends BlackeyBaseFragment<FragmentMarketBinding,MarketViewModel> {

    private List<Fragment> mFragments;
    private List<String> titlePager;



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_market;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }




    @Override
    public void initData() {


        viewModel.binding = binding;
        viewModel.spinnerview =  binding.include.ivAddIcon ;
        //初始化标题
        viewModel.initToolbar();


        binding.bannerMainAlpha.setAdapter(new BGABanner.Adapter<ImageView, Banner>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, Banner model, int position) {

                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.holder)
                        //异常占位图(当加载异常的时候出现的图片)
                        .error(R.drawable.holder)
                        .centerCrop()
                        .dontAnimate()
                        //禁止Glide硬盘缓存缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                Glide.with(getActivity())
                        .load(model.getBannerImg())
                        .apply(options)
                        .into(itemView);
            }
        });

        binding.bannerMainAlpha.setDelegate(new BGABanner.Delegate<ImageView, Banner>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, Banner model, int position) {
                Bundle bundle= new Bundle();
                bundle.putString("URL",model.getLink());
                startActivity(WebViewActivity.class,bundle);
            }
        });

        viewModel.initBanner(binding.bannerMainAlpha);

        viewModel.requestNetWork();



    }



    private  void  initTab(ObservableArrayList<Currency> list){
        mFragments = pagerFragment(list);
        titlePager = pagerTitleString(list);
        //设置Adapter
        BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), mFragments, titlePager);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabs));
    }

    @Override
    public void initViewObservable() {

        viewModel.uc.finishRequestNetWork.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableArrayList<Currency>>() {

            @Override
            public void onChanged(ObservableArrayList<Currency> sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableArrayList<Currency> sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(ObservableArrayList<Currency> sender, int positionStart, int itemCount) {
                initTab(sender);
            }

            @Override
            public void onItemRangeMoved(ObservableArrayList<Currency> sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableArrayList<Currency> sender, int positionStart, int itemCount) {

            }
        });



    }

    protected List<Fragment> pagerFragment(ObservableArrayList<Currency> currencies) {
        List<Fragment> list = new ArrayList<>();
        int index = 0;
        for(Currency currency: currencies){
            list.add(new MarketListFragment(index,currency));
            index++;
        }

        return list;
    }

    protected List<String> pagerTitleString(ObservableArrayList<Currency> currencies) {
        List<String> list = new ArrayList<>();
        for(Currency currency: currencies){
            list.add(currency.getCurrencyNameEn());
        }

        return list;
    }

    //开始滚动
    @Override
    public void onResume() {
        super.onResume();

        //MarketListFragment fragment = (MarketListFragment) mFragments.get(binding.viewPager.getCurrentItem());

        //fragment.onResume();
        //binding.vtvPrice.startAutoScroll();
    }
    //停止滚动
    @Override
    public void onPause() {
        super.onPause();
       // binding.vtvPrice.stopAutoScroll();
    }
}
