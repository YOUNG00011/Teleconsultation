package com.wxsoft.teleconsultation.entity.diseasecounseling;

import com.wxsoft.teleconsultation.entity.Entity2;

import java.util.ArrayList;
import java.util.List;

public class Cost extends Entity2 {

    public String diseaseCounselingType;
    public String diseaseCounselingTypeName;
    public float amount;
    public String doctorId;
    public String doctorName;
    public boolean isEnable;
    public boolean isNotice;
    public List<DiseaseCounselingTime> diseaseCounselingTimes=new ArrayList<>();

}
