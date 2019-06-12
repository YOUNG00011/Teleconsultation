package com.wxsoft.teleconsultation.ui.fragment.user.sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.CheckPhoneFragment;
import com.wxsoft.teleconsultation.ui.fragment.user.info.UserInfoFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MySignFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, MySignFragment.class, null);
    }

    @BindView(R.id.ll_sign)
    LinearLayout mSignView;

    private RequestManager mGlide;
    private RequestOptions options;

    private User mUser;

    @OnClick(R.id.btn_sign_again)
    void signAgainClick() {
        showInputUserPwdDialog();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_sign;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mGlide = Glide.with(MySignFragment.this);
        options = new RequestOptions();
        options.centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        mUser = AppContext.getUser();
        updateSignView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ModifySignFragment.REQUEST_MODIFY_SIGN) {
                if (data != null) {
                    String signUrl = data.getStringExtra(ModifySignFragment.KEY_SIGN_URL);
                    mUser.setSignatureImageUrl(signUrl);
                    AppContext.setUser(mUser);
                    updateSignView();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.sign_title);
    }

    private void showInputUserPwdDialog() {
        new MaterialDialog.Builder(_mActivity)
                .title(R.string.sign_verify_password)
                .content(R.string.sign_verify_password_hint)
                .cancelable(false)
                .input(null, null, (dialog, input) -> {

                })
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    String pwd = dialog.getInputEditText().getText().toString();
                    if (TextUtils.isEmpty(pwd)) {
                        ViewUtil.showMessage("请输入密码");
                        return;
                    }
                    dialog.dismiss();
                    verifyPassword(pwd);
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void verifyPassword(String password) {
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getUserApi().verifyPassword(AppContext.getUser().getId(), password)
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
                            ModifySignFragment.launch(_mActivity);
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void showInputUserPwdErrorDialog() {
        new MaterialDialog.Builder(_mActivity)
                .content(R.string.sign_input_error_hint)
                .cancelable(false)
                .positiveText(R.string.sign_forget_password)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    showInputUserPwdDialog();
                    CheckPhoneFragment.launchForForgetPwd(_mActivity);
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                    showInputUserPwdDialog();
                })
                .show();
    }

    private void updateSignView() {
        if (mSignView.getChildCount() > 0) {
            mSignView.removeAllViews();
        }

        ImageView imageView = new ImageView(_mActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        String signatureImgUrl = mUser.getSignatureImageUrl();
        if (!TextUtils.isEmpty(signatureImgUrl)) {
            mGlide.setDefaultRequestOptions(options)
                    .load(signatureImgUrl)
                    .into(imageView);
        }

        mSignView.addView(imageView);
    }
}
