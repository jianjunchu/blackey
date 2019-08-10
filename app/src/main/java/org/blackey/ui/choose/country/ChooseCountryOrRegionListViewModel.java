package org.blackey.ui.choose.country;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.blackey.BR;
import org.blackey.R;
import org.blackey.entity.CountryOrRegion;
import org.blackey.service.DictApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.SystemLanguageUtil;
import org.blackey.utils.strategy.ISortStrategy;
import org.blackey.utils.strategy.impl.EnglishSortStrategy;
import org.blackey.utils.strategy.impl.PinyinSortStrategy;
import org.blackey.utils.strategy.impl.StrokeSortStrategy;
import com.vondear.rxtool.RxTool;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ChooseCountryOrRegionListViewModel extends ToolbarViewModel {

    public String returnToken;

    private List<CountryOrRegion> mSourceDateList;

    private ISortStrategy sortStrategy;

    private RecyclerView sortListView;

    //给RecyclerView添加ObservableList
    public ObservableList<ChooseCountryOrRegionListItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ChooseCountryOrRegionListItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_sort_listview);
    //RecyclerView多布局写法
//    public ItemBinding<ItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<ItemViewModel>() {
//        @Override
//        public void onItemBind(ItemBinding itemBinding, int position, ItemViewModel item) {
//            if (position == 0) {
//                //设置头布局
//                itemBinding.set(BR.viewModel, R.layout.head_netword);
//            } else {
//                itemBinding.set(BR.viewModel, R.layout.item_network);
//            }
//        }
//    });
    //给RecyclerView添加Adpter，请使用自定义的Adapter继承BindingRecyclerViewAdapter，重写onBindBinding方法
    public final BindingRecyclerViewAdapter<ChooseCountryOrRegionListItemViewModel> adapter = new BindingRecyclerViewAdapter<>();


    public ChooseCountryOrRegionListViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.choose_country));
    }

    public void requestNetWork() {

        RetrofitClient.getInstance().create(DictApiService.class)
                .countrys()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<CountryOrRegion>>>() {
                    @Override
                    public void accept(BaseResponse<List<CountryOrRegion>> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            initAndShowList(response.getBody());
                        }
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //关闭对话框
                        //请求刷新完成收回
                    }
                });

    }

    /**
     * 初始化数据并显示
     */
    private void initAndShowList(List<CountryOrRegion> countryOrRegionList) {
        //初始化排序策略
        sortStrategy = initSortStrategy();
        Observable.just(countryOrRegionList)
                .map(new Func1<List<CountryOrRegion>, List<CountryOrRegion>>() {
                    @Override
                    public List<CountryOrRegion> call(List<CountryOrRegion> countryOrRegionList) {
                        //填充数据
                        mSourceDateList = filledData(countryOrRegionList);
                        //排序
                        if (mSourceDateList != null) {
                            mSourceDateList = sortStrategy.getSortedCountryOrRegionList(mSourceDateList);
                        }
                        return mSourceDateList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CountryOrRegion>>() {
                    @Override
                    public void call(List<CountryOrRegion> countryOrRegions) {

                        for(CountryOrRegion item : countryOrRegions){

                            if(hasSortLetters(observableList,item)){
                                item.setVisibilityObservable(View.GONE);
                            }
                            item.setShowAreaCode("+"+item.getAreaCode());
                            ChooseCountryOrRegionListItemViewModel itemViewModel = new ChooseCountryOrRegionListItemViewModel(ChooseCountryOrRegionListViewModel.this, item);
                            //双向绑定动态添加Item
                            observableList.add(itemViewModel);
                        }

                    }
                });
    }

    private boolean hasSortLetters(ObservableList<ChooseCountryOrRegionListItemViewModel> observableList, CountryOrRegion item) {
        for(ChooseCountryOrRegionListItemViewModel chooseCountryOrRegionListItemViewModel : observableList){
            if(item.getSortLetters().equals(chooseCountryOrRegionListItemViewModel.entity.get().getSortLetters())){
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化排序策略
     */
    private ISortStrategy initSortStrategy() {
        //获取语言地区代码，比如：zh-CN zh-HK en-US
        String localLanguageAndCountry = SystemLanguageUtil.getLocalLanguageAndCountry(RxTool.getContext());
        KLog.d( "localLanguageAndCountry = " + localLanguageAndCountry);
        //如果是使用繁体字的几个地区的话，则使用  笔画排序
        if (localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_TW_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_HK_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_MO_VALUE)
                || localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_HANT)) {
            KLog.d( "使用笔画排序");
            return new StrokeSortStrategy();
        } else if (localLanguageAndCountry.contains(SystemLanguageUtil.LANGUAGE_ZH_CN_VALUE_DETAILS)) {
            //简体中文 使用 拼音排序
            KLog.d( "使用拼音排序");
            return new PinyinSortStrategy();
        } else {
            //其他的都使用 英文排序
            KLog.d( "使用英文排序");
            return new EnglishSortStrategy();
        }
    }

    /**
     * 为ListView填充数据
     */
    private List<CountryOrRegion> filledData(List<CountryOrRegion> mCountryOrRegionList) {
        List<CountryOrRegion> mSortList = new ArrayList<>();
        if (mCountryOrRegionList == null) {
            return mSortList;
        }
        for (int i = 0; i < mCountryOrRegionList.size(); i++) {
            CountryOrRegion sortModel = mCountryOrRegionList.get(i);
            //获取到要显示的国家或地区的名称
            String name = ResourcesUtils.getCountryNameByCountryCode(RxTool.getContext(), sortModel.getCountryCode());
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            sortModel.setName(name);

            //根据不同的策略，返回不同语言下的中文汉字的笔画
            sortModel.setStrokeCount(sortStrategy.getStrokeCount(name, RxTool.getContext()));
            //根据不同的策略，返回不同的pinyin或者English
            String pinyin = sortStrategy.getPinyinOrEnglish(sortModel);
            sortModel.setPinyinName(pinyin);
            //根据不同的策略，返回拼音排序或者英文排序需要展示的 英文首字母
            String sortLetters = sortStrategy.getSortLetters(pinyin);
            sortModel.setSortLetters(sortLetters);

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    public void dealSideBarItemSelected(String s) {
        // 该字母首次出现的位置
        int position = sortStrategy.getSideBarSortSectionFirstShowPosition(mSourceDateList, s);
        if (position != -1) {
            sortListView.scrollToPosition(position);
        }
    }


    public RecyclerView getSortListView() {
        return sortListView;
    }

    public void setSortListView(RecyclerView sortListView) {
        this.sortListView = sortListView;
    }
}
