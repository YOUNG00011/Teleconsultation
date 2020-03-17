package com.wxsoft.telereciver.entity.requestbody;

import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientTag;

import java.util.List;

public class PatientTagBody {

    private List<PatientTag> patientTagList;

    private List<Patient> patientInfoList;

    public PatientTagBody(List<PatientTag> patientTagList, List<Patient> patientInfoList) {
        this.patientTagList = patientTagList;
        this.patientInfoList = patientInfoList;
    }
}
