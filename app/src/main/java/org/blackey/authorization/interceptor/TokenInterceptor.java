package org.blackey.authorization.interceptor;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.blackey.authorization.Token;
import org.blackey.model.request.RefreshTokenRequest;
import org.blackey.service.AccountApiService;
import org.blackey.ui.login.LoginActivity;
import org.blackey.utils.RetrofitClient;
import org.blackey.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;

public class TokenInterceptor implements Interceptor {


    private  Context mContext;



    public TokenInterceptor(Context mContext) {

        this.mContext = mContext;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {

        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        ResponseBody responseBody = originalResponse.body();

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("utf8");;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("utf8"));
        }
        String bodyStr = buffer.clone().readString(charset);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(bodyStr);
            if(jsonObject!= null &&  401 == jsonObject.getInt("code") ){

               String refreshToken =  TokenManager.getRefreshToken();
               if(TextUtils.isEmpty(refreshToken)){
                   mContext.startActivity(new Intent(mContext, LoginActivity.class));
                   return originalResponse;
               }

                Call<BaseResponse<Token>> call =  RetrofitClient.getInstance().create(AccountApiService.class)
                        .refreshToken(new RefreshTokenRequest(refreshToken));

                retrofit2.Response<BaseResponse<Token>> response = call.clone().execute();//刷新token必须使用同步请求

                BaseResponse<Token> token = response.body();

                if(token!=null && token.isOk()){
                    Token tokenInfo = response.body().getBody();
                    TokenManager.setToken(tokenInfo);
                    Request newRequest=request.newBuilder().header("Authorization", tokenInfo.getAccess_token()).build();//重新拼装请求头
                    RetrofitClient.refresh();
                    return chain.proceed(newRequest);//重试request
                }else{

                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    return originalResponse;
                }
            }else{
                return originalResponse;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  originalResponse;
    }
}
