package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.AccountJournals;
import com.wxsoft.telereciver.entity.BankAccount;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessSetting;
import com.wxsoft.telereciver.entity.DoctorInfo;
import com.wxsoft.telereciver.entity.DrwaMoneyApplyRecord;
import com.wxsoft.telereciver.entity.Evaluate;
import com.wxsoft.telereciver.entity.IntegralAccount;
import com.wxsoft.telereciver.entity.LocalSettingHWAccount;
import com.wxsoft.telereciver.entity.MenuItem;
import com.wxsoft.telereciver.entity.SystemMessage;
import com.wxsoft.telereciver.entity.Todo;
import com.wxsoft.telereciver.entity.cloudclinc.ClincSchedulingTime;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryCityData;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.entity.transfertreatment.MessageTemplate;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface CommApi {

    /**
     * 获取城市列表
     *
     * @return
     */
    @GET("api/platform/getcitydata?isMobile=true")
    Observable<BaseResp<QueryCityData>> getCityData();

    @GET("api/platform/GetUserMenuOnClinet?isMobile=true")
    Observable<BaseResp<List<MenuItem>>> getMenu(@Query("userId")String id);

    @GET("api/Doctor/GetBusinessSettingByDoctorId?isMobile=true")
    Observable<BaseResp<BusinessSetting>> getBusinessSetting(@Query("doctorId")String docId);

    @GET("api/Doctor/GetCloudClincSchedulingByDoctorId?isMobile=true")
    Observable<BaseResp<List<ClincSchedulingTime>>> getBusinessSettingWeek(@Query("doctorId")String docId);

    @POST("api/Doctor/SaveCloudClincSchedulingWeek?isMobile=true")
    Observable<BaseResp<List<ClincSchedulingTime>>> saveBusinessTime(@Body List<ClincSchedulingTime> time);

    @POST("api/Doctor/SaveBusinessSetting?isMobile=true")
    Observable<BaseResp<BusinessSetting>> setBusinessSetting(@Body BusinessSetting body);

    @POST("api/Doctor/GetToDoListByDoctorId?isMobile=true")
    Observable<BaseResp<QueryResponseData<Todo>>> queryConsultationWaitHandle(@Body QueryRequestBody body);

    @POST("api/Doctor/QueryEvaluatesByDoctorId?isMobile=true")
    Observable<BaseResp<QueryResponseData<Evaluate>>> getEvsluates(@Body QueryRequestBody body);



    /**
     * 改变免打扰状态
     * @param userId
     * @param userName
     * @param regId
     * @param isNoDisturb
     * @return
     */
    @FormUrlEncoded
    @POST("api/Account/SaveJPushAccount?isMobile=true")
    Observable<BaseResp> saveJPushAccount(@Field("userId") String userId,
                                          @Field("userName") String userName,
                                          @Field("regId") String regId,
                                          @Field("isNoDisturb") int isNoDisturb);

    /**
     * 获取系统消息
     * @param body
     * @return
     */
    @POST("api/Platform/QueryNotification?isMobile=true")
    Observable<BaseResp<QueryResponseData<SystemMessage>>> queryNotification(@Body QueryRequestBody body);

    @POST("api/Platform/SettingLocaHWAccount?isMobile=true")
    Observable<BaseResp> saveLocalHWAccount(@Body LocalSettingHWAccount body);

    @POST("api/Doctor/QueryDoctorAccountJournals?isMobile=true")
    Observable<BaseResp<QueryResponseData<AccountJournals>>> getIntegralHistory(@Body QueryRequestBody body);

    @POST("api/Doctor/QueryDrwaMoneyApplyRecords?isMobile=true")
    Observable<BaseResp<QueryResponseData<DrwaMoneyApplyRecord>>> getDrwaMoneyHistory(@Body QueryRequestBody body);

    @POST("api/Doctor/SaveBankAccount?isMobile=true")
    Observable<BaseResp> saveBankAccount(@Body BankAccount body);

    @POST("api/Doctor/SaveDrawMoneyApply?isMobile=true")
    Observable<BaseResp> saveDrawMoneyApply(@Body DrwaMoneyApplyRecord body);


    @GET("api/Doctor/GetDoctorInfoById?isMobile=true")
    Observable<BaseResp<DoctorInfo>> queryDoctor(@Query("id")String docId);

    @GET("api/Doctor/GetDoctorAccountInfoByDocId?isMobile=true")
    Observable<BaseResp<IntegralAccount>> queryIntegral(@Query("doctId")String docId);

    @GET("api/Doctor/GetBankAccounts?isMobile=true")
    Observable<BaseResp<List<BankAccount>>> queryBankAccounts(@Query("doctId")String docId);

    @GET("api/Doctor/GetMessageTemplateByDoctorId?isMobile=true")
    Observable<BaseResp<List<MessageTemplate>>> getTemplate(@Query("doctorId")String docId);

    @GET("api/Doctor/DeleteMessageTemplate?isMobile=true")
    Observable<BaseResp> deleteTemplate(@Query("msgTemplateId")String templateId);


    @POST("api/Doctor/SaveMessageTemplate?isMobile=true")
    Observable<BaseResp> saveTemplate(@Body MessageTemplate body);
}
