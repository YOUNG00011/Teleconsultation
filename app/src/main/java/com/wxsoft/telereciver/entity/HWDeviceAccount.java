package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class HWDeviceAccount implements Serializable {

    private String id;

    private String consultationId;

    private String hwDeviceAccoutName;

    private String hwDeviceAccoutId;

    private String userId;

    public HWDeviceAccount() {
    }

    public HWDeviceAccount(String consultationId, String hWDeviceAccoutName, String hWDeviceAccoutId, String userId) {
        this.consultationId = consultationId;
        this.hwDeviceAccoutName = hWDeviceAccoutName;
        this.hwDeviceAccoutId = hWDeviceAccoutId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getHwDeviceAccoutName() {
        return hwDeviceAccoutName;
    }

    public String getHwDeviceAccoutId() {
        return hwDeviceAccoutId;
    }

    public String getUserId() {
        return userId;
    }
}
