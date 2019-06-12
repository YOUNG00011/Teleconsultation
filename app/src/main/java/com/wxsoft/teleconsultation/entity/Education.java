package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Education implements Serializable {

    @SerializedName("education")
    private String educationEnum;

    private String educationName;

    public static List<Education> getEducations(List<CommEnum> commEnums) {
        List<Education> educations = new ArrayList<>();
        for (CommEnum commEnum : commEnums) {
            Education education = new Education(commEnum.getId(), commEnum.getItemName());
            educations.add(education);
        }

        return educations;
    }

    public Education(String educationEnum, String educationName) {
        this.educationEnum = educationEnum;
        this.educationName = educationName;
    }

    public String getEducationEnum() {
        return educationEnum;
    }

    public void setEducationEnum(String educationEnum) {
        this.educationEnum = educationEnum;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }

        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) {
            return false;
        }

        // 类型相同, 比较内容是否相同
        Education education = (Education) obj;

        return educationEnum.equals(education.getEducationEnum());
    }
}
