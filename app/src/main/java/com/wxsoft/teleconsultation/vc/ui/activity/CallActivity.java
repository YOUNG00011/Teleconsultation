package com.wxsoft.teleconsultation.vc.ui.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.meeting.ConfInfo;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Confrence;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.util.ViewUtil;
import com.wxsoft.teleconsultation.vc.LayoutUtil;
import com.wxsoft.teleconsultation.vc.service.TupNotify;
import com.wxsoft.teleconsultation.vc.service.call.CallService;
import com.wxsoft.teleconsultation.vc.service.call.data.OneKeyJoinConfParam;
import com.wxsoft.teleconsultation.vc.service.call.data.SessionBean;
import com.wxsoft.teleconsultation.vc.service.common.CallConstants;
import com.wxsoft.teleconsultation.vc.service.conf.ConferenceService;
import com.wxsoft.teleconsultation.vc.service.conf.DataConfService;
import com.wxsoft.teleconsultation.vc.service.login.LoginService;
import com.wxsoft.teleconsultation.vc.service.login.data.LoginParams;
import com.wxsoft.teleconsultation.vc.service.notify.VCDataConfNotify;
import com.wxsoft.teleconsultation.vc.service.utils.TUPLogUtil;
import com.wxsoft.teleconsultation.vc.service.utils.Tools;

import java.io.File;
import java.util.ArrayList;

import common.TupCallParam;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * The type Call activity.
 * <p/>
 * CallActivity
 * A number input box, three buttons corresponding to voice,
 * video call and write off
 */
public class CallActivity extends Activity implements TupNotify, VCDataConfNotify {

    public static void launchForCreate(Activity from, ArrayList<String> numbers, long groupId) {
        Intent intent = new Intent(from, CallActivity.class);
        intent.putExtra(EXTRA_IS_CREATE, true);
        intent.putExtra(EXTRA_NUMBERS, numbers);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        from.startActivity(intent);
    }

    public static void launchForCall(Activity from, String number) {
        Intent intent = new Intent(from, CallActivity.class);
        intent.putExtra(EXTRA_IS_CALL, true);
        intent.putExtra(EXTRA_CALL, number);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        from.startActivity(intent);
    }

    public static void launchForJoin(Activity from, String confId) {
        Intent intent = new Intent(from, CallActivity.class);
        intent.putExtra(EXTRA_IS_JOIN, true);
        intent.putExtra(EXTRA_CONF_ID, confId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        from.startActivity(intent);
    }

    public static void launchFromComing(Context from, SessionBean sessionBean) {
        Intent intent = new Intent(from, CallActivity.class);
        intent.putExtra(EXTRA_SESSIONBEAN, sessionBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.P) {
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }else{
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        from.startActivity(intent);
    }

    private static final String EXTRA_IS_CREATE = "EXTRA_IS_CREATE";
    private static final String EXTRA_IS_CALL = "EXTRA_IS_CALL";
    private static final String EXTRA_NUMBERS = "EXTRA_NUMBERS";
    private static final String EXTRA_CALL = "EXTRA_CALL";
    private static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    private static final String EXTRA_IS_JOIN = "EXTRA_IS_JOIN";
    private static final String EXTRA_CONF_ID = "EXTRA_CONF_ID";
    private static final String EXTRA_SESSIONBEAN = "EXTRA_SESSIONBEAN";

    private static final String TAG = CallActivity.class.getSimpleName();
    private static final int THREAD_SLEEP_TIME = 1000;
    private static final int DELAY_TIME = 200;
    private static final int DATACONF_MSG = 998;
    private static final int LOOPS = 0;
    private static final String RING_FILE = "call_ring.wav";

    private static Instance instance = new Instance();

    private Handler handler;


    private Button audioCallButton;
    private Button videoCallButton;
    private Button audioJoinConfButton;
    private Button videoJoinConfButton;
    private Button exitButton;
    private Button changePasswordBtn;
    private Button openVideoPreviewButton;
    private Button closeVideoPreviewButton;
    private Button bookConfButton;
    private Button contactsBtn;
    private RelativeLayout videoPreviewRelativeLayout;
    private EditText callNumEditText;
    private EditText confIDEditText;
    private EditText confAccessCodeEditText;
    private EditText confPasswordEditText;
    private int cameraOritation = 0;
    private int localOritation = 0;
    private String filePath = "";
    /**
     * control layout
     */
    private LinearLayout controlsAreaLayout;// = (LinearLayout) findViewById(R.id.rl_controls);

    /**
     * call layout
     */
    private LinearLayout callAreaLayout;
    private CallFragment callFragment;

    private TextView accountTv;

    private long mGroupId;

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int id = view.getId();
            switch (id)
            {
                case R.id.btn_audio_call:
                {
                    //Hide soft keyboard
                    hideSoftKeyboard();
                    if (CallConstants.STATUS_CLOSE == CallService.getInstance().getVoipStatus())
                    {
                        String callNumber = callNumEditText.getText().toString();
                        if (Tools.isStringEmpty(callNumber))
                        {
//                            Toast.makeText(CallActivity.this,
//                                    getString(R.string.callnumber_is_null), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_AUDIOVIEW,
                                callNumber);
                    }
                    else
                    {
                        showCallFailDialog();
                    }
                }
                break;

                case R.id.btn_video_call:
                    {
                    //Hide soft keyboard
                    hideSoftKeyboard();
                    if (CallConstants.STATUS_CLOSE == CallService.getInstance().getVoipStatus())
                    {
                        String callNumber = callNumEditText.getText().toString();
                        if (Tools.isStringEmpty(callNumber))
                        {
//                            Toast.makeText(CallActivity.this,
//                                    getString(R.string.callnumber_is_null), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_VIDEOVIEW,
                                callNumber);
                    }
                    else
                    {
                        showCallFailDialog();
                    }
                }
                break;

                case R.id.btn_audio_join_conf:
                {
                    //Hide soft keyboard
                    hideSoftKeyboard();
                    if (CallConstants.STATUS_CLOSE == CallService.getInstance().getVoipStatus())
                    {
                        String confAccessCode = confAccessCodeEditText.getText().toString();
                        String confId = confIDEditText.getText().toString();
                        String confPassword = confPasswordEditText.getText().toString();

                        //String confAccessCode = "+865711001";
                        //String confId = "121313446";
                        //String confPassword = "32955676";

                        if (Tools.isStringEmpty(confAccessCode) || Tools.isStringEmpty(confId) || Tools.isStringEmpty(confPassword))
                        {
                            Toast.makeText(CallActivity.this, "Access code, conference id or password cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        OneKeyJoinConfParam joinConfParam = new OneKeyJoinConfParam();
                        joinConfParam.setAccessCode(confAccessCode);
                        joinConfParam.setConfID(confId);
                        joinConfParam.setConfPaswd(confPassword);
                        joinConfParam.setVideoJoinConf(false);

                        callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_VIDEO_JOIN_CONF_VIEW,
                                joinConfParam);
                    }
                    else
                    {
                        showCallFailDialog();
                    }
                }
                break;

                case R.id.btn_video_join_conf:
                {
                    //Hide soft keyboard
                    hideSoftKeyboard();
                    if (CallConstants.STATUS_CLOSE == CallService.getInstance().getVoipStatus())
                    {
                        String confAccessCode = confAccessCodeEditText.getText().toString();
                        String confId = confIDEditText.getText().toString();
                        String confPassword = confPasswordEditText.getText().toString();

//                        String confAccessCode = "+865711001";
//                        String confId = "121313446";
//                        String confPassword = "32955676";

                        if (Tools.isStringEmpty(confAccessCode) || Tools.isStringEmpty(confId) || Tools.isStringEmpty(confPassword))
                        {
                            Toast.makeText(CallActivity.this, "Access code, conference id or password cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        OneKeyJoinConfParam joinConfParam = new OneKeyJoinConfParam();
                        joinConfParam.setAccessCode(confAccessCode);
                        joinConfParam.setConfID(confId);
                        joinConfParam.setConfPaswd(confPassword);
                        joinConfParam.setVideoJoinConf(true);

                        callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_VIDEO_JOIN_CONF_VIEW,
                                joinConfParam);
                    }
                    else
                    {
                        showCallFailDialog();
                    }
                }
                break;

                case R.id.btn_logout:
                {
                    logoutApp();
                }

                break;
                case R.id.btn_open_local_preview:
                case R.id.btn_close_local_preview:
                    handleLocalPreview(id);
                    break;
                case R.id.contactsBtn:
//                    Intent contactsIntent = new Intent(CallActivity.this, EnterpriseContactsActivity.class);
//                    startActivity(contactsIntent);
                    break;

                case R.id.changePasswordBtn:
//                    startActivityForResult(new Intent(CallActivity.this, ChangePasswordActivity.class), 1);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

//        if (requestCode == 100) {
//
//            doInit();
//
//        }else {
            int result = data.getExtras().getInt("result");
            TUPLogUtil.i(TAG, "onActivityResult result=" + result);
            if (result == 0) {
                Toast.makeText(CallActivity.this, "change password success，need to re-login", Toast.LENGTH_LONG).show();
                TUPLogUtil.i(TAG, "onActivityResult logout");
                LoginService.getInstance().unInit();
                CallService.getInstance().tupCallUninit();
                logoutProcess();
            } else if (result != -1) {
                Toast.makeText(CallActivity.this, "change password fail，reasonCode=" + result, Toast.LENGTH_SHORT).show();
            }
//        }

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static CallActivity getInstance()
    {
        return instance.ins;
    }

    /**
     * Gets call fragment.
     *
     * @return the call fragment
     */
    public CallFragment getCallFragment()
    {
        return callFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        TUPLogUtil.i(TAG, "CallActivity onCreate");
        super.onCreate(savedInstanceState);
        doInit();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(!Settings.canDrawOverlays(getApplicationContext())) {
//                //启动Activity让用户授权
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent,100);
//            }else{
//                doInit();
//            }
//        }else{
//
//        }
    }


    private void doInit(){
        CallService.getInstance().renderCreate();
        App.isCallActivityStarted = true;
        setContentView(R.layout.activity_tup_call);
        LoginService.getInstance().registerTupNotify(this);
        ConferenceService.getInstance().registerVCDataConfNotify(this);
        DataConfService.getInstance().registerVCDataConfNotify(this);
        initView();
        initHandler();
        LayoutUtil.getInstance().initialize();
        instance.ins = this;
        boolean isCreate = getIntent().getBooleanExtra(EXTRA_IS_CREATE, false);
        if (isCreate) {

            mGroupId = getIntent().getLongExtra(EXTRA_GROUP_ID, 0);


            //gotoConf(numbers);
        } else {

            boolean isCall=getIntent().getBooleanExtra(EXTRA_IS_CALL, false);
            if(isCall){
                String number=getIntent().getStringExtra(EXTRA_CALL);

                new Handler().postDelayed(() -> {
                    callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_VIDEOVIEW,
                            number);
                }, 500);
            }else {
                boolean isJoin = getIntent().getBooleanExtra(EXTRA_IS_JOIN, false);
                if (isJoin) {
                    String confId = getIntent().getStringExtra(EXTRA_CONF_ID);
                    new Handler().postDelayed(() -> {
                        callFragment.sendHandlerMessage(CallConstants.MSG_SHOW_VIDEOVIEW,
                                confId);
                    }, 500);
                } else {
                    SessionBean sessionBean = (SessionBean) getIntent().getSerializableExtra(EXTRA_SESSIONBEAN);
                    startCallComingActivity(sessionBean);
                }
            }
        }
    }

    /**
     * Send handler message.
     *
     * @param what   the what
     * @param object the object
     */
    public void sendHandlerMessage(int what, Object object)
    {
        if (handler == null)
        {
            return;
        }
        Message msg = handler.obtainMessage(what, object);
        handler.sendMessage(msg);
    }

    /**
     * Sets camera oritation.
     *
     * @param v the v
     */
    public void setCameraOritation(View v)
    {
        CallService.getInstance().setCameraDegree(++cameraOritation, localOritation);
    }

    /**
     * Sets local oritation.
     *
     * @param v the v
     */
    public void setLocalOritation(View v)
    {
        CallService.getInstance().setCameraDegree(cameraOritation, ++localOritation);
    }


    public void gotoConf(ArrayList<String> numbers) {
        CreateConfActivity.launch(CallActivity.this, numbers);
    }


    private void initView()
    {

        controlsAreaLayout = findViewById(R.id.rl_controls);
        initCallFragment();
        accountTv = findViewById(R.id.mainAccount);
        accountTv.setText("current account :" + LoginParams.getInstance().getSipNumber());
        callAreaLayout = findViewById(R.id.linear_local);
        callNumEditText = findViewById(R.id.et_call_number);
        videoPreviewRelativeLayout = findViewById(R.id.video_preview_view);
//        callNumEditText.setText(getString(R.string.sip_number));

        audioCallButton = findViewById(R.id.btn_audio_call);
        videoCallButton = findViewById(R.id.btn_video_call);

        exitButton = findViewById(R.id.btn_logout);

        changePasswordBtn = findViewById(R.id.changePasswordBtn);

        openVideoPreviewButton = findViewById(R.id.btn_open_local_preview);
        closeVideoPreviewButton = findViewById(R.id.btn_close_local_preview);
        bookConfButton = (Button) findViewById(R.id.btn_book_conf);

        contactsBtn = findViewById(R.id.contactsBtn);

        confIDEditText = findViewById(R.id.et_conference_id);
        confAccessCodeEditText = findViewById(R.id.et_access_code);
        confPasswordEditText = findViewById(R.id.et_conference_password);
        audioJoinConfButton = findViewById(R.id.btn_audio_join_conf);
        videoJoinConfButton = findViewById(R.id.btn_video_join_conf);

        audioCallButton.setOnClickListener(clickListener);
        videoCallButton.setOnClickListener(clickListener);
        audioJoinConfButton.setOnClickListener(clickListener);
        videoJoinConfButton.setOnClickListener(clickListener);
        exitButton.setOnClickListener(clickListener);
        openVideoPreviewButton.setOnClickListener(clickListener);
        closeVideoPreviewButton.setOnClickListener(clickListener);
        changePasswordBtn.setOnClickListener(clickListener);

        contactsBtn.setOnClickListener(clickListener);
    }

    /**
     * initCallFragment
     */
    private void initCallFragment()
    {
        if (null != callFragment)
        {
            return;
        }
        callFragment = new CallFragment();

        // get FragmentManager
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.call_frag_layout, callFragment);
        try
        {
            transation.commitAllowingStateLoss();
        }
        catch (IllegalStateException e)
        {
            TUPLogUtil.i(TAG, "IllegalStateException error.");
        }
    }

    private void handleLocalPreview(int resId)
    {
        switch (resId)
        {
            case R.id.btn_open_local_preview:
                videoPreviewRelativeLayout.removeAllViews();
                if (!CallService.getInstance().isInitCallVideo())
                {
                    CallService.getInstance().initCallVideo();
                    TUPLogUtil.i(TAG, "initCallVideo");
                }
                int localVideoHandle = CallService.getInstance().getVideoHandle();
                CallService.getInstance().openLocalPreview(localVideoHandle);
                openVideoPreviewButton.setEnabled(false);
                audioCallButton.setEnabled(false);
                videoCallButton.setEnabled(false);
                bookConfButton.setEnabled(false);
                audioJoinConfButton.setEnabled(false);
                videoJoinConfButton.setEnabled(false);
                sleepThread();
                SurfaceView localVV = CallService.getInstance().getLocalHideView();
                CallService.getInstance().addViewToContainer(localVV, videoPreviewRelativeLayout);
                break;

            case R.id.btn_close_local_preview:
                CallService.getInstance().closeLocalPreview();
                CallService.getInstance().clearCallVideo();
                videoPreviewRelativeLayout.removeAllViews();
                videoPreviewRelativeLayout.setVisibility(View.GONE);
                openVideoPreviewButton.setEnabled(true);
                audioCallButton.setEnabled(true);
                videoCallButton.setEnabled(true);
                bookConfButton.setEnabled(true);
                audioJoinConfButton.setEnabled(true);
                videoJoinConfButton.setEnabled(true);

                break;
            default:
                break;
        }
    }

    /**
     * init handler
     */
    private void initHandler()
    {

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                TUPLogUtil.i(TAG, "what:" + msg.what);
                parallelHandleMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    /**
     * handle message
     *
     * @param msg
     */
    private void parallelHandleMessage(Message msg)
    {
        switch (msg.what)
        {
            case CallConstants.CALL_CLOSE_BACK_NOTIFY:
                backToWelcome();
                break;

            case CallConstants.VOIP_CALL_HANG_UP:
                Toast.makeText(CallActivity.this, ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case TupCallParam.CallEvent.CALL_E_EVT_CALL_OUTGOING:
//                showToast(R.string.call_be_issued);
                break;

            case TupCallParam.CallEvent.CALL_E_EVT_CALL_RINGBACK:
                filePath = Environment.getExternalStorageDirectory() + File.separator + RING_FILE;
                File file = new File(filePath);
                if (file.exists())
                {
                    int result = CallService.getInstance().startMediaPlay(LOOPS, filePath);
                    CallService.getInstance().setPlayHandle(result);
                }
                break;
            case CallConstants.SHOW_CALL_LAYOUT:
                showCallLayout();
                break;
            case CallConstants.MSG_NOTIFY_CALLCLOSE:
                removeCallComingActivity();
                break;
            case TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING:
                SessionBean sessionBean = (SessionBean) msg.obj;
                String displayName = sessionBean.getCallerDisplayname();
                if (mGroupId != 0) {
                    ApiFactory.getClinicManagerApi().saveHWVideoGroupId(String.valueOf(mGroupId), displayName)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(baseResp -> {
                            });
                }
                startCallComingActivity(sessionBean);
                break;
            case CallConstants.CALL_LOGOUT_NOTIFY:
                LoginService.getInstance().unInit();
                CallService.getInstance().tupCallUninit();
//                showToast(R.string.account_be_kick);
                sleepThread();
                logoutProcess();
                break;

            case DATACONF_MSG:
                TUPLogUtil.i(TAG,"----------------DATACONF_MSG");
                DataConfService.getInstance().initConf();
                DataConfService.getInstance().joinConf( (ConfInfo) msg.obj);
                break;

            default:
                break;
        }
    }


    private void startCallComingActivity(SessionBean sessionBean)
    {
        Intent intent = new Intent();
        intent.putExtra(CallConstants.VOIP_CALLNUMBER, sessionBean.getCallerNumber());
        intent.putExtra(CallConstants.VOIP_CALL_DISPLAY_NAME,
                sessionBean.getCallerDisplayname());
        intent.putExtra(CallConstants.COMING_VIEW_TYPE, sessionBean.getCallType());
        intent.putExtra(CallConstants.VOIP_CALLID, sessionBean.getCallID());
        intent.setClass(instance.ins, CallComingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.ins.startActivity(intent);
    }

    private void showToast(int resId)
    {
        Toast.makeText(CallActivity.this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    private void sleepThread()
    {
        try
        {
            Thread.sleep(THREAD_SLEEP_TIME);
        }
        catch (InterruptedException e)
        {
            TUPLogUtil.e(TAG, "thread sleep error.");
        }
    }

    /**
     * logoutApp
     */
    private void logoutApp()
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(CallActivity.this);
//        builder.setTitle(getString(R.string.logout));
//        builder.setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton(getString(R.string.logout), new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                TUPLogUtil.i(TAG, "logout");
//                LoginService.getInstance().unInit();
//                CallService.getInstance().tupCallUninit();
//                logoutProcess();
//                dialog.dismiss();
//            }
//
//        });
//        builder.create().show();
    }

    /**
     * logoutProcess
     */
    private void logoutProcess()
    {
        if (null == handler)
        {
            return;
        }
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                LoginService.getInstance().logout();
                logout();
            }
        }, DELAY_TIME);
    }

    private void logout()
    {
//        ActivityStackManager.INSTANCE.finishAllViewInTask();
    }

    /**
     * Remove incoming view
     */
    private void removeCallComingActivity()
    {
        TUPLogUtil.i(TAG, "removeCallComingActivity");
        callAreaLayout.setVisibility(View.GONE);
        controlsAreaLayout.setVisibility(View.VISIBLE);

        if (null != CallComingActivity.getInstance())
        {
            CallComingActivity.getInstance().finish();
        }
    }

    /**
     * Update the contents of the call interface display
     */
    private void showCallLayout()
    {
        TUPLogUtil.i(TAG, "showCallLayout()");
        callAreaLayout.setVisibility(View.VISIBLE);
        controlsAreaLayout.setVisibility(View.GONE);
    }

    private void backToWelcome()
    {
        TUPLogUtil.i(TAG, "backToWelcome() ");
        callAreaLayout.setVisibility(View.GONE);
        controlsAreaLayout.setVisibility(View.VISIBLE);
        finish();
    }

    private void showCallFailDialog()
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(CallActivity.this);
////        builder.setTitle(getString(R.string.msg_tip));
////        builder.setMessage(getString(R.string.call_cannot_launched));
////        builder.setPositiveButton(getString(R.string.confirm),
////                new DialogInterface.OnClickListener()
////                {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which)
////                    {
////                        dialog.dismiss();
////                    }
////                });
////        builder.create().show();
    }

    private void hideSoftKeyboard()
    {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(
                        CallActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onBackPressed() {
        backToWelcome();
    }

    /**
     * On register result.
     *
     * @param registerResult the register result
     * @param errorCode      the error code
     */
    @Override
    public void onRegisterNotify(int registerResult, int errorCode)
    {
        switch (registerResult)
        {
            case CallConstants.CALL_LOGOUT_NOTIFY:
                sendHandlerMessage(CallConstants.CALL_LOGOUT_NOTIFY, null);
                break;

            default:
                break;
        }

    }

    @Override
    public void onSMCLogin(int smcAuthorizeResult, String errorReason)
    {

    }


    @Override
    public void onCallNotify(int code, Object object)
    {
        switch (code)
        {
            case TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING:
                SessionBean sessionBean = (SessionBean) object;
                sendHandlerMessage(TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING, sessionBean);
                break;

            case TupCallParam.CallEvent.CALL_E_EVT_CALL_OUTGOING:
                sendHandlerMessage(TupCallParam.CallEvent.CALL_E_EVT_CALL_OUTGOING, null);
                break;

            case TupCallParam.CallEvent.CALL_E_EVT_CALL_RINGBACK:
                sendHandlerMessage(TupCallParam.CallEvent.CALL_E_EVT_CALL_RINGBACK, null);
                break;

            case CallConstants.VOIP_CALL_HANG_UP:
                String reasonText = (String) object;
                sendHandlerMessage(code, reasonText);
                break;


            case CallConstants.MSG_NOTIFY_CALLCLOSE:
                String callid = (String) object;
                sendHandlerMessage(CallConstants.MSG_NOTIFY_CALLCLOSE, callid);
                break;

            case CallConstants.MSG_CALL_UPDATE_UI:
               landscape=true;
                break;
            default:
                break;
        }
    }


    private boolean landscape;

    @Override
    protected void onRestart() {
        super.onRestart();

        if( !landscape) return;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (null != handler)
        {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        instance.ins = null;
        LoginService.getInstance().unregisterTupNotify(this);
        App.isCallActivityStarted = false;
        super.onDestroy();
    }

//    /**
//     * clear Activity init data
//     */
//    @Override
//    public void clearData()
//    {
//        if (null != handler)
//        {
//            handler.removeCallbacksAndMessages(null);
//            handler = null;
//        }
//        instance.ins = null;
//    }

    @Override
    public void onGetDataConfParamsResult(ConfInfo confInfo)
    {
        TUPLogUtil.i(TAG,"--------------onGetDataConfParamsResult---");
        sendHandlerMessage(DATACONF_MSG, confInfo);
    }

    @Override
    public void onDataShareResult(int shareVal, int shareStatus)
    {
        TUPLogUtil.i(TAG,"------------onDataShareResult---shareVal:"+shareVal+",shareStatus:"+shareStatus);
    }

    private static class Instance
    {
        private CallActivity ins;

        @Override
        public String toString()
        {
            return "Instance [ins=" + ins + ']';
        }
    }


}
