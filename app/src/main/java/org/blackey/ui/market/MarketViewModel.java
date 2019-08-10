package org.blackey.ui.market;

import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.vondear.rxtool.view.RxToast;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.databinding.FragmentMarketBinding;
import org.blackey.entity.Banner;
import org.blackey.entity.Currency;
import org.blackey.service.DictApiService;
import org.blackey.ui.login.LoginActivity;
import org.blackey.ui.market.my.MyAdsFragment;
import org.blackey.ui.market.popupwindowMenu.PopUpMenuBean;
import org.blackey.ui.market.popupwindowMenu.PopupWindowMenuUtil;
import org.blackey.ui.market.search.MarketSearchActivity;
import org.blackey.ui.market.toolbar.MarketToolbarViewModel;
import org.blackey.ui.market.trust.TrustManageFragment;
import org.blackey.ui.wallet.WalletActivity;
import org.blackey.ui.webview.WebViewActivity;
import org.blackey.utils.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;


/**
 * 市场
 */
public class MarketViewModel extends MarketToolbarViewModel {

    public FragmentMarketBinding binding;

    public ObservableField<String> priceText = new ObservableField<>();

    public View spinnerview;

    List<String> currencys = new ArrayList<>();

    List<String> prices = new ArrayList<>();

    public ObservableField<String> usdText = new ObservableField<>("...");
    public ObservableField<String> cnyText = new ObservableField<>("...");
    public ObservableField<String> eurText = new ObservableField<>("...");
    public ObservableField<String> gbpText = new ObservableField<>("...");
    public ObservableField<String> jpyText = new ObservableField<>("...");
    public ObservableField<String> krwText = new ObservableField<>("...");
    public ObservableField<String> audText = new ObservableField<>("...");
    public ObservableField<String> hkdText = new ObservableField<>("...");
    public ObservableField<String> twdText = new ObservableField<>("...");
    public ObservableField<String> sgdText = new ObservableField<>("...");
    public ObservableField<String> idrText = new ObservableField<>("...");
    public ObservableField<String> myrText = new ObservableField<>("...");



    public SwipeRefreshLayout swipeRefresh;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();


    public void initBanner(final BGABanner bannerMainAlpha) {

        testOkhttpGet();

        RetrofitClient.getInstance().create(DictApiService.class)
                .getBanners()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<Banner>>>() {
                    @Override
                    public void accept(BaseResponse<List<Banner>> response) throws Exception {

                        //请求成功
                        if(response.isOk()){

                            List<String> titles = new ArrayList<>();

                            for(Banner banner : response.getBody()){
                                titles.add(banner.getTitle());
                            }



                            bannerMainAlpha.setData(response.getBody(),titles);
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

    private void setPriceText() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getApplication().getString(R.string.transaction_value)).append(":  ");
        for(int i= 0;i < currencys.size();i++){
            stringBuffer.append(currencys.get(i)).append(":").append(prices.get(i)).append("  ");
        }

        priceText.set(stringBuffer.toString());
    }


    public class UIChangeObservable {
        //下拉刷新完成
        public ObservableArrayList finishRequestNetWork = new ObservableArrayList();

        public ObservableBoolean finishRefreshing = new ObservableBoolean(false);
    }

    public MarketViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     */
    public void initToolbar() {
        //初始化标题栏
        setTitleText(getApplication().getString(R.string.title_market));

    }
    public void requestNetWork() {

        RetrofitClient.getInstance().create(DictApiService.class)
                .currencys()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<Currency>>>() {
                    @Override
                    public void accept(BaseResponse<List<Currency>> response) throws Exception {
                        //请求成功
                        if(response.isOk()){

                            List<Currency> list = response.getBody();

                            uc.finishRequestNetWork.addAll(list);
                            for(Currency currency:list){
                                currencys.add(currency.getCurrencyNameEn());
                                prices.add("-");
                            }
                            setPriceText();
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
     * 搜索按钮点击事件
     */
    @Override
    public BindingCommand rightSearchOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {
                startActivity(MarketSearchActivity.class);
            }
        });
    }

    /**
     * 刷新事件
     */
    public BindingCommand swipeOnRefresh() {
        return new BindingCommand(new BindingAction() {
            @Override
            public void call() {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //uc.finishRefreshing.set(!uc.finishRefreshing.get());

                    }
                }, 10);
            }
        });
    }

    /**
     * 添加按钮点击事件
     */
    @Override
    public BindingCommand rightAddOnClick() {
        return new BindingCommand(new BindingConsumer<View>() {
            @Override
            public void call(View view) {

                final ArrayList<PopUpMenuBean> menuList = new ArrayList<PopUpMenuBean>();

                PopUpMenuBean popUpMenuBean = new PopUpMenuBean();
                popUpMenuBean.setImgResId(R.mipmap.publish);
                popUpMenuBean.setItemStr(getApplication().getString(R.string.publish));
                menuList.add(popUpMenuBean);

                /*PopUpMenuBean popUpMenuBean1 = new PopUpMenuBean();
                popUpMenuBean1.setImgResId(R.mipmap.advertising);
                popUpMenuBean1.setItemStr(getApplication().getString(R.string.advertising));
                menuList.add(popUpMenuBean1);
*/
                PopUpMenuBean popUpMenuBean2 = new PopUpMenuBean();
                popUpMenuBean2.setImgResId(R.mipmap.trust);
                popUpMenuBean2.setItemStr(getApplication().getString(R.string.trust));
                menuList.add(popUpMenuBean2);

                PopupWindowMenuUtil.showPopupWindows(getApplication().getApplicationContext(),spinnerview, menuList, new PopupWindowMenuUtil.OnListItemClickLitener() {
                    @Override
                    public void onListItemClick(int position) {
                        //如果position == -1，预留位，用来标明是点击弹出框外面的区域
                        if(position != -1) {
                            if(BlackeyApplication.getCurrent()==null){
                                startActivity(LoginActivity.class);
                                return;
                            }

                            if(menuList.get(position).getImgResId() == R.mipmap.publish){

                                Bundle mBundle = new Bundle();
                                mBundle.putString("action","ReleaseAdvertiseFragment");
                                startActivity(WalletActivity.class,mBundle);
                            }else if(menuList.get(position).getImgResId() == R.mipmap.advertising){
                                startContainerActivity(MyAdsFragment.class.getCanonicalName());
                            }else if(menuList.get(position).getImgResId() == R.mipmap.trust){
                                startContainerActivity(TrustManageFragment.class.getCanonicalName());
                            }
                        }
                    }
                });

            }
        });
    }

    /**
     * 测试okhttp的get方法
     */
    private void testOkhttpGet() {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=monero&vs_currencies=USD,CNY,EUR,GBP,JPY,KRW,AUD,HKD,TWD,SGD,IDR,MYR";
        OkHttpClient okHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).get().build();//.get()可以不要，Builder的默认构造方法里面就是get请求
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {


            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                JSONObject object = JSONObject.parseObject(response.body().string());
                JSONObject monero = object.getJSONObject("monero");
                usdText.set(monero.getString("usd"));
                cnyText.set(monero.getString("cny"));
                eurText.set(monero.getString("eur"));
                gbpText.set(monero.getString("gbp"));
                jpyText.set(monero.getString("jpy"));
                krwText.set(monero.getString("krw"));
                audText.set(monero.getString("aud"));
                hkdText.set(monero.getString("hkd"));
                twdText.set(monero.getString("twd"));
                sgdText.set(monero.getString("sgd"));
                idrText.set(monero.getString("idr"));
                myrText.set(monero.getString("myr"));

            }
        });

    }

    public BindingCommand moreClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {

            Bundle bundle= new Bundle();
            bundle.putString("URL","https://www.coingecko.com/en/coins/monero");
            startActivity(WebViewActivity.class,bundle);

        }
    });

}
