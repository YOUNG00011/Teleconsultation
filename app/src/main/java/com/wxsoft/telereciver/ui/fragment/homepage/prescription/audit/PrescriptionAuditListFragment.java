package com.wxsoft.telereciver.ui.fragment.homepage.prescription.audit;

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
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.WeChatAccount;
import com.wxsoft.telereciver.entity.prescription.OnlinePrescription;
import com.wxsoft.telereciver.entity.prescription.Recipe;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.event.TreatMentStateChangeEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll.PrescriptionCallTheRollFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrescriptionAuditListFragment extends BaseFragment {

    public static PrescriptionAuditListFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        PrescriptionAuditListFragment fragment = new PrescriptionAuditListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<String> statuses=new ArrayList<>();
    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<OnlinePrescription> mAdapter;
    private int mCurrentPosition = 0;
    private int statusIndex = 0;
    private int mPage = 1;
    String[] filters ;

    private RequestManager mGlide;
    private RequestOptions mOptions;

    private static final String ALLOW_AUDIT = "ALLOW_AUDIT";
    private boolean allowAudit = false;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

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
    public void load(Object object){
        if(object instanceof TreatMentStateChangeEvent)
            loadData();
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
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<OnlinePrescription>(_mActivity) {
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

            PrescriptionCallTheRollFragment.launch(_mActivity,mAdapter.getItem(position),true);

        });

        loadData();
    }

    private void loadData() {
        boolean done = mCurrentPosition==1;
        String doctId = AppContext.getUser().getDoctId();
        QueryRequestBody body = QueryRequestBody.getPrescriptionRequestBody(doctId, done, AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getPrescriptionApi().getPrescriptions(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData< OnlinePrescription>>>() {
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
                    public void onNext(BaseResp<QueryResponseData<OnlinePrescription>> resp) {
                        showRefreshing(false);
//                        String s=resp.getData().toString();
//                        Log.i(s,s);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<OnlinePrescription>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    mPage=1;
                    loadData();
                });
            } else {
                ViewUtil.showMessage("????????????");
            }
            return;
        }else{

            List<OnlinePrescription> clinics = resp.getData().getResultData();
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


            List<OnlinePrescription> targetClinics = new ArrayList<>();
            if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
                targetClinics.addAll(mAdapter.getAllData());
            }

            targetClinics.addAll(clinics);


            Collections.sort(targetClinics,(obj1,obj2)->{

                int i=obj1.status.compareTo(obj2.status);
                if(i==0){
                    return -1*(obj1.createdDate.compareTo(obj2.createdDate));
                }else{
                    return i;
                }
            });
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

    private class MyApplyViewHolder extends BaseViewHolder<OnlinePrescription> {

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
        public void setData(OnlinePrescription data) {
            super.setData(data);

            try {

                WeChatAccount account = data.weChatAccount;
                mNameView.setText(account.name);

                mGlide.setDefaultRequestOptions(mOptions.error(account.sex==1 ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                  .load(account.headimgurl)
                  .into(mAvatarView);

                if(mCurrentPosition==0) {
                    mDescribeView.setText(data.diseaseDescription);

                }else if(mCurrentPosition==1){
                    if (data.recipes!=null && data.recipes.size()>0){
                        Recipe recipe=data.recipes.get(0);
                        mDescribeView.setText(recipe.medicineProductName);
                    }else{
                        mDescribeView.setText(data.diseaseDescription);
                    }
                }

                mTimeView.setText(data.createdDate.replace("T", " ").substring(5,16));
                mStatusView.setText(data.statusName);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


}
