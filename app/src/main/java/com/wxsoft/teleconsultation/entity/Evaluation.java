package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;

public class Evaluation implements Serializable {

    // 会诊的意见或建议
    private String suggest;
    // 目前病情
    private String condition;
    // 会诊单Id
    private String consultationId;
    // 评价人Id
    private String evaluaterId;

    // 评价人名称
    private String evaluateName;

    //        private String EvakuateDate;
    // 治疗效果
    private String treatResult;
    // 病人去向
    private String direction;

    public String getSuggest() {
        return suggest;
    }

    public String getCondition() {
        return condition;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getEvaluaterId() {
        return evaluaterId;
    }

    public String getEvaluateName() {
        return evaluateName;
    }

    public String getTreatResult() {
        return treatResult;
    }

    public String getDirection() {
        return direction;
    }
}
