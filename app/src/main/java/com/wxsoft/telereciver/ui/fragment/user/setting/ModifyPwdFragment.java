package com.wxsoft.telereciver.ui.fragment.user.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.LoginActivity;
import com.wxsoft.telereciver.ui.base.BaseAppManager;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.ViewUtil;

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
            ViewUtil.showMessage("??????????????????");
            return;
        }

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(newPasswordAgsin)) {
            ViewUtil.showMessage("??????????????????");
            return;
        }

        if (!newPassword.equals(newPasswordAgsin)) {
            ViewUtil.showMessage("???????????????????????????");
            return;
        }

        ViewUtil.createProgressDialog(_mActivity, "?????????");
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
