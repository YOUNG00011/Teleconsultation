package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.CommEnum;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.PatientEMR;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.entity.RecommendTag;
import com.wxsoft.teleconsultation.entity.requestbody.PatientTagBody;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.responsedata.PatientEMRResp;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface PatientManagerApi {

    /**
     * 通过医生的id获取该医生下的所有标签
     * @param doctId
     * @return
     */
    @GET("api/PatientInfo/GetPatientTags?isMobile=true")
    Observable<BaseResp<List<PatientManagerTag>>> getPatientTags(@Query("doctId") String doctId);


    /**
     * 通过医生的id获取该医生下的所有患者
     * @param doctId
     * @return
     */
    @GET("api/PatientInfo/GetPatientInfosByDoctId?isMobile=true&isDecryptDES=1")
    Observable<BaseResp<List<Patient>>> getPatientInfosByDoctId(@Query("doctId") String doctId);

    /**
     * 通过医生的id和标签id获取该标签下所有的患者
     * @param doctId
     * @param tag
     * @return
     */
    @GET("api/PatientInfo/GetPatientInfoByTag?isMobile=true&isDecryptDES=1")
    Observable<BaseResp<List<Patient>>> getPatientInfoByTag(@Query("doctId") String doctId, @Query("tag") String tag);

    /**
     * 获取所有的标签
     * @return
     */
    @GET("api/PatientInfo/GetAllTags?isMobile=true")
    Observable<BaseResp<List<PatientTag>>> getAllTags();


    /**
     * 获取推荐的标签
     * @return
     */
    @GET("api/PatientInfo/GetRecommendTags?isMobile=true")
    Observable<BaseResp<List<RecommendTag>>> getRecommendTags();


    /**
     * 保存标签
     * @return
     */
    @POST("api/PatientInfo/SaveTag?isMobile=true")
    Observable<BaseResp<RecommendTag>> saveTag(@Body RecommendTag recommendTag);

    /**
     * 获取患者标签
     * @return
     */
    @POST("api/PatientInfo/SavePatientInfoTag?isMobile=true")
    Observable<BaseResp> savePatientInfoTag(@Body PatientTagBody body);

    /**
     * 查询患者
     * @param body
     * @return
     */
    @POST("api/PatientInfo/QueryPatientInfo?isMobile=true&isDecryptDES=1")
    Observable<BaseResp<QueryResponseData<Patient>>> queryPatientInfo(@Body QueryRequestBody body);

    /**
     * 保存患者信息
     * @param patient
     * @return
     */
    @POST("api/PatientInfo/SavePatientInfo?isMobile=true")
    Observable<BaseResp<Patient>> savePatientInfo(@Body Patient patient);

    /**
     * 获取历史患者
     * @param doctId
     * @return
     */
    @GET("api/Doctor/GetHistoryPatient?isMobile=true&isDecryptDES=1")
    Observable<BaseResp<List<Patient>>> getHistoryPatient(@Query("doctId") String doctId);

    /**
     * 获取所有的医保类型
     * @return
     */
    @GET("api/platform/GetDictWithItemsByName?isMobile=true&name=" + AppConstant.REQUEST_TYPE_NAME.MEDICALINSURANCES)
    Observable<BaseResp<List<CommEnum>>> getMedicalInsurances();


    /**
     * 通过患者id获取患者所有电子病历
     * @param patientId
     * @return
     */
    @GET("api/PatientInfo/GetPatientEMRs?isMobile=true")
    Observable<BaseResp<List<PatientEMR>>> getPatientEMRs(@Query("patientId") String patientId);

    /**
     * 保存病历
     * @param body
     * @return
     */
    @POST("api/PatientInfo/SavePatientEMR?isMobile=true")
    Observable<BaseResp<PatientEMR>> savePatientEMR(@Body PatientEMR body);


    /**
     * 修改患者描述
     * @param patientId
     * @param description
     * @return
     */
    @GET("api/PatientInfo/SavePatientDescription?isMobile=true")
    Observable<BaseResp> savePatientDescription(@Query("patientId") String patientId,@Query("doctorId") String doc, @Query("description") String description);

    /**
     * 根据hospitalId难道所有tab
     * @param hospitalId
     * @return
     */
    @GET("api/PatientInfo/GetAllEMRNode?isMobile=true")
    Observable<BaseResp<List<EMRTab>>> getAllEMRNode(@Query("hospitalId") String hospitalId);

    @GET("api/PatientInfo/GetPatientEMRById?isMobile=true")
    Observable<BaseResp<PatientEMRResp>> getPatientEMRById(@Query("id") String id, @Query("nodeid") String nodeId);

    @GET("api/Platform/DeleteAttachment?isMobile=true")
    Observable<BaseResp> deleteAttachment(@Query("id") String attachmentId);

// ****************************会诊中心*****************************************






}
