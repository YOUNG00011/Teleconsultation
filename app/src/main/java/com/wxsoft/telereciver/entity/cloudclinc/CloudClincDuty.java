package com.wxsoft.telereciver.entity.cloudclinc;

import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Patient;

public class CloudClincDuty {

    public String id;
    public int itemType;
    public Patient patient;
    public Doctor doctor;
    public int waitNumber;
    public ClincRecord clincRecord;
}
