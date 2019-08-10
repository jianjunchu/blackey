package org.blackey.ui.market.sell;

import android.app.Activity;
import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import org.blackey.R;
import org.blackey.app.BlackeyApplication;
import org.blackey.entity.Advertise;
import org.blackey.entity.Currency;
import org.blackey.model.request.AdvertiseRequest;
import org.blackey.service.AdvertiseApiService;
import org.blackey.service.DictApiService;
import org.blackey.ui.choose.paymentMode.ChoosePaymentModeListActivity;
import org.blackey.ui.choose.paymentMode.ChoosePaymentModeListViewModel;
import org.blackey.ui.wallet.send.SendFragment;
import org.blackey.utils.RetrofitClient;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.m2049r.xmrwallet.data.TxData;
import com.m2049r.xmrwallet.data.UserNotes;
import com.m2049r.xmrwallet.model.PendingTransaction;
import com.m2049r.xmrwallet.model.Wallet;
import com.m2049r.xmrwallet.service.WalletService;
import com.m2049r.xmrwallet.util.Helper;
import com.vondear.rxtool.view.RxToast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * 市场
 */
public class ReleaseAdvertiseViewModel extends BaseViewModel {

    public static final String TOKEN_SELECT_PAYMENT_MODE = "token_select_payment_mode";

    public TxData txData = new TxData();

    public Activity activity;

    public Advertise advertise;

    public SellConfirmationDialog dialog;

    public long unlockedBalance;

    private OptionsPickerView  sunPickerView;
    private OptionsPickerView  monPickerView;
    private OptionsPickerView  tuesPickerView;
    private OptionsPickerView  wedPickerView;
    private OptionsPickerView  thurPickerView;
    private OptionsPickerView  friPickerView;
    private OptionsPickerView  satPickerView;

    private OptionsPickerView  currencyPickerView;


    private ArrayList<String> times = new ArrayList<>();
    private List<Currency> currencies = new ArrayList<>();
    private List<String> currencyNames = new ArrayList<>();

    public AdvertiseRequest request = new AdvertiseRequest();

    public ObservableField<String> hintFormMarketText = new ObservableField<>();

    public ObservableField<String> offerOrderText = new ObservableField<>("");
    public ObservableField<Integer> offerOrderVisibility = new ObservableField<>(View.GONE);

    public ObservableField<Boolean> anytime = new ObservableField<>(true);
    public ObservableField<Boolean> custom = new ObservableField<>(false);
    public ObservableField<Integer> customVisibility = new ObservableField<>(View.GONE);


    public ObservableField<String> sunSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> sunEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> monSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> monEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> tuesSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> tuesEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> wedSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> wedEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> thurSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> thurEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> friSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> friEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> satSatrtTimeText = new ObservableField<>("00:00");
    public ObservableField<String> satEndTimeText = new ObservableField<>("23:59");

    public ObservableField<String> currencyText = new ObservableField<>("");

    public ObservableField<Integer> icon1 = new ObservableField<>(R.mipmap.payment_cash_deposit);
    public ObservableField<Integer> icon2 = new ObservableField<>(R.mipmap.payment_cash_deposit);
    public ObservableField<Integer> icon3 = new ObservableField<>(R.mipmap.payment_cash_deposit);


    public ObservableField<Integer> hintPaymentModeVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> paymentModeListVisibility = new ObservableField<>(View.GONE);

    public ObservableField<Integer> paymentModeIcon1Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeIcon2Visibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> paymentModeIcon3Visibility = new ObservableField<>(View.GONE);


    ReleaseAdvertiseFragment.Listener listenerCallback = null;



    public ReleaseAdvertiseViewModel(@NonNull Application application) {
        super(application);
    }

    private WalletService mBoundService = null;
    private boolean mIsBound = false;



    public void initData() {

        String tradingFee = "0.7%";
        if(BlackeyApplication.getSystemConfig()!=null && BlackeyApplication.getSystemConfig().getTradingFee()!=null){
            tradingFee = BlackeyApplication.getSystemConfig().getTradingFee().stripTrailingZeros().toPlainString()+"%";
        }

        String adfee  = "0.00003";
        if(BlackeyApplication.getSystemConfig()!=null && BlackeyApplication.getSystemConfig().getAdFee()!=null){
            adfee =  BlackeyApplication.getSystemConfig().getAdFee().stripTrailingZeros().toPlainString();
        }

        hintFormMarketText.set(getApplication().getString(R.string.hint_form_market,adfee,tradingFee));

        getNoLinkData();
        initTimeOptionsPicker();
        initCurrencyOptions();

        request.setOpenTime(AdvertiseRequest.OPEN_TIME_ANY);

        //周日
        request.setSunSatrtTime("00:00:00");
        request.setSunEndTime("23:59:00");
        //周一
        request.setMonSatrtTime("00:00:00");
        request.setMonEndTime("23:59:00");
        //周二
        request.setTuesSatrtTime("00:00:00");
        request.setTuesEndTime("23:59:00");
        //周三
        request.setWedSatrtTime("00:00:00");
        request.setWedEndTime("23:59:00");
        //周四
        request.setThurSatrtTime("00:00:00");
        request.setThurEndTime("23:59:00");
        //周五
        request.setFriSatrtTime("00:00:00");
        request.setFriEndTime("23:59:00");
        //周六
        request.setSatSatrtTime("00:00:00");
        request.setSatEndTime("23:59:00");

        Messenger.getDefault().register(this, ReleaseAdvertiseViewModel.TOKEN_SELECT_PAYMENT_MODE, new BindingAction() {
            @Override
            public void call() {

                request.setPaymentModeId1(null);
                request.setPaymentModeId2(null);
                request.setPaymentModeId3(null);

                paymentModeIcon1Visibility.set(View.GONE);
                paymentModeIcon2Visibility.set(View.GONE);
                paymentModeIcon3Visibility.set(View.GONE);

                hintPaymentModeVisibility.set(View.VISIBLE);
                paymentModeListVisibility.set(View.GONE);

                if(ChoosePaymentModeListViewModel.selected.size()>0){
                    hintPaymentModeVisibility.set(View.GONE);
                    paymentModeListVisibility.set(View.VISIBLE);
                    paymentModeIcon1Visibility.set(View.VISIBLE);
                    icon1.set(ChoosePaymentModeListViewModel.selected.get(0).imageViewSrc.get());
                    request.setPaymentModeId1(ChoosePaymentModeListViewModel.selected.get(0).entity.get().getUserPaymentModeId());
                }
                if(ChoosePaymentModeListViewModel.selected.size()>1){
                    hintPaymentModeVisibility.set(View.GONE);
                    paymentModeListVisibility.set(View.VISIBLE);
                    paymentModeIcon2Visibility.set(View.VISIBLE);
                    icon2.set(ChoosePaymentModeListViewModel.selected.get(1).imageViewSrc.get());
                    request.setPaymentModeId2(ChoosePaymentModeListViewModel.selected.get(1).entity.get().getUserPaymentModeId());
                }
                if(ChoosePaymentModeListViewModel.selected.size()>2){
                    hintPaymentModeVisibility.set(View.GONE);
                    paymentModeListVisibility.set(View.VISIBLE);
                    paymentModeIcon3Visibility.set(View.VISIBLE);
                    icon3.set(ChoosePaymentModeListViewModel.selected.get(2).imageViewSrc.get());
                    request.setPaymentModeId3(ChoosePaymentModeListViewModel.selected.get(2).entity.get().getUserPaymentModeId());
                }


            }
        });

    }



    public BindingCommand selSundayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            sunPickerView.show();
        }
    });

    public BindingCommand selMondayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            monPickerView.show();
        }
    });

    public BindingCommand selTuesdayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            tuesPickerView.show();
        }
    });

    public BindingCommand selWednesdayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            wedPickerView.show();
        }
    });

    public BindingCommand selThursdayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            thurPickerView.show();
        }
    });

    public BindingCommand selFridayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            friPickerView.show();
        }
    });

    public BindingCommand selSaturdayOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            satPickerView.show();
        }
    });

    public BindingCommand<Boolean> anytimeCheckedChangeCommand = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(Boolean isChecked) {
            custom.set(!isChecked);
            anytime.set(isChecked);
            if(isChecked){
                customVisibility.set(View.GONE);
                request.setOpenTime(AdvertiseRequest.OPEN_TIME_ANY);
            }

        }
    });

    public BindingCommand<Boolean> customCheckedChangeCommand = new BindingCommand(new BindingConsumer<Boolean>() {

        @Override
        public void call(Boolean isChecked) {
            custom.set(isChecked);
            anytime.set(!isChecked);
            if(isChecked){
                customVisibility.set(View.VISIBLE);
                request.setOpenTime(AdvertiseRequest.OPEN_TIME_CUSTOM);
            }

        }
    });



    public BindingCommand selectCurrencyOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            currencyPickerView.show();

        }
    });

    public BindingCommand selectPaymentModeOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            startActivity(ChoosePaymentModeListActivity.class);

        }
    });



    public BindingCommand confirmOnClickCommand = new BindingCommand(new BindingConsumer<View>() {

        @Override
        public void call(View view) {
            submit();

        }
    });
    private void submit() {

        if (TextUtils.isEmpty(request.getPrice())) {
            RxToast.showToast(R.string.hint_unit_price);
            return;
        }

        if (TextUtils.isEmpty(request.getTotalInventory())) {
            RxToast.showToast(R.string.hint_total_inventory);
            return;
        }

        if (TextUtils.isEmpty(request.getMinLimit())) {
            request.setMinLimit(request.getTotalInventory());
        }

        if (request.getPaymentModeId1() ==null) {
            RxToast.showToast(R.string.hint_payment_term);
            return;
        }

        if (TextUtils.isEmpty(request.getLeaveWord())) {
            RxToast.showToast(R.string.market_explain);
            return;
        }

        dialog  = new SellConfirmationDialog(activity);
        dialog.initSpinnerData();

        dialog.getmTradingValue().setText(request.getPrice());
        dialog.getmTradingValueUnit().setText(currencyText.get());

        dialog.getmTotalsold().setText(request.getTotalInventory());

        BigDecimal price = new BigDecimal(request.getPrice());
        BigDecimal totalInventory = new BigDecimal(request.getTotalInventory());
        BigDecimal grossAmount = price.multiply(totalInventory);
        dialog.getmGrossAmount().setText(grossAmount.stripTrailingZeros().toPlainString());

        dialog.getmMinimumQuantity().setText(request.getMinLimit());


        BigDecimal minLimit = new BigDecimal(request.getMinLimit());
        BigDecimal minimumAmount = price.multiply(minLimit);
        dialog.getmMinimumAmount().setText(minimumAmount.stripTrailingZeros().toPlainString());
        dialog.getmMinimumAmountUnit().setText(currencyText.get());


        dialog.getmTvSure().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!dialog.isSync()){

                    Helper.promptPassword(activity, dialog.getSpinnerData(), false,
                            new Helper.PasswordAction() {
                                @Override
                                public void action(String walletName, String password, boolean fingerprintUsed) {
                                    releaseAdvertise(walletName,password,fingerprintUsed);
                                }
                            });
                }
            }
        });
        dialog.getmTvCancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dialog.isSync()){
                    dialog.cancel();
                }

            }
        });
        dialog.show();
    }

    /**
     * 后台服务端创建一个临时钱包。临时钱包密钥密文方式存储在数据库中。
     * @param walletName
     * @param password
     * @param fingerprintUsed
     */
    private void releaseAdvertise(final String walletName, final String password, boolean fingerprintUsed) {


        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .create(request)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Advertise>>() {
                    @Override
                    public void accept(BaseResponse<Advertise> response) throws Exception {
                        dismissDialog();
                        //请求成功
                        if(response.isOk()){
                            dialog.getmTvSure().setEnabled(false);
                            advertise = response.getBody();
                            request.setAdId(advertise.getAdId());

                            txData.setDestinationAddress(advertise.getCashDepositAddress());
                            txData.setAmount(Wallet.getAmountFromString(advertise.getTotalInventory().toPlainString()));
                            txData.setUserNotes(new UserNotes(advertise.getAdId().toString()));
                            txData.setPriority(PendingTransaction.Priority.Priority_Default);
                            txData.setMixin(SendFragment.MIXIN);
                            txData.setPaymentId(Wallet.generatePaymentId());


                            listenerCallback.startWalletService(walletName,password);

                        }else if (500 == response.getCode()){
                            RxToast.error(response.getMessage());
                        }
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dialog.getmTvSure().setEnabled(true);
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dialog.getmTvSure().setEnabled(true);
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }

    private void prepareSend(String walletName, String password) {
    }


    public void initCurrencyOptions() {
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
                            initCurrencyOptionsPicker(response.getBody());

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

    private void initCurrencyOptionsPicker(final List<Currency> list) {

        if(list ==null || list.size()==0){
            return;
        }

        request.setCurrencyId(list.get(0).getCurrencyId());
        currencyText.set(list.get(0).getCurrencyNameEn());

        for(Currency currency : list){
            currencies.add(currency);
            currencyNames.add(currency.getCurrencyNameEn());
        }

        currencyPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                KLog.i(currencyNames.get(options1));
                request.setCurrencyId(list.get(options1).getCurrencyId());
                currencyText.set(currencyNames.get(options1));
            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 0, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        currencyPickerView.setNPicker(currencyNames,null,null);
    }


    public void initTimeOptionsPicker() {// 不联动的多级选项
        sunPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {


                if(options1 ==0 || options2 ==0){
                    request.setSunSatrtTime("00:00:00");
                    request.setSunEndTime("00:00:00");
                    sunSatrtTimeText.set(times.get(0));
                    sunEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setSunSatrtTime(times.get(options1)+":00");
                    request.setSunEndTime(times.get(options2)+":00");
                    sunSatrtTimeText.set(times.get(options1));
                    sunEndTimeText.set(times.get(options2));
                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        sunPickerView.setNPicker(times,times,null);

        monPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1 ==0 || options2==0){
                    request.setMonSatrtTime("00:00:00");
                    request.setMonEndTime("00:00:00");
                    monSatrtTimeText.set(times.get(0));
                    monEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setMonSatrtTime(times.get(options1)+":00");
                    request.setMonEndTime(times.get(options2)+":00");
                    monSatrtTimeText.set(times.get(options1));
                    monEndTimeText.set(times.get(options2));
                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        monPickerView.setNPicker(times,times,null);



        tuesPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1 ==0 || options2 ==0){
                    request.setTuesSatrtTime("00:00:00");
                    request.setTuesEndTime("00:00:00");
                    tuesSatrtTimeText.set(times.get(0));
                    tuesEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setTuesSatrtTime(times.get(options1)+":00");
                    request.setTuesEndTime(times.get(options2)+":00");
                    tuesSatrtTimeText.set(times.get(options1));
                    tuesEndTimeText.set(times.get(options2));
                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        tuesPickerView.setNPicker(times,times,null);

        wedPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1 ==0 || options2 ==0){
                    request.setWedSatrtTime("00:00:00");
                    request.setWedEndTime("00:00:00");
                    wedSatrtTimeText.set(times.get(0));
                    wedEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setWedSatrtTime(times.get(options1)+":00");
                    request.setWedEndTime(times.get(options2)+":00");
                    wedSatrtTimeText.set(times.get(options1));
                    wedEndTimeText.set(times.get(options2));
                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        wedPickerView.setNPicker(times,times,null);

        thurPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1 ==0 || options2 ==0){
                    request.setThurSatrtTime("00:00:00");
                    request.setThurEndTime("00:00:00");
                    thurSatrtTimeText.set(times.get(0));
                    thurEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setThurSatrtTime(times.get(options1)+":00");
                    request.setThurEndTime(times.get(options2)+":00");
                    thurSatrtTimeText.set(times.get(options1));
                    thurEndTimeText.set(times.get(options2));

                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        thurPickerView.setNPicker(times,times,null);

        friPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1==0 || options2==0){
                    request.setFriSatrtTime("00:00:00");
                    request.setFriEndTime("00:00:00");
                    friSatrtTimeText.set(times.get(0));
                    friEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setFriSatrtTime(times.get(options1)+":00");
                    request.setFriEndTime(times.get(options2)+":00");
                    friSatrtTimeText.set(times.get(options1));
                    friEndTimeText.set(times.get(options2));
                }

            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        friPickerView.setNPicker(times,times,null);

        satPickerView = new OptionsPickerBuilder(activity, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                if(options1==0 || options2==0){
                    request.setSatSatrtTime("00:00:00");
                    request.setSatEndTime("00:00:00");
                    satSatrtTimeText.set(times.get(0));
                    satEndTimeText.set(times.get(0));
                }else if(options2 < options1){
                    RxToast.showToast(R.string.error_select_time);
                    return ;
                }else{
                    request.setSatSatrtTime(times.get(options1)+":00");
                    request.setSatEndTime(times.get(options2)+":00");
                    satSatrtTimeText.set(times.get(options1));
                    satEndTimeText.set(times.get(options2));
                }
            }
        })

                .setCancelText(getApplication().getString(R.string.cancel))//取消按钮文字
                .setSubmitText(getApplication().getString(R.string.confirm))//确认按钮文字
                .setTitleColor(R.color.colorPrimaryDark)//标题文字颜色
                .setSubmitColor(R.color.colorPrimaryDark)//确定按钮文字颜色
                .setCancelColor(R.color.colorPrimaryDark)//取消按钮文字颜色
                .setSelectOptions(1, 2, 0)
                .setTitleText(getApplication().getString(R.string.opening_hours))
                .build();
        satPickerView.setNPicker(times,times,null);


    }


    private void getNoLinkData() {
        times.add("关闭");
        for(int i = 0;i < 24;i++){
            times.add(String.format("%02d:00",i));
        }
        times.add("23:59");
    }


    public void onTransactionSent(String txId, String txkey) {

        RetrofitClient.getInstance().create(AdvertiseApiService.class)
                .transactionSent(advertise.getAdId(),txId,txkey)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog(getApplication().getString(R.string.loading));
                    }
                })
                .subscribe(new Consumer<BaseResponse<Advertise>>() {
                    @Override
                    public void accept(BaseResponse<Advertise> response) throws Exception {
                        dismissDialog();
                        //请求成功
                        if(response.isOk()){
                            RxToast.success(getApplication().getString(R.string.advertise_create_success));
                            finish();
                        }else if (500 == response.getCode()){
                            RxToast.error(response.getMessage());
                        }
                    }
                }, new Consumer<ResponseThrowable>() {
                    @Override
                    public void accept(ResponseThrowable throwable) throws Exception {
                        dialog.getmTvSure().setEnabled(true);
                        dismissDialog();
                        //请求刷新完成收回
                        throwable.printStackTrace();
                        RxToast.error(throwable.message);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        dialog.getmTvSure().setEnabled(true);
                        //关闭对话框
                        dismissDialog();
                        //请求刷新完成收回

                    }
                });
    }
}
