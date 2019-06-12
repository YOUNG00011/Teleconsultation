package com.wxsoft.teleconsultation.ui.fragment.homepage.register;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.register.RegisterItem;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.event.UpdateClinicStateEvent;
import com.wxsoft.teleconsultation.event.UpdateDiseaseCounselingStatusEvent;
import com.wxsoft.teleconsultation.event.UpdateRegisterEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

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

public class RegisterListFragment extends BaseFragment {

    public static RegisterListFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        RegisterListFragment fragment = new RegisterListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";
    private static final int APPLY_FILTER_STATUS_ALL           = 0;
    private static final int APPLY_FILTER_STATUS_TODO          = 1;
    private static final int APPLY_FILTER_STATUS_CONSULTATION  = 2;
//    private static final String[] MY_APPLY_FILTER = {"全部", "未处理", "会诊中", "已完成"};
    private static final int CLINIC_FILTER_STATUS_ALL           = 0;
    private static final int CLINIC_FILTER_STATUS_TODO          = 2;
    private static final int CLINIC_FILTER_STATUS_FINISHED      = 3;
//    private static final String[] MY_CLINIC_FILTER = {"全部", "会诊中", "已完成"};

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<RegisterItem> mAdapter;
    private int mCurrentPosition = 0;
    private int mApplyFilterStatus = APPLY_FILTER_STATUS_ALL;
    private int mClinicFilterStatus = CLINIC_FILTER_STATUS_ALL;
    private int mPage = 1;

    @OnClick(R.id.fabtn_filter)
    void filterClick() {
        String[] myRegisterFilters = {
                getString(R.string.register_list_filter_text_1),
                getString(R.string.register_list_filter_text_2),
                getString(R.string.register_list_filter_text_3)
        };

        String[] tomeFilters = {
                getString(R.string.register_list_filter_text_1),
                getString(R.string.register_list_filter_text_4),
                getString(R.string.register_list_filter_text_5),
                getString(R.string.register_list_filter_text_6),
                getString(R.string.register_list_filter_text_7)
        };

       int index = 0;
       if (mCurrentPosition==0) {
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
                .items(mCurrentPosition==0 ? myRegisterFilters : tomeFilters)
                .itemsCallbackSingleChoice(index, (dialog, view, which, text) -> {
                    if (mCurrentPosition==0) {
                        mApplyFilterStatus=which;

                    } else {
                        mClinicFilterStatus=which;
                    }
                    showRefreshing(true);
                    mPage = 1;
                    mAdapter.clear();
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

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdateRegisterEvent) {
            mPage = 1;
            mAdapter.clear();
            loadData();
        }
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
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<RegisterItem>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyApplyViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            mAdapter.clear();
            loadData();
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            loadData();
        });

        mAdapter.setOnItemClickListener(position -> {
            String clinicId = mAdapter.getItem(position).id;
            RegisterDetailFragment.launch(_mActivity, clinicId);

        });

        loadData();
    }

    private void loadData() {
        String queryType = mCurrentPosition==0?"0":"1";
        int status = mCurrentPosition==0 ? mApplyFilterStatus : 0;
        int booktype = mCurrentPosition==0 ? 0 : mClinicFilterStatus;
        String doctId = AppContext.getUser().getDoctId();
        QueryRequestBody body = QueryRequestBody.getRegisterRequestBody(doctId, queryType, String.valueOf(status),String.valueOf(booktype), AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getRegisterApi().getRegisterRecords(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData< RegisterItem>>>() {
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
                    public void onNext(BaseResp<QueryResponseData<RegisterItem>> resp) {
                        showRefreshing(false);
//                        String s=resp.getData().toString();
//                        Log.i(s,s);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<RegisterItem>> resp) {
        showRefreshing(false);
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
        }else{


            List<RegisterItem> clinics = resp.getData().getResultData();
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


            List<RegisterItem> targetClinics = new ArrayList<>();
            if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
                targetClinics.addAll(mAdapter.getAllData());
            }

            targetClinics.addAll(clinics);
            mRecyclerView.showRecycler();
            mAdapter.clear();
            mAdapter.addAll(targetClinics);
        }
    }


    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    @Subscribe
    public void onEvent(UpdateClinicStateEvent updateClinicStateEvent) {
        mPage = 1;
        mAdapter.clear();
        loadData();
    }

    private class MyApplyViewHolder extends BaseViewHolder<RegisterItem> {

        private TextView mGroupTitleView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mHealthView;
        private LinearLayout mTagsView;
        private TextView mDescribeView;
        private TextView mTimeView;
        private TextView mStatusView;
        private Context mContext;

        public MyApplyViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_register);

            mContext = context;

            mGroupTitleView = $(R.id.tv_group_title);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mHealthView = $(R.id.tv_health);
            mTagsView = $(R.id.ll_tag);
            mDescribeView = $(R.id.tv_describe_text);
            mTimeView = $(R.id.tv_time);
            mStatusView = $(R.id.tv_status_name);
        }

        @Override
        public void setData(RegisterItem data) {
            super.setData(data);

            try {


                Patient patient = data.patient;
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

                mDescribeView.setText(data.registerDate.substring(0,10)+" "+data.visitTime);


                mStatusView.setText(data.statusName);

                mTimeView.setText(DateUtil.getCustomTimeStamp(getActivity(),data.createdDate.substring(0,19).replace("T"," "),null));
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }
}
