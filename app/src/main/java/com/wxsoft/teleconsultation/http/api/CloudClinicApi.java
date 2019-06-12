package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.cloudclinc.ClincCallRecord;
import com.wxsoft.teleconsultation.entity.cloudclinc.ClincRecord;
import com.wxsoft.teleconsultation.entity.cloudclinc.CloudClincDuty;
import com.wxsoft.teleconsultation.entity.cloudclinc.CloudClincOnDutyDoctor;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;

import java.util.Date;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface CloudClinicApi {
    /**
     *修改、保存云门诊记录
     * @param body
     * @return
     */
    @POST("api/CloudClinc/SaveClincRecord?isMobile=true")
    Observable<BaseResp> save(@Body ClincRecord body);

    /**
     * 获取云门诊详情 包含呼叫记录
     * @param id
     * @return
     */
    @GET("api/CloudClinc/GetClincRecordById?isMobile=true")
    Observable<BaseResp<ClincRecord>> getDetail(@Query("recordId")String id);

    /**
     * 保存云门诊呼叫记录
     * @param id
     * @return
     */
    @POST("api/CloudClinc/SaveClincCallRecord?isMobile=true")
    Observable<BaseResp<ClincCallRecord>> saveCallRecord(@Body ClincCallRecord record);

    /**
     * 接诊医生接收云门诊
     * @param id
     * @return
     */
    @GET("api/CloudClinc/ReciveClincRecord?isMobile=true")
    Observable<BaseResp> receive(@Query("recordId")String id);

    /**
     * 支付云门诊（只更新状态） 暂不启用
     * @param id
     * @return
     */
    @GET("api/CloudClinc/PayClincRecord?isMobile=true")
    Observable<BaseResp> pay(@Query("recordId")String id);

    /**
     * 支付云门诊（只更新状态） 暂不启用
     * @param id
     * @return
     */
    @GET("api/CloudClinc/PayClincRecord?isMobile=true")
    Observable<BaseResp> saveSummary(@Query("recordId")String id,@Query("summary")String summary);

    /**
     * 删除云门诊
     * @param id
     * @return
     */
    @GET("api/CloudClinc/DeleteClincRecord?isMobile=true")
    Observable<BaseResp> delete(@Query("recordId")String id);

    /**
     * 获取云门诊今日待办事情清单
     * @param id
     * @return
     */
    @GET("api/CloudClinc/GetTodayTobeDones?isMobile=true")
    Observable<BaseResp<List<CloudClincDuty>>> getCurrentDuty(@Query("doctorId")String id);
    /**
     * 获取云门诊今日待办事情清单
     * @param id
     * @return
     */
    @GET("api/CloudClinc/GetCloudClincFeeByDoctorId?isMobile=true")
    Observable<BaseResp> getFee(@Query("applyDoctorId")String apply,@Query("acceptDoctorId")String  accept);


    /**
     * 获取咨询预约单分页 conditions: QueryType (0：申请记录，1：接诊记录）
     * doctorId （医生Id） status(502-0001 ：待支付 502-0002 ：待接收 502-0003：待呼叫 502-0004 ：已呼叫，502-0005 已结束）不传此条件默认全部
     * @param id
     * @return
     */
    @POST("api/CloudClinc/GetClincRecordByPage?isMobile=true")
    Observable<BaseResp<ClincRecord>> getRecords(@Body QueryRequestBody body);

    /**
     *
     * @param body
     * @return
     */
    @POST("api/CloudClinc/GetClincRecordByPage?isMobile=true")
    Observable<BaseResp<QueryResponseData<ClincRecord>>> getRecordHistory(@Body QueryRequestBody body);

    /**
     * 获取医生在线状态
     * @param id
     * @return
     */
    //@GET("api/Doctor/GetDoctorOnlineStatus?isMobile=true")
    @GET("api/Doctor/GetDoctorCloudClincStatus?isMobile=true")
    Observable<BaseResp<Boolean>> getDocOnlineStatus(@Query("doctorId")String id);

    /**
     * 设置医生在线状态
     * @param id
     * @return
     */
    //@GET("api/Doctor/SetDoctorOnlineStatus?isMobile=true")
    @GET("api/Doctor/SetStartCloudClinc?isMobile=true")
    Observable<BaseResp<Boolean>> setDocOnlineStatus(@Query("doctorId")String id,@Query("isOnline")boolean online);

      /**
     * 设置医生在线状态
     * @param
     * @return
     */
    @POST("api/Doctor/QueryOnlineDoctors?isMobile=true")
    Observable<BaseResp<QueryResponseData<Doctor>>> getOnlineDoctors(@Body QueryRequestBody body);
    @POST("api/Doctor/QueryCloudClincOnDutyDoctors?isMobile=true")
    Observable<BaseResp<QueryResponseData<CloudClincOnDutyDoctor>>> getDoctorDuties(@Body QueryRequestBody body);


    /**
     * 设置医生在线状态
     * @param
     * @return
     */
    @POST("api/CloudClinc/SaveClincRecord?isMobile=true")
    Observable<BaseResp> saveRecord(@Body ClincRecord body);
}


