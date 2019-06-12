package com.wxsoft.teleconsultation.entity.register;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Entity;

import java.io.Serializable;

public class ScheduDateMap  implements Serializable{

    public String doctorId;
    public String schedulingDate;
    public float registerFee;
    public String scheduType;
    public String scheduTypeName;
    public int resourceNoCount;
    public String id;
    @SerializedName("doctorInfo")
    public DoctorInfo doctor;

    public class DoctorInfo extends Entity{

        public String name;
        public String hospitalId;
        public String hospitalName;
        public String departmentId;
        public String departmentName;
        public String goodAt;
        public String sex;
        public String userId;
        public String specialistId;
        public String specialistName;
        public String phone;
        public String dynamic;
        public String education;
        public String educationName;
        public String yearWork;
        public String jobTitle;
        public String jobTitleName;
        public String duty;
        public String dutyName;
        public String introduce;
        public String achievement;
        public String userAttachmentId;
        public String userImgUrl;
        public String statusName;
        public String status;
        public String counselingCount;
        public String counselingEvaluateCount;

    }

}
