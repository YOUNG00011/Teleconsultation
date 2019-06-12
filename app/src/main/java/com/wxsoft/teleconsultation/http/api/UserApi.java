package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.CommEnum;
import com.wxsoft.teleconsultation.entity.requestbody.UpdateUserInfoBody;
import com.wxsoft.teleconsultation.entity.responsedata.CheckValidCodeResponseData;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface UserApi {

    /**
     * 查询患者
     * @param body
     * @return
     */
    @POST("api/Platform/UpdateArchives?isMobile=true")
    Observable<BaseResp> updateArchives(@Body UpdateUserInfoBody body);

    /**
     *  获取所有的学历
     * @return
     */
    @GET("api/platform/GetDictWithItemsByName?isMobile=true&name=" + AppConstant.REQUEST_TYPE_NAME.EDUCATION)
    Observable<BaseResp<List<CommEnum>>> getAllEducations();

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/SendVerificationCode?isMobile=true")
    Observable<BaseResp> sendVerificationCode(@Field("phone") String phone, @Field("IsRegister") int isRegister);

    /**
     * 验证验证码
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/CheckVerificationCode?isMobile=true")
    Observable<BaseResp<String>> checkVerificationCode(@Field("phone") String phone, @Field("verificationCode") String validCode);

    /**
     * 重置密码
     * @param userId
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/UpdatePassword?isMobile=true")
    Observable<BaseResp> updatePassword(@Field("userId") String userId, @Field("password") String password);

    /**
     * 验证密码
     * @param userId
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/VerifyPassword?isMobile=true")
    Observable<BaseResp> verifyPassword(@Field("userId") String userId, @Field("password") String password);

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/updatePasswordByPassword?isMobile=true")
    Observable<BaseResp> updatePasswordByPassword(@Field("userId") String userId, @Field("OldPassword") String oldPassword, @Field("NewPassword") String newPassword);


    @GET("api/Doctor/GetDoctorQRCodeUrl?isMobile=true")
    Observable<BaseResp<String>> getQRCode(@Query("doctId") String id);
}
