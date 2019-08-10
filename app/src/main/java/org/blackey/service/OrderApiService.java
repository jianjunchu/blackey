package org.blackey.service;

import org.blackey.entity.Order;
import org.blackey.model.request.OrderRequest;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.PageResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApiService {

    /**
     * 新建订单
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/otc/order/add")
    Observable<BaseResponse<Order>> create(@Body OrderRequest request);

    /**
     * 获取
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/otc/{id}/order")
    Observable<BaseResponse<Order>> get(@Path("id") Long id);

    /**
     * 取消订单
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/otc/{id}/cancel")
    Observable<BaseResponse<Order>> cancel(@Path("id") Long id);

    /**
     * 我的订单
     * @param page
     * @param rows
     * @param isClosed 0：未完结 1:已完结
     * @param type 0：all 1:我出售的 2:我购买的
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/otc/order/my")
    Observable<BaseResponse<PageResponse<Order>>> myOrderList(
            @Query("page") int page
            , @Query("rows") int rows
            , @Query("isClosed") Integer isClosed
            , @Query("type") Integer type);

    /**
     * 确认付款
     * @param orderId
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/order/{id}/paid")
    Observable<BaseResponse<Order>> confirmPayment(@Path("id")Long orderId);

    /**
     * 确认收款
     * @param orderId
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/order/{id}/gathering")
    Observable<BaseResponse<Order>> confirmReceipt(@Path("id")Long orderId);


}
