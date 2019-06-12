package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;

public class ConsultationFeedbackTranslate implements Serializable {

    private String consultationId;
    private String consultationFeedbackId;
    private String feedbackType;
    private String translateContent;
    private String translater;
    private String translateDate;

    public String getConsultationId() {
        return consultationId;
    }

    public String getConsultationFeedbackId() {
        return consultationFeedbackId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getTranslateContent() {
        return translateContent;
    }

    public String getTranslater() {
        return translater;
    }

    public String getTranslateDate() {
        return translateDate;
    }
}
