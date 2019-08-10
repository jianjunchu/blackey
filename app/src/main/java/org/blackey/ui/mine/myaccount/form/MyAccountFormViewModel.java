package org.blackey.ui.mine.myaccount.form;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.blackey.R;
import org.blackey.entity.Bank;
import org.blackey.entity.Currency;
import org.blackey.entity.PaymentMode;
import org.blackey.entity.UserPaymentMode;
import org.blackey.model.request.UserPaymentModeRequest;
import org.blackey.service.DictApiService;
import org.blackey.service.UserPaymentModeApiService;
import org.blackey.ui.base.viewmodel.ToolbarViewModel;
import org.blackey.utils.ResourcesUtils;
import org.blackey.utils.RetrofitClient;
import com.vondear.rxtool.view.RxToast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.binding.viewadapter.spinner.IKeyAndValue;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.RxUtils;

public class MyAccountFormViewModel extends ToolbarViewModel {

    public UserPaymentModeRequest request  = null;

    public Spinner spinnerCurrency;
    public Spinner spinnerAccountType;
    public Spinner spinnerBankName;

    public List<IKeyAndValue> paymentModes = new ArrayList<>();
    public List<IKeyAndValue> banks = new ArrayList<>();
    public List<IKeyAndValue> currencys = new ArrayList<>();

    public ObservableField<String> accountNameText = new ObservableField<>(getApplication().getString(R.string.account_name));
    public ObservableField<Integer> bankNameVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> openingBankVisibility = new ObservableField<>(View.GONE);

    public MyAccountFormViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化Toolbar
     * @param string
     */
    public void initToolbar(String string) {

        //初始化标题栏
        setTitleText(string);
    }

    public void initAccountTypeData() {

        RetrofitClient.getInstance().create(DictApiService.class)
                .paymentMode()
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<PaymentMode>>>() {
                    @Override
                    public void accept(final BaseResponse<List<PaymentMode>> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            paymentModes = new ArrayList<>();
                            for(PaymentMode paymentMode : response.getBody()){
                                if(!"CASH".equalsIgnoreCase(paymentMode.getCode())){
                                    paymentMode.setName(ResourcesUtils.getPaymentName(getApplication(),paymentMode.getCode()));
                                    paymentModes.add(paymentMode);
                                }
                            }

                            List<String> lists = new ArrayList<>();
                            for (IKeyAndValue iKeyAndValue : paymentModes) {
                                lists.add(iKeyAndValue.getKey());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(spinnerAccountType.getContext(), android.R.layout.simple_spinner_item, lists);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerAccountType.setAdapter(adapter);




                            spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    IKeyAndValue iKeyAndValue = paymentModes.get(position);
                                    //将IKeyAndValue对象交给ViewModel
                                    request.setPaymentModeId(iKeyAndValue.getValue());
                                    initPaymentModeView((PaymentMode) iKeyAndValue);

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            if (!TextUtils.isEmpty(request.getPaymentModeId())) {
                                for (int i = 0; i < paymentModes.size(); i++) {
                                    IKeyAndValue iKeyAndValue = paymentModes.get(i);
                                    if (request.getPaymentModeId().equals(iKeyAndValue.getValue())) {
                                        spinnerAccountType.setSelection(i);
                                        return;
                                    }
                                }
                            }else {
                                initPaymentModeView((PaymentMode) paymentModes.get(0));
                            }

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

    public void initBankNameData(String value) {

        RetrofitClient.getInstance().create(DictApiService.class)
                .listBankByCurrencyId(value)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new Consumer<BaseResponse<List<Bank>>>() {
                    @Override
                    public void accept(BaseResponse<List<Bank>> response) throws Exception {
                        //请求成功
                        if(response.isOk()){
                            banks = new ArrayList<>();
                            for(Bank bank : response.getBody()){
                                bank.setBankName(ResourcesUtils.getBankName(getApplication(),bank.getSwiftCode()));
                                banks.add(bank);
                            }

                            List<String> lists = new ArrayList<>();
                            for (IKeyAndValue iKeyAndValue : banks) {
                                lists.add(iKeyAndValue.getKey());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(spinnerBankName.getContext(), android.R.layout.simple_spinner_item, lists);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerBankName.setAdapter(adapter);


                            spinnerBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    IKeyAndValue iKeyAndValue = banks.get(position);
                                    request.setBankId(iKeyAndValue.getValue());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            if (!TextUtils.isEmpty(request.getBankId())) {
                                for (int i = 0; i < banks.size(); i++) {
                                    IKeyAndValue iKeyAndValue = banks.get(i);
                                    if (request.getBankId().equals(iKeyAndValue.getValue())) {
                                        spinnerBankName.setSelection(i);
                                        return;
                                    }
                                }
                            }

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



    public BindingCommand<Boolean> forPaymentCheckedChangeCommand = new BindingCommand<>(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean isChecked) {

            if(isChecked){
                request.setForPayment(1);
            }else {
                request.setForPayment(0);
            }
        }
    });

    public BindingCommand<Boolean> forReceivingCheckedChangeCommand = new BindingCommand<>(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean isChecked) {
            if(isChecked){
                request.setForCollection(1);
            }else {
                request.setForCollection(0);
            }
        }
    });



    private void initPaymentModeView(PaymentMode paymentMode) {

        if("BANKACCOUNT".equalsIgnoreCase(paymentMode.getCode())){
            accountNameText.set(getApplication().getString(R.string.account_name));
            bankNameVisibility.set(View.VISIBLE);
            openingBankVisibility.set(View.VISIBLE);
        }else{
            accountNameText.set(getApplication().getString(R.string.real_name));
            bankNameVisibility.set(View.GONE);
            openingBankVisibility.set(View.GONE);
            request.setBankId(null);
        }

    }


    BindingCommand createCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(final View view) {

            RetrofitClient.getInstance().create(UserPaymentModeApiService.class)
                    .create(request)
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                    .compose(RxUtils.schedulersTransformer()) //线程调度
                    .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showDialog();
                        }
                    })
                    .subscribe(new Consumer<BaseResponse<List<UserPaymentMode>>>() {
                        @Override
                        public void accept(BaseResponse<List<UserPaymentMode>> response) throws Exception {
                            dismissDialog();
                            //请求成功
                            if(response.isOk()){
                                RxToast.showToast(R.string.successfully_added);
                                finish();
                            }

                        }
                    }, new Consumer<ResponseThrowable>() {
                        @Override
                        public void accept(ResponseThrowable throwable) throws Exception {
                            //请求刷新完成收回
                            dismissDialog();
                            throwable.printStackTrace();
                            RxToast.error(throwable.message);
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            //关闭对话框
                            dismissDialog();
                            //请求刷新完成收回
                        }
                    });

        }
    });

    BindingCommand editCommand = new BindingCommand(new BindingConsumer<View>() {
        @Override
        public void call(final View view) {

            RetrofitClient.getInstance().create(UserPaymentModeApiService.class)
                    .edit(request)
                    .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) //请求与View周期同步
                    .compose(RxUtils.schedulersTransformer()) //线程调度
                    .compose(RetrofitClient.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            showDialog();
                        }
                    })
                    .subscribe(new Consumer<BaseResponse<List<UserPaymentMode>>>() {
                        @Override
                        public void accept(BaseResponse<List<UserPaymentMode>> response) throws Exception {
                            dismissDialog();
                            //请求成功
                            if(response.isOk()){
                                RxToast.showToast(R.string.successfully_added);
                                finish();
                            }

                        }
                    }, new Consumer<ResponseThrowable>() {
                        @Override
                        public void accept(ResponseThrowable throwable) throws Exception {
                            //请求刷新完成收回
                            dismissDialog();
                            throwable.printStackTrace();
                            RxToast.error(throwable.message);
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            //关闭对话框
                            dismissDialog();
                            //请求刷新完成收回
                        }
                    });

        }
    });

    public BindingCommand confirmClickCommand() {

        if(request.getUserPaymentModeId() ==null){
            return createCommand;
        }else{
            return editCommand;
        }
    }


    public void initCurrencyData() {
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
                            currencys = new ArrayList<>();
                            for(Currency currency : response.getBody()){
                                currencys.add(currency);
                            }

                            List<String> lists = new ArrayList<>();
                            for (IKeyAndValue iKeyAndValue : currencys) {
                                lists.add(iKeyAndValue.getKey());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(spinnerCurrency.getContext(), android.R.layout.simple_spinner_item, lists);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCurrency.setAdapter(adapter);


                            spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    IKeyAndValue iKeyAndValue = currencys.get(position);
                                    request.setBankId(iKeyAndValue.getValue());
                                    initBankNameData(iKeyAndValue.getValue());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            if (!TextUtils.isEmpty(request.getCurrencyId())) {
                                for (int i = 0; i < currencys.size(); i++) {
                                    IKeyAndValue iKeyAndValue = currencys.get(i);
                                    if (request.getCurrencyId().equals(iKeyAndValue.getValue())) {
                                        spinnerCurrency.setSelection(i);
                                        return;
                                    }
                                }
                            }

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
}
