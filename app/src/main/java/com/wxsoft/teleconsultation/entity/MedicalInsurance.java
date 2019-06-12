package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedicalInsurance implements Serializable {

    @SerializedName("medicalInsurance")
    private String medicalInsuranceEnum;

    private String medicalInsuranceName;

    public static List<MedicalInsurance> getMedicalInsurances(List<CommEnum> commEnums) {
        List<MedicalInsurance> medicalInsurances = new ArrayList<>();
        for (CommEnum commEnum : commEnums) {
            MedicalInsurance medicalInsurance = new MedicalInsurance(commEnum.getId(), commEnum.getItemName());
            medicalInsurances.add(medicalInsurance);
        }

        return medicalInsurances;
    }

    public MedicalInsurance(String medicalInsuranceEnum, String medicalInsuranceName) {
        this.medicalInsuranceEnum = medicalInsuranceEnum;
        this.medicalInsuranceName = medicalInsuranceName;
    }

    public String getMedicalInsuranceEnum() {
        return medicalInsuranceEnum;
    }

    public void setMedicalInsuranceEnum(String medicalInsuranceEnum) {
        this.medicalInsuranceEnum = medicalInsuranceEnum;
    }

    public String getMedicalInsuranceName() {
        return medicalInsuranceName;
    }

    public void setMedicalInsuranceName(String medicalInsuranceName) {
        this.medicalInsuranceName = medicalInsuranceName;
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
        MedicalInsurance medicalInsurance = (MedicalInsurance) obj;

        return medicalInsuranceEnum.equals(medicalInsurance.getMedicalInsuranceEnum());
    }
}
