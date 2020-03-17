package com.wxsoft.telereciver.ui.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.vc.service.TupEventHandler;
import com.wxsoft.telereciver.vc.service.TupServiceNotify;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;
import object.KickOutInfo;
import object.OnLineState;
import object.TupRegisterResult;
import tupsdk.TupCall;

/**
 * Created by liping on 2018/3/19.
 */
public abstract  class SupportBaseActivity extends SupportActivity implements TupServiceNotify {

    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        ButterKnife.bind(this);
        BaseAppManager.getInstance().addActivity(this);
        TupEventHandler.getTupEventHandler().registerTupServiceNotify(this);
        setupViews(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract int getLayoutId();

    protected abstract   void setupViews(Bundle savedInstanceState);

    @Override
    public void onRegisterResult(TupRegisterResult regRet) {

    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo) {

        Message message=new Message();
        message.what=0;
        message.obj=kickOutInfo.getIpAddress();
        handler.sendMessage(message);
    }

    @Override
    public void onCallComing(TupCall call) {

    }

    @Override
    public void onCallGoing(TupCall tupCall) {

    }

    @Override
    public void onCallRingBack(TupCall tupCall) {

    }

    @Override
    public void onCallConnected(TupCall call) {

    }

    @Override
    public void onCallAddVideo(TupCall call) {

    }

    @Override
    public void onCallDelVideo(TupCall call) {

    }

    @Override
    public void onCallViedoResult(TupCall call) {

    }

    @Override
    public void onCallRefreshView(TupCall call) {

    }

    @Override
    public void onCallEnded(TupCall call) {

    }

    @Override
    public void onCallHoldSuccess(TupCall tupCall) {

    }

    @Override
    public void onCallHoldFailed(TupCall tupCall) {

    }

    @Override
    public void onCallUnHoldSuccess(TupCall tupCall) {

    }

    @Override
    public void onCallUnHoldFailed(TupCall tupCall) {

    }

    @Override
    public void onCallBldTransferSuccess(TupCall tupCall) {

    }

    @Override
    public void onCallBldTransferFailed(TupCall tupCall) {

    }

    @Override
    public void onSetIptServiceSuc(int i) {

    }

    @Override
    public void onSetIptServiceFal(int i) {

    }

    @Override
    public void onDataReady(int var1, int var2) {

    }

    @Override
    public void onBFCPReinited(int var1) {

    }

    @Override
    public void onDataSending(int var1) {

    }

    @Override
    public void onDataReceiving(int var1) {

    }

    @Override
    public void onDataStopped(int var1) {

    }

    @Override
    public void onDataStartErr(int var1, int var2) {

    }

    @Override
    public void onLineStateNotify(OnLineState var1) {

    }

    @Override
    public void onDataFramesizeChange(TupCall var1) {

    }


    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:                    //更新UI操作

                    if(shown) {
                        new MaterialDialog.Builder(SupportBaseActivity.this)
                                .content("您的视频账号在其他设备(" + msg.obj.toString() + ")登录")
                                .positiveText(R.string.ok).onPositive((dialog, which) -> {

                        }).build().show();
                    }

                    break;


            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        shown=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        shown = true;
    }

    protected KickOutInfo info;
    protected boolean kickouted=false;

    protected boolean shown=true;
}
