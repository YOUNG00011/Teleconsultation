package com.wxsoft.telereciver.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.RegUtil;
import com.wxsoft.telereciver.util.ViewUtil;

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
            ViewUtil.showMessage("???????????????????????????");
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
        Observable.interval(0, 1, TimeUnit.SECONDS)//??????0???????????????????????????????????????
                .take(count+1) //????????????+1???
                .map(aLong -> {
                    return count - aLong; //
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> {
                    mGetValidCodeView.setEnabled(false);//?????????????????????????????????????????????
                    mGetValidCodeView.setTextColor(Color.GRAY);
                })
                .observeOn(AndroidSchedulers.mainThread())//??????UI?????????UI??????
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        mGetValidCodeView.setEnabled(true);
                        mGetValidCodeView.setText(R.string.verify_phone_valid_code_obtain);//??????????????????????????????????????????
                        mGetValidCodeView.setTextColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //????????????????????????????????????UI
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
