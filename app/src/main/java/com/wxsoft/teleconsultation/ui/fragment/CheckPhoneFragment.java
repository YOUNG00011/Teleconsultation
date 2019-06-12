package com.wxsoft.teleconsultation.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.responsedata.CheckValidCodeResponseData;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.LoginActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.RegUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CheckPhoneFragment extends BaseFragment {

    public static void launchForForgetPwd(Activity from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_IS_REGISTER, false);
        FragmentContainerActivity.launch(from, CheckPhoneFragment.class, args);
    }

    public static void launchForRegister(Activity from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_IS_REGISTER, true);
        FragmentContainerActivity.launch(from, CheckPhoneFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_IS_REGISTER = "FRAGMENTARGS_KEY_IS_REGISTER";

    @BindView(R.id.cet_phone)
    ClearableEditText mPhoneView;

    @BindView(R.id.cet_valid_code)
    ClearableEditText mValidCodeView;

    @BindView(R.id.tv_get_valid_code)
    TextView mGetValidCodeView;

    private boolean isRegister;

    @OnClick(R.id.tv_get_valid_code)
    void getValidCodeClick() {
        getValidCode();
    }

    @OnClick(R.id.btn_ok)
    void okClick() {
        commit();
    }

    @OnTextChanged(R.id.cet_phone)
    void phoneChanged(CharSequence s, int start, int before, int count) {
        String phone = s.toString();
        if (phone.length() > 0) {
            mGetValidCodeView.setTextColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        } else {
            mGetValidCodeView.setTextColor(Color.GRAY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_phone;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        isRegister = getArguments().getBoolean(FRAGMENTARGS_KEY_IS_REGISTER);
        setupToolbar();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(isRegister ? R.string.sign_up : R.string.verify_phone_recover_password);
    }

    private void getValidCode() {
        String phone = mPhoneView.getText().toString().trim();
        if (!RegUtil.isMobileExact(phone)) {
            ViewUtil.showMessage("请输入正确的手机号");
            return;
        }

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getUserApi().sendVerificationCode(phone, isRegister ? 1 : 0)
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
                            startCountdown();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void startCountdown() {
        int count = 60;
        Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take(count+1) //设置循环+1次
                .map(aLong -> {
                    return count - aLong; //
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    mGetValidCodeView.setEnabled(false);//在发送数据的时候设置为不能点击
                    mGetValidCodeView.setTextColor(Color.GRAY);
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        mGetValidCodeView.setEnabled(true);
                        mGetValidCodeView.setText(R.string.verify_phone_valid_code_obtain);//数据发送完后设置为原来的文字
                        mGetValidCodeView.setTextColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接受到一条就是会操作一次UI
                        mGetValidCodeView.setText(aLong + "s");
                    }
                });
    }

    private void commit() {
        String phone = mPhoneView.getText().toString().trim();
        String validCode = mValidCodeView.getText().toString().trim();

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getUserApi().checkVerificationCode(phone, validCode)
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
                            if (isRegister) {
                                PrefectInfoFragment.launch(_mActivity, phone);
                            } else {
                                String userId = resp.getData();
                                ResetPwdFragment.launch(_mActivity, userId);
                            }
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
