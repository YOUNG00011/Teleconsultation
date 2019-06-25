package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;

public class AssociatedPatient implements Serializable {

	public String id;
	public String patientId;
	public String patientName;
	public String escortPersonnel;
	public String escortPersonnelCardNo;
	public String escortPersonnelPhone;
	public WeChatAccount weChatAccount;
	public Patient patientInfo;
}
