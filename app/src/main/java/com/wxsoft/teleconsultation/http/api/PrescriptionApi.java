package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.entity.prescription.Medicine;
import com.wxsoft.teleconsultation.entity.prescription.MedicineCategory;
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
	@GET("api/Prescription/GetPrescriptionById?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> getPrescription(@Query("preId") String preId);

	/**
	 * 获取药品分类
	 * @param preId
	 * @return
	 */
	@GET("api/Prescription/GetAllMedicines?isMobile=true")
	Observable<BaseResp<List<MedicineCategory>>> getMedTypes(@Query("category") String preId);

	/**
	 * 根据药品分类获取药品列表
	 * @param categoryId 分类id
	 * @return
	 */
	@GET("api/Prescription/GetMedicines?isMobile=true")
	Observable<BaseResp<List<Medicine>>> getMedicines(@Query("medicineCategoryId") String categoryId);

	/**
	 * 根据关键字获取药品列表
	 * @param key 搜索关键字
	 * @return
	 */
	@GET("api/Prescription/GetMedicinesByKey?isMobile=true")
	Observable<BaseResp<List<Medicine>>> getMedicinesByKey(@Query("key") String key);

	/**
	 * 获取医生常用药品列表
	 * @param doctorId 医生id
	 * @return
	 */
	@GET("api/Prescription/GetCommonMedicine?isMobile=true")
	Observable<BaseResp<List<Medicine>>> getCommonMedicines(@Query("doctorId") String doctorId);
}
