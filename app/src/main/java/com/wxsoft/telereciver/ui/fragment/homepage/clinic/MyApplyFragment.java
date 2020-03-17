package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Clinic;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.event.UpdateClinicStateEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyApplyFragment extends BaseFragment {

    public static MyApplyFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        MyApplyFragment fragment = new MyApplyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";
    private static final int APPLY_FILTER_STATUS_ALL           = 0;
    private static final int APPLY_FILTER_STATUS_TODO          = 1;
    private static final int APPLY_FILTER_STATUS_CONSULTATION  = 2;
    private static final int APPLY_FILTER_STATUS_FINISHED      = 3;
//    private static final String[] MY_APPLY_FILTER = {"全部", "未处理", "会诊中", "已完成"};
    private static final int CLINIC_FILTER_STATUS_ALL           = 0;
    private static final int CLINIC_FILTER_STATUS_TODO          = 2;
    private static final int CLINIC_FILTER_STATUS_FINISHED      = 3;
//    private static final String[] MY_CLINIC_FILTER = {"全部", "会诊中", "已完成"};

    private static final String QUERY_TYPE_MY_APPLY = "apply";
    private static final String QUERY_TYPE_MY_CLINIC= "consultation";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Clinic> mAdapter;
    private int mCurrentPosition = 0;
    private int mApplyFilterStatus = APPLY_FILTER_STATUS_ALL;
    private int mClinicFilterStatus = CLINIC_FILTER_STATUS_ALL;
    private int mPage = 1;

    @OnClick(R.id.fabtn_filter)
    void filterClick() {
        String[] myApplyFilters = {
                getString(R.string.consultation_list_filter_text_1),
                getString(R.string.consultation_list_filter_text_2),
                getString(R.string.consultation_list_filter_text_3),
                getString(R.string.consultation_list_filter_text_4)
        };

        String[] myClinicFilters = {
                getString(R.string.consultation_list_filter_text_1),
                getString(R.string.consultation_list_filter_text_3),
                getString(R.string.consultation_list_filter_text_4)
        };

       int index = 0;
       if (isMyApply()) {
           index = mApplyFilterStatus;
       } else {
           if (mClinicFilterStatus == CLINIC_FILTER_STATUS_ALL) {
               index = 0;
           } else if (mClinicFilterStatus == CLINIC_FILTER_STATUS_TODO) {
               index = 1;
           } else if (mClinicFilterStatus == CLINIC_FILTER_STATUS_FINISHED) {
               index = 2;
           }
       }
       new MaterialDialog.Builder(_mActivity)
                .items(isMyApply() ? myApplyFilters : myClinicFilters)
                .itemsCallbackSingleChoice(index, (dialog, view, which, text) -> {
                    if (isMyApply()) {
                        switch (which) {
                            case 0:
                                mApplyFilterStatus = APPLY_FILTER_STATUS_ALL;
                                break;
                            case 1:
                                mApplyFilterStatus = APPLY_FILTER_STATUS_TODO;
                                break;
                            case 2:
                                mApplyFilterStatus = APPLY_FILTER_STATUS_CONSULTATION;
                                break;
                            case 3:
                                mApplyFilterStatus = APPLY_FILTER_STATUS_FINISHED;
                                break;
                        }
                    } else {
                        switch (which) {
                            case 0:
                                mClinicFilterStatus = CLINIC_FILTER_STATUS_ALL;
                                break;
                            case 1:
                                mClinicFilterStatus = CLINIC_FILTER_STATUS_TODO;
                                break;
                            case 2:
                                mClinicFilterStatus = CLINIC_FILTER_STATUS_FINISHED;
                                break;
                        }
                    }
                    showRefreshing(true);
                    mPage = 1;
                    loadData();
                    return true;
                })
                .show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_apply;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mCurrentPosition = getArguments().getInt(EXTRAS_KEY_POSITION);
        setupRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Clinic>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyApplyViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            loadData();
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            loadData();
        });

        mAdapter.setOnItemClickListener(position -> {
            String clinicId = mAdapter.getItem(position).getId();

            ClinicDetailActivity.launch(_mActivity, clinicId);
        });

        loadData();
    }

    private void loadData() {
        String queryType = isMyApply() ? QUERY_TYPE_MY_APPLY : QUERY_TYPE_MY_CLINIC;
        int status = isMyApply() ? mApplyFilterStatus : mClinicFilterStatus;
        String doctId = AppContext.getUser().getDoctId();
        QueryRequestBody body = QueryRequestBody.getMyApplyRequestBody(doctId, queryType, String.valueOf(status), AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getClinicManagerApi().queryConsultation(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<Clinic>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdapter.getCount() == 0) {
                            ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
                            mRecyclerView.showError();
                            mRecyclerView.getErrorView().setOnClickListener(v -> {
                                mRecyclerView.showProgress();
                                loadData();
                            });
                        } else {
                            showRefreshing(false);
                            ViewUtil.showMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<Clinic>> resp) {
                        showRefreshing(false);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<Clinic>> resp) {
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }

        List<Clinic> clinics = resp.getData().getResultData();
        if (clinics == null || clinics.isEmpty()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getEmptyView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadData();
                });
                mRecyclerView.showEmpty();
            } else {
                if (mPage == 1) {
                    mAdapter.clear();
                    mRecyclerView.showEmpty();
                } else {
                    mAdapter.stopMore();
                }
            }
            return;
        }


        List<Clinic> targetClinics = new ArrayList<>();
        if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
            targetClinics.addAll(mAdapter.getAllData());
        }

        targetClinics.addAll(clinics);
        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(targetClinics);
    }


    private boolean isMyApply() {
        return mCurrentPosition == 0;
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    @Subscribe
    public void onEvent(UpdateClinicStateEvent updateClinicStateEvent) {
        loadData();
    }

    private class MyApplyViewHolder extends BaseViewHolder<Clinic> {

        private TextView mGroupTitleView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mHealthView;
        private LinearLayout mTagsView;
        private TextView mDescribeView;
        private TextView mInviteDoctorView;
        private LinearLayout mMyStatusRootView;
        private TextView mMyStatusView;
        private TextView mTimeView;
        private TextView mStatusView;
        private Context mContext;

        public MyApplyViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_my_apply);

            mContext = context;

            mGroupTitleView = $(R.id.tv_group_title);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mHealthView = $(R.id.tv_health);
            mTagsView = $(R.id.ll_tag);
            mDescribeView = $(R.id.tv_describe_text);
            mInviteDoctorView = $(R.id.tv_invite_doctor_text);
            mMyStatusRootView = $(R.id.ll_my_status);
            mMyStatusView = $(R.id.tv_my_status_text);
            mTimeView = $(R.id.tv_time);
            mStatusView = $(R.id.tv_status_name);
        }

        @Override
        public void setData(Clinic data) {
            super.setData(data);

            try {
                if (TextUtils.isEmpty(data.getGroupTitle())) {
                    mGroupTitleView.setVisibility(View.GONE);
                } else {
                    mGroupTitleView.setVisibility(View.VISIBLE);
                    mGroupTitleView.setText(data.getGroupTitle());
                }

                Patient patient = data.getPatientInfoDTO();
                mNameView.setText(patient.getName());
                mAvatarView.setImageResource(patient.getAvatarDrawableRes());
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mHealthView.setText(patient.getMedicalInsuranceName());

                mTagsView.removeAllViews();
                List<PatientTag> patientTags = patient.getPatientTags();
                if (patientTags == null || patientTags.isEmpty()) {
                    mTagsView.setVisibility(View.INVISIBLE);
                } else {
                    mTagsView.setVisibility(View.VISIBLE);
                    for (PatientTag patientTag : patientTags) {
                        mTagsView.addView(AppUtil.getTagTextView(mContext, patientTag.getTagName()));
                    }
                }

                mDescribeView.setText(data.getDescribe());

                String myStatus = "";
                List<Doctor> doctors = data.getConsultationDoctors();
                if (doctors != null && !doctors.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Doctor doctor : doctors) {
                        sb.append(",").append(doctor.getName());
                        if (doctor.getId().equals(AppContext.getUser().getDoctId())) {
                            myStatus = doctor.getStatusName();
                        }
                    }

                    mInviteDoctorView.setText(sb.substring(1).toString());
                } else {
                    mInviteDoctorView.setText("无");
                }

                if (TextUtils.isEmpty(myStatus)) {
                    mMyStatusRootView.setVisibility(View.GONE);
                } else {
                    mMyStatusRootView.setVisibility(View.VISIBLE);
                    mMyStatusView.setText(myStatus);
                }



                String statusName = data.getStatusName();
                mStatusView.setText(statusName);

                String createDatetime = data.getCreatedDate().replace("T", " ");
                if (createDatetime.contains(".")) {
                    int lastPoi = createDatetime.lastIndexOf('.');
                    createDatetime = createDatetime.substring(0, lastPoi);
                }
                mTimeView.setText(createDatetime);
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }
}
