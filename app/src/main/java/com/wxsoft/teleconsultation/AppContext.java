package com.wxsoft.teleconsultation;

import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.helper.SharedPreferencesHelper;
import com.wxsoft.teleconsultation.util.SdcardUtil;

public class AppContext {

    public static final String PROXY_SERVER = "124.117.250.59"; //VC6.0
    public static final String REGISTER_SERVER = "124.117.250.59";
    public static final String PORT = "5061";

    public static User user;

    public static void login(User user) {
        AppContext.user = user;
        SharedPreferencesHelper.setUser(user);
    }

    public static void logout() {
        AppContext.user = null;
        SharedPreferencesHelper.setUser(null);
    }

    public static User getUser() {
//        UserInfo userInfo = JMessageClient.getMyInfo();
//        if (userInfo == null) {
//            return null;
//        }

        user = SharedPreferencesHelper.getUser();
        return user;
    }

    public static void setUser(User targetUser) {
        user = targetUser;
        SharedPreferencesHelper.setUser(user);
    }

    public static String getBasePath() {
        return SdcardUtil.getSdcardPath() + "/" +
                AppConstant.BASE_DIR;
    }

    public static String getTmpPath() {
        return getBasePath() + AppConstant.TMP_DIR;
    }

    public static String getUserAvatarPath() {
        return getBasePath() + AppConstant.USER_AVATAR_FILE_NAME;
    }


}
