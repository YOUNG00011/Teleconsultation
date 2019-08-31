package com.wxsoft.teleconsultation;

public class AppConstant {

    // 外网
    //public static final String BASE_URL = "http://218.22.27.205:8008/";
  public static final String BASE_URL = "http://hz.xjwnsoft.com:7000/";
    // 明成
//    public static final String BASE_URL = "http://192.168.0.192:49927/";
//    public static final String BASE_URL = "http://192.168.0.192:8001/";
//    public static final String BASE_URL = "http://192.168.0.191/HMCS.WebAPI/";
//    http:///HMCS.WebAPI/
    public static final String USER_AVATAR_FILE_NAME = "avatar.jpg";

    public static final String EXTRA_BUNDLE = "extra_bundle";

    public static final String BASE_DIR = "Teleconsultation/";
    public static final String TMP_DIR  = "Tmp/";


    public static final int SIZE_OF_PAGE = 10;

    public static final class Cache{

        // 图片缓存子目录
        public static final String GLIDE_CATCH_DIR = "image_catch";
    }

    public static final class REQUEST_TYPE_NAME {
        // 职称类型
        public static final int POSITION_TITLE = 103;

        // 职称类型
        public static final int DUTY = 200;

        // 学历
        public static final int EDUCATION = 211;

        // 医保
        public static final int MEDICALINSURANCES = 101;

        // 会诊单类型
        // 点名会诊
        public static final String CLINIC_FROM_TYPE_CALL_THE_ROLL       = "104-0001";
        // 会诊中心
        public static final String CLINIC_FROM_TYPE_CENTRE              = "104-0002";

        // 待处理
        public static final String CLINIC_FROM_STATUS_TODO              = "105-0001";
        // 处理中
        public static final String CLINIC_FROM_STATUS_PROGRESS          = "105-0002";
        // 会诊中
        public static final String CLINIC_FROM_STATUS_CONSULTATION      = "105-0003";
        // 已会诊
        public static final String CLINIC_FROM_STATUS_HAS_CONSULTATION  = "105-0008";
        // 已完成
        public static final String CLINIC_FROM_STATUS_FINISHED          = "105-0010";
        // 取消
        public static final String CLINIC_FROM_STATUS_CANCEL            = "105-0015";
        // 拒绝
        public static final String CLINIC_FROM_STATUS_REFUSE            = "105-0016";

        // 进行中
        public static final String JOIN_DOCTOR_STATUS_TODO = "108-0001";
        // 拒绝
        public static final String JOIN_DOCTOR_STATUS_REFUSE = "108-0005";
        // 已会诊
        public static final String JOIN_DOCTOR_STATUS_FINISHED  = "108-0006";
        // 已取消
        public static final String JOIN_DOCTOR_STATUS_CANCEL = "108-0007";
        public static final String MEDICAINE_HZ = "902";
        public static final String MEDICAINE_USING = "903";
        public static final String MEDICAINE_USING_COUNT_DAYLY = "904";
        public static final String MEDICAINE_USING_COUNT_DAYS = "MEDICAINE_USING_COUNT_DAYS";
        public static final String MEDICAINE_USING_AMOUNT = "MEDICAINE_USING_AMOUNT";
        public static final String MEDICAINE_COMMON_MEMO = "905";
    }
}