package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface PrescriptionApi {

	/**
	 * 通过医生的id获取该医生下的所有标签
	 * @param doctId
	 * @return
	 */
	@POST("api/Prescription/SavePrescription?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> savePrescription(@Query("doctId") String doctId);

	/**
	 * 通过医生的id获取该医生下的所有标签
	 * @param doctId
	 * @return
	 */
	@POST("api/Prescription/SavePrescriptionRecipe?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> savePrescriptionRecipe(@Query("doctId") String doctId);

	/**
	 * 通过医生的id获取该医生下的所有标签
	 * @param doctId
	 * @return
	 */
	@POST("api/Prescription/SavePrescription_PharmacistAudit?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> pharmacistPrescription(@Query("doctId") String doctId);

	/**
	 * 通过医生的id获取该医生下的所有标签
	 * @param doctId
	 * @return
	 */
	@POST("api/Prescription/QueryDiseaseCounseling?isMobile=true")
	Observable<BaseResp<List<PatientManagerTag>>> queryDiseaseCounseling(@Query("doctId") String doctId);
}
