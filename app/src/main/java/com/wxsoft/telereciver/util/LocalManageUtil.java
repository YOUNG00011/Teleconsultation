package com.wxsoft.telereciver.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.helper.SharedPreferencesHelper;

import java.util.Locale;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SharedPreferencesHelper.getSystemCurrentLocal();
    }

    public static String getSelectLanguage(Context context) {
        switch (SharedPreferencesHelper.getSelectLanguage()) {
            case 0:
                return context.getString(R.string.language_auto);
            case 1:
                return context.getString(R.string.language_en);
            case 2:
                return context.getString(R.string.language_ru);
            case 3:
                return context.getString(R.string.language_ar);
            case 4:
            default:
                return context.getString(R.string.language_cn);
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {

        switch (SharedPreferencesHelper.getSelectLanguage()) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.ENGLISH;
            case 2:
                return new Locale("ru");
            case 3:
                return new Locale("ar");
            case 4:
            default:
                return Locale.CHINA;
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SharedPreferencesHelper.setLanguage(select);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        SharedPreferencesHelper.setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context){
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
