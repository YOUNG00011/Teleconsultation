package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class Doctor implements Serializable{


    public String shareUrl ;//刘浩
    private String id;
    private String name;
    private String sex;
    private String hospitalId;
    public String userId;
    private String hospitalName;
    private String departmentId;
    private String departmentName;
    private String goodAt;
    private String introduction;
    private boolean isEnable;
    private String positionTitle;
    private String positionTitleName;
    private String status;
    private String statusName;
    private String dynamic;
    private String education;
    private String educationName;
    private double yearWork;
    private String duty;
    private String dutyName;
    private String introduce;
    private String achievement;
    private String userImgUrl;
    public boolean isStartCloudClinc;
    public boolean hasLivePermission;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getFriendlySex() {
        return (sex.equals("1") || sex.equals("男")) ? "男" : "女";
    }

    public boolean isMan() {
        return (sex.equals("1") || sex.equals("男"));
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public String getIntroduction() {
        return introduction;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public PositionTitle getPositionTitle() {
        return new PositionTitle(positionTitle, positionTitleName);
    }

    public String getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public String getDynamic() {
        return dynamic;
    }

    public String getEducation() {
        return education;
    }

    public String getEducationName() {
        return educationName;
    }

    public double getYearWork() {
        return yearWork;
    }

    public String getDuty() {
        return duty;
    }

    public String getDutyName() {
        return dutyName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getAchievement() {
        return achievement;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }

        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) {
            return false;
        }

        // 类型相同, 比较内容是否相同
        Doctor doctor = (Doctor) obj;

        return id.equals(doctor.id);
    }
}
