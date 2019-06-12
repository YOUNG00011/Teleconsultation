package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.cloudclinc.ClincRecord;
import com.wxsoft.teleconsultation.entity.register.RegisterItem;
import com.wxsoft.teleconsultation.entity.register.ScheduDateItem;
import com.wxsoft.teleconsultation.entity.register.ScheduDateMap;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.requestbody.QuerySchedulingItemCountbody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;

import java.util.Date;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 *
 */
public interface RegisterApi {
    /**
     *修改、保存云门诊记录
     * @param body
     * @return
     */
    @POST("api/CloudClinc/SaveClincRecord?isMobile=true")
    Observable<BaseResp> save(@Body ClincRecord body);

    /**
     * 获取预约挂号记录详情
     * @param id
     * @return
     */
    @GET("api/Register/GetRegisterRecordById?isMobile=true")
    Observable<BaseResp<RegisterItem>> getDetail(@Query("id") String id);

    /**
     * 微信端获取挂号记录
     * @param id 微信aid
     * @return
     */
    @GET("api/Register/GetRegisterRecordByWeChatAccountId?isMobile=true")
    Observable<BaseResp> getDetailByWechat(@Query("weChatAccountId") String id);

    /**
     * 查询挂号记录分页
     * Condition : QueryType : 0 ：我申请的 1 ：预约我的
     * doctorId ：医生ID
     * status ： 0 全部 1待就诊 2 已就诊
     * BookType: 0:全部 1:当日 2明日 3：7日内 4 ：7日后
     * @param id
     * @return
     */
    @POST("api/Register/QueryRegisterRecordByPaged?isMobile=true")
    Observable<BaseResp<QueryResponseData<RegisterItem>>> getRegisterRecords(@Body QueryRequestBody body);
   /**
     * 获取医生预约挂号号源与排班情况（Date)
     * Condition : ScheduDate ：排班日期（YYYY-MM-DD)
     * SelfHospitalId : 当前医生医院Id(微信端不传）
     * @param id
     * @return
     */
    @POST("api/Register/QueryScheduDateMapByDate?isMobile=true")
    Observable<BaseResp<QueryResponseData<ScheduDateMap>>> getScheduMapyDate(@Body QueryRequestBody body);

    /**
     * 接诊医生预约保存预约记录
     * @param schedingItemId 预约号源ID
     * @param did 医生id
     * @param pid 病人id
     * @return
     */
    @GET("api/Register/SaveRegisterRecord?isMobile=true")
    Observable<BaseResp> saveScheding(@Query("schedingItemId")String schedingItemId,@Query("doctorId")String did,@Query("patientId") String pid);
    /**
     * 接诊医生预约保存预约记录
     * @param schedingItemId 预约号源ID
     * @param wechatId 微信id
     * @param pid 病人id
     * @return
     */
    @GET("api/Register/SaveRegisterRecord?isMobile=true")
    Observable<BaseResp> receive(@Query("schedingItemId")String schedingItemId,@Query("weChatAccountId")String wechatId,@Query("patientId") String pid);

    /**
     * 根据号源Id 获取号源详情
     * @param id
     * @return
     */
    @GET("api/Register/GetSchedulingItemById?isMobile=true")
    Observable<BaseResp> getItemDetailById(@Query("schedulingItemId") String id);

    /**
     * 根据医生Id 获取号源详情
     * @param id
     * @return
     */
    @GET("api/Register/GetSchedulingItemByDocId?isMobile=true")
    Observable<BaseResp> getItemDetailBydocId(@Query("doctorId") String id);

    /**
     * 接诊医生预约保存预约记录
     * @param body 预约号源ID
     * @return
     */
    @POST("api/Register/GetSchedulingItemCountByDocId?isMobile=true")
    Observable<BaseResp> getSchedulingItemCountByDoc(@Body QuerySchedulingItemCountbody body);
    /**
     * 根据部门，按日期统计可预约数量
     * @param body 预约号源ID
     * @return
     */
    @POST("api/Register/GetSchedulingItemCountByDeptId?isMobile=true")
    Observable<BaseResp> getSchedulingItemCountByDept(@Body QuerySchedulingItemCountbody body);

    /**
     * 根据科室、日期获取排班
     * @param body 预约号源ID
     * @return
     */
    @GET("api/Register/GetSchedulingItemByDeptId?isMobile=true")
    Observable<BaseResp> getSchedulingItemCountByDept(@Query("deptId") String deptId,@Query("scheduingDate")Date date);
    /**
     * 根据科室、日期获取排班
     * @param body 预约号源ID
     * @return
     */
    @GET("api/Register/GetSchedulingItemByDate?isMobile=true")
    Observable<BaseResp<List<ScheduDateItem>>> getSchedulingItemCountByDoc(@Query("doctId") String docId, @Query("scheduingDate")String date);



}


