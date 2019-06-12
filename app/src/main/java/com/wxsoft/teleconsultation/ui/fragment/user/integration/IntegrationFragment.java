package com.wxsoft.teleconsultation.ui.fragment.user.integration;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.DoctorInfo;
import com.wxsoft.teleconsultation.entity.Evaluate;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IntegrationFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, IntegrationFragment.class, null);
    }

    private int mPage=1;

    @BindView(R.id.stars)
    RatingBar ratingBar;

    @BindView(R.id.score)
    TextView score;

    @BindView(R.id.the_count)
    TextView in_count;

    @BindView(R.id.ll_root)
    LinearLayout root;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Evaluate> mAdapter;
    private User mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_integration;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mUser = AppContext.getUser();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.evaluate_title);
    }

    private void setupRecyclerView() {
        // 设置上边距10dp

        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Evaluate>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new UserInfoViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {

        });
        loadScore();
        loadData();
    }

    private class UserInfoViewHolder extends BaseViewHolder<Evaluate> {

        private TextView name;
        private TextView instruction;
        private TextView content;
        private TextView btype;
        private RatingBar rating;

        public UserInfoViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_evolu);
            name = $(R.id.tv_name);
            instruction = $(R.id.tv_instruction);
            content = $(R.id.tv_describe);
            btype = $(R.id.tv_type_name);
            rating = $(R.id.rating);

        }

        @Override
        public void setData(Evaluate data) {
            super.setData(data);

            if(data.evaluaterName==null ||data.evaluaterName.length()==0){
                name.setText(R.string.evaluater_name_unkown);
            }else {
                String s="";
                for(int i=0;i<data.evaluaterName.length()-1;i++){
                    s+="*";
                }
                name.setText(data.evaluaterName.substring(0,1)+s);
            }
//            name.setText(String.format("%1$"+data.evaluaterName.length()+ "*",data.evaluaterName.substring(0,1)));
            instruction.setText(data.evaluateInstruction);
            content.setText(data.evaluateContent);
            btype.setText(data.bussinessTypeName);
            rating.setRating(data.evaluateScore);
        }
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getIntegrationRequestBody(mUser.getDoctId(),  AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getCommApi().getEvsluates(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<Evaluate>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<Evaluate>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void loadScore() {

        ApiFactory.getCommApi().queryDoctor(mUser.getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<DoctorInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<DoctorInfo> stringBaseResp) {

                        if(stringBaseResp.isSuccess()){
                            if(root.getVisibility()==View.GONE){
                                root.setVisibility(View.VISIBLE);
                            }
                            DoctorInfo info=stringBaseResp.getData();
                            ratingBar.setRating(info.counselingEvaluateAvgScore);
                            score.setText(String.valueOf(info.counselingEvaluateAvgScore)+"分");
                        }

                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<Evaluate>> resp) {
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

        List<Evaluate> clinics = resp.getData().getResultData();
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

        if(root.getVisibility()==View.GONE){
            root.setVisibility(View.VISIBLE);
        }
        List<Evaluate> targetClinics = new ArrayList<>();
        if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
            targetClinics.addAll(mAdapter.getAllData());
        }

        targetClinics.addAll(clinics);
        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(targetClinics);
        int total=resp.getData().getQueryObject().getTotalCount();
        in_count.setText(getResources().getText(R.string.integration_count)+"("+String.valueOf(total)+")");


    }

}
