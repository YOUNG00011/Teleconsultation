package com.wxsoft.telereciver.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.utils.ZipUtil;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.LocalSettingHWAccount;
import com.wxsoft.telereciver.entity.User;
import com.wxsoft.telereciver.entity.responsedata.LoginResp;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.service.ListenCallService;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.ui.fragment.CheckPhoneFragment;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.ViewUtil;
import com.wxsoft.telereciver.vc.service.TupNotify;
import com.wxsoft.telereciver.vc.service.call.CallService;
import com.wxsoft.telereciver.vc.service.common.CallConstants;
import com.wxsoft.telereciver.vc.service.login.LoginService;
import com.wxsoft.telereciver.vc.service.login.data.LoginParams;
import com.wxsoft.telereciver.vc.service.utils.TUPLogUtil;
import com.wxsoft.telereciver.vc.service.utils.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import common.TupCallParam;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends SupportBaseActivity implements TupNotify {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, LoginActivity.class));
    }

    private static final String TAG = LoginActivity.class.getSimpleName();
    //wifi???4G???3G login
    private static final String NETWORK_COMMON = "common";
    private static final String RING_FILE = "call_ring.wav";
    private static final int TOAST_FLAG = 1;
    private static final int FIRST_LOGIN = 0;
    private static final int ALREADY_LOGIN = 1;
    private static final int SMC_LOGIN_SUCCESS = 100;
    private static final int SMC_LOGIN_FAILED = 101;
    private static final Object LOCK = new Object();


    @BindView(R.id.cet_password)
    ClearableEditText mPasswordView;

    @BindView(R.id.show_waiting)
    TextView mWaiting;

    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.rooting)
    LinearLayout rooting;


    public static LoginActivity instance;

    private String networkType = NETWORK_COMMON;
    private String ipAddress;
    private String sipURI;
    private Handler mHandler;
    private User mUser;

    @OnClick(R.id.btn_login)
    void loginClick() {
        login();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        LoginService.getInstance().registerTupNotify(this);
        instance = this;


        ListenCallService.start(this);
        setupHandler();
    }

    @Override
    protected void onResume() {
        networkType = NETWORK_COMMON;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        TUPLogUtil.i(TAG, "onDestroy()");
        LoginService.getInstance().unregisterTupNotify(this);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        instance = null;
        super.onDestroy();

    }

    @Override
    public void onRegisterNotify(int registerResult, int errorCode) {
        switch (registerResult) {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                TUPLogUtil.i(TAG, "register success");
                ViewUtil.dismissProgressDialog();
                CallService.getInstance().renderCreate();
                ViewUtil.dismissProgressDialog();
                mHandler.sendEmptyMessage(SMC_LOGIN_SUCCESS);
                break;
            case TupCallParam.CallEvent.CALL_E_EVT_REG_UNSUPORTED_CONEVNE:
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                TUPLogUtil.i(TAG, "register fail");
                ViewUtil.dismissProgressDialog();
                TUPLogUtil.i(TAG, "errorCode->" + errorCode);
                handleRequestError(errorCode, LoginActivity.this);
                mHandler.sendEmptyMessage(SMC_LOGIN_FAILED);
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

    private void parallelHandleMessage(Message msg) {
        switch (msg.what) {
            case TOAST_FLAG:
                ViewUtil.showMessage(((String) msg.obj));
                break;

            case SMC_LOGIN_SUCCESS:
                ViewUtil.showMessage(((String) msg.obj));
                rooting.setBackgroundResource(R.mipmap.the_bg);
                mPasswordView.setVisibility(View.GONE);
                btnLogin.setVisibility(View.GONE);
                mWaiting.setVisibility(View.VISIBLE);
                break;

            case SMC_LOGIN_FAILED:
                ViewUtil.dismissProgressDialog();
                ViewUtil.showMessage(((String) msg.obj));
                rooting.setBackgroundResource(R.color.white);
                mPasswordView.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                mWaiting.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    private void hwLogin() {
        sipURI = mUser.getHwUserName() + "@" + AppContext.REGISTER_SERVER;
        getIpAddress();
        LoginService.getInstance().setIpAddress(ipAddress);
        importHWCer();
        importRingFile();
        processLogin(mUser.getHwUserName(), mUser.getHwPassword());
    }

    private void importRingFile() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist) {
            TUPLogUtil.e(TAG, "sdcard is not exist");
            return;
        }

        TUPLogUtil.i(TAG, "import call ring file!~");
        try {
            String pathtrg = Environment.getExternalStorageDirectory() + File.separator + RING_FILE;
            InputStream in = getAssets().open(RING_FILE);
            copyAssetsFile(in, pathtrg);
        } catch (IOException e) {
            TUPLogUtil.e(TAG, "Progress get an IOException.");
        }
    }

    private void importHWCer() {
        try {
            InputStream in = getAssets().open("sc_root.pem");
            String pathtrg = ZipUtil.getCanonicalPath(getFilesDir()) + '/' + "root_cert_use.pem";
            copyAssetsFile(in, pathtrg);
        } catch (IOException e) {
//            Log.e(TAG, "Progress get an IOException.");
        }
    }



    private void checkJpushRegistrationId() {
        String id = JPushInterface.getRegistrationID(this);
        if (mUser == null) {
            mUser = AppContext.getUser();
        }
        ApiFactory.getCommApi().saveJPushAccount(mUser.getId(), mUser.getName(), id, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(BaseResp resp) {

                    }
                });
    }

    /**
     * Reset local ip.
     */
    private void getIpAddress() {
        if (Tools.isStringEmpty(networkType)) {
            return;
        }
        if (networkType.equals(NETWORK_COMMON)) {
            this.ipAddress = Tools.getLocalIp();
        }
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
        new Thread(() -> login(App.getApplication())).run();
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

    /**
     * handleRequestError
     *
     * @param errorCode
     */
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

    private void sendHandlerMessage(int what, Object object) {
        if (mHandler == null) {
            return;
        }
        Message msg = mHandler.obtainMessage(what, object);
        mHandler.sendMessage(msg);
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

    private void login() {
        // ????????????

        String code = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            ViewUtil.showMessage("?????????????????????");
            return;
        }


        ViewUtil.createProgressDialog(this, "?????????...");

        ApiFactory.getPrescriptionApi().getHWDeviceAccountByCode(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<HWAccount>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<HWAccount> resp) {
                        if (resp.isSuccess()) {

                            ViewUtil.dismissProgressDialog();

                            HWAccount account = resp.getData();
                            if(account!=null) {

                                sipURI = account.getHwUserName() + "@" + AppContext.REGISTER_SERVER;
                                getIpAddress();
                                LoginService.getInstance().setIpAddress(ipAddress);
                                importHWCer();
                                importRingFile();
                                processLogin(account.getHwUserName(), account.getHwPassword());
                            }else{

                                ViewUtil.showMessage("???????????????,????????????????????????????????????");
                            }

                        } else {
                            ViewUtil.dismissProgressDialog();
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private String save(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }

        File dir = new File(AppContext.getTmpPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(AppContext.getUserAvatarPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private boolean isContainChinese(String str) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    private boolean whatStartWith(String str) {
        Pattern pattern = Pattern.compile("^([A-Za-z]|[0-9])");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    private boolean whatContain(String str) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z][a-zA-Z0-9_\\-@\\.]{3,127}$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

}
