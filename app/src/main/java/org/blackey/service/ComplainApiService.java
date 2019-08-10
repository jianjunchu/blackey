package org.blackey.service;

import org.blackey.entity.UserCertification;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ComplainApiService {

    @POST("api/v1/sys/complain/add")
    Observable<BaseResponse<UserCertification>> add(@Body MultipartBody multipartBody);
}
