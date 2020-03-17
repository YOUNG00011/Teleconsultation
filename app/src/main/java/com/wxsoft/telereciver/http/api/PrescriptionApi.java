package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.CommEnum;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.PatientManagerTag;
import com.wxsoft.telereciver.entity.prescription.ChatRecord;
import com.wxsoft.telereciver.entity.prescription.Medicine;
import com.wxsoft.telereciver.entity.prescription.MedicineCategory;
import com.wxsoft.telereciver.entity.prescription.OnlinePrescription;
import com.wxsoft.telereciver.entity.prescription.PrescriptionCon;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;

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
	 * 保存处方申请
	 * @param onlinePrescription 处方
	 * @return
	 */
	@POST("api/Prescription/SavePrescriptionWithOutAttachment?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> savePrescriptionWithOutAttach(@Body OnlinePrescription onlinePrescription);

	/**
	 * 保存诊断和开药信息
	 * @param onlinePrescription 处方
	 * @return
	 */
	@POST("api/Prescription/SavePrescriptionRecipe?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> savePrescriptionRecipe(@Body OnlinePrescription onlinePrescription);

	/**
	 * 保存药师审批结果
	 * @param onlinePrescription 处方 修改status就好
	 * @return
	 */
	@POST("api/Prescription/SavePrescription_PharmacistAudit?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> pharmacistPrescription(@Body OnlinePrescription onlinePrescription);

	/**
	 * 查询处方列表
	 * @param body 查询条件
	 * @return
	 */
	@POST("api/Prescription/QueryPrescription?isMobile=true")
	Observable<BaseResp<QueryResponseData<OnlinePrescription>>> getPrescriptions(@Body QueryRequestBody body);

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


	/**
	 * 往下的实现没有调试
	 */

	/**
	 * 获取咨询列表
	 * @param body
	 * @return
	 */
	@POST("api/Prescription/QueryMedicationConsultation?isMobile=true")
	Observable<BaseResp<QueryResponseData<PrescriptionCon>>> getConsultation(@Body QueryRequestBody body);

	/**
	 * 获取咨询详情
	 * @param couId 咨询id
	 * @return
	 */
	@GET("api/Prescription/GetMedicationConsultationById?isMobile=true")
	Observable<BaseResp<PrescriptionCon>> getConsultationDetail(@Query("couId") String couId);

	/**
	 * 保存咨询信息
	 * @param onlinePrescription 处方 修改status就好
	 * @return
	 */
	@POST("api/Prescription/SaveMedicationConsultation?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> saveConsultation(@Body OnlinePrescription onlinePrescription);


	/**
	 * 保存咨询信息
	 * @param onlinePrescription 处方 修改status就好
	 * @return
	 */
	@POST("api/Prescription/SaveMedicationConsultationWithOutAttachment?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> saveConsultationWithOutAttach(@Body OnlinePrescription onlinePrescription);


	/**
	 * 取消
	 * @param id 咨询id
	 * @return
	 */
	@GET("api/Prescription/CancelMedicationConsultation?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> cancelConsultation(@Query("id") String id,@Query("reason")String reason);

	/**
	 * 拒绝
	 * @param id 咨询id
	 * @return
	 */
	@GET("api/Prescription/RefuseMedicationConsultation?isMobile=true")
	Observable<BaseResp> refuseConsultation(@Query("id") String id,@Query("reason")String reason);

	/**
	 * 完成
	 * @param id 咨询id
	 * @return
	 */
	@GET("api/Prescription/CompleteMedicationConsultation?isMobile=true")
	Observable<BaseResp<OnlinePrescription>> completeConsultation(@Query("id") String id);

	/**
	 * 保存咨询信息
	 * @param record 消息实体
	 * @return
	 */
	@POST("api/Prescription/SaveMedicationConsultationChatRecord?isMobile=true")
	Observable<BaseResp> saveChatRecord(@Body ChatRecord record);

	/**
	 * 发送邀请码
	 * 处方流转中，医生给患者发送视频会诊邀请码，患者输入邀请码进行视频会诊
	 * 在线开放KaiFang;患者咨询你DrugZiXun;
	 * @param prescriptionId 在线问诊Id
	 * @param moduleName 对应模块
	 * @return
	 */
	@GET("api/Prescription/SendInvitationCode?isMobile=true")
	Observable<BaseResp>  sendInvitationCode(@Query("prescriptionId") String prescriptionId,@Query("moduleName") String moduleName);

	/**
	 * 根据业务Id获取验证码
	 * @param id
	 * @return
	 */
	@GET("api/Prescription/GetInvitationCodeByBusinessId?isMobile=true")
	Observable<BaseResp>  getInvitationCodeByBusinessId(@Query("id") String id);

	/**
	 * 根据业务Id获取华为账号
	 * @param id
	 * @return
	 */
	@GET("api/Prescription/GetHWAccountByBusinessId?isMobile=true")
	Observable<BaseResp>  getHWAccountByBusinessId(@Query("id") String id);

	/**
	 * 根据业务Id获取华为账号
	 * @param id
	 * @return
	 */
	@GET("api/Prescription/GetHWAccountByInvitationCode?isMobile=true")
	Observable<BaseResp>  getHWAccountByInvitationCode(@Query("invitationCode") String id);

	/**
	 * 获取指定类型的字典表
	 * @return
	 */
	@GET("api/platform/GetDictWithItemsByName?isMobile=true")
	Observable<BaseResp<List<CommEnum>>> getMedicalInsurances(@Query("name") String name);

	/**
	 * 根据邀请码赋值本地华为账号
	 * @param code
	 * @return
	 */
	@GET("api/Prescription/GetHWAccountByInvitationCode??isMobile=true")
	Observable<BaseResp<HWAccount>> getHWDeviceAccountByCode(@Query("invitationCode") String code);


}
