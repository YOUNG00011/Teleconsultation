package com.wxsoft.telereciver.entity.transfertreatment;

import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Entity;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.diseasecounseling.Attachment;

import java.util.List;

public class TreatMent extends Entity {
    public String applyDoctorId;
    public String applyDoctorName;
    public String patientId;
    public String patientName;
    public String diagnosis;
    public String describe;
    public String acceptDoctorId;
    public String acceptDoctorName;
    public String transferType;
    public String transferTypeName;
    public String status;
    public String statusName;
    public String summary;
    public String businessTime;
    public String businessMemo;
    public List<Attachment> transferTreatmentAttachments;
    public TreatMentReceive transferTreatmentRecivedRecord;
    public Patient patient;
    public Doctor applyDoctor;
    public Doctor acceptDoctor;
}
