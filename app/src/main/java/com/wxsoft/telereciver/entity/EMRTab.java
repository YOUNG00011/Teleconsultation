package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class EMRTab implements Serializable {

    private String id;
    private String nodeType;
    private int index;
    private boolean isEnable;
    private String hospitalId;

    public static EMRTab getAll() {
        EMRTab emrTab = new EMRTab();
        emrTab.setId("");
        emrTab.setNodeType("全部");
        return emrTab;
    }

    public String getId() {
        return id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public String getHospitalId() {
        return hospitalId;
    }
}
