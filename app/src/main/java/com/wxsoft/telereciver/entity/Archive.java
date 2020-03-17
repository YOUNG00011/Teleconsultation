package com.wxsoft.telereciver.entity;

import android.text.TextUtils;

import java.io.Serializable;

public class Archive implements Serializable {

    private String userId;
    private String userName;
    private String userAttachmentId;
    private String userImgUrl;
    private String qrcodeAttachmentId;
    private String qrcodeImgUrl;
    private String nickname;
    private String idCard;
    private String email;
    private String dynamic;
    private String education;
    private String educationName;
    private double yearWork;
    private String jobTitle;
    private String jobTitleName;
    private String duty;
    private String dutyName;
    private String introduce;
    private String achievement;
    private String goodAt;
    private int sex;
    private String hospitalId;
    private String hospitalName;
    private String departmentId;
    private String departmentName;
    private String password;
    private String phone;
    private String signatureId;
    private String signatureImageUrl;


    public static Archive getRegisterArchive(String phone,
                                             String name,
                                             int sex,
                                             String password,
                                             String idCard,
                                             String hospitalId,
                                             String hospitalName,
                                             String departmentId,
                                             String departmentName,
                                             String jobTitle,
                                             String jobTitleName,
                                             String duty,
                                             String dutyName) {
        Archive archive = new Archive();
        archive.phone = phone;
        archive.userName = name;
        archive.sex = sex;
        archive.password = password;
        archive.idCard = idCard;
        archive.hospitalId = hospitalId;
        archive.hospitalName = hospitalName;
        archive.departmentId = departmentId;
        archive.departmentName = departmentName;
        archive.jobTitle =  jobTitle;
        archive.jobTitleName = jobTitleName;
        archive.duty = duty;
        archive.dutyName = dutyName;
        return archive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAttachmentId() {
        return userAttachmentId;
    }

    public void setUserAttachmentId(String userAttachmentId) {
        this.userAttachmentId = userAttachmentId;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getQrcodeAttachmentId() {
        return qrcodeAttachmentId;
    }

    public void setQrcodeAttachmentId(String qrcodeAttachmentId) {
        this.qrcodeAttachmentId = qrcodeAttachmentId;
    }

    public String getQrcodeImgUrl() {
        return qrcodeImgUrl;
    }

    public void setQrcodeImgUrl(String qrcodeImgUrl) {
        this.qrcodeImgUrl = qrcodeImgUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public Education getEducation() {
        if (TextUtils.isEmpty(education)) {
            return null;
        } else {
            return new Education(education, educationName);
        }
    }

    public void setEducation(Education education) {
        this.education = education.getEducationEnum();
        this.educationName = education.getEducationName();
    }

    public double getYearWork() {
        return yearWork;
    }

    public void setYearWork(double yearWork) {
        this.yearWork = yearWork;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public void setJobTitleName(String jobTitleName) {
        this.jobTitleName = jobTitleName;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public String getSignatureImageUrl() {
        return signatureImageUrl;
    }

    public void setSignatureImageUrl(String signatureImageUrl) {
        this.signatureImageUrl = signatureImageUrl;
    }
}
