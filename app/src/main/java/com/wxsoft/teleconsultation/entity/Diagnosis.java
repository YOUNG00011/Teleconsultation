package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Diagnosis implements Serializable {

    private String id;
    private String name;
    private String code;
    private String category;
    @SerializedName("py")
    private String PY;
    @SerializedName("wb")
    private String WB;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public String getPY() {
        return PY;
    }

    public String getWB() {
        return WB;
    }

    public void setName(String name) {
        this.name = name;
    }
}
