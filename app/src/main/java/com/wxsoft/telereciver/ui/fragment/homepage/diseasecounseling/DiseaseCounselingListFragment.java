package com.wxsoft.telereciver.ui.fragment.homepage.diseasecounseling;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.diseasecounseling.DiseaseCounseling;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.event.UpdateDiseaseCounselingStatusEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DiseaseCounselingListFragment extends BaseFragment {


    public static DiseaseCounselingListFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        DiseaseCounselingListFragment fragment = new DiseaseCounselingListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<DiseaseCounseling> mAdapter;
    private String mCurrentPosition ;
    private int mPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mCurrentPosition = getArguments().getInt(EXTRAS_KEY_POSITION)==0?"0":"1";
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
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<DiseaseCounseling>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent, _mActivity);
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

            String diseaseCounselingId = mAdapter.getItem(position).id;

            DiseaseCounselingDetailFragment.launch(_mActivity,diseaseCounselingId);

        });

        loadData();
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdateDiseaseCounselingStatusEvent) {
            mPage=1;
            loadData();
        }
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getDiseaseCounselingRequestBody(AppContext.getUser().getDoctId(),mCurrentPosition,  AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getDiseaseCounselingApi().queryList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<DiseaseCounseling>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<DiseaseCounseling>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<DiseaseCounseling>> resp) {
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

        List<DiseaseCounseling> clinics = resp.getData().getResultData();
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


        List<DiseaseCounseling> targetClinics = new ArrayList<>();
        if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
            targetClinics.addAll(mAdapter.getAllData());
        }

        targetClinics.addAll(clinics);
        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(targetClinics);

    }



    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }


    private class ViewHolder extends BaseViewHolder<DiseaseCounseling> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mMedicalInsuranceNameView;
        private TextView mDescribeView;
        private TextView mStatusView;
        private TextView mTimeView;
        private Context mContext;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public ViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_disease_counseling);

            mContext = context;

            mTimeView = $(R.id.tv_time);
            mStatusView = $(R.id.tv_status);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mMedicalInsuranceNameView=$(R.id.tv_medical_insurance_name);
            mDescribeView = $(R.id.tv_describe);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();

            mGlide= Glide.with(App.getApplication());
        }

        @Override
        public void setData(DiseaseCounseling data) {
            super.setData(data);

            try {


                Patient patient = data.patient;
                mNameView.setText(patient.getName());
                String avatar=data.patientHeadImage;
//                if(avatar==null){
//                    avatar=patient.
//                }
                mGlide.setDefaultRequestOptions(mOptions.error(data.patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                        .load(avatar)
                        .into(mAvatarView);
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mMedicalInsuranceNameView.setText(patient.getMedicalInsuranceName());
                String te=getResources().getString(R.string.discription_item_disease_counseling);
                mDescribeView.setText( Html.fromHtml(te+data.describe));
                mStatusView.setText(data.statusName);
                if(data.startTime!=null)
                    mTimeView.setText(data.startTime.replace("T"," "));
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }
}
