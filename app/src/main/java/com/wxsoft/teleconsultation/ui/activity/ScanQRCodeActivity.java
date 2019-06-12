package com.wxsoft.teleconsultation.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;

public class ScanQRCodeActivity extends SupportBaseActivity {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, ScanQRCodeActivity.class));
    }

    @BindView(R.id.zxingview)
    QRCodeView mQRCodeView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        setupToolbar();
        setupQRCodeView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void setupToolbar() {
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("二维码");
    }

    private void setupQRCodeView() {
        mQRCodeView.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                // 显示扫描结果
                ViewUtil.showMessage(result);
                // 振动
                vibrate();
                // 继续识别
                mQRCodeView.startSpot();

            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                ViewUtil.showMessage("打开相机出错);");
            }
        });

        // 开始识别
        mQRCodeView.startSpot();
    }

    /**
     * 振动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

}
