package com.wxsoft.teleconsultation.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.utils.ZipUtil;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.LocalSettingHWAccount;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.responsedata.LoginResp;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.ui.fragment.CheckPhoneFragment;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
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

public class Login2Activity extends SupportBaseActivity implements TupNotify {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, Login2Activity.class));
    }

    private TimeCount timeCount;

    private static final String TAG = Login2Activity.class.getSimpleName();
    //wifi、4G、3G login
    private static final String NETWORK_COMMON = "common";
    private static final String RING_FILE = "call_ring.wav";
    private static final int TOAST_FLAG = 1;
    private static final int SMC_LOGIN_SUCCESS = 100;
    private static final int SMC_LOGIN_FAILED = 101;
    private static final Object LOCK = new Object();

    @BindView(R.id.cet_phone)
    ClearableEditText mPhoneView;

    @BindView(R.id.cet_password)
    ClearableEditText mPasswordView;
    @BindView(R.id.tv_send_code)
    TextView textCode;

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
        return R.layout.activity_login_code;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        timeCount = new TimeCount(60*1000,1000);
        LoginService.getInstance().registerTupNotify(this);
        setupHandler();
//        mPhoneView.setText("13685536183");
//        mPasswordView.setText("162518");
    }

    @Override
    protected void onResume() {
        networkType = NETWORK_COMMON;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TUPLogUtil.i(TAG, "onDestroy()");
        LoginService.getInstance().unregisterTupNotify(this);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

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
                handleRequestError(errorCode, Login2Activity.this);
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

    private void hwLogin() {
        sipURI = mUser.getHwUserName() + "@" + AppContext.REGISTER_SERVER;
        getIpAddress();
        LoginService.getInstance().setIpAddress(ipAddress);
        importHWCer();
        importRingFile();
        processLogin(mUser.getHwUserName(), mUser.getHwPassword());
    }

    private void importRingFile()
    {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (!sdCardExist)
        {
            TUPLogUtil.e(TAG, "sdcard is not exist");
            return;
        }

        TUPLogUtil.i(TAG, "import call ring file!~");
        try
        {
            String pathtrg = Environment.getExternalStorageDirectory() + File.separator + RING_FILE;
            InputStream in = getAssets().open(RING_FILE);
            copyAssetsFile(in, pathtrg);
        }
        catch (IOException e)
        {
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

    private void onLoginSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                    ViewUtil.dismissProgressDialog();
                    checkJpushRegistrationId();
                    AppContext.login(mUser);
                    HomeActivity.launch(Login2Activity.this);
                    finish();
                }
            }
        }).start();
    }

    private void checkJpushRegistrationId() {
        String id = JPushInterface.getRegistrationID(this);
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
        // 登录验证
        String username = mPhoneView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ViewUtil.showMessage("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ViewUtil.showMessage("密码不能为空");
            return;
        }

        ViewUtil.createProgressDialog(this, "登录中...");
        ApiFactory.getLoginApi().loginByCode(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<LoginResp>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<LoginResp> resp) {
                        if (resp.isSuccess()) {

                            if (resp.getData() == null) {
                                ViewUtil.dismissProgressDialog();
                                ViewUtil.showMessage(resp.getMessage());
                            } else {
                                mUser = User.getUser(resp.getData());
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
                                JMessageClient.login(mUser.getjUsername(), mUser.getjPassword(), new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            AppContext.login(mUser);
                                            HomeActivity.launch(Login2Activity.this);
                                            finish();

                                            LoginActivity.instance.finish();
//                                            if (TextUtils.isEmpty(mUser.getHwUserName())) {
//                                                ViewUtil.dismissProgressDialog();
//                                                ViewUtil.showMessage("该用户没有分配华为账号，视频暂不可用");
//                                                checkJpushRegistrationId();
//                                                AppContext.login(mUser);
//                                                HomeActivity.launch(Login2Activity.this);
//                                                finish();
//
//                                                LoginActivity.instance.finish();
//                                            } else {
//                                                hwLogin();
//                                            }
//
                                        } else {
                                            ViewUtil.dismissProgressDialog();
                                            ViewUtil.showMessage(responseCode + ":" + responseMessage);
                                        }
                                    }
                                });
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

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            textCode.setText("重新验证");
            textCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            textCode.setClickable(false);
            textCode.setText(String.valueOf(millisUntilFinished / 1000 )+"s");
        }
    }

    @OnClick(R.id.tv_send_code)
    void send(){
        String phone = mPhoneView.getText().toString();
        if(phone.equals("") ) {
            ViewUtil.showMessage("手机号不能为空");
        }

        timeCount.start();
        ApiFactory.getLoginApi().sendCode(phone)
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
                        if (resp.isSuccess()) {

                        }
                    }
                });

    }
}
