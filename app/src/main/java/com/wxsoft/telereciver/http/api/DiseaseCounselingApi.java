package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.diseasecounseling.CallComment;
import com.wxsoft.telereciver.entity.diseasecounseling.ChatRecord;
import com.wxsoft.telereciver.entity.diseasecounseling.Cost;
import com.wxsoft.telereciver.entity.diseasecounseling.DiseaseCounseling;
import com.wxsoft.telereciver.entity.diseasecounseling.DiseaseCounselingTime;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 咨询api接口
 */
public interface DiseaseCounselingApi {

    /**
     *
     * @param body
     * @return
     */
    @POST("api/DiseaseCounseling/QueryDiseaseCounseling?isMobile=true")
    Observable<BaseResp<QueryResponseData<DiseaseCounseling>>> queryList(@Body QueryRequestBody body);

    /**
     *获取咨询详情
     * @param id 咨询号id
     * @return
     */
    @GET("api/DiseaseCounseling/GetDiseaseCounselingById?isMobile=true")
    Observable<BaseResp<DiseaseCounseling>> getDetail(@Query("couId") String id);

    /**
     * 保存咨询信息
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounseling?isMobile=true")
    Observable<BaseResp<String>> save(@Body DiseaseCounseling body);

    /**
     * 保存咨询信息
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingWithOutAttachment")
    Observable<BaseResp<String>> saveNoAttachment(@Body QueryRequestBody body);

    /**
     *取消咨询详情
     * @param id 咨询号id
     * @param reason 取消原因
     * @return
     */
    @GET("api/DiseaseCounseling/CancelDiseaseCounseling?isMobile=true")
    Observable<BaseResp<String>> cancel(@Query("id") int id,@Query("reason")String reason);

    /**
     *拒绝咨询详情
     * @param id 咨询号id
     * @param reason 拒绝原因
     * @return
     */
    @GET("api/DiseaseCounseling/RefuseDiseaseCounseling?isMobile=true")
    Observable<BaseResp<String>> refuse(@Query("id") String id,@Query("reason")String reason);

    /**
     *完成咨询详情
     * @param id 咨询号id
     * @return
     */
    @GET("api/DiseaseCounseling/CompleteDiseaseCounseling?isMobile=true")
    Observable<BaseResp<String>> complete(@Query("id") String id);

    /**
     * 保存咨询支付情况
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingPayment?isMobile=true")
    Observable<BaseResp<String>> savePayment(@Body QueryRequestBody body);

    /**
     * 保存咨询评价
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingEvaluate?isMobile=true")
    Observable<BaseResp<String>> saveEvaluate(@Body QueryRequestBody body);

    /**
     * 保存电话咨询记录
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingCallRecord?isMobile=true")
    Observable<BaseResp<String>> saveCallingRecord(@Body QueryRequestBody body);

    /**
     * 保存图文咨询记录
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingChatRecord?isMobile=true")
    Observable<BaseResp> saveChatRecord(@Body ChatRecord body);

    /**
     * 保存电话留言记录
     * @param body 信息体
     * @return
     */
    @POST("api/DiseaseCounseling/SaveDiseaseCounselingCallComment?isMobile=true")
    Observable<BaseResp> saveCallingComment(@Body CallComment body);

    @GET("api/Doctor/GetCounselingFeeSettingByDoctorId?isMobile=true")
    Observable<BaseResp<List<Cost>>> getCost(@Query("docId") String id);

    @POST("api/Doctor/SaveCounselingFeeSetting?isMobile=true")
    Observable<BaseResp<Cost>> saveCost(@Body Cost body);


    @POST("api/Doctor/SaveCounselingOpenTimeSetting?isMobile=true")
    Observable<BaseResp> saveTimeSetting(@Body DiseaseCounselingTime body);


}
