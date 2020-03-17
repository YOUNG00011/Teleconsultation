package com.wxsoft.telereciver.ui.fragment.user.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.requestbody.UpdateUserInfoBody;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.RegUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ModifyEmailFragment extends BaseFragment {

    public static void launch(Fragment from, String email) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_EMAIL, email);
        FragmentContainerActivity.launchForResult(from, ModifyEmailFragment.class, args, REQUEST_EMAIL);
    }

    private static final String FRAGMENTARGS_KEY_EMAIL = "FRAGMENTARGS_KEY_EMAIL";
    public static final int REQUEST_EMAIL = 106;
    public static final String KEY_EMAIL = "KEY_EMAIL";

    @BindView(R.id.cet_email)
    ClearableEditText mEmailView;

    private String mEmail;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_email;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mEmail = getArguments().getString(FRAGMENTARGS_KEY_EMAIL);
        if (!TextUtils.isEmpty(mEmail)) {
            mEmailView.setText(mEmail);
            mEmailView.setSelection(mEmail.length());
        }
        setupToolbar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.information_email);
        setHasOptionsMenu(true);
    }

    private void commit() {
        String email = mEmailView.getText().toString();
        if (!TextUtils.isEmpty(email) && !RegUtil.isEmail(email)) {
            ViewUtil.showMessage("邮箱格式不正确");
            return;
        }

        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        UpdateUserInfoBody body = UpdateUserInfoBody.updateUserEmailBody(AppContext.getUser().getId(), email);
        ApiFactory.getUserApi().updateArchives(body)
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
                            Intent intent = new Intent();
                            intent.putExtra(KEY_EMAIL, email);
                            _mActivity.setResult(RESULT_OK, intent);
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
