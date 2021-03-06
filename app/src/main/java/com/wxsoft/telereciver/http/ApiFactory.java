package com.wxsoft.telereciver.http;

import com.wxsoft.telereciver.http.api.ClinicManagerApi;
import com.wxsoft.telereciver.http.api.CloudClinicApi;
import com.wxsoft.telereciver.http.api.CommApi;
import com.wxsoft.telereciver.http.api.DiseaseCounselingApi;
import com.wxsoft.telereciver.http.api.LiveApi;
import com.wxsoft.telereciver.http.api.LoginApi;
import com.wxsoft.telereciver.http.api.PatientManagerApi;
import com.wxsoft.telereciver.http.api.PrescriptionApi;
import com.wxsoft.telereciver.http.api.RegisterApi;
import com.wxsoft.telereciver.http.api.SmcApi;
import com.wxsoft.telereciver.http.api.TransferTreatmentApi;
import com.wxsoft.telereciver.http.api.UserApi;

public class ApiFactory {

    protected static final Object monitor = new Object();

    /**
     *
     */
    private static DiseaseCounselingApi diseaseCounselingApi;
    private static TransferTreatmentApi transferTreatmentApi;

    // 公共API
    private static CommApi commApi;
    // 用户
    private static UserApi userApi;
    private static LiveApi liveApi;
    // 登录
    private static LoginApi loginApi;
    // 患者管理
    private static PatientManagerApi patientManagerApi;
    // 会诊管理
    private static ClinicManagerApi clinicManagerApi;
    private static SmcApi smcApi;
    /**
     * 处方api
     */
    private static PrescriptionApi prescriptionApi;
    /**
     * 云门诊
     */
    private static CloudClinicApi cloudClinicApi;
    private static RegisterApi registerApi;

    public static CloudClinicApi getCloudClinicApi() {
        if (cloudClinicApi == null) {
            synchronized (monitor) {
                cloudClinicApi = RetrofitManager.getInstance().create(CloudClinicApi.class);
            }
        }
        return cloudClinicApi;
    }

    public static SmcApi getSmcApi() {
        if (smcApi == null) {
            synchronized (monitor) {
                smcApi = RetrofitManager.getInstance().create(SmcApi.class);
            }
        }
        return smcApi;
    }

    public static CommApi getCommApi() {
        if (commApi == null) {
            synchronized (monitor) {
                commApi = RetrofitManager.getInstance().create(CommApi.class);
            }
        }
        return commApi;
    }


    public static LiveApi getLiveApi() {
        if (liveApi == null) {
            synchronized (monitor) {
                liveApi = RetrofitManager.getInstance().create(LiveApi.class);
            }
        }
        return liveApi;
    }

    public static TransferTreatmentApi getTransferTreatmentApi() {
        if (transferTreatmentApi == null) {
            synchronized (monitor) {
                transferTreatmentApi = RetrofitManager.getInstance().create(TransferTreatmentApi.class);
            }
        }
        return transferTreatmentApi;
    }

    public static DiseaseCounselingApi getDiseaseCounselingApi() {
        if (diseaseCounselingApi == null) {
            synchronized (monitor) {
                diseaseCounselingApi = RetrofitManager.getInstance().create(DiseaseCounselingApi.class);
            }
        }
        return diseaseCounselingApi;
    }

    public static UserApi getUserApi() {
        if (userApi == null) {
            synchronized (monitor) {
                userApi = RetrofitManager.getInstance().create(UserApi.class);
            }
        }
        return userApi;
    }

    public static LoginApi getLoginApi() {
        if (loginApi == null) {
            synchronized (monitor) {
                loginApi = RetrofitManager.getInstance().create(LoginApi.class);
            }
        }
        return loginApi;
    }

    public static PatientManagerApi getPatientManagerApi() {
        if (patientManagerApi == null) {
            synchronized (monitor) {
                patientManagerApi = RetrofitManager.getInstance().create(PatientManagerApi.class);
            }
        }
        return patientManagerApi;
    }

    public static PrescriptionApi getPrescriptionApi() {
        if (prescriptionApi == null) {
            synchronized (monitor) {
                prescriptionApi = RetrofitManager.getInstance().create(PrescriptionApi.class);
            }
        }
        return prescriptionApi;
    }

    public static ClinicManagerApi getClinicManagerApi() {
        if (clinicManagerApi == null) {
            synchronized (monitor) {
                clinicManagerApi = RetrofitManager.getInstance().create(ClinicManagerApi.class);
            }
        }
        return clinicManagerApi;
    }

    public static RegisterApi getRegisterApi() {
        if (registerApi == null) {
            synchronized (monitor) {
                registerApi = RetrofitManager.getInstance().create(RegisterApi.class);
            }
        }
        return registerApi;
    }

    public static void reset() {
        commApi = null;
        userApi = null;
        loginApi = null;
        patientManagerApi = null;
        clinicManagerApi = null;
    }
}
