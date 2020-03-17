package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.live.Live;
import com.wxsoft.telereciver.entity.live.LivePermission;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface LiveApi {


    @GET("api/Live/GetOnlineLive?isMobile=true")
    Observable<BaseResp> getOnLineLives(@Query("wechatId")String id);

    @GET("api/Live/PublishLive?isMobile=true")
    Observable<BaseResp> publish(@Query("liveRecordId")String id);

    @GET("api/Live/StartLive?isMobile=true")
    Observable<BaseResp> start(@Query("liveRecordId")String id);
    @GET("api/Live/StopLive?isMobile=true")
    Observable<BaseResp> stop(@Query("liveRecordId")String id);


    @GET("api/Live/DeleteLive?isMobile=true")
    Observable<BaseResp> delete(@Query("liveRecordId")String id);

    @GET("api/Live/CancelLive?isMobile=true")
    Observable<BaseResp> cancel(@Query("liveRecordId")String id,@Query("reason")String reason);

    @GET("api/Live/GetLastLivePermissionApply?isMobile=true")
    Observable<BaseResp<LivePermission>> getLivePermission(@Query("doctorId")String id);

    @POST("api/Live/SaveLivePermissionApply?isMobile=true")
    Observable<BaseResp> saveLivePermission(@Body LivePermission permission);


    @POST("api/Live/SaveLive?isMobile=true")
    //Observable<BaseResp> save(@Body Live body);
    Observable<BaseResp> save(@Body MultipartBody body);

    @POST("api/Live/QueryLiveRecordsByPage?isMobile=true")
    Observable<BaseResp<QueryResponseData<Live>>> getLives(@Body QueryRequestBody body);
}
