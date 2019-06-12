package com.wxsoft.teleconsultation;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Debug;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.huawei.application.BaseApp;
import com.huawei.tup.TUPInterfaceService;
import com.wxsoft.teleconsultation.entity.City;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.MedicalInsurance;
import com.wxsoft.teleconsultation.util.LocalManageUtil;
import com.wxsoft.teleconsultation.vc.service.TupEventMgr;
import com.wxsoft.teleconsultation.vc.service.call.CallService;
import com.wxsoft.teleconsultation.vc.service.conf.ConferenceService;
import com.wxsoft.teleconsultation.vc.service.contacts.ContactService;
import com.wxsoft.teleconsultation.vc.service.login.LoginService;
import com.wxsoft.teleconsultation.vc.service.utils.CrashHandlerUtil;

import java.util.ArrayList;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatformConfig;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

public class App extends Application{

    public static final String TARGET_ID = "targetId";
    public static final String GROUP_ID = "groupId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String MsgIDs = "msgIDs";

    // 城市
    public static ArrayList<City> mCities = new ArrayList<>();
    // 病例节点
    public static ArrayList<EMRTab> mEMRTabs = new ArrayList<>();

    public static ArrayList<MedicalInsurance> mMedicalInsurances = new ArrayList<>();

    private static Application app;

    public static boolean isCallActivityStarted = false;

    /**
     * Gets app.
     *
     * @return the app
     */
    public static Application getApplication()
    {
        return app;
    }

    private void setApplication(Application application)
    {
        app = application;
    }

    private TUPInterfaceService tupInterfaceService;

    @Override
    public void onCreate() {
        setApplication(this);
        TupEventMgr.setTupContext(app);
        CrashHandlerUtil.getInstance().init(app);
        CallService.getInstance().tupCallInit();
        tupInterfaceService = new TUPInterfaceService();
        tupInterfaceService.StartUpService();
        tupInterfaceService.SetAppPath(getApplicationInfo().dataDir + "/lib");
        LoginService.getInstance().init(tupInterfaceService);
        ConferenceService.getInstance().confInit(tupInterfaceService);

        BaseApp.setApp(this);
        ContactService.getInstance().setLogParam(3, 2 * 1024, 2
                , Environment.getExternalStorageDirectory().toString() + "/VCLOG/contacts");

        ContactService.getInstance().startServer();
        super.onCreate();

        LocalManageUtil.setApplicationLanguage(this);
        setupJPushInterface();
        setupJMessageClient();

        if(BuildConfig.DEBUG){
            JShareInterface.setDebugMode(true);
        }
        PlatformConfig config=new PlatformConfig();
        //config.setWechat("wxc40e16f3ba6ebabc", "dcad950cd0633a27e353477c4ec12e7a");
        config.setWechat("wxbd317ad2a5e8e93f","65e00ba30f59aadcf4d20d16ce0ebc47");
        JShareInterface.init(this,config);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (tupInterfaceService != null) {
            tupInterfaceService.ShutDownService();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        //保存系统选择语言
        LocalManageUtil.saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //保存系统选择语言
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }

    private void setupJPushInterface() {
        if(BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }
        JPushInterface.init(this);
    }

    private void setupJMessageClient() {
        JMessageClient.init(getApplicationContext(), true);
        if(BuildConfig.DEBUG) {
            JMessageClient.setDebugMode(true);
        }
    }
}
