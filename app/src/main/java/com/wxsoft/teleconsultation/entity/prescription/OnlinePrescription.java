package com.wxsoft.teleconsultation.entity.prescription;

import com.wxsoft.teleconsultation.entity.Entity2;
import com.wxsoft.teleconsultation.entity.WeChatAccount;

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
	public WeChatAccount weChatAccount;
}
