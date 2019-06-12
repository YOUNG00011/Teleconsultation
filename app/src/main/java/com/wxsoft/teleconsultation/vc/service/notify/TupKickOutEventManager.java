package com.wxsoft.teleconsultation.vc.service.notify;

import android.util.Log;

import com.wxsoft.teleconsultation.vc.service.TupEventHandler;
import com.wxsoft.teleconsultation.vc.service.TupEventMgr;
import com.wxsoft.teleconsultation.vc.service.TupServiceNotifyImpl;
import com.wxsoft.teleconsultation.vc.service.common.CallConstants;
import com.wxsoft.teleconsultation.vc.service.utils.TUPLogUtil;

import common.TupBool;
import common.TupCallParam;
import object.KickOutInfo;
import object.TupRegisterResult;


/**
 * The type Tup register event manager.
 */
public final class TupKickOutEventManager extends TupServiceNotifyImpl
{
    /**
     * The constant TAG.
     */
    private static final String TAG = TupKickOutEventManager.class.getSimpleName();
    /**
     * The constant tupLoginEventManager.
     */
    private static TupKickOutEventManager tupLoginEventManager = new TupKickOutEventManager();

    /**
     * Instantiates a new Tup login event manager.
     */
    private TupKickOutEventManager()
    {
        TupEventHandler.getTupEventHandler().registerTupServiceNotify(this);
    }

    /**
     * Gets tup event manager.
     *
     * @return the tup event manager
     */
    public static TupKickOutEventManager getTupLoginEventManager()
    {
        return tupLoginEventManager;
    }

    @Override
    public void onRegisterResult(TupRegisterResult tupRegisterResult)
    {
        if (null == tupRegisterResult)
        {
            TUPLogUtil.e(TAG, "tupRegisterResult is null");
            return;
        }
        int regState = tupRegisterResult.getRegState();
        int errorCode = tupRegisterResult.getReasonCode();
        Log.e(TAG,"---------errorCode:"+errorCode);
        onRegisterEvent(regState, errorCode);
    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo)
    {
        if (null == kickOutInfo)
        {
            TUPLogUtil.e(TAG, "kickOutInfo is null");
            return;
        }
        handleTupKickedOut(kickOutInfo);
    }

    /**
     * On register event.
     *
     * @param code      the code
     * @param errorCode the error code
     */
    private void onRegisterEvent(int code, int errorCode)
    {
        TUPLogUtil.i(TAG, "code" + "|" + code + "errorCode" + "|" + errorCode);
        switch (code)
        {
            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER:
                TUPLogUtil.i(TAG, "errorCode" + "|" + errorCode);
                onLoginResult(CallConstants.State.UNREGISTER, errorCode);
                TUPLogUtil.i(TAG, "unregister.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING:
                onLoginResult(CallConstants.State.REGISTERING, errorCode);
                TUPLogUtil.i(TAG, "registering.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING:
                onLoginResult(CallConstants.State.DEREGISTERING, errorCode);
                TUPLogUtil.i(TAG, "deregistering.");
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED:
                TUPLogUtil.i(TAG, "register success.");
                onLoginResult(CallConstants.State.REGISTERED, TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
                break;

            case TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT:
                onLoginResult(CallConstants.State.BUTT, errorCode);
                TUPLogUtil.i(TAG, "login out success.");
                break;

            default:
                break;
        }
    }

    /**
     * Handle tup kicked out.
     *
     * @param kickOutInfo the kick out info
     */
    private void handleTupKickedOut(KickOutInfo kickOutInfo)
    {
        TupBool isKickOff = kickOutInfo.getIsKickOff();
        TUPLogUtil.i(TAG, "isKickOff->" + isKickOff);

        if (TupBool.TUP_TRUE.equals(isKickOff))
        {
            TupEventMgr.onRegisterEventNotify(CallConstants.CALL_LOGOUT_NOTIFY,
                    TupCallParam.CALL_TUP_RESULT.TUP_SUCCESS);
        }
    }

    /**
     * onLoginResult.
     *
     * @param status    the status
     * @param errorCode the error code
     */
    private void onLoginResult(CallConstants.State status, int errorCode)
    {
        TUPLogUtil.i(TAG, "run onLoginResult");
        if (status == null)
        {
            TUPLogUtil.e(TAG, "status is null");
            return;
        }

        TUPLogUtil.i(TAG, "MenuItem value" + status.ordinal());

        if (status == CallConstants.State.REGISTERED)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED, errorCode);
        }
        else if (status == CallConstants.State.UNREGISTER)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_UNREGISTER, errorCode);
        }
        else if (status == CallConstants.State.REGISTERING)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERING, errorCode);
        }
        else if (status == CallConstants.State.DEREGISTERING)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING, errorCode);
        }
        else if (status == CallConstants.State.BUTT)
        {
            TupEventMgr.onRegisterEventNotify(
                    TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_BUTT, errorCode);
        }

    }


}
