package com.wxsoft.teleconsultation.entity.diseasecounseling;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Entity;
import com.wxsoft.teleconsultation.entity.JMessageAccount;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.WeChatAccount;

import java.io.Serializable;
import java.util.List;

public class DiseaseCounseling extends Entity {

    /**
     * 类型
     */
    @SerializedName("diseaseCounselingType")
    public String type;

    /**
     * 类型名称
     */
    @SerializedName("diseaseCounselingTypeName")
    public String typeName;

    /**
     * 描述
     */
    @SerializedName("describe")
    public String describe;

    /**
     * 状态
     */
    @SerializedName("status")
    public String status;

    /**
     * 状态名称
     */
    @SerializedName("statusName")
    public String statusName;

    /**
     * 咨询时间
     */
    @SerializedName("counsultDate")
    public String counsultDate;

    /**
     * 预约开始时间
     */
    @SerializedName("startTime")
    public String startTime;

    /**
     * 预约结束时间
     */
    @SerializedName("endTime")
    public String endTime;

    public String patientName;
    public String patientHeadImage;

    public String patientSex;

    public String patientBirthday;

    public String patientPhone;

    public String weChatAccountId;

    public String patientId;

    public String doctorId;

    /**
     * 取消，拒绝原因
     */
    @SerializedName("memo")
    public String memo;

    /**
     * 业务时间(对应状态-拒绝日期，取消日期，完成日期）
     */
    @SerializedName("businessDate")
    public String businessDate;
    @SerializedName("patientInfo")
    public Patient patient;

    public WeChatAccount weChatAccount;

    @SerializedName("doctorInfoMap")
    public Doctor doctor;

    @SerializedName("doctorJMessageAccount")
    public JMessageAccount dJMessageAccount;

    /**
     * 图文咨询聊天记录
     */
    @SerializedName("diseaseCounselingChatRecords")
    public List<ChatRecord> chatRecords;

    /**
     * 电话咨询通话记录
     */
    @SerializedName("diseaseCounselingCallRecords")
    public List<CallRecord> callRecords;
    @SerializedName("diseaseCounselingCallComment")
    public List<CallComment> callComments;
    /**
     * 支付记录
     */
    @SerializedName("diseaseCounselingPayments")
    public List<Payment> payments;
    @SerializedName("diseaseCounselingEvaluates")
    public List<Evaluate> evaluates;

    @SerializedName("diseaseCounselingAttachments")
    public List<Attachment> attachments;




}
