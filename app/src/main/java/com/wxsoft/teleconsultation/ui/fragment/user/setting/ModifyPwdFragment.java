package com.wxsoft.teleconsultation.ui.fragment.user.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.LoginActivity;
import com.wxsoft.teleconsultation.ui.base.BaseAppManager;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ModifyPwdFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, ModifyPwdFragment.class, null);
    }

    @BindView(R.id.cet_old_password)
    ClearableEditText mOldPasswordView;

    @BindView(R.id.cet_new_password)
    ClearableEditText mNewPasswordView;

    @BindView(R.id.cet_new_password_again)
    ClearableEditText mNewPasswordAgainView;

    @OnClick(R.id.btn_ok)
    void okClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_pwd;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.setting_modify_password);
    }

    private void commit() {
        String oldPassword = mOldPasswordView.getText().toString().trim();
        String newPassword = mNewPasswordView.getText().toString().trim();
        String newPasswordAgsin = mNewPasswordAgainView.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            ViewUtil.showMessage("请输入旧密码");
            return;
        }

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordAgsin)) {
            ViewUtil.showMessage("请输入新密码");
            return;
        }

        if (!newPassword.equals(newPasswordAgsin)) {
            ViewUtil.showMessage("两次密码输入不一致");
            return;
        }

        ViewUtil.createProgressDialog(_mActivity, "提交中");
        ApiFactory.getUserApi().updatePasswordByPassword(AppContext.getUser().getId(), oldPassword, newPassword)
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
                            AppContext.logout();
                            JMessageClient.logout();
                            BaseAppManager.getInstance().clear();
                            LoginActivity.launch(_mActivity);
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

}
