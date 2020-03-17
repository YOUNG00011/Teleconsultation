package com.wxsoft.telereciver.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.CommEnum;
import com.wxsoft.telereciver.entity.PositionTitle;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectPositionTitleFragment extends BaseFragment {

    public static void launch(Fragment from, PositionTitle positionTitle) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_POSITION_TITLE, positionTitle);
        FragmentContainerActivity.launchForResult(from, SelectPositionTitleFragment.class, args, REQUEST_SELECT_POSITION_TITLE);
    }

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectPositionTitleFragment.class, null, REQUEST_SELECT_POSITION_TITLE);
    }

    private static final String FRAGMENTARGS_KEY_POSITION_TITLE = "FRAGMENTARGS_KEY_POSITION_TITLE";
    public static final int REQUEST_SELECT_POSITION_TITLE = 119;
    public static final String KEY_POSITION_TITLE = "KEY_POSITION_TITLE";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<PositionTitle> mAdapter;
    private PositionTitle mCurrentPositionTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mCurrentPositionTitle = (PositionTitle) getArguments().getSerializable(FRAGMENTARGS_KEY_POSITION_TITLE);
        }
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_job_title_title);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color),
                DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<PositionTitle>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PositionTitleViewHolder(parent, _mActivity);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_POSITION_TITLE, mAdapter.getItem(position));
            _mActivity.setResult(RESULT_OK, intent);
            _mActivity.finish();
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getClinicManagerApi().getAllPositonTitle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<CommEnum>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<CommEnum>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<CommEnum>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<PositionTitle> positionTitles = PositionTitle.getPositionTitles(resp.getData());
        if (mCurrentPositionTitle != null) {
            positionTitles.add(0, PositionTitle.getNoLimitPositionTitle());
        }
        mAdapter.addAll(positionTitles);
    }

    private class PositionTitleViewHolder extends BaseViewHolder<PositionTitle> {

        private Context mContext;
        private TextView mTitleView;
        private ImageView mSelectView;

        public PositionTitleViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.comm_item_one_select);
            mContext = context;
            mTitleView = $(R.id.tv_title);
            mSelectView = $(R.id.iv_select);
        }

        @Override
        public void setData(PositionTitle data) {
            super.setData(data);
            mTitleView.setText(data.getPositionTitleName());
            mSelectView.setVisibility((mCurrentPositionTitle != null && mCurrentPositionTitle.equals(data)) ? View.VISIBLE : View.GONE);
        }
    }
}
