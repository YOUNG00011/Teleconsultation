package com.wxsoft.telereciver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.entity.SystemMessage;
import com.wxsoft.telereciver.ui.activity.SystemMessageActivity;
import com.wxsoft.telereciver.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.telereciver.util.AppUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;


public class MyReceiver extends BroadcastReceiver {

    public final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
//            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
                JSONObject jsonObject = new JSONObject(json);
                String msgType = jsonObject.getString("MsgType");

                if (msgType.equals(SystemMessage.ExtendFiled.MSG_TYPE_CLINIC)) {
                    String clinicId = jsonObject.getString("Object");
                    if (AppUtil.isAppRunning(context, "com.wxsoft.telereciver")) {
                        ClinicDetailActivity.launch(context, clinicId);
                    } else {
                        Intent launchIntent = context.getPackageManager().
                                getLaunchIntentForPackage("com.wxsoft.telereciver");
                        launchIntent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        Bundle args = new Bundle();
                        args.putString("clinicId", clinicId);
                        launchIntent.putExtra(AppConstant.EXTRA_BUNDLE, args);
                        context.startActivity(launchIntent);
                    }
                } else if (msgType.equals(SystemMessage.ExtendFiled.MSG_TYPE_SYSTEM)) {
                    String systemId = jsonObject.getString("Object");
                    if (AppUtil.isAppRunning(context, "com.wxsoft.telereciver")) {
                        SystemMessageActivity.launch(context);
                    } else {
                        Intent launchIntent = context.getPackageManager().
                                getLaunchIntentForPackage("com.wxsoft.telereciver");
                        launchIntent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        Bundle args = new Bundle();
                        args.putString("systemId", systemId);
                        launchIntent.putExtra(AppConstant.EXTRA_BUNDLE, args);
                        context.startActivity(launchIntent);
                    }
                }
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e){

        }
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e(TAG, "message:" + message + "-" + "extras" + extras);
    }
}
