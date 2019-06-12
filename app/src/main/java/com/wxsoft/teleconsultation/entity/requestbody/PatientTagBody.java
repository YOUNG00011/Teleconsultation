package com.wxsoft.teleconsultation.entity.requestbody;

import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;

import java.util.List;

public class PatientTagBody {

    private List<PatientTag> patientTagList;

    private List<Patient> patientInfoList;

    public PatientTagBody(List<PatientTag> patientTagList, List<Patient> patientInfoList) {
        this.patientTagList = patientTagList;
        this.patientInfoList = patientInfoList;
    }
}
