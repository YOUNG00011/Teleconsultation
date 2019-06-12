package com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.KeyboardUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;

public class SMSFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, SMSFragment.class, null);
    }

    @BindView(R.id.et_sms)
    EditText mSmsView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sms;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();

        // 在onCreate()立即调用无法显示，要有延迟
        Observable.timer(200, TimeUnit.MILLISECONDS).subscribe(aLong -> {
            // 动态显示软键盘
            KeyboardUtil.showSoftInput(_mActivity, mSmsView);
        });
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("发送短信");
    }
}
