package com.wxsoft.teleconsultation.entity.register;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.entity.Entity;
import com.wxsoft.teleconsultation.entity.Patient;

public class RegisterItem extends Entity {
    public String patientId;
    public String patientName;
    public String weChatAccountId;
    public String schedulingItemId;
    public String registerItemType;
    public String registerItemTypeName;
    public String registerDate;
    public String visitTime;
    public String registerId;
    public String registerName;
    public String status;
    public String statusName;
    public float registerFee;
    public String payDate;
    public String cancelDate;
    public String cancelId;
    public String cancelName;
    public String registerOrgId;
    public String registerOrgName;
    public String registerDeptId;
    public String registerDeptname;
    public String registerDoctorId;
    public String registerDoctorName;
    public String registerBy;
    public String registerByName;
    public ScheduDateItem schedulingItem;
    @SerializedName("patientInfo")
    public Patient patient;
}
