package com.wxsoft.telereciver.entity;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 类型咨询
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({BusinessType.CONSULTATION, BusinessType.CLOUDCLINC,BusinessType.TRANSFERTREATMENT,BusinessType.REGISTER,BusinessType.COUNSELING})
public @interface BusinessType {

    /**
     * 业务类型咨询
     */
    String CONSULTATION = "Consultation";
    /**
     * 云诊疗
     */
    String CLOUDCLINC = "CloudClinc ";
    /**
     * 转诊
     */
    String TRANSFERTREATMENT = "TransferTreatment";
    /**
     * 预约
     */
    String REGISTER = "Register";
    /**
     * 咨询
     */
    String COUNSELING = "Counseling";
    /**
     * 电子处方
     */
    String PRESCRIPTION = "Prescription";
}
