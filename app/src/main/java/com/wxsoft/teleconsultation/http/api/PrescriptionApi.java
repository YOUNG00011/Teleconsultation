package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.entity.prescription.OnlinePrescription;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface PrescriptionApi {

	/**
	 * 保存处方申请
	 * @param onlinePrescription 处方
	 * @return
	 */
	@POST("api/Prescription/SavePrescription?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> savePrescription(@Body OnlinePrescription onlinePrescription);

	/**
	 * 保存诊断和开药信息
	 * @param onlinePrescription 处方
	 * @return
	 */
	@POST("api/Prescription/SavePrescriptionRecipe?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> savePrescriptionRecipe(@Body OnlinePrescription onlinePrescription);

	/**
	 * 保存药师审批结果
	 * @param onlinePrescription 处方 修改status就好
	 * @return
	 */
	@POST("api/Prescription/SavePrescription_PharmacistAudit?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> pharmacistPrescription(@Body OnlinePrescription onlinePrescription);

	/**
	 * 查询处方列表
	 * @param body 查询条件
	 * @return
	 */
	@POST("api/Prescription/QueryPrescription?isMobile=true")
	Observable<BaseResp<QueryResponseData<OnlinePrescription>>> queryDiseaseCounseling(@Body QueryRequestBody body);

	/**
	 * 获取处方详情
	 * @param preId 处方id
	 * @return
	 */
	@GET("api/Prescription/GetPrescriptionById?preId={preId}&isMobile=true")
	Observable<BaseResp<OnlinePrescription>> getPrescription(@Query("preId") String preId);

	/**
	 * 获取药品分类
	 * @param preId
	 * @return
	 */
	@GET("api/Prescription/GetAllMedicines?preId={preId}&isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> getMedTypes(@Query("preId") String preId);
}
