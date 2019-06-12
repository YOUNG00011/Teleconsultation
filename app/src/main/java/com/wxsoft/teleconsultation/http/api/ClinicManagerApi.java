package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.BusinessType;
import com.wxsoft.teleconsultation.entity.Clinic;
import com.wxsoft.teleconsultation.entity.CommEnum;
import com.wxsoft.teleconsultation.entity.Department;
import com.wxsoft.teleconsultation.entity.Diagnosis;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Evaluation;
import com.wxsoft.teleconsultation.entity.Feedback;
import com.wxsoft.teleconsultation.entity.HWAccount;
import com.wxsoft.teleconsultation.entity.HWDeviceAccount;
import com.wxsoft.teleconsultation.entity.Hospital;
import com.wxsoft.teleconsultation.entity.requestbody.ClinicAddDoctorRequestBody;
import com.wxsoft.teleconsultation.entity.requestbody.QueryDoctorInfoBody;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ClinicManagerApi {

    /**
     * 获取待办事项
     * @param body
     * @return
     */
    @POST("api/ConsultationManager/QueryConsultationWaitHandle?isMobile=true")
    Observable<BaseResp<QueryResponseData<Clinic>>> queryConsultationWaitHandle(@Body QueryRequestBody body);

    /**
     * 通过医院的id获取该医院所有的科室
     * @param organizationId 医院的id（为空则获取所有的科室）
     * @return
     */
    @GET("api/Platform/GetDepartmentByOrganizationId?isMobile=true")
    Observable<BaseResp<List<Department>>> getDepartmentByOrganizationId(@Query("organizationId") String organizationId);

    /**
     * 通过所有的职称
     * @return
     */
    @GET("api/platform/GetDictWithItemsByName?isMobile=true&name=" + AppConstant.REQUEST_TYPE_NAME.POSITION_TITLE)
    Observable<BaseResp<List<CommEnum>>> getAllPositonTitle();

    /**
     * 通过所有的职务
     * @return
     */
    @GET("api/platform/GetDictWithItemsByName?isMobile=true&name=" + AppConstant.REQUEST_TYPE_NAME.DUTY)
    Observable<BaseResp<List<CommEnum>>> getAllDuties();

    /**
     * 通过条件搜索医生
     * @return
     */
    @POST("api/Doctor/QueryDoctorInfos?isMobile=true")
    Observable<BaseResp<List<Doctor>>> queryDoctorInfos(@Body QueryDoctorInfoBody body);

    /**
     * 通过医生id获取粉丝的医生
     * @return
     */
    @GET("api/Doctor/GetFansDoctor?isMobile=true")
    Observable<BaseResp<List<Doctor>>> getFansDoctors(@Query("doctId") String doctId);



    /**
     * 通过医生id获取关注的医生
     * @return
     */
    @GET("api/Doctor/GetFavoriteDoctors?isMobile=true")
    Observable<BaseResp<List<Doctor>>> getFavoriteDoctors(@Query("doctId") String doctId,@Query("businessType")String type);

    /**
     * 通过医生id获取常用的医生
     * @param doctId
     * @return
     */
    @GET("api/Doctor/GetCommonDoctors?isMobile=true")
    Observable<BaseResp<List<Doctor>>> getCommonDoctors(@Query("doctId") String doctId, @Query("businessType")String type);


    /**
     * 通过名字搜索疾病
     * @param name
     * @return
     */
    @GET("api/ConsultationManager/GetDiagnosis?isMobile=true")
    Observable<BaseResp<List<Diagnosis>>> getDiagnosis(@Query("name") String name);

    /**
     * 通过地区搜索医院
     * @param provinceId
     * @param cityId
     * @param districtId
     * @return
     */
    @GET("api/Platform/GetHospitals?isMobile=true")
    Observable<BaseResp<List<Hospital>>> getHospitals(@Query("province") String provinceId,
                                                      @Query("city") String cityId,
                                                      @Query("district") String districtId,
                                                      @Query("hospitalId") String hospitalId);

    /**
     * 获取所有医院
     * @return
     */
    @GET("api/Platform/GetAllHospitails?isMobile=true")
    Observable<BaseResp<List<Hospital>>> GetAllHospitails();



    /**
     * 通过医院id和科室id搜索医生
     * @param hospitalId
     * @param departmentId
     * @return
     */
    @GET("api/Doctor/GetDoctorsByHospital?isMobile=true")
    Observable<BaseResp<List<Doctor>>> getDoctorsByHospital(@Query("hospitalId") String hospitalId,
                                                      @Query("departmentId") String departmentId,
                                                            @Query("businessType") String business);

    /**
     * 通过医院名称搜索医院
     * @param name
     * @return
     */
    @GET("api/Platform/GetHospitalByName?isMobile=true")
    Observable<BaseResp<List<Hospital>>> getHospitalByName(@Query("name") String name);

    /**
     * 获取会诊单
     * @param body
     * @return
     */
    @POST("api/ConsultationManager/QueryConsultation?isMobile=true")
    Observable<BaseResp<QueryResponseData<Clinic>>> queryConsultation(@Body QueryRequestBody body);

    /**
     * 获取会诊单详情
     * @param clinicId
     * @return
     */
    @GET("api/ConsultationManager/GetConsultationById?isMobile=true")
    Observable<BaseResp<Clinic>> getConsultationById(@Query("conId") String clinicId);



    /**
     * 结束会诊
     * @param clinicId
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("api/ConsultationManager/CompletedConsultation?isMobile=true")
    Observable<BaseResp> completedConsultation(@Field("ConId") String clinicId, @Field("Content") String content);

    /**
     * 取消会诊
     * @param clinicId
     * @param content
     * @return
     */
    @FormUrlEncoded
    @POST("api/ConsultationManager/cancelConsultation?isMobile=true")
    Observable<BaseResp> cancelConsultation(@Field("ConId") String clinicId, @Field("Content") String content);

    /**
     * 拒绝会诊
     * @param clinicId
     * @param content
     * @param consultationDoctorId
     * @param consultationDoctorName
     * @return
     */
    @FormUrlEncoded
    @POST("api/ConsultationManager/RejectConsultation?isMobile=true")
    Observable<BaseResp> rejectConsultation(@Field("ConId") String clinicId, @Field("Content") String content, @Field("ConsultationDoctorId") String consultationDoctorId, @Field("ConsultationDoctorName") String consultationDoctorName);

    /**
     * 回复会诊意见
     * @param clinicId
     * @param content
     * @param consultationDoctorId
     * @param consultationDoctorName
     * @return
     */
    @FormUrlEncoded
    @POST("api/ConsultationManager/SaveConsultationFeedback?isMobile=true")
    Observable<BaseResp> saveConsultationFeedback(@Field("ConsultationId") String clinicId, @Field("FeedbackContent") String content, @Field("doctorId") String consultationDoctorId, @Field("doctorName") String consultationDoctorName);

    /**
     * 获取会诊意见
     * @param clinicId
     * @return
     */
    @GET("api/ConsultationManager/GetConsultationFeedback?isMobile=true")
    Observable<BaseResp<List<Feedback>>> getConsultationFeedback(@Query("conId") String clinicId);

    /**
     * 获取会诊评价
     * @param clinicId
     * @return
     */
    @GET("api/ConsultationManager/GetConsultationEvaluation?isMobile=true")
    Observable<BaseResp<Evaluation>> getConsultationEvaluation(@Query("conId") String clinicId);

    /**
     * 提交评价
     * @param evaluaterId
     * @param evaluateName
     * @param consultationId
     * @param treatResult
     * @param condition
     * @param suggest
     * @param direction
     * @return
     */
    @FormUrlEncoded
    @POST("api/ConsultationManager/SaveConsultationEvaluationDTO?isMobile=true")
    Observable<BaseResp> saveConsultationEvaluationDTO(@Field("EvaluaterId") String evaluaterId,
                                                       @Field("EvaluateName") String evaluateName,
                                                       @Field("ConsultationId") String consultationId,
                                                       @Field("TreatResult") String treatResult,
                                                       @Field("Condition") String condition,
                                                       @Field("Suggest") String suggest,
                                                       @Field("Direction") String direction);

    /**
     * 通过群租Id获取视频会议Id
     * @param groupId
     * @return
     */
    @GET("api/platform/GetHWVideoGroupIdByJMGroupId?isMobile=true")
    Observable<BaseResp<String>> getHWVideoGroupIdByJMGroupId(@Query("groupId") String groupId);

    /**
     * 上传视频会议Id
     * @param groupId
     * @return
     */
    @FormUrlEncoded
    @POST("api/Platform/SaveHWVideoGroupId?isMobile=true")
    Observable<BaseResp> saveHWVideoGroupId(@Field("JGGroupId") String JGGroupId, @Field("GroupId") String groupId);


    /**
     * 获取会诊医生的华为账号
     * @param groupId
     * @return
     */
    @GET("api/Platform/GetHWAccountByConGroupId?isMobile=true")
    Observable<BaseResp<List<HWAccount>>> getHWAccountByConGroupId(@Query("groupId") String groupId);


    /**
     * 获取某个医生的关注状态
     * @param docId
     * @param fansDocId
     * @return
     */
    @GET("api/Doctor/GetAttentionStatus?isMobile=true")
    Observable<BaseResp<Boolean>> getAttentionStatus(@Query("docId") String docId, @Query("fansDocId") String fansDocId);

    /**
     * 改变关注状态
     * @param doctorId
     * @param doctorName
     * @param favoriteDoctorId
     * @param favoriteDoctorName
     * @param isAttention
     * @return
     */
    @FormUrlEncoded
    @POST("api/Doctor/SaveFavoriteDoctor?isMobile=true")
    Observable<BaseResp<Boolean>> saveFavoriteDoctor(@Field("doctorId") String doctorId,
                                                     @Field("doctorName") String doctorName,
                                                     @Field("FavoriteDoctorId") String favoriteDoctorId,
                                                     @Field("FavoriteDoctorName") String favoriteDoctorName,
                                                     @Field("IsAttention") int isAttention);

    @POST("api/ConsultationManager/ConsultationAddDocs?isMobile=true")
    Observable<BaseResp<Boolean>> consultationAddDocs(@Body ClinicAddDoctorRequestBody body);

    /**
     * 通过机构id获取华为账号
     * @param orgId
     * @return
     */
    @GET("api/Platform/GetHWDeviceAccountByOrgId?isMobile=true")
    Observable<BaseResp<List<HWAccount>>> getHWDeviceAccountByOrgId(@Query("orgId") String orgId);

    @POST("api/ConsultationManager/SaveConsultationHWDeviceAccountDTO?isMobile=true")
    Observable<BaseResp<HWDeviceAccount>> saveConsultationHWDeviceAccountDTO(@Body HWDeviceAccount hwDeviceAccount);

    @GET("api/ConsultationManager/DeleteConsultationHWDeviceAccountDTO?isMobile=true")
    Observable<BaseResp> deleteConsultationHWDeviceAccountDTO(@Query("consultationHWDeviceAccountId") String consultationHWDeviceAccountId);
}
