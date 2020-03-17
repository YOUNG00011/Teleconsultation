package com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.wxsoft.telereciver.entity.cloudclinc.ClincRecord;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CloudClincHistoryListFragment extends BaseFragment {


    public static CloudClincHistoryListFragment newInstance(int position,String status) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        args.putString(EXTRAS_KEY_STATUS, status);
        CloudClincHistoryListFragment fragment = new CloudClincHistoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";
    private static final String EXTRAS_KEY_STATUS = "EXTRAS_KEY_STATUS";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<ClincRecord> mAdapter;
    private String mCurrentPosition ;
    private String status;
    private int mPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        int position=getArguments().getInt(EXTRAS_KEY_POSITION);

            Log.i("为啥不显示",String.valueOf(position));

        mCurrentPosition = position==0?"0":"1";
        status = getArguments().getString(EXTRAS_KEY_STATUS);
        setupRecyclerView();
    }


    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<ClincRecord>(_mActivity) {
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
            CloudClincDetailFragment.launch(_mActivity, diseaseCounselingId);


        });

        loadData();
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getCloudClincHistoryRequestBody(AppContext.getUser().getDoctId(),mCurrentPosition,status,  AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getCloudClinicApi().getRecordHistory(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<ClincRecord>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<ClincRecord>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private void processResponse(BaseResp<QueryResponseData<ClincRecord>> resp) {
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mPage=1;
                    mRecyclerView.showProgress();
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }

        List<ClincRecord> clinics = resp.getData().getResultData();
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


        List<ClincRecord> targetClinics = new ArrayList<>();
        if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
            targetClinics.addAll(mAdapter.getAllData());
        }

        targetClinics.addAll(clinics);
        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(targetClinics);

    }



    private class ViewHolder extends BaseViewHolder<ClincRecord> {

        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mDocView;
        private TextView mTimeView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public ViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_cloud_clinc);

            mTimeView = $(R.id.time);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.sex);
            mAgeView = $(R.id.age);
            mDocView=$(R.id.tv_doc);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();

            mGlide= Glide.with(App.getApplication());
        }

        @Override
        public void setData(ClincRecord data) {
            super.setData(data);

            try {


                Patient patient = data.patient;
                mNameView.setText(patient.getName());
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mDocView.setText(data.acceptDoctorName);;
                mTimeView.setText(data.createdDate.replace("T"," ").substring(5,16));
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }
}
