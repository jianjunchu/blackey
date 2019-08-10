package org.blackey.service;

import org.blackey.entity.UserCertification;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MineApiService {


    @POST("api/v1/user/photo/upload")
    Observable<BaseResponse<String>> uploadPhoto(@Body MultipartBody multipartBody);

    @POST("api/v1/user/certification/add")
    Observable<BaseResponse<UserCertification>> addCertification(@Body MultipartBody multipartBody);

    @GET("api/v1/user/certification/get")
    Observable<BaseResponse<UserCertification>> getCertification();


}
