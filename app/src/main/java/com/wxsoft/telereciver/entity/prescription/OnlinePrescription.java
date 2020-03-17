package com.wxsoft.telereciver.entity.prescription;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.telereciver.entity.AssociatedPatient;
import com.wxsoft.telereciver.entity.Entity2;
import com.wxsoft.telereciver.entity.WeChatAccount;

import java.util.List;

/***
 * 在线处方
 */
public class OnlinePrescription extends Entity2 {

	public String weChatAccountId;
	public String associatedPatientId;
	public String diseaseDescription;
	public String hospitalId;
	public String hospitalName;
	public String departmentId;
	public String departmentName;
	public String doctorId;
	public String doctorName;
	public String pharmacistId;
	public String pharmacistName;
	public String memo;
	public String diagnosis;
	public String status;
	public String  patientEMRId;
//	public String  patientEMRId;
	@SerializedName("weChatAccountDTO")
	public WeChatAccount weChatAccount;

	@SerializedName("associatedPatientDTO")
	public AssociatedPatient patient;
	public List<Recipe> recipes;
	public String statusName;
}
