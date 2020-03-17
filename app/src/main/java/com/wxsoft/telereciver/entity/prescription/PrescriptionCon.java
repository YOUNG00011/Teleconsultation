package com.wxsoft.telereciver.entity.prescription;

import com.wxsoft.telereciver.entity.DoctorInfo;
import com.wxsoft.telereciver.entity.Entity2;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.WeChatAccount;
import com.wxsoft.telereciver.entity.diseasecounseling.Attachment;
import com.wxsoft.telereciver.entity.responsedata.LoginResp;

import java.util.List;

public class PrescriptionCon extends Entity2 {
	 public String describe;
	 public String medicationname;
	 public String status;
	 public String patientName;
	 public String patientSex;
	 public String patientBirthday;
	 public String patientPhone;
	 public String weChatAccountId;
	 public String doctorId;
	 public String patientId;
	 public String patientHeadImage;
	 public List<Attachment> medicationConsultationAttachments;
	 public List<ChatRecord> medicationCounselingChatRecords;
	 public WeChatAccount weChatAccount;
	 public DoctorInfo doctorInfoMap;
	 public LoginResp.JMessagAccount doctorJMessageAccount;
	 public Patient  patientInfo;

	 public String getStatusName(){

	 	String n="";
	 	if(status.equals("906-0001")){
	 		n="已咨询";
	  }else if(status.equals("906-0002")){
		  n="已回复";
	  }else if(status.equals("906-0003")){
		  n="已完成";
	  }else if(status.equals("906-0004")){
		  n="已取消";
	  }else if(status.equals("906-0005")){
		  n="已拒绝";
	  }

	 	return n;
	 }
}
