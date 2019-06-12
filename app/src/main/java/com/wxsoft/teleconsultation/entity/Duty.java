package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Duty implements Serializable {

    @SerializedName("duty")
    private String dutyEnum;

    private String dutyName;

    public static List<Duty> getDuties(List<CommEnum> commEnums) {
        List<Duty> duties = new ArrayList<>();
        for (CommEnum commEnum : commEnums) {
            Duty duty = new Duty(commEnum.getId(), commEnum.getItemName());
            duties.add(duty);
        }

        return duties;
    }

    public Duty(String dutyEnum, String dutyName) {
        this.dutyEnum = dutyEnum;
        this.dutyName = dutyName;
    }

    public String getDutyEnum() {
        return dutyEnum;
    }

    public String getDutyName() {
        return dutyName;
    }
}
