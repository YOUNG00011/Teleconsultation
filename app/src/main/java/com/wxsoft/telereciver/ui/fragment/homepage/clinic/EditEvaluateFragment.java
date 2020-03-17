package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnTextChanged;

public class EditEvaluateFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, EditEvaluateFragment.class, null);
    }

    private static final int EDITTEXT_MAX_LENGTH = 1000;

    @BindView(R.id.et_situation)
    EditText mSituationView;

    @BindView(R.id.tv_situation_count)
    TextView mSituationCountView;

    @BindView(R.id.et_condition)
    EditText mConditionView;

    @BindView(R.id.tv_condition_count)
    TextView mConditionCountView;

    @BindView(R.id.et_advice)
    EditText mAdviceView;

    @BindView(R.id.tv_advice_count)
    TextView mAdviceCountView;

    @BindView(R.id.et_gone)
    EditText mGoneView;

    @BindView(R.id.tv_gone_count)
    TextView mGoneCountView;

    @OnTextChanged(R.id.et_situation)
    void situationTextChanged(CharSequence s, int start, int before, int count) {
        mSituationCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_condition)
    void conditionTextChanged(CharSequence s, int start, int before, int count) {
        mConditionCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_advice)
    void adviceTextChanged(CharSequence s, int start, int before, int count) {
        mAdviceCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_gone)
    void goneTextChanged(CharSequence s, int start, int before, int count) {
        mGoneCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_evaluate;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle("发布");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                submit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("发表评价");
        setHasOptionsMenu(true);
    }

    private void submit() {
        String situation = mSituationView.getText().toString();
        String condition = mConditionView.getText().toString();
        String advice = mAdviceView.getText().toString();
        String gone = mGoneView.getText().toString();
        if (TextUtils.isEmpty(situation) ||
                TextUtils.isEmpty(condition) ||
                TextUtils.isEmpty(advice) ||
                TextUtils.isEmpty(gone)) {
            ViewUtil.showMessage("请至少选择一项填写");
            return;
        }

        // TODO: 2018/4/18
    }
}
