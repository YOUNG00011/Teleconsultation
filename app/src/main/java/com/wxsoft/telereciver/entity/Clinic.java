package com.wxsoft.telereciver.entity;

import java.io.Serializable;
import java.util.List;

public class Clinic implements Serializable {

    private String id;
    private Patient patientInfoDTO;
    private Doctor applyDoctorInfoDTO;
    private PatientEMR patientEMR;
    private List<Doctor> consultationDoctors;
    private String diagnosis;
    private String describe;
    private String doctorType;
    private String departmentId;
    private List<Department> departments;
    private String conDate;
    private String createdDate;
    private String status;

    private String patientEMRId;
    private String statusName;
    private String reason;
    private List<ConsultationHopeOrgAndDept> consultationHopeOrgAndDepts;
    private List<Evaluation> consultationEvaluations;
    private List<ConsultationFeedback> consultationFeedbacks;
    private List<Translationer> consultationTranslationers;
    private String consultationType;
    private String groupTitle;
    private ConsultationChat consultationChat;
    private ConsultationTranslate consultationTranslateDTO;
    private List<ConsultationFeedbackTranslate> consultationFeedbackTranslates;
    private List<HWDeviceAccount> consultationHWDeviceAccounts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatientInfoDTO() {
        return patientInfoDTO;
    }

    public void setPatientInfoDTO(Patient patientInfoDTO) {
        this.patientInfoDTO = patientInfoDTO;
    }

    public Doctor getApplyDoctorInfoDTO() {
        return applyDoctorInfoDTO;
    }

    public void setApplyDoctorInfoDTO(Doctor applyDoctorInfoDTO) {
        this.applyDoctorInfoDTO = applyDoctorInfoDTO;
    }

    public PatientEMR getPatientEMR() {
        return patientEMR;
    }

    public List<Doctor> getConsultationDoctors() {
        return consultationDoctors;
    }

    public void setConsultationDoctors(List<Doctor> consultationDoctors) {
        this.consultationDoctors = consultationDoctors;
    }

    public String getPatientEMRId() {
        return patientEMRId;
    }
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public String getConDate() {
        return conDate;
    }

    public void setConDate(String conDate) {
        this.conDate = conDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ConsultationHopeOrgAndDept> getConsultationHopeOrgAndDepts() {
        return consultationHopeOrgAndDepts;
    }

    public List<Evaluation> getConsultationEvaluations() {
        return consultationEvaluations;
    }

    public void setConsultationEvaluations(List<Evaluation> consultationEvaluations) {
        this.consultationEvaluations = consultationEvaluations;
    }

    public List<ConsultationFeedback> getConsultationFeedbacks() {
        return consultationFeedbacks;
    }

    public void setConsultationFeedbacks(List<ConsultationFeedback> consultationFeedbacks) {
        this.consultationFeedbacks = consultationFeedbacks;
    }

    public List<Translationer> getConsultationTranslationers() {
        return consultationTranslationers;
    }

    public void setConsultationTranslationers(List<Translationer> consultationTranslationers) {
        this.consultationTranslationers = consultationTranslationers;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public ConsultationChat getConsultationChat() {
        return consultationChat;
    }

    public ConsultationTranslate getConsultationTranslateDTO() {
        return consultationTranslateDTO;
    }

    public List<ConsultationFeedbackTranslate> getConsultationFeedbackTranslates() {
        return consultationFeedbackTranslates;
    }

    public List<HWDeviceAccount> getConsultationHWDeviceAccounts() {
        return consultationHWDeviceAccounts;
    }

    public static class ConsultationHopeOrgAndDept implements Serializable {

        private String consultationId;
        private String hospitalId;
        private String hospitalName;
        private String departmentId;
        private String departmentName;
        private String consultation;
        private String id;

        public String getConsultationId() {
            return consultationId;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public String getHospitalName() {
            return hospitalName;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public String getConsultation() {
            return consultation;
        }

        public String getId() {
            return id;
        }

    }

    public static class ConsultationChat implements Serializable {

        private int groupId;

        public int getGroupId() {
            return groupId;
        }
    }

    public static class ConsultationFeedback implements Serializable {

        private String doctorName;

        private String feedbackContent;

        public String getDoctorName() {
            return doctorName;
        }

        public String getFeedbackContent() {
            return feedbackContent;
        }
    }
}
