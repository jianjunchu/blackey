package org.blackey.service;

import org.blackey.entity.UserPaymentMode;
import org.blackey.model.request.UserPaymentModeRequest;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserPaymentModeApiService {

    /**
     * 新建账号
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/user/paymentmode/add")
    Observable<BaseResponse<UserPaymentMode>> create(@Body UserPaymentModeRequest request);

    /**
     * 新建账号
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/user/paymentmode/my")
    Observable<BaseResponse<List<UserPaymentMode>>> list();


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @DELETE("api/v1/user/paymentmode/{id}/delete")
    Observable<BaseResponse<Integer>> delete(@Path("id") Long id);

    /**
     * 编辑账号
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/user/paymentmode/edit")
    Observable<BaseResponse<UserPaymentMode>> edit(@Body UserPaymentModeRequest request);
}
