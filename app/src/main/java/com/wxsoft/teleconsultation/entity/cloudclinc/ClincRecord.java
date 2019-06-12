package com.wxsoft.teleconsultation.entity.cloudclinc;

import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Entity;
import com.wxsoft.teleconsultation.entity.Patient;

import java.util.Date;
import java.util.List;

public class ClincRecord extends Entity {
    /**
     * 申请医生
     */
    public String applyDoctorId;
    /**
     * 申请医生姓名
     */
    public String applyDoctorName;
    public Doctor applyDoctor;

    /**
     * 患者ID
     */
    public String patientId;
    /**
     * 患者姓名
     */
    public String patientName;

    /**
     * 接诊医生
     */
    public String acceptDoctorId;
    /**
     * 接诊医生姓名
     */
    public String acceptDoctorName;
    public Doctor acceptDoctor;

    public Patient patient;

    public List<ClincCallRecord>  clincCallRecords;

    /**
     * 状态 枚举502
     */
    public String status;
    /**
     * 状态名称
     */
    public String statusName;

    /**
     * 状态 枚举501
     */
    public String clincType;
    /**
     * 状态名称
     */
    public String clincTypeName;
    /**
     * 预约时间
     */
    public String appointDate;

    /**
     * 云门诊费用
     */
    public float fee;
    /**
     * 小结
     */
    public String summary;

    /**
     * 申请时间
     */
    public String applyDate;


    /**
     * 支付时间
     */
    public String payDate;

    /**
     * 接收时间
     */
    public String acceptDate;
}
