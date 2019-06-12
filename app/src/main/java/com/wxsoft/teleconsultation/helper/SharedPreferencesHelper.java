package com.wxsoft.teleconsultation.helper;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.util.PreferencesLoader;

import java.util.Locale;

public class SharedPreferencesHelper {

    private static final String USER = "USER";
    private static final String NO_DISTURB = "NO_DISTURB";
    private static final String TAG_LANGUAGE = "TAG_LANGUAGE";
    private static final String CONVERSATION_TOP_CANCEL = "CONVERSATION_TOP_CANCEL";

    static PreferencesLoader preferencesLoader;
    private static Locale systemCurrentLocal = Locale.getDefault();

    private static PreferencesLoader checkPreferencesLoader() {
        if(null == preferencesLoader){
            preferencesLoader = new PreferencesLoader(App.getApplication());
        }
        return preferencesLoader;
    }

    public static void setUser(User user) {
        checkPreferencesLoader();
        String userJson = "";
        if (user != null) {
            userJson = new Gson().toJson(user);
        }
        preferencesLoader.saveString(USER, userJson);
    }

    public static User getUser() {
        checkPreferencesLoader();
        String userJson = preferencesLoader.getString(USER);
        if (TextUtils.isEmpty(userJson)) {
            return null;
        }
        return new Gson().fromJson(userJson, User.class);
    }

    public static void setNoDisturb(int noDisturb) {
        checkPreferencesLoader();
        preferencesLoader.saveInt(NO_DISTURB, noDisturb);
    }

    public static int getNoDisturb() {
        checkPreferencesLoader();
        return preferencesLoader.getInt(NO_DISTURB);
    }

    public static void setLanguage(int select) {
        checkPreferencesLoader();
        preferencesLoader.saveInt(TAG_LANGUAGE, select);
    }

    public static int getSelectLanguage() {
        checkPreferencesLoader();
        return preferencesLoader.getInt(TAG_LANGUAGE);
    }

    public static Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public static void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }

    public static void setCancelTopSize(int height){
        checkPreferencesLoader();
        preferencesLoader.saveInt(CONVERSATION_TOP_CANCEL, height);

    }

    public static int getCancelTopSize(){
        checkPreferencesLoader();
        return preferencesLoader.getInt(CONVERSATION_TOP_CANCEL);
    }

}
