package com.wxsoft.telereciver.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.SystemMessage;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SystemMessageActivity extends BaseActivity {

    public static void launch(Context from) {
        from.startActivity(new Intent(from, SystemMessageActivity.class));
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<SystemMessage> mAdapter;
    private int mPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_message;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.system_message);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<SystemMessage>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MessageViewHolder(parent);
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
            SystemMessage systemMessage = mAdapter.getItem(position);
            SystemMessage.ExtendFiled extendFiled = systemMessage.getExtendFiled();
            if (extendFiled == null) {
                return;
            }

            if (systemMessage.isClinic()) {
                String clinicId = extendFiled.getObject().toString();
                ClinicDetailActivity.launch(this, clinicId);
            }
        });

        loadData();
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getQueryNotificationRequestBody(AppContext.getUser().getId(), AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getCommApi().queryNotification(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<SystemMessage>>>() {
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
                    public void onNext(BaseResp<QueryResponseData<SystemMessage>> resp) {
                        showRefreshing(false);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<SystemMessage>> resp) {
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadData();
                });
                mRecyclerView.showError();
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }

        List<SystemMessage> systemMessages = resp.getData().getResultData();
        if (systemMessages == null || systemMessages.isEmpty()) {
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

        mRecyclerView.showRecycler();
        if (mPage == 1) {
            mAdapter.clear();
        }

        mAdapter.addAll(systemMessages);
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private class MessageViewHolder extends BaseViewHolder<SystemMessage> {

        private TextView mTilteView;
        private TextView mContentView;
        private TextView mTimeView;

        public MessageViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_system_message);
            mTilteView = $(R.id.tv_title);
            mContentView = $(R.id.tv_content);
            mTimeView = $(R.id.tv_time);
        }

        @Override
        public void setData(SystemMessage data) {
            super.setData(data);
            mTilteView.setText(data.getTitle());
            mContentView.setText(data.getContent());

            String createDatetime = data.getCreatedDate().replace("T", " ");
            if (createDatetime.contains(".")) {
                int lastPoi = createDatetime.lastIndexOf('.');
                createDatetime = createDatetime.substring(0, lastPoi);
            }
            mTimeView.setText(createDatetime);
        }
    }
}
