package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Clinic;
import com.wxsoft.telereciver.entity.ConsultationFeedbackTranslate;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Evaluation;
import com.wxsoft.telereciver.entity.Feedback;
import com.wxsoft.telereciver.event.CommitEvaluateSuccessEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ClinicAdviceFragment extends BaseFragment {

    public static void launch(Activity from, Clinic clinic, boolean isMyApply, String consultationFeedbackTranslatesJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_CLINIC, clinic);
        args.add(FRAGMENTARGS_KEY_IS_MYAPPLY, isMyApply);
        args.add(FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON, consultationFeedbackTranslatesJson);
        FragmentContainerActivity.launch(from, ClinicAdviceFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_CLINIC = "FRAGMENTARGS_KEY_CLINIC";
    private static final String FRAGMENTARGS_KEY_IS_MYAPPLY = "FRAGMENTARGS_KEY_IS_MYAPPLY";
    private static final String FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON = "FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private Clinic mClinic;
    private boolean isMyApply;
    private Evaluation mEvaluation;
    private RecyclerArrayAdapter<Feedback> mAdapter;
    private List<ConsultationFeedbackTranslate> consultationFeedbackTranslates;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mClinic = (Clinic) getArguments().getSerializable(FRAGMENTARGS_KEY_CLINIC);
        isMyApply = getArguments().getBoolean(FRAGMENTARGS_KEY_IS_MYAPPLY);
        String consultationFeedbackTranslatesJson = getArguments().getString(FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON);
        if (!TextUtils.isEmpty(consultationFeedbackTranslatesJson)) {
            consultationFeedbackTranslates = new Gson().fromJson(consultationFeedbackTranslatesJson, new TypeToken<List<ConsultationFeedbackTranslate>>() {
            }.getType());
        }
        EventBus.getDefault().register(this);
        setupToolbar();
        setupRecyclerView();

        loadAdviceData();
        loadEvaluateData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one, menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        String status = mClinic.getStatus();
        if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_HAS_CONSULTATION) ||
                status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_FINISHED)) {
            menuItem.setVisible(true);
            if (isMyApply) {
                if (mEvaluation == null) {
                    menuItem.setTitle(R.string.view_comments_evaluate);
                } else {
                    menuItem.setTitle(R.string.view_comments_view_evaluate);
                }
            } else {
                if (mEvaluation == null) {
                    menuItem.setTitle("");
                } else {
                    menuItem.setTitle(R.string.view_comments_view_evaluate);
                }
            }
        } else {
            menuItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                if (isMyApply) {
                    if (mEvaluation == null) {
                        EvaluateFragment.launch(_mActivity, mClinic.getId());
                    } else {
                        EvaluateFragment.launch(_mActivity, mEvaluation);
                    }
                } else {
                    if (mEvaluation != null) {
                        EvaluateFragment.launch(_mActivity, mEvaluation);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.view_comments_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Feedback>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new AdviceViewHolder(parent, Glide.with(ClinicAdviceFragment.this));
            }
        });

        loadAdviceData();
    }

    private void loadAdviceData() {
        ApiFactory.getClinicManagerApi().getConsultationFeedback(mClinic.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Feedback>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
                        mRecyclerView.showError();
                        mRecyclerView.getErrorView().setOnClickListener(v -> {
                            mRecyclerView.showProgress();
                            loadAdviceData();
                        });
                    }

                    @Override
                    public void onNext(BaseResp<List<Feedback>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<Feedback>> resp) {
        if (!resp.isSuccess()) {
            ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(resp.getMessage());
            mRecyclerView.showError();
            mRecyclerView.getErrorView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadAdviceData();
            });
            return;
        }

        List<Feedback> feedbacks = resp.getData();
        if (feedbacks == null || feedbacks.isEmpty()) {
            mRecyclerView.showEmpty();
            mRecyclerView.getEmptyView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadAdviceData();
            });
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(feedbacks);
    }

    private void loadEvaluateData() {
        ApiFactory.getClinicManagerApi().getConsultationEvaluation(mClinic.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Evaluation>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
                        mRecyclerView.showError();
                        mRecyclerView.getErrorView().setOnClickListener(v -> {
                            mRecyclerView.showProgress();
                            loadAdviceData();
                        });
                    }

                    @Override
                    public void onNext(BaseResp<Evaluation> resp) {
                        if (!resp.isSuccess()) {
                            return;
                        }

                        mEvaluation = resp.getData();
                        _mActivity.invalidateOptionsMenu();
                    }
                });
    }

    @Subscribe
    public void onEvent(CommitEvaluateSuccessEvent commitEvaluateSuccessEvent) {
        loadEvaluateData();
    }

    private class AdviceViewHolder extends BaseViewHolder<Feedback> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mPositionTitleView;
        private TextView mDepartmentView;
        private TextView mHospitalView;
        private TextView mGoodAtView;
        private TextView mStatusView;
        private TextView mContentView;
        private TextView mTimeView;


        private RequestManager mGlide;
        private RequestOptions mOptions;

        public AdviceViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.item_clinic_advice);
            mGlide = glide;

            mAvatarView = $(R.id.iv_doctor_avatar);
            mNameView = $(R.id.tv_doctor_name);
            mPositionTitleView = $(R.id.tv_position_title);
            mDepartmentView = $(R.id.tv_department);
            mHospitalView = $(R.id.tv_hospital);
            mGoodAtView = $(R.id.tv_goodat);
            mStatusView = $(R.id.tv_doctor_status);
            mContentView = $(R.id.tv_content);
            mTimeView = $(R.id.tv_time);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(Feedback data) {
            super.setData(data);
            Doctor doctor = data.getDoctorInfoDTO();
            mNameView.setText(doctor.getName());
            mPositionTitleView.setText(doctor.getPositionTitle().getPositionTitleName());
            mDepartmentView.setText(doctor.getDepartmentName());
            mHospitalView.setText(doctor.getHospitalName());
            mGoodAtView.setVisibility(View.GONE);

            String content = data.getFeedbackContent();
            if (consultationFeedbackTranslates != null && !consultationFeedbackTranslates.isEmpty()) {
                for (ConsultationFeedbackTranslate consultationFeedbackTranslate : consultationFeedbackTranslates) {
                    if (consultationFeedbackTranslate.getConsultationFeedbackId().equals(data.getId()) &&
                            !TextUtils.isEmpty(consultationFeedbackTranslate.getTranslateContent())) {
                        content = content + "\n" + consultationFeedbackTranslate.getTranslateContent();
                    }
                }
            }
            mContentView.setText(content);

            String time = data.getFeedbackDate().replace("T", " ");
            if (time.contains(".")) {
                int lastPoi = time.lastIndexOf('.');
                time = time.substring(0, lastPoi);
            }
            mTimeView.setText(time);

            String goodAt = "";
            if (!TextUtils.isEmpty(doctor.getGoodAt())) {
                goodAt = "擅长:   " + doctor.getGoodAt();
            }

            mGoodAtView.setText(goodAt);

            String statusName = doctor.getStatusName();
            if (TextUtils.isEmpty(statusName)) {
                mStatusView.setVisibility(View.GONE);
            } else {
                mStatusView.setVisibility(View.VISIBLE);
                mStatusView.setText(statusName);
            }

            mGlide.setDefaultRequestOptions(mOptions.error(doctor.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(doctor.getUserImgUrl())
                    .into(mAvatarView);
        }
    }
}
