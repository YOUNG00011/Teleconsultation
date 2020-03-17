package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Evaluation;
import com.wxsoft.telereciver.event.CommitEvaluateSuccessEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EvaluateFragment extends BaseFragment {

    public static void launch(Activity from, Evaluation evaluation) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_EVALUATION, evaluation);
        FragmentContainerActivity.launch(from, EvaluateFragment.class, args);
    }

    public static void launch(Activity from, String clinicId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_CLINIC_ID, clinicId);
        FragmentContainerActivity.launch(from, EvaluateFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_EVALUATION = "FRAGMENTARGS_KEY_EVALUATION";
    private static final String FRAGMENTARGS_KEY_CLINIC_ID = "FRAGMENTARGS_KEY_CLINIC_ID";
    private static final int EDITTEXT_MAX_LENGTH = 1000;

    @BindView(R.id.tv_treat_title)
    TextView mTreatTitleView;

    @BindView(R.id.tv_treat)
    TextView mTreatView;

    @BindView(R.id.rl_treat)
    RelativeLayout mEditTreatLayout;

    @BindView(R.id.et_treat)
    EditText mEditTreatView;

    @BindView(R.id.tv_treat_count)
    TextView mTreatCountView;

    @BindView(R.id.tv_condition_title)
    TextView mConditionTitleView;

    @BindView(R.id.tv_condition)
    TextView mConditionView;

    @BindView(R.id.rl_condition)
    RelativeLayout mEditConditionLayout;

    @BindView(R.id.et_condition)
    EditText mEditConditionView;

    @BindView(R.id.tv_condition_count)
    TextView mConditionCountView;

    @BindView(R.id.tv_suggest_title)
    TextView mSuggestTitleView;

    @BindView(R.id.tv_suggest)
    TextView mSuggestView;

    @BindView(R.id.rl_suggest)
    RelativeLayout mEditSuggestLayout;

    @BindView(R.id.et_suggest)
    EditText mEditSuggestView;

    @BindView(R.id.tv_suggest_count)
    TextView mSuggestCountView;

    @BindView(R.id.tv_direction_title)
    TextView mDirectionTitleView;

    @BindView(R.id.tv_direction)
    TextView mDirectionView;

    @BindView(R.id.rl_direction)
    RelativeLayout mEditDirectionLayout;

    @BindView(R.id.et_direction)
    EditText mEditDirectionView;

    @BindView(R.id.tv_direction_count)
    TextView mDirectionCountView;

    private String mClinicId;
    private Evaluation mEvaluation;

    @OnTextChanged(R.id.et_treat)
    void treatTextChanged(CharSequence s, int start, int before, int count) {
        mTreatCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_condition)
    void conditionTextChanged(CharSequence s, int start, int before, int count) {
        mConditionCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_suggest)
    void suggestTextChanged(CharSequence s, int start, int before, int count) {
        mSuggestCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @OnTextChanged(R.id.et_direction)
    void directionTextChanged(CharSequence s, int start, int before, int count) {
        mDirectionCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_evaluate;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mClinicId = getArguments().getString(FRAGMENTARGS_KEY_CLINIC_ID);
        mEvaluation = (Evaluation) getArguments().getSerializable(FRAGMENTARGS_KEY_EVALUATION);
        setupToolbar();
        setupView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one, menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle(R.string.ok);
        menuItem.setVisible(mEvaluation == null);
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
        activity.getSupportActionBar().setTitle(R.string.evaluate_edit_title);
        setHasOptionsMenu(true);
    }

    private void setupView() {
        if (mEvaluation == null) {
            mTreatView.setVisibility(View.GONE);
            mConditionView.setVisibility(View.GONE);
            mSuggestView.setVisibility(View.GONE);
            mDirectionView.setVisibility(View.GONE);
            mEditTreatLayout.setVisibility(View.VISIBLE);
            mEditConditionLayout.setVisibility(View.VISIBLE);
            mEditSuggestLayout.setVisibility(View.VISIBLE);
            mEditDirectionLayout.setVisibility(View.VISIBLE);

            mEditTreatView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EDITTEXT_MAX_LENGTH)});
            mConditionCountView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EDITTEXT_MAX_LENGTH)});
            mSuggestView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EDITTEXT_MAX_LENGTH)});
            mDirectionView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EDITTEXT_MAX_LENGTH)});
        } else {
            mEditTreatLayout.setVisibility(View.GONE);
            mEditConditionLayout.setVisibility(View.GONE);
            mEditSuggestLayout.setVisibility(View.GONE);
            mEditDirectionLayout.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(mEvaluation.getTreatResult())) {
                mTreatView.setVisibility(View.VISIBLE);
                mTreatTitleView.setVisibility(View.VISIBLE);
                mTreatView.setText(mEvaluation.getTreatResult());
            } else {
                mTreatView.setVisibility(View.GONE);
                mTreatTitleView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mEvaluation.getCondition())) {
                mConditionView.setVisibility(View.VISIBLE);
                mConditionTitleView.setVisibility(View.VISIBLE);
                mConditionView.setText(mEvaluation.getCondition());
            } else {
                mConditionView.setVisibility(View.GONE);
                mConditionTitleView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mEvaluation.getSuggest())) {
                mSuggestView.setVisibility(View.VISIBLE);
                mSuggestTitleView.setVisibility(View.VISIBLE);
                mSuggestView.setText(mEvaluation.getSuggest());
            } else {
                mSuggestView.setVisibility(View.GONE);
                mSuggestTitleView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mEvaluation.getDirection())) {
                mDirectionView.setVisibility(View.VISIBLE);
                mDirectionTitleView.setVisibility(View.VISIBLE);
                mDirectionView.setText(mEvaluation.getDirection());
            } else {
                mDirectionView.setVisibility(View.GONE);
                mDirectionTitleView.setVisibility(View.GONE);
            }
        }
    }

    private void commit() {
        String treat = mEditTreatView.getText().toString();
        String condition = mEditConditionView.getText().toString();
        String suggest = mEditSuggestView.getText().toString();
        String direction = mEditDirectionView.getText().toString();
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getClinicManagerApi().saveConsultationEvaluationDTO(AppContext.getUser().getDoctId(), AppContext.getUser().getName(), mClinicId, treat, condition, suggest, direction)
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
                            EventBus.getDefault().post(new CommitEvaluateSuccessEvent());
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

}
