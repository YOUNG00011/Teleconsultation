package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.prescription.PrescriptionCon;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.event.TreatMentStateChangeEvent;
import com.wxsoft.teleconsultation.event.UpdatePrescriptionConStatusEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.PrescriptionCallTheRollFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.diseasecounse.DiseaseCounselingDetailFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrescriptionConListFragment extends BaseFragment {
    private RequestManager mGlide;
    private RequestOptions mOptions;
    public static PrescriptionConListFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        PrescriptionConListFragment fragment = new PrescriptionConListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        FragmentContainerActivity.launch(from, PrescriptionConListFragment.class, args);
    }

    private List<String> statuses=new ArrayList<>();
    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<PrescriptionCon> mAdapter;
    private int mCurrentPosition = 0;
    private int statusIndex = 0;
    private int mPage = 1;
    String[] filters ;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mGlide = Glide.with(this);

        mOptions = new RequestOptions()
          .centerCrop()
          .dontAnimate();
        statuses.add(null);
        statuses.add("602-0001");
        statuses.add("602-0002");
        statuses.add("602-0003");
        statuses.add("602-0004");
        statuses.add("602-0005");


        filters= new String[]{
                getString(R.string.transfer_treatment_list_filter_text_1),
                getString(R.string.transfer_treatment_list_filter_text_2),
                getString(R.string.transfer_treatment_list_filter_text_3),
                getString(R.string.transfer_treatment_list_filter_text_4),
                getString(R.string.transfer_treatment_list_filter_text_5),
                getString(R.string.transfer_treatment_list_filter_text_6)
        };
        mCurrentPosition = getArguments().getInt(EXTRAS_KEY_POSITION);
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        setupRecyclerView();
    }


    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdatePrescriptionConStatusEvent) {
            loadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }



    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<PrescriptionCon>(_mActivity) {
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

            DiseaseCounselingDetailFragment.launch(_mActivity,mAdapter.getItem(position).id);
        });

        loadData();
    }

    private void loadData() {
        String queryType = mCurrentPosition==0?"0":"1";
        String status = statuses.get(statusIndex);
        String doctId = AppContext.getUser().getDoctId();
        QueryRequestBody body = QueryRequestBody.getPrescriptionConRequestBody(doctId,AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getPrescriptionApi().getConsultation(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<PrescriptionCon>>>() {
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
                    public void onNext(BaseResp<QueryResponseData<PrescriptionCon>> resp) {
                        showRefreshing(false);
//                        String s=resp.getData().toString();
//                        Log.i(s,s);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<PrescriptionCon>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    mPage=1;
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }else{

            List<PrescriptionCon> clinics = resp.getData().getResultData();
            if (clinics == null || clinics.isEmpty()) {
                if (mAdapter.getAllData().isEmpty()) {
                    mRecyclerView.getEmptyView().setOnClickListener(v -> {
                        mPage=1;
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


            List<PrescriptionCon> targetClinics = new ArrayList<>();
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

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.title_prescription_con);
        setHasOptionsMenu(true);
    }

    private class MyApplyViewHolder extends BaseViewHolder<PrescriptionCon> {

        private TextView mGroupTitleView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mHealthView;
        private LinearLayout mTagsView;
        private TextView mDescribeView;
        private TextView mTransToView;
        private TextView mTimeView;
        private TextView mStatusView;
        private Context mContext;

        public MyApplyViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_prescription_manager1);

            mContext = context;

            mGroupTitleView = $(R.id.tv_group_title);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.iv_gender);
            mAgeView = $(R.id.tv_age);
            mHealthView = $(R.id.tv_health);
            mTagsView = $(R.id.ll_tag);
            mDescribeView = $(R.id.med_name);
            mTransToView = $(R.id.tv_transto);
            mTimeView = $(R.id.time);
            mStatusView = $(R.id.med_status);
        }

        @Override
        public void setData(PrescriptionCon data) {
            super.setData(data);

            try {

                Patient patient = data.patientInfo;
                mNameView.setText(patient.getName());

                mGlide.setDefaultRequestOptions(mOptions.error(patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                  .load(patient.avatar)
                  .into(mAvatarView);
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mHealthView.setText(patient.getMedicalInsuranceName());
                mDescribeView.setText(data.describe);
                mTimeView.setText(data.createdDate.replace("T"," "));
                mStatusView.setText(data.getStatusName());

            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }



}
