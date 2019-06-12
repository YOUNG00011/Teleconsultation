package com.wxsoft.teleconsultation.entity.cloudclinc;

import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Patient;

public class CloudClincDuty {

    public String id;
    public int itemType;
    public Patient patient;
    public Doctor doctor;
    public int waitNumber;
    public ClincRecord clincRecord;
}
