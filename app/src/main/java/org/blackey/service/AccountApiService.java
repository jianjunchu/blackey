package org.blackey.service;

import org.blackey.authorization.Token;
import org.blackey.entity.CurrentUser;
import org.blackey.model.request.LoginRequest;
import org.blackey.model.request.PhoneRegisterRequest;
import org.blackey.model.request.RefreshTokenRequest;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AccountApiService {

    /**
     * 登录接口
     * @param request
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/login")
    Observable<BaseResponse<Token>> login(@Body LoginRequest request);

    /**
     * 注册发送短信验证码
     * @param request
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/sms/register")
    Observable<BaseResponse<Object>> getRegisterCaptcha(@Body PhoneRegisterRequest request);

    /**
     * 获取当前登录用户信息
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("api/v1/user/current")
    Observable<BaseResponse<CurrentUser>> current();

    /**
     * 手机号注册
     * @param request
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/register/phone")
    Observable<BaseResponse<Boolean>> phoneRegister(@Body  PhoneRegisterRequest request);

    /**
     * 刷新token，获取新的token
     * @param request
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api/v1/refresh_token")
    Call<BaseResponse<Token>> refreshToken(@Body RefreshTokenRequest request);
}
