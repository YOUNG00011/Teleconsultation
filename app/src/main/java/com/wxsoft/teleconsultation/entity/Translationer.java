package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;

public class Translationer implements Serializable {

    private String consultationId;

    private String userId;

    private String userName;

    public String getConsultationId() {
        return consultationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
