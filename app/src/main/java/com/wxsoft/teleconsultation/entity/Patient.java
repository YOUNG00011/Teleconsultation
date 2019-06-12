package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Entity2{

    private String name;
    private int age;
    private String birthday;
    private String sex;
    private String describe;
    @SerializedName("idc")
    private String IDC;
    private String phone;
    @SerializedName("medicalInsurance")
    private String medicalInsuranceEnum;
    @SerializedName("headImageUrl")
    public String avatar;
    private String medicalInsuranceName;
    private String doctId;
    private List<PatientTag> patientTags;

    public Patient() {
    }

    public Patient(String doc,String name, int age, String birthday, String sex, String IDC, String phone, String doctId, String medicalInsuranceEnum, String medicalInsuranceName) {
        this.doctId=doc;
        this.name = name;
        this.age = age;
        this.birthday = birthday;
        this.sex = sex;
        this.IDC = IDC;
        this.phone = phone;
        this.doctId = doctId;
        this.medicalInsuranceEnum = medicalInsuranceEnum;
        this.medicalInsuranceName = medicalInsuranceName;
        this.id = "";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAvatarDrawableRes() {
        if (sex.equals("1") || sex.equals("男")) {
            return R.drawable.ic_patient_man;
        } else {
            return R.drawable.ic_patient_women;
        }
    }

    public int getAge() {
        return age;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getFriendlySex() {
        String male = App.getApplication().getString(R.string.male);
        String female = App.getApplication().getString(R.string.female);
        return (sex.equals("1") || sex.equals(male)) ? male : female;
    }

    public String getSex() {
        return sex;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIDC() {
        return IDC;
    }

    public String getPhone() {
        return phone;
    }

    public String getMedicalInsuranceEnum() {
        return medicalInsuranceEnum;
    }

    public String getMedicalInsuranceName() {
        return medicalInsuranceName;
    }

    public String getDoctId() {
        return doctId;
    }

    public List<PatientTag> getPatientTags() {
        return patientTags;
    }

    public void setPatientTags(List<PatientTag> patientTags) {
        this.patientTags = patientTags;
    }

    public static List<Patient> getTestPatients() {
        List<Patient> patients = new ArrayList<>();
        Patient patient1 = new Patient();
        patient1.id = "12345";
        patient1.name = "李平";
        patient1.age = 28;
        patient1.sex = "男";
        patient1.patientTags = getTags();
        patient1.IDC = "340202199104162518";
        patient1.phone = "13685536183";
        patients.add(patient1);

        Patient patient2 = new Patient();
        patient2.id = "54321";
        patient2.name = "小红";
        patient2.age = 18;
        patient2.sex = "女";
        patient2.patientTags = getTags();
        patient2.IDC = "23022119950311092X";
        patient2.phone = "15099184420";
        patients.add(patient2);
        return patients;
    }

    private static List<PatientTag> getTags() {
        List<PatientTag> patientTags = new ArrayList<>();
        PatientTag patientTag1 = new PatientTag();
        patientTag1.setId("11111");
        patientTag1.setTagName("住院患者");
        patientTags.add(patientTag1);

        PatientTag patientTag2 = new PatientTag();
        patientTag2.setId("22222");
        patientTag2.setTagName("重点关注");
        patientTags.add(patientTag2);

        PatientTag patientTag3 = new PatientTag();
        patientTag3.setId("22222");
        patientTag3.setTagName("心血管病");
        patientTags.add(patientTag3);

        return patientTags;
    }
}
