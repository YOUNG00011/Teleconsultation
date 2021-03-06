package com.wxsoft.telereciver.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResetPwdFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, ResetPwdFragment.class, null);
    }

    public static void launch(Activity from, String userId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_USER_ID, userId);
        FragmentContainerActivity.launch(from, ResetPwdFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_USER_ID = "FRAGMENTARGS_KEY_USER_ID";

    @BindView(R.id.cet_password)
    ClearableEditText mPasswordView;

    @BindView(R.id.cet_password_again)
    ClearableEditText mPasswordAgainView;

    private String mUserId;

    @OnClick(R.id.btn_ok)
    void okClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_pwd;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        if (getArguments() != null) {
            mUserId = getArguments().getString(FRAGMENTARGS_KEY_USER_ID);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.reset_password_title);
    }

    private void commit() {
        String password = mPasswordView.getText().toString().trim();
        String passwordAgsin = mPasswordAgainView.getText().toString().trim();

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordAgsin)) {
            ViewUtil.showMessage("???????????????");
            return;
        }

        if (!password.equals(passwordAgsin)) {
            ViewUtil.showMessage("???????????????????????????");
            return;
        }

        if (mUserId == null) {
            mUserId = AppContext.getUser().getId();
        }

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getUserApi().updatePassword(mUserId, password)
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
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
