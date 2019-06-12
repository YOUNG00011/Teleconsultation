package com.wxsoft.teleconsultation.entity.cloudclinc;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.entity.diseasecounseling.Cost;

import java.io.Serializable;

public class ClincSchedulingTime implements Serializable {

    public String weekDay;
    public String weekDayName;
    public String startTime;
    public String endTime;
    public String doctorId;
    public String doctorName;

    public Cost  cost;

}
