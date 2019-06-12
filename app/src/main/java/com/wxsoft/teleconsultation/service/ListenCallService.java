package com.wxsoft.teleconsultation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.vc.service.TupNotify;
import com.wxsoft.teleconsultation.vc.service.call.data.SessionBean;
import com.wxsoft.teleconsultation.vc.service.login.LoginService;
import com.wxsoft.teleconsultation.vc.service.utils.TUPLogUtil;
import com.wxsoft.teleconsultation.vc.ui.activity.CallActivity;

import common.TupCallParam;

public class ListenCallService extends Service implements TupNotify {

    private static final String TAG = ListenCallService.class.getSimpleName();

    public static void start(Context context) {
        context.startService(new Intent(context, ListenCallService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, ListenCallService.class));
    }

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        LoginService.getInstance().registerTupNotify(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TUPLogUtil.i(TAG, "what:" + msg.what);
                parallelHandleMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRegisterNotify(int registerResult, int errorCode) {

    }

    @Override
    public void onSMCLogin(int smcAuthorizeResult, String errorReason) {

    }

    @Override
    public void onCallNotify(int code, Object object) {
        switch (code) {
            case TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING:
                SessionBean sessionBean = (SessionBean) object;
                sendHandlerMessage(TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING, sessionBean);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        LoginService.getInstance().unregisterTupNotify(this);
        super.onDestroy();
    }

    /**
     * Send handler message.
     *
     * @param what   the what
     * @param object the object
     */
    public void sendHandlerMessage(int what, Object object) {
        if (handler == null) {
            return;
        }
        Message msg = handler.obtainMessage(what, object);
        handler.sendMessage(msg);
    }

    /**
     * handle message
     *
     * @param msg
     */
    private void parallelHandleMessage(Message msg) {
        switch (msg.what) {
            case TupCallParam.CallEvent.CALL_E_EVT_CALL_INCOMMING:
                if (!App.isCallActivityStarted) {
                    SessionBean sessionBean = (SessionBean) msg.obj;
                    CallActivity.launchFromComing(ListenCallService.this, sessionBean);
                }
                break;
            default:
                break;
        }
    }
}
