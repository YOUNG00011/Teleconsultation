package com.wxsoft.telereciver.entity.cloudclinc;

import com.wxsoft.telereciver.entity.Doctor;

import java.io.Serializable;

public class CloudClincOnDutyDoctor  implements Serializable {

    public String id;
    public String doctorId;
    public String doctorName;
    public String weekDayName;
    public String startTime;
    public String endTime;
    public float cost;
    public Doctor doctorInfo;
    public float yymzsl;
    public float ssmzsl;
    public boolean isFull;
}
