package com.wxsoft.teleconsultation.ui.fragment.homepage.live;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.live.Live;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.event.UpdateDiseaseCounselingStatusEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.live.calltheroll.OnLineCallTheRollFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OnLineListFragment extends BaseFragment {


    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, OnLineListFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Live> mAdapter;
    private int mPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        EventBus.getDefault().register(this);
        setupRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.live_list_title);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_register, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action:
                OnLineCallTheRollFragment.launch(_mActivity);
                return true;

            default:
                return true;
        }
    }
    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Live>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent);
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
//            String diseaseCounselingId = mAdapter.getItem(position).id;
//            if(mAdapter.getItem(position).status.equals("303-0002")) {
//                Intent intent = new Intent(_mActivity, ChatActivity.class);
//                intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE,"咨询详情");
//                intent.putExtra(ChatActivity.EXTRA_KEY_DISEASECOUNSELING_ID,diseaseCounselingId);
//                intent.putExtra(ChatActivity.EXTRA_KEY_SINGE,true);
//                startActivity(intent);
//
//            }else
//                DiseaseCounselingDetailFragment.launch(_mActivity,diseaseCounselingId);

        });

        loadData();
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdateDiseaseCounselingStatusEvent) {
            loadData();
        }
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getLiveRequestBody(AppContext.getUser().getDoctId(), AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getLiveApi().getLives(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<Live>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<Live>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<Live>> resp) {
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

        List<Live> clinics = resp.getData().getResultData();
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


        List<Live> targetClinics = new ArrayList<>();
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


    private class ViewHolder extends BaseViewHolder<Live> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mStatusView;
        private TextView mCostVIew;
        private TextView mTimeView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_live);

            mTimeView = $(R.id.tv_time);
            mCostVIew = $(R.id.tv_cost);
            mStatusView = $(R.id.tv_status);
            mAvatarView = $(R.id.iv_avatar);
            mNameView = $(R.id.tv_name);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();

            mGlide= Glide.with(App.getApplication());
        }

        @Override
        public void setData(Live data) {
            super.setData(data);

            String time=data.liveDate.replace("T"," ");
            mTimeView.setText(time);
            mCostVIew.setText(String.valueOf(data.price)+getString(R.string.dollar));
            mStatusView.setText(data.statusName);
            mNameView.setText(data.liveTitle);

            mGlide.load(data.url).into(mAvatarView);

        }
    }
}
