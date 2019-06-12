package com.wxsoft.teleconsultation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.utils.ZipUtil;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.HWAccount;
import com.wxsoft.teleconsultation.entity.LocalSettingHWAccount;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.helper.SharedPreferencesHelper;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;
import com.wxsoft.teleconsultation.vc.service.TupNotify;
import com.wxsoft.teleconsultation.vc.service.call.CallService;
import com.wxsoft.teleconsultation.vc.service.common.CallConstants;
import com.wxsoft.teleconsultation.vc.service.login.LoginService;
import com.wxsoft.teleconsultation.vc.service.login.data.LoginParams;
import com.wxsoft.teleconsultation.vc.service.utils.TUPLogUtil;
import com.wxsoft.teleconsultation.vc.service.utils.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import common.TupCallParam;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends SupportBaseActivity implements TupNotify {

    private User mUser;
    //wifi、4G、3G login
    private static final String NETWORK_COMMON = "common";
    private static final int TOAST_FLAG = 1;
    private static final int SMC_LOGIN_SUCCESS = 100;
    private static final int SMC_LOGIN_FAILED = 101;
    private static final Object LOCK = new Object();

    private String networkType = NETWORK_COMMON;
    private String ipAddress;
    private String sipURI;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        LoginService.getInstance().registerTupNotify(this);

        mUser = AppContext.getUser();
        if (mUser == null) {
            LoginActivity.launch(this);
            finish();
        } else {
            setupHandler();
            mUser.setHWAccount(null);
            checkJpushRegistrationId();
            login();
        }
    }

    @Override
    protected void onResume() {
        networkType = NETWORK_COMMON;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LoginService.getInstance().unregisterTupNotify(this);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRegisterNotify(int registerResult, int errorCode) {
        switch (registerResult) {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                TUPLogUtil.i(TAG, "register success");
                ViewUtil.dismissProgressDialog();
                CallService.getInstance().renderCreate();
                onLoginSuccess();
                break;
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                TUPLogUtil.i(TAG, "register fail");
                ViewUtil.dismissProgressDialog();
                TUPLogUtil.i(TAG, "errorCode->" + errorCode);
                handleRequestError(errorCode, SplashActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSMCLogin(int smcAuthorizeResult, String errorReason) {

    }

    @Override
    public void onCallNotify(int code, Object object) {

    }

    private void checkJpushRegistrationId() {
        String id = JPushInterface.getRegistrationID(this);
        ApiFactory.getCommApi().saveJPushAccount(mUser.getId(), mUser.getName(), id, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp resp) {

                    }
                });
    }

    private void setupHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TUPLogUtil.i(TAG, "what:" + msg.what);
                parallelHandleMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    private void sendHandlerMessage(int what, Object object) {
        if (mHandler == null) {
            return;
        }
        Message msg = mHandler.obtainMessage(what, object);
        mHandler.sendMessage(msg);
    }

    private void parallelHandleMessage(Message msg) {
        switch (msg.what) {
            case TOAST_FLAG:
                ViewUtil.showMessage(((String) msg.obj));
                break;

            case SMC_LOGIN_SUCCESS:
                processLogin(mUser.getHwUserName(), mUser.getHwPassword());
                ViewUtil.showMessage(((String) msg.obj));
                break;

            case SMC_LOGIN_FAILED:
                ViewUtil.dismissProgressDialog();
                ViewUtil.showMessage(((String) msg.obj));
                break;

            default:
                break;
        }
    }

    private void login() {
        ApiFactory.getLoginApi().getHWAccountByUserId(mUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<HWAccount>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                        finish();
                    }

                    @Override
                    public void onNext(BaseResp<HWAccount> resp) {
                        if (!resp.isSuccess() || resp.getData() == null) {
                            toHome();
                            return;
                        }

                        mUser.setHWAccount(resp.getData());
                        hwLogin();
                    }
                });

        LocalSettingHWAccount account=new LocalSettingHWAccount();
        account.userId=mUser.getId();
        account.userName=mUser.getName();
        account.hWAccountId=mUser.getHwUserName();
        account.hWAccountName=mUser.getHwUserName();
        ApiFactory.getCommApi().saveLocalHWAccount(account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        ViewUtil.dismissProgressDialog();
                    }
                });
    }

    private void hwLogin() {
        sipURI = mUser.getHwUserName() + "@" + AppContext.REGISTER_SERVER;
        getIpAddress();
        LoginService.getInstance().setIpAddress(ipAddress);
        importHWCer();
        processLogin(mUser.getHwUserName(), mUser.getHwPassword());
    }

    private void processLogin(String account, String password) {
        LoginAuthorizeResult hostedLoginResult = LoginService.getInstance().getHostedLoginResult();
        LoginParams loginParams = LoginParams.getInstance();
        if (hostedLoginResult == null) {
            loginParams.setProxyServerIp(AppContext.PROXY_SERVER);
            loginParams.setRegisterServerIp(AppContext.REGISTER_SERVER);
            loginParams.setServerPort(AppContext.PORT);
            loginParams.setSipURI(sipURI);
            loginParams.setVoipNumber(account);
            loginParams.setVoipPassword(password);
            loginParams.setSipImpi(account);
        }
        loginParams.setLocalIpAddress(ipAddress);
        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        if (!Tools.isNetworkAvailable(App.getApplication())) {
            TUPLogUtil.e(TAG, " network has been disconnected");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                login(App.getApplication());
            }
        }).run();
    }

    private boolean login(Context context) {
        TUPLogUtil.i(TAG, "login.");
        if (CallService.getInstance() == null) {
            TUPLogUtil.i(TAG, "login fail.");
            return false;
        } else if (Tools.isWifiOr3GAvailable(context)) {
            //sip register
            LoginService.getInstance().login();
            return true;
        } else {
            return false;
        }
    }

    private void onLoginSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                    AppContext.login(mUser);
                    toHome();
                }
            }
        }).start();
    }

    private void toHome() {
        JMessageClient.login(mUser.getjUsername(), mUser.getjPassword(), new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                if (responseCode == 0) {
                    if (TextUtils.isEmpty(mUser.getHwUserName())) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage("该用户没有分配华为账号，视频暂不可用");
                        checkJpushRegistrationId();

                    } else {
                        hwLogin();
                    }
//
                } else {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage(responseCode + ":" + responseMessage);
                }
            }
        });
        Intent intent = new Intent(this, HomeActivity.class);
        if(getIntent().getBundleExtra(AppConstant.EXTRA_BUNDLE) != null){
            intent.putExtra(AppConstant.EXTRA_BUNDLE,
                    getIntent().getBundleExtra(AppConstant.EXTRA_BUNDLE));
        }
        startActivity(intent);
        finish();
    }

    private void handleRequestError(int errorCode, SupportBaseActivity activity) {
        if (activity == null) {
            return;
        }
        String msg = null;
        switch (errorCode) {
            // 400 bad request
            case CallConstants.CALL_E_REASON_CODE_BADREQUEST:
                msg = "Bad request";
                break;
            //402 payment required
            case CallConstants.CALL_E_REASON_CODE_PAYMENTREQUIRED:
                msg = "Account number over limit";
                break;
            //403 forbidden
            case CallConstants.CALL_E_REASON_CODE_FORBIDDEN:
                msg = "Account error";
                break;
            //404 not found
            case CallConstants.CALL_E_REASON_CODE_NOTFOUND:
                msg = "Not found";
                break;
            //405 method no allowed
            case CallConstants.CALL_E_REASON_CODE_METHODNOTALLOWED:
                msg = "Method not allowed";
                break;
            //406 not acceptable
            case CallConstants.CALL_E_REASON_CODE_RESNOTACCEPTABLE:
                msg = "Not acceptable";
                break;
            //408 request timeout
            case CallConstants.CALL_E_REASON_CODE_REQUESTTIMEOUT:
                msg = "Request timeout";
                break;
            //500 server internal error
            case CallConstants.CALL_E_REASON_CODE_SERVERINTERNALERROR:
                msg = "Server internal error";
                break;
            //501 not implemented
            case CallConstants.CALL_E_REASON_CODE_NOTIMPLEMENTED:
                msg = "Not implemented";
                break;
            //502 bad gateway
            case CallConstants.CALL_E_REASON_CODE_BADGATEWAY:
                msg = "Bad gateway";
                break;
            //503 service unavailable
            case CallConstants.CALL_E_REASON_CODE_SERVICEUNAVAILABLE:
                msg = "Service unavailable";
                break;
            //504 server time-out
            case CallConstants.CALL_E_REASON_CODE_SERVERTIMEOUT:
                msg = "Server timeout";
                break;
            //505 version not supported
            case CallConstants.CALL_E_REASON_CODE_VERSIONNOTSUPPORTED:
                msg = "Version not supported";
                break;
            default:
                break;
        }
        if (msg != null) {
            sendHandlerMessage(TOAST_FLAG, msg);
        }
    }

    private void getIpAddress() {
        if (Tools.isStringEmpty(networkType)) {
            return;
        }
        if (networkType.equals(NETWORK_COMMON)) {
            this.ipAddress = Tools.getLocalIp();
        }
    }

    private void importHWCer() {
        try {
            InputStream in = getAssets().open("sc_root.pem");
            String pathtrg = ZipUtil.getCanonicalPath(getFilesDir()) + '/' + "root_cert_use.pem";
            copyAssetsFile(in, pathtrg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyAssetsFile(InputStream in, String pathtrg) {
        BufferedInputStream inBuff = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuffStream = null;
        byte[] b = new byte[1024 * 8];
        File filetrg = new File(pathtrg);
        try {
            if (!filetrg.exists()) {
                boolean isCreateSuccess = filetrg.createNewFile();
                if (!isCreateSuccess) {
                    return;
                }
            }
            inBuff = new BufferedInputStream(in);
            output = new FileOutputStream(filetrg);
            outBuffStream = new BufferedOutputStream(output);
            int inBuflen = inBuff.read(b);
            int i = 0; //
            boolean resultBool = inBuflen != -1;
            while (resultBool) {
                i++;
                outBuffStream.write(b, 0, inBuflen);
                if (i == 64) {
                    Thread.sleep(20);
                    i = 0;
                }
                inBuflen = inBuff.read(b);
                resultBool = (inBuflen != -1);
            }
            outBuffStream.flush();
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // close stream
            closeOutputStream(outBuffStream);
            closeOutputStream(output);
            closeInputStream(inBuff);
            closeInputStream(in);
            b = null;
        }

    }

    private void closeInputStream(InputStream iStream) {
        try {
            if (null != iStream) {
                iStream.close();
            }
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }

    private void closeOutputStream(OutputStream oStream) {
        try {
            if (null != oStream) {
                oStream.close();
            }
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }
}
