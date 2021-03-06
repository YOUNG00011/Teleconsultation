package com.wxsoft.telereciver.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.huawei.utils.ZipUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.conversation.Event;
import com.wxsoft.telereciver.entity.conversation.EventType;
import com.wxsoft.telereciver.entity.prescription.ChatRecord;
import com.wxsoft.telereciver.entity.prescription.PrescriptionCon;
import com.wxsoft.telereciver.event.ImageEvent;
import com.wxsoft.telereciver.event.UpdatePrescriptionConStatusEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.adapter.ChattingListAdapter;
import com.wxsoft.telereciver.ui.base.BaseActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll.SelectDoctorFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.diseasecounseling.DiseaseCounselingRefuseFragment;
import com.wxsoft.telereciver.ui.widget.ChatView;
import com.wxsoft.telereciver.ui.widget.SimpleAppsGridView;
import com.wxsoft.telereciver.ui.widget.listview.DropDownListView;
import com.wxsoft.telereciver.util.ViewUtil;
import com.wxsoft.telereciver.util.keyboard.XhsEmoticonsKeyBoard;
import com.wxsoft.telereciver.util.keyboard.utils.EmoticonsKeyboardUtils;
import com.wxsoft.telereciver.util.keyboard.widget.FuncLayout;
import com.wxsoft.telereciver.util.sendimage.SendImageHelper;
import com.wxsoft.telereciver.vc.service.TupNotify;
import com.wxsoft.telereciver.vc.service.call.CallService;
import com.wxsoft.telereciver.vc.service.common.CallConstants;
import com.wxsoft.telereciver.vc.service.login.LoginService;
import com.wxsoft.telereciver.vc.service.login.data.LoginParams;
import com.wxsoft.telereciver.vc.service.utils.TUPLogUtil;
import com.wxsoft.telereciver.vc.service.utils.Tools;
import com.wxsoft.telereciver.vc.ui.activity.CallActivity;

import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import common.TupCallParam;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class Chat2Activity extends BaseActivity
        implements FuncLayout.OnFuncKeyBoardListener,
        View.OnClickListener,
        TupNotify {

    public static final String EXTRA_KEY_FROM_GROUP = "fromGroup";
    public static final String EXTRA_KEY_SINGE = "single";
    public static final String EXTRA_KEY_MODULE = "module";
    public static final String EXTRA_KEY_CONV = "conv";
    public static final String EXTRA_KEY_DISEASECOUNSELING_ID = "dis_id";
    public static final String EXTRA_KEY_CONV_TITLE = "CONV_TITLE";
    public static final String EXTRA_KEY_MEMBERS_COUNT = "MEMBERS_COUNT";
    public static final String EXTRA_KEY_GROUP_ID = "GROUP_ID";
    public static final String EXTRA_KEY_DRAFT = "EXTRA_KEY_DRAFT";

    public static final String JPG = ".jpg";
    private static String MsgIDs = "msgIDs";

    private String diseaseCounselingId;
    private PrescriptionCon con;

    @BindView(R.id.chat_view)
    ChatView mChatView;

    @BindView(R.id.ll_4th_action)
    LinearLayout mDoubleActionLayout;

    @BindView(R.id.tv_double_action_1)
    TextView mDoubleAction1View;

    @BindView(R.id.tv_double_action_2)
    TextView mDoubleAction2View;

    @BindView(R.id.jmui_complate)
    TextView tvDiseaseComplate;

    @BindView(R.id.rel_input)
    RelativeLayout mInputLayout;

    @BindView(R.id.lv_chat)
    DropDownListView lvChat;

    @BindView(R.id.ek_bar)
    XhsEmoticonsKeyBoard ekBar;

    private float mDensity;
    private int mDensityDpi;

    private String mTitle;
    private boolean mLongClick = false;

    private static final String GROUP_NAME = "groupName";

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String TARGET_APP_ALLOW_EDIT = "TARGET_APP_ALLOW_EDIT";
    //    private ArrayList<ImageItem> selImageList; //???????????????????????????
    public static final int REQUEST_CODE_SELECT = 100;
    private boolean mIsSingle = true;
    private Conversation mConv;
    private Activity mContext;
    private ChattingListAdapter mChatAdapter;
    int maxImgCount = 9;
    private List<UserInfo> mAtList;
    private long mGroupId;
    private static final int REFRESH_LAST_PAGE = 0x1023;
    private static final int REFRESH_CHAT_TITLE = 0x1024;
    private static final int REFRESH_GROUP_NAME = 0x1025;
    private static final int REFRESH_GROUP_NUM = 0x1026;

    private GroupInfo mGroupInfo;
    private UserInfo mMyInfo;
    private int mUnreadMsgCnt;
    private boolean mShowSoftInput = false;
    private List<UserInfo> forDel = new ArrayList<>();

    Window mWindow;
    InputMethodManager mImm;
    private final UIHandler mUIHandler = new UIHandler(this);
    private boolean mAtAll = false;

    //wifi???4G???3G login
    private static final String NETWORK_COMMON = "common";
    private static final int TOAST_FLAG = 1;
    private static final int SMC_LOGIN_SUCCESS = 100;
    private static final int SMC_LOGIN_FAILED = 101;
    private static final Object LOCK = new Object();

    private String networkType = NETWORK_COMMON;
    private String module;
    private String ipAddress;
    private String sipURI;
    private Handler mHandler;

    @OnClick(R.id.jmui_complate)
    void complateClick() {
        if (con.status.equals("906-0002")) {
            new MaterialDialog.Builder(this)
                    .title("??????24????????????????????????")
                    .content("???????????????????????????????????????")
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ApiFactory.getDiseaseCounselingApi().complete(diseaseCounselingId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<BaseResp<String>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ViewUtil.dismissProgressDialog();
                                        ViewUtil.showMessage(e.getMessage());
                                    }

                                    @Override
                                    public void onNext(BaseResp<String> resp) {
                                        ViewUtil.dismissProgressDialog();
                                        if (resp.isSuccess()) {
                                            org.greenrobot.eventbus.EventBus.getDefault().post(new UpdatePrescriptionConStatusEvent(diseaseCounselingId, "906-0003"));
                                            stateView("303-0006");
                                        } else {


                                            ViewUtil.showMessage(resp.getMessage());
                                        }
                                    }
                                });
                    })
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else if (con.status.equals("906-0002")) {
            DiseaseCounselingRefuseFragment.launch(this, diseaseCounselingId);
        }
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdatePrescriptionConStatusEvent) {
            UpdatePrescriptionConStatusEvent event = (UpdatePrescriptionConStatusEvent) object;
            if (diseaseCounselingId.equals(event.id)) {
                con.status = event.status;
                stateView(event.status);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        LoginService.getInstance().registerTupNotify(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;

        mContext = this;
        //??????sdk???event??????????????????event??????
        JMessageClient.registerEventReceiver(this);
        mChatView.initModule(mDensity, mDensityDpi);
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        this.mWindow = getWindow();
        this.mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatView.setListeners(this);

        setupHandler();

        initData();
        initView();
    }


    @Subscribe

    private void initData() {
        Intent intent = getIntent();
        module = intent.getStringExtra(EXTRA_KEY_MODULE);
        mTitle = intent.getStringExtra(EXTRA_KEY_CONV_TITLE);
        mMyInfo = JMessageClient.getMyInfo();

        mGroupId = intent.getLongExtra(EXTRA_KEY_GROUP_ID, 0);
        final boolean fromGroup = intent.getBooleanExtra("fromGroup", false);
        mIsSingle = intent.getBooleanExtra(EXTRA_KEY_SINGE, false);
        if (mIsSingle) {
            String user = intent.getStringExtra(EXTRA_KEY_CONV);


            if (user == null) {
                diseaseCounselingId = intent.getStringExtra(EXTRA_KEY_DISEASECOUNSELING_ID);
                boolean allowEdit = intent.getBooleanExtra(TARGET_APP_ALLOW_EDIT, true);
                // ekBar.findViewById(R.id.edit_panel).setVisibility(allowEdit?View.VISIBLE:View.GONE);
                ApiFactory.getPrescriptionApi().getConsultationDetail(diseaseCounselingId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseResp<PrescriptionCon>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ViewUtil.dismissProgressDialog();
                                ViewUtil.showMessage(e.getMessage());
                            }

                            @Override
                            public void onNext(BaseResp<PrescriptionCon> resp) {
                                ViewUtil.dismissProgressDialog();
                                if (resp.isSuccess()) {
                                    con = resp.getData();
                                    mChatView.setChatTitle(mTitle);
                                    stateView(con.status);
                                    mConv = JMessageClient.getSingleConversation(con.weChatAccount.jMessagAccount.getjUserName(), mMyInfo.getAppKey());
                                    if (mConv == null) {
                                        mConv = Conversation.createSingleConversation(con.weChatAccount.jMessagAccount.getjUserName(), mMyInfo.getAppKey());
                                    }

                                    mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);//????????????????????????
                                    mChatView.setChatListAdapter(mChatAdapter);
                                    mChatView.getListView().setOnDropDownListener(() -> mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000));
                                    mChatView.setToBottom();
                                    mChatView.setConversation(mConv);
                                } else {


                                    ViewUtil.showMessage(resp.getMessage());
                                }
                            }
                        });
            } else {

                ekBar.findViewById(R.id.edit_panel).setVisibility(View.VISIBLE);
                mConv = JMessageClient.getSingleConversation(user, mMyInfo.getAppKey());
                mChatView.setChatTitle(mTitle);
                mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);//????????????????????????
                mChatView.setChatListAdapter(mChatAdapter);
                mChatView.getListView().setOnDropDownListener(() -> mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000));
                mChatView.setToBottom();
                mChatView.setConversation(mConv);
            }


        }
    }

    private void initView() {
        initEmoticonsKeyBoardBar();
        initListView();
    }

    private void initEmoticonsKeyBoardBar() {
        SimpleAppsGridView gridView = new SimpleAppsGridView(this);
        ekBar.addFuncView(gridView);

        ekBar.getEtChat().setOnSizeChangedListener((w, h, oldw, oldh) -> scrollToBottom());
        //????????????
        ekBar.getBtnSend().setOnClickListener(v -> {
            String mcgContent = ekBar.getEtChat().getText().toString();
            scrollToBottom();
            if (mcgContent.equals("")) {
                return;
            }
            Message msg;
            TextContent content = new TextContent(mcgContent);
            if (mAtAll) {
                msg = mConv.createSendMessageAtAllMember(content, null);
                mAtAll = false;
            } else if (null != mAtList) {
                msg = mConv.createSendMessage(content, mAtList, null);
            } else {
                msg = mConv.createSendMessage(content);
            }
            //????????????????????????
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);
            JMessageClient.sendMessage(msg, options);
            mChatAdapter.addMsgFromReceiptToList(msg);
            if (con != null && con.status.equals("303-0002")) {
                start(mcgContent);
            }
            //EventBus.getDefault().post(new UpdatePrescriptionConStatusEvent(diseaseCounselingId,"303-0003"));
            ekBar.getEtChat().setText("");
            if (mAtList != null) {
                mAtList.clear();
            }
            if (forDel != null) {
                forDel.clear();
            }
        });
        //??????????????????
        ekBar.getVoiceOrText().setOnClickListener(v -> {
            int i = v.getId();
            if (i == R.id.btn_voice_or_text) {
                ekBar.setVideoText();
                ekBar.getBtnVoice().initConv(mConv, mChatAdapter, mChatView);
            }
        });


    }

    private void initListView() {
        lvChat.setAdapter(mChatAdapter);
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ekBar.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    protected void onResume() {
        networkType = NETWORK_COMMON;
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (!mIsSingle) {
            long groupId = getIntent().getLongExtra(EXTRA_KEY_GROUP_ID, 0);
            if (groupId != 0) {
                JMessageClient.enterGroupConversation(groupId);
            }
        }
        if (mChatAdapter != null)
            mChatAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
        ekBar.reset();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnBtn();
    }

    @Override
    protected void onDestroy() {
        LoginService.getInstance().unregisterTupNotify(this);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        JMessageClient.unRegisterEventReceiver(this);
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
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
                handleRequestError(errorCode, this);
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
            public void handleMessage(android.os.Message msg) {
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
        android.os.Message msg = mHandler.obtainMessage(what, object);
        mHandler.sendMessage(msg);
    }

    private void parallelHandleMessage(android.os.Message msg) {
        switch (msg.what) {
            case TOAST_FLAG:
                ViewUtil.showMessage(((String) msg.obj));
                break;

            case SMC_LOGIN_SUCCESS:
                processLogin(AppContext.getUser().getHwUserName(), AppContext.getUser().getHwPassword());
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
        sipURI = AppContext.getUser().getHwUserName() + "@" + AppContext.REGISTER_SERVER;
        getIpAddress();
        LoginService.getInstance().setIpAddress(ipAddress);
        importHWCer();
        processLogin(AppContext.getUser().getHwUserName(), AppContext.getUser().getHwPassword());
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

    private void onLoginSuccess() {
//        new Thread(() -> {
//            synchronized (LOCK) {
//                startVideo();
//            }
//        }).start();
    }

    private void handleRequestError(int errorCode, Activity activity) {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jmui_return_btn:
                returnBtn();
                break;
            default:
                break;
        }
    }

    private void returnBtn() {
        if (mConv == null) return;
        mConv.resetUnreadCount();
        dismissSoftInput();
        JMessageClient.exitConversation();
        //??????????????????????????????????????????
        EventBus.getDefault().post(new Event.Builder().setType(EventType.draft)
                .setConversation(mConv)
                .setDraft(ekBar.getEtChat().getText().toString())
                .build());
        finish();
    }

    private void dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(ekBar.getEtChat().getWindowToken(), 0);
                mShowSoftInput = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {

    }

    private void scrollToBottom() {
        lvChat.requestLayout();
        lvChat.post(() -> lvChat.setSelection(lvChat.getBottom()));
    }

    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();

        runOnUiThread(() -> {
            Object obj = message.getTargetInfo();

            if (mIsSingle) {
                String userId = ((UserInfo) obj).getUserName();
                String theId = con.weChatAccount.jMessagAccount.getUserId();
                if (userId.equals(theId)) {
                    Message lastMsg = mChatAdapter.getLastMsg();
                    if (lastMsg == null || message.getId() != lastMsg.getId()) {
                        mChatAdapter.addMsgToList(message);
                    } else {
                        mChatAdapter.notifyDataSetChanged();
                    }
                }
            } else {


                long groupId = ((GroupInfo) obj).getGroupID();
                if (groupId == mGroupId) {
                    Message lastMsg = mChatAdapter.getLastMsg();
                    if (lastMsg == null || message.getId() != lastMsg.getId()) {
                        mChatAdapter.addMsgToList(message);
                    } else {
                        mChatAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
        if (groupId == mGroupId) {
            List<Message> offlineMessageList = event.getOfflineMessageList();
            if (offlineMessageList != null && offlineMessageList.size() > 0) {
                mChatView.setToBottom();
                mChatAdapter.addMsgListToList(offlineMessageList);
            }
        }
    }

    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        mChatAdapter.delMsgRetract(retractedMessage);
    }

    private ChattingListAdapter.ContentLongClickListener longClickListener = new ChattingListAdapter.ContentLongClickListener() {

        @Override
        public void onContentLongClick(final int position, View view) {
//            final Message msg = mChatAdapter.getMessage(position);
//
//            if (msg == null) {
//                return;
//            }
//            //?????????????????????
//            if ((msg.getContentType() == ContentType.image) && ((ImageContent) msg.getContent()).getStringExtra("businessCard") == null) {
//
//            }
//                //?????????
//                if (msg.getDirect() == MessageDirect.receive) {
//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    float OldListY = (float) location[1];
//                    float OldListX = (float) location[0];
//                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
//                            .addItem(new TipItem("??????"))
//                            .addItem(new TipItem("??????"))
//                            .setOnItemClickListener(new TipView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(String str, final int position) {
//                                    if (position == 0) {
//                                        if (msg.getContentType() == ContentType.text) {
//                                            final String content = ((TextContent) msg.getContent()).getText();
//                                            if (Build.VERSION.SDK_INT > 11) {
//                                                ClipboardManager clipboard = (ClipboardManager) mContext
//                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
//                                                ClipData clip = ClipData.newPlainText("Simple text", content);
//                                                clipboard.setPrimaryClip(clip);
//                                            } else {
//                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
//                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
//                                                if (clip.hasText()) {
//                                                    clip.getText();
//                                                }
//                                            }
//                                            Toast.makeText(ChatActivity.this, "?????????", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(ChatActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        //??????
//                                        mConv.deleteMessage(msg.getId());
//                                        mChatAdapter.removeMessage(msg);
//                                    }
//                                }
//
//                                @Override
//                                public void dismiss() {
//
//                                }
//                            })
//                            .create();
//                    //?????????
//                } else {
//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    float OldListY = (float) location[1];
//                    float OldListX = (float) location[0];
//                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
//                            .addItem(new TipItem("??????"))
//                            .addItem(new TipItem("??????"))
//                            .addItem(new TipItem("??????"))
//                            .setOnItemClickListener(new TipView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(String str, final int position) {
//                                    if (position == 0) {
//                                        if (msg.getContentType() == ContentType.text) {
//                                            final String content = ((TextContent) msg.getContent()).getText();
//                                            if (Build.VERSION.SDK_INT > 11) {
//                                                ClipboardManager clipboard = (ClipboardManager) mContext
//                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
//                                                ClipData clip = ClipData.newPlainText("Simple text", content);
//                                                clipboard.setPrimaryClip(clip);
//                                            } else {
//                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
//                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
//                                                if (clip.hasText()) {
//                                                    clip.getText();
//                                                }
//                                            }
//                                            Toast.makeText(ChatActivity.this, "?????????", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(ChatActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else if (position == 1) {
//                                        //??????
//                                        mConv.retractMessage(msg, new BasicCallback() {
//                                            @Override
//                                            public void gotResult(int i, String s) {
//                                                if (i == 855001) {
//                                                    Toast.makeText(ChatActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
//                                                } else if (i == 0) {
//                                                    mChatAdapter.delMsgRetract(msg);
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        //??????
//                                        mConv.deleteMessage(msg.getId());
//                                        mChatAdapter.removeMessage(msg);
//                                    }
//                                }
//
//                                @Override
//                                public void dismiss() {
//
//                                }
//                            })
//                            .create();
//                }
//                //?????????????????????????????????????????????
//            } else {
//                //?????????
//                if (msg.getDirect() == MessageDirect.receive) {
//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    float OldListY = (float) location[1];
//                    float OldListX = (float) location[0];
//                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
//                            .addItem(new TipItem("??????"))
//                            .setOnItemClickListener(new TipView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(String str, final int position) {
//                                    if (position == 0) {
//                                        //??????
//                                        mConv.deleteMessage(msg.getId());
//                                        mChatAdapter.removeMessage(msg);
//                                    }
//                                }
//
//                                @Override
//                                public void dismiss() {
//
//                                }
//                            })
//                            .create();
//                    //?????????
//                } else {
//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    float OldListY = (float) location[1];
//                    float OldListX = (float) location[0];
//                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
//                            .addItem(new TipItem("??????"))
//                            .addItem(new TipItem("??????"))
//                            .setOnItemClickListener(new TipView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(String str, final int position) {
//                                    if (position == 0) {
//                                        //??????
//                                        mConv.retractMessage(msg, new BasicCallback() {
//                                            @Override
//                                            public void gotResult(int i, String s) {
//                                                if (i == 855001) {
//                                                    Toast.makeText(ChatActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
//                                                } else if (i == 0) {
//                                                    mChatAdapter.delMsgRetract(msg);
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        //??????
//                                        mConv.deleteMessage(msg.getId());
//                                        mChatAdapter.removeMessage(msg);
//                                    }
//                                }
//
//                                @Override
//                                public void dismiss() {
//
//                                }
//                            })
//                            .create();
//                }
//            }
        }
    };

    /**
     * ??????????????????
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        List<MessageReceiptStatusChangeEvent.MessageReceiptMeta> messageReceiptMetas = event.getMessageReceiptMetas();
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : messageReceiptMetas) {
            long serverMsgId = meta.getServerMsgId();
            int unReceiptCnt = meta.getUnReceiptCnt();
            mChatAdapter.setUpdateReceiptCount(serverMsgId, unReceiptCnt);
        }
    }

    public void onEventMainThread(ImageEvent event) {
        switch (event.getFlag()) {
            case ImageEvent.IMAGE_MESSAGE:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .isCamera(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case ImageEvent.TAKE_PHOTO_MESSAGE:
                PictureSelector.create(this)
                        .openCamera(PictureMimeType.ofImage())
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case ImageEvent.VIDEO_CLINIC_MESSAGE:
                if (TextUtils.isEmpty(AppContext.user.getHwUserName())) {
                    ViewUtil.createProgressDialog(Chat2Activity.this, "");
                    ApiFactory.getLoginApi().getHWAccountByUserId(AppContext.user.getId())
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
                                    if (!resp.isSuccess() || resp.getData() == null) {
                                        ViewUtil.dismissProgressDialog();
                                        ViewUtil.showMessage("???????????????????????????");
                                        return;
                                    }

                                    AppContext.getUser().setHWAccount(resp.getData());
                                    hwLogin();
                                }
                            });
                    return;
                }

                checkCodeSended();

//                startVideo();
                break;
            default:
                break;
        }
    }

    private void checkCodeSended() {
        ApiFactory.getPrescriptionApi().getInvitationCodeByBusinessId(diseaseCounselingId)
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
                    public void onNext(BaseResp baseResp) {
                        if(baseResp.getData().equals("")){
                            showMessageDialog();
                        }else {
                            startVideo(baseResp.getData().toString());
                        }
                    }
                });
    }

    private void showMessageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");
        builder.setMessage("??????????????????????????????????????????????????????????????????");
        builder.setPositiveButton("??????", (dialogInterface, i) -> sendInvitationCode());
        builder.setNegativeButton("??????", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInvitatedCode(String code){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????");
        builder.setMessage(code);
        builder.setNegativeButton(getString(R.string.refuse), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendInvitationCode(){
        ApiFactory.getPrescriptionApi().sendInvitationCode(diseaseCounselingId,module)
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
                        if (resp.isSuccess()) {
                            showInvitatedCode(resp.getData().toString());
                            Toast.makeText(Chat2Activity.this,"????????????",Toast.LENGTH_SHORT).show();

                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void startVideo(String phone) {
        CallActivity.launchForCall(this, phone);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                onPickImageActivityResult(requestCode, data);
                break;
        }
    }


    /**
     * ??????????????????
     */
    private void onPickImageActivityResult(int requestCode, Intent data) {
        if (data == null) {
            return;
        }

        sendImageAfterSelfImagePicker(data);
    }

    /**
     * ????????????
     */

    private void sendImageAfterSelfImagePicker(final Intent data) {
        SendImageHelper.sendImageAfterSelfImagePicker(this, data, (file, isOrig) -> {
            // ??????????????????????????????
            ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                    if (responseCode == 0) {
                        Message msg = mConv.createSendMessage(imageContent);
                        handleSendMsg(msg.getId());
                    }
                }
            });
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param data intent
     */
    private void handleSendMsg(int data) {
        mChatAdapter.setSendMsgs(data);
        mChatView.setToBottom();
    }

    private static class UIHandler extends Handler {
        private final WeakReference<Chat2Activity> mActivity;

        public UIHandler(Chat2Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Chat2Activity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_LAST_PAGE:
                        activity.mChatAdapter.dropDownToRefresh();
                        activity.mChatView.getListView().onDropDownComplete();
                        if (activity.mChatAdapter.isHasLastPage()) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                activity.mChatView.getListView()
                                        .setSelectionFromTop(activity.mChatAdapter.getOffset(),
                                                activity.mChatView.getListView().getHeaderHeight());
                            } else {
                                activity.mChatView.getListView().setSelection(activity.mChatAdapter
                                        .getOffset());
                            }
                            activity.mChatAdapter.refreshStartPosition();
                        } else {
                            activity.mChatView.getListView().setSelection(0);
                        }
                        //???????????????????????????18???
                        activity.mChatView.getListView()
                                .setOffset(activity.mChatAdapter.getOffset());
                        break;
                    case REFRESH_GROUP_NAME:
                        if (activity.mConv != null) {
                            int num = msg.getData().getInt(EXTRA_KEY_MEMBERS_COUNT);
                            String groupName = msg.getData().getString(GROUP_NAME);
                            activity.mChatView.setChatTitle(groupName, num);
                        }
                        break;
                    case REFRESH_GROUP_NUM:
                        int num = msg.getData().getInt(EXTRA_KEY_MEMBERS_COUNT);
                        activity.mChatView.setChatTitle(R.string.group, num);
                        break;
                    case REFRESH_CHAT_TITLE:
                        if (activity.mGroupInfo != null) {
                            //??????????????????????????????
                            UserInfo info = activity.mGroupInfo.getGroupMemberInfo(activity.mMyInfo.getUserName(),
                                    activity.mMyInfo.getAppKey());
                            if (!TextUtils.isEmpty(activity.mGroupInfo.getGroupName())) {
                                if (info != null) {
                                    activity.mChatView.setChatTitle(activity.mTitle,
                                            activity.mGroupInfo.getGroupMembers().size());
                                } else {
                                    activity.mChatView.setChatTitle(activity.mTitle);
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

    private void stateView(String state){
        if(state.equals("906-0001")||state.equals("906-0002")){
            mInputLayout.setVisibility(View.VISIBLE);
            mDoubleActionLayout.setVisibility(View.GONE);
            //tvDiseaseComplate.setVisibility(View.VISIBLE);
            if(state.equals("906-0002")){
                tvDiseaseComplate.setText(getString(R.string.str_disease_counseling_complate));
            }
        }else if(state.equals("906-0003") ||state.equals("906-0004")){
            mInputLayout.setVisibility(View.GONE);
            mDoubleActionLayout.setVisibility(View.VISIBLE);
            mDoubleAction1View.setText(R.string.str_recommend);
            mDoubleAction2View.setText(R.string.str_transfer_treatment);
            tvDiseaseComplate.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {
        SelectDoctorFragment.launch(this, BusinessType.COUNSELING);
    }

    @OnClick(R.id.tv_double_action_2)
    void doubleAction2Click() {
        SelectDoctorFragment.launch(this, BusinessType.COUNSELING);
    }

    private void start(String content){
        //if(!con.status.equals("303-0002"))return;
       // con.status="303-0003";

        ChatRecord record=new ChatRecord();
        record.contentType="305-0001";
        record.content=content;
        record.businessId=diseaseCounselingId;
        record.msgDirectionType="309-0001";
        record.weChatAccountId=con.weChatAccountId;
        record.weChatNickName=con.weChatAccount.nickname;

        record.doctorId=AppContext.getUser().getDoctId();
        record.doctorName=AppContext.getUser().getName();
        ApiFactory.getPrescriptionApi().saveChatRecord(record)
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
                        if (resp.isSuccess()) {
                            org.greenrobot.eventbus.EventBus.getDefault().post(new UpdatePrescriptionConStatusEvent(diseaseCounselingId,"303-0003"));

                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
