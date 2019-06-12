package com.wxsoft.teleconsultation.ui.fragment.user.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.requestbody.UpdateUserInfoBody;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InputFragment extends BaseFragment {

    public static void launchForDynamic(Fragment from, String content) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_FLAG, 1);
        args.add(FRAGMENTARGS_KEY_HINT, "编辑个性签名");
        args.add(FRAGMENTARGS_KEY_INPUT_MAX_LENGTH, 50);
        args.add(FRAGMENTARGS_KEY_CONTENT, content);
        FragmentContainerActivity.launchForResult(from, InputFragment.class, args, REQUEST_DYNAMIC);
    }

    public static void launchForIntroduction(Fragment from, String content) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_FLAG, 2);
        args.add(FRAGMENTARGS_KEY_HINT, "请输入个人介绍");
        args.add(FRAGMENTARGS_KEY_INPUT_MAX_LENGTH, 200);
        args.add(FRAGMENTARGS_KEY_CONTENT, content);
        FragmentContainerActivity.launchForResult(from, InputFragment.class, args, REQUEST_INTRODUCTION);
    }

    public static void launchForGoodAt(Fragment from, String content) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_FLAG, 3);
        args.add(FRAGMENTARGS_KEY_HINT, "请输入擅长领域");
        args.add(FRAGMENTARGS_KEY_INPUT_MAX_LENGTH, 200);
        args.add(FRAGMENTARGS_KEY_CONTENT, content);
        FragmentContainerActivity.launchForResult(from, InputFragment.class, args, REQUEST_GOODAT);
    }

    public static void launchForAchievement(Fragment from, String content) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_FLAG, 4);
        args.add(FRAGMENTARGS_KEY_HINT, "请输入成果荣誉");
        args.add(FRAGMENTARGS_KEY_INPUT_MAX_LENGTH, 200);
        args.add(FRAGMENTARGS_KEY_CONTENT, content);
        FragmentContainerActivity.launchForResult(from, InputFragment.class, args, REQUEST_ACHIEVEMENT);
    }

    private static final String FRAGMENTARGS_KEY_TITLE = "FRAGMENTARGS_KEY_TITLE";
    private static final String FRAGMENTARGS_KEY_HINT = "FRAGMENTARGS_KEY_HINT";
    private static final String FRAGMENTARGS_KEY_INPUT_MAX_LENGTH = "FRAGMENTARGS_KEY_INPUT_MAX_LENGTH";
    private static final String FRAGMENTARGS_KEY_CONTENT = "FRAGMENTARGS_KEY_CONTENT";
    private static final String FRAGMENTARGS_KEY_FLAG = "FRAGMENTARGS_KEY_FLAG";

    private static final int FLAG_DYNAMIC = 1;
    private static final int FLAG_INTRODUCTION = 2;
    private static final int FLAG_GOODAT = 3;
    private static final int FLAG_ACHIEVEMENT = 4;

    public static final int REQUEST_DYNAMIC = 96;
    public static final int REQUEST_INTRODUCTION = 97;
    public static final int REQUEST_GOODAT  = 98;
    public static final int REQUEST_ACHIEVEMENT = 99;
    public static final String KEY_CONTENT = "KEY_CONTENT";

    @BindView(R.id.et_content)
    EditText mContentView;

    @BindView(R.id.tv_count)
    TextView mCountView;

    private int mFlag;
    private String mContent;
    private int mInputMaxLength;
    private String mInputHint;

    @OnTextChanged(R.id.et_content)
    void textChanged(CharSequence s, int start, int before, int count) {
        mCountView.setText(s.length() + "/" + mInputMaxLength);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info_input;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mFlag = getArguments().getInt(FRAGMENTARGS_KEY_FLAG);
        mContent = getArguments().getString(FRAGMENTARGS_KEY_CONTENT);
        mInputMaxLength = getArguments().getInt(FRAGMENTARGS_KEY_INPUT_MAX_LENGTH);
        mInputHint = getArguments().getString(FRAGMENTARGS_KEY_HINT);

        mCountView.setHint(mInputHint);
        int currentCount = 0;
        if (!TextUtils.isEmpty(mContent)) {
            mContentView.setText(mContent);
            mContentView.setSelection(mContent.length());
            currentCount = mContent.length();
        }
        mCountView.setText(currentCount + "/" + mInputMaxLength);
        mContentView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mInputMaxLength)});
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
        String title = "";
        if (mFlag == FLAG_DYNAMIC) {
            title = getString(R.string.dynamic);
        } else if (mFlag == FLAG_INTRODUCTION) {
            title = getString(R.string.introduction);
        } else if (mFlag == FLAG_GOODAT) {
            title = getString(R.string.good_at);
        } else if (mFlag == FLAG_ACHIEVEMENT){
            title = getString(R.string.achievement);
        }
        activity.getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    private void commit() {
        String content = mContentView.getText().toString();

        String userId = AppContext.getUser().getId();
        UpdateUserInfoBody body = null;
        if (mFlag == FLAG_DYNAMIC) {
            body = UpdateUserInfoBody.updateUserDynamicBody(userId, content);
        } else if (mFlag == FLAG_INTRODUCTION) {
            body = UpdateUserInfoBody.updateUserIntroduceBody(userId, content);
        } else if (mFlag == FLAG_GOODAT){
            body = UpdateUserInfoBody.updateUserGoodatBody(userId, content);
        } else if (mFlag == FLAG_ACHIEVEMENT) {
            body = UpdateUserInfoBody.updateUserAchievementBody(userId, content);
        }

        ViewUtil.createProgressDialog(_mActivity, "提交中...");
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
                            intent.putExtra(KEY_CONTENT, content);
                            _mActivity.setResult(RESULT_OK, intent);
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
