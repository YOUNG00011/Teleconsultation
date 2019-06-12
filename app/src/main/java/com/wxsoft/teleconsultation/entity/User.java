package com.wxsoft.teleconsultation.entity;

import android.text.TextUtils;

import com.wxsoft.teleconsultation.entity.responsedata.LoginResp;
import com.wxsoft.teleconsultation.vc.service.conf.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    public String shareUrl;
    private String password;
    private String id;
    private String doctId;
    private List<String> userRoles;

    // Doctor
    private String name;
    private String hospitalId;
    private String hospitalName;
    private String departmentId;
    private String departmentName;
    private PositionTitle positionTitle;

    // Archive
    private String userImgUrl;
    private String qrcodeImgUrl;
    private String signatureImageUrl;
    private int sex;
    private String phone;
    private String nickname;
    private String idCard;
    private String email;
    private String dynamic;
    private Education education;
    private double yearWork;
    private String jobTitle;
    private String jobTitleName;
    private String duty;
    private String dutyName;
    private String achievement;
    private String introduce;
    private String goodAt;

    public boolean isStartCloudClinc;
    public boolean hasLivePermission;

    // JMessage
    private String jUserId;
    private String jUsername;
    private String jNickname;
    private String jAvatar;
    private String jPassword;

    // Huawei
    private String hwUserName;
    private String hwNickname;
    private String hwPassword;

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDoctId() {
        return doctId;
    }

    public boolean isTranslationer() {
        return userRoles != null &&
                userRoles.size() == 1 &&
                !TextUtils.isEmpty(userRoles.get(0)) &&
                userRoles.get(0).equals(LoginResp.UserRole.ROLE_TRANSLATIONER);
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

    public PositionTitle getPositionTitle() {
        return positionTitle;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getQrcodeImgUrl() {
        return qrcodeImgUrl;
    }

    public void setQrcodeImgUrl(String qrcodeImgUrl) {
        this.qrcodeImgUrl = qrcodeImgUrl;
    }

    public String getSignatureImageUrl() {
        return signatureImageUrl;
    }

    public void setSignatureImageUrl(String signatureImageUrl) {
        this.signatureImageUrl = signatureImageUrl;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
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

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getjUserId() {
        return jUserId;
    }

    public String getjUsername() {
        return jUsername;
    }

    public String getjNickname() {
        return jNickname;
    }

    public String getjAvatar() {
        return jAvatar;
    }

    public String getjPassword() {
        return jPassword;
    }

    public String getHwUserName() {
        return hwUserName;
    }

    public String getHwNickname() {
        return hwNickname;
    }

    public String getHwPassword() {
        return hwPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public void setPositionTitle(PositionTitle positionTitle) {
        this.positionTitle = positionTitle;
    }

    public void setjUserId(String jUserId) {
        this.jUserId = jUserId;
    }

    public void setjUsername(String jUsername) {
        this.jUsername = jUsername;
    }

    public void setjNickname(String jNickname) {
        this.jNickname = jNickname;
    }

    public void setjAvatar(String jAvatar) {
        this.jAvatar = jAvatar;
    }

    public void setjPassword(String jPassword) {
        this.jPassword = jPassword;
    }

    public void setHwUserName(String hwUserName) {
        this.hwUserName = hwUserName;
    }

    public void setHwNickname(String hwNickname) {
        this.hwNickname = hwNickname;
    }

    public void setHwPassword(String hwPassword) {
        this.hwPassword = hwPassword;
    }

    public void setHWAccount(HWAccount hwAccount) {
        if (hwAccount == null) {
            this.hwUserName = "";
            this.hwNickname = "";
            this.hwPassword = "";
        } else {
            this.hwUserName = hwAccount.getHwUserName();
            this.hwNickname = hwAccount.getHwNickname();
            this.hwPassword = hwAccount.getHwPassword();
        }
    }

    public static User getUser(LoginResp loginResp) {
        User user = new User();

        user.password = loginResp.getPassword();
        user.id = loginResp.getId();
        user.userRoles = new ArrayList<>();
        List<LoginResp.UserRole> userRoles = loginResp.getUserRole();
        if (userRoles != null && !userRoles.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (LoginResp.UserRole userRole : userRoles) {
                user.userRoles.add(userRole.getRoleId());
            }
        }

        Doctor doctor = loginResp.getDoctor();
        user.doctId = doctor.getId();
        user.name = doctor.getName();
        user.hospitalId = doctor.getHospitalId();
        user.hospitalName = doctor.getHospitalName();
        user.departmentId = doctor.getDepartmentId();
        user.departmentName = doctor.getDepartmentName();
        user.positionTitle = doctor.getPositionTitle();

        user.isStartCloudClinc=doctor.isStartCloudClinc;
        user.hasLivePermission=doctor.hasLivePermission;

        Archive archive = loginResp.getArchives();
        user.userImgUrl = archive.getUserImgUrl();
        user.qrcodeImgUrl = archive.getQrcodeImgUrl();
        user.nickname = archive.getNickname();
        user.idCard = archive.getIdCard();
        user.email = archive.getEmail();
        user.dynamic = archive.getDynamic();
        user.education = archive.getEducation();
        user.yearWork = archive.getYearWork();
        user.duty = archive.getDuty();
        user.dutyName = archive.getDutyName();
        user.sex = archive.getSex();
        user.phone = archive.getPhone();
        user.jobTitle = archive.getJobTitle();
        user.jobTitleName = archive.getJobTitleName();
        user.achievement = archive.getAchievement();
        user.introduce = archive.getIntroduce();
        user.goodAt = archive.getGoodAt();

        user.shareUrl=doctor.shareUrl;
        LoginResp.JMessagAccount jMessagAccount = loginResp.getJMessagAccount();
        if (jMessagAccount != null) {
            user.jUserId = jMessagAccount.getUserId();
            user.jUsername = jMessagAccount.getjUserName();
            user.jNickname = jMessagAccount.getjNickname();
            user.jAvatar = jMessagAccount.getjAvatar();
            user.jPassword = jMessagAccount.getjPassword();
        }

        HWAccount hwAccount = loginResp.getHwAccount();
        if (hwAccount != null) {
            user.hwUserName = hwAccount.getHwUserName();
            user.hwNickname = hwAccount.getHwNickname();
            user.hwPassword = hwAccount.getHwPassword();
        }
        return user;
    }
}
