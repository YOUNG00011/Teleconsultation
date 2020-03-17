package com.wxsoft.telereciver.entity.cloudclinc;

import com.wxsoft.telereciver.entity.diseasecounseling.Cost;

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
