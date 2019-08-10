package org.blackey.service;

import org.blackey.entity.Bank;
import org.blackey.entity.Banner;
import org.blackey.entity.CountryOrRegion;
import org.blackey.entity.Currency;
import org.blackey.entity.PaymentMode;
import org.blackey.entity.SystemConfig;
import org.blackey.entity.SystemRpcNodeInfo;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DictApiService {

    /**
     * 地区字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/country/listAll")
    Observable<BaseResponse<List<CountryOrRegion>>> countrys();

    /**
     * 银行字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/bank/listAll")
    Observable<BaseResponse<List<Bank>>> banks();

    /**
     * 根据货币类型获取银行字典列表
     * @param currencyId
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/bank/{currencyId}/currency")
    Observable<BaseResponse<List<Bank>>> listBankByCurrencyId(@Path("currencyId") String currencyId);

    /**
     * 货币类型字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/currency/listAll")
    Observable<BaseResponse<List<Currency>>> currencys();

    /**
     * 支付方式字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/paymentMode/listAll")
    Observable<BaseResponse<List<PaymentMode>>> paymentMode();


    /**
     * 支付方式字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/xmr/node")
    Observable<BaseResponse<SystemRpcNodeInfo>> getXmrNode();

    /**
     * 支付方式字典列表
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/dict/config/sys")
    Observable<BaseResponse<SystemConfig>> getSysConfig();

    /**
     *Banner list
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/sys/banner/listAll")
    Observable<BaseResponse<List<Banner>>> getBanners();
}
