package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.Archive;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.responsedata.LoginResp;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface LoginApi {

    /**
     * 登录
     *
     * @param body
     * @return
     */
    @POST("api/Platform/RegisterUser?isMobile=true")
    Observable<BaseResp> registerUser(@Body Archive body);

    /**
     * 登录
     * @param name
     * @param possword
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/Login?isMobile=true")
    Observable<BaseResp<LoginResp>> login(@Field("Phone") String name, @Field("Password") String possword);

    /**
     * 获取华为账号
     * @param userId
     * @return
     */
    @GET("api/Platform/GetHWAccountByUserId?isMobile=true")
    Observable<BaseResp<HWAccount>> getHWAccountByUserId(@Query("userId") String userId);

    /**
     *
     * @param phone
     * @return
     */
    @GET("api/Platform/SendLoginCode?isMobile=true")
    Observable<BaseResp> sendCode(@Query("phone") String phone);

    /**
     *
     * @param phone
     * @param code
     * @return
     */
    @GET("api/Platform/LoginByCode?isMobile=true")
    Observable<BaseResp<LoginResp>> loginByCode(@Query("phone") String phone,@Query("code")String code);
}
