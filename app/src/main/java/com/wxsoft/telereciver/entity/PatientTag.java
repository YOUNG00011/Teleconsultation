package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class PatientTag implements Serializable {

    public static final String TAG_NORMAL = "100-0001";
    public static final String TAG_ADMIN  = "100-0002";

    private String tagId;
    private String tagName;
    private String patientInfoId;
    private Patient patientInfo;
    private String id;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getPatientInfoId() {
        return patientInfoId;
    }

    public void setPatientInfoId(String patientInfoId) {
        this.patientInfoId = patientInfoId;
    }

    public Patient getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(Patient patientInfo) {
        this.patientInfo = patientInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
