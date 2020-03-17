package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class PatientEMR implements Serializable {

    private String id;
    private String name;
    private String version;
    private String patientId;
    private boolean isDelete;

    public static PatientEMR getNewPatientEMRRequestBody(String name, String patientId) {
        PatientEMR patientEMR = new PatientEMR();
        patientEMR.name = name;
        patientEMR.patientId = patientId;
        patientEMR.version = "1.0";
        return patientEMR;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPatientId() {
        return patientId;
    }

    public boolean isDelete() {
        return isDelete;
    }
}
