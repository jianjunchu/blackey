package org.blackey.service;

import org.blackey.entity.Advertise;
import org.blackey.entity.UserPaymentMode;
import org.blackey.model.request.AdvertiseRequest;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.PageResponse;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdvertiseApiService {

    /**
     * 新建广告
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/otc/advertise/add")
    Observable<BaseResponse<Advertise>> create(@Body AdvertiseRequest request);

    /**
     * 获取广告
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/otc/advertise/{id}")
    Observable<BaseResponse<Advertise>> get(@Path("id") Long id);

    @GET("api/v1/otc/advertise/list")
    Observable<BaseResponse<PageResponse<Advertise>>> list(
            @Query("page") int page
            ,@Query("rows") int rows
            ,@Query("currencyId") Long currencyId);

    @GET("api/v1/otc/advertise/my/list")
    Observable<BaseResponse<PageResponse<Advertise>>> my_list(
            @Query("page") int page
            ,@Query("rows") int rows
            ,@Query("status") Integer status);

    /**
     * 删除
     * @param id
     * @return
     */
    @DELETE("api/v1/otc/advertise/{id}/delete")
    Observable<BaseResponse<Integer>> delete(@Path("id") Long id);

    /**
     * 撤单
     * @param id
     * @return
     */
    @GET("api/v1/otc/advertise/{id}/revocation")
    Observable<BaseResponse<Boolean>> revocation(@Path("id") Long id);

    @GET("api/v1/otc/advertise/{id}/sent")
    Observable<BaseResponse<Boolean>>  transactionSent(@Path("id") Long id,  @Query("txId")String txId, @Query("txkey")String txkey);

    @GET("api/v1/otc/advertise/{id}/paymentMode")
    Observable<BaseResponse<List<UserPaymentMode>>>  getAdPaymentMode(@Path("id") Long id);
}
