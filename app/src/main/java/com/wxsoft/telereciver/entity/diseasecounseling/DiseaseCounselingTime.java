package com.wxsoft.telereciver.entity.diseasecounseling;

import com.google.gson.annotations.SerializedName;

public class DiseaseCounselingTime {

    public String weekDay;
    public String weekDayName;
    public String startTime;
    public String endTime;

    @SerializedName("diseaseCounselingPostCostRuleId")
    public String  costId;
    @SerializedName("diseaseCounselingPostCostRule")
    public Cost  cost;

}
