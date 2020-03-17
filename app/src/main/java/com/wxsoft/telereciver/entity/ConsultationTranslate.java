package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class ConsultationTranslate implements Serializable {

    private String consultationId;
    private String patientName;
    private String sex;
    private String age;
    private String diagnosis;
    private String describe;
    private String applyDoctorName;
    private String translateLanguage;
    private String translateLanguageName;
    private String translateDate;
    private String translateId;
    private String translateName;
    private String id;

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getApplyDoctorName() {
        return applyDoctorName;
    }

    public void setApplyDoctorName(String applyDoctorName) {
        this.applyDoctorName = applyDoctorName;
    }

    public String getTranslateLanguage() {
        return translateLanguage;
    }

    public void setTranslateLanguage(String translateLanguage) {
        this.translateLanguage = translateLanguage;
    }

    public String getTranslateLanguageName() {
        return translateLanguageName;
    }

    public void setTranslateLanguageName(String translateLanguageName) {
        this.translateLanguageName = translateLanguageName;
    }

    public String getTranslateDate() {
        return translateDate;
    }

    public void setTranslateDate(String translateDate) {
        this.translateDate = translateDate;
    }

    public String getTranslateId() {
        return translateId;
    }

    public void setTranslateId(String translateId) {
        this.translateId = translateId;
    }

    public String getTranslateName() {
        return translateName;
    }

    public void setTranslateName(String translateName) {
        this.translateName = translateName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
