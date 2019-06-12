package com.wxsoft.teleconsultation.entity;

public class Feedback {

    private String feedbackType;
    private String feedbackContent;
    private String doctorId;
    private String doctorName;
    private String feedbackDate;
    private String consultationId;
    private String consultation;
    private String consultationFeedbackTranslates;
    private String id;
    private Doctor doctorInfoDTO;

    public String getFeedbackType() {
        return feedbackType;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getFeedbackDate() {
        return feedbackDate;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getConsultation() {
        return consultation;
    }

    public String getConsultationFeedbackTranslates() {
        return consultationFeedbackTranslates;
    }

    public String getId() {
        return id;
    }

    public Doctor getDoctorInfoDTO() {
        return doctorInfoDTO;
    }
}
