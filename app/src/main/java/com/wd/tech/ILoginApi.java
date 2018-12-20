package com.wd.tech;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mumu on 2018/7/9.
 */

//是用来定义一个个的http请求的
public interface ILoginApi {

    @FormUrlEncoded
    @POST("user/v1/register")
    Observable<RegisterBean> register(@Field("phone") String phone, @Field("nickName") String nickName, @Field("pwd") String password);

}
