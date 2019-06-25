package com.wxsoft.teleconsultation.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;

import butterknife.BindView;

public class SelectMedicalFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectMedicalFragment.class, null, REQUEST_SELECT_MEDICALTYPE);
    }

    public static final int REQUEST_SELECT_MEDICALTYPE = 130;
    public static final int REQUEST_SELECT_MEDICAL = 131;
    public static final String KEY_MED_TYPE= "KEY_MEDICAL_TYPE";
    public static final String KEY_MEDICAL= "KEY_MEDICAL";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<String> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("PR");
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color),
                DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<String>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DutyViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_MED_TYPE, mAdapter.getItem(position));
            _mActivity.setResult(RESULT_OK, intent);
            _mActivity.finish();
        });

        mAdapter.add("西药");
        mAdapter.add("中成药");
        mRecyclerView.showRecycler();
//        loadData();
    }

//    private void loadData() {
//        ApiFactory.getClinicManagerApi().getAllDuties()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseResp<List<CommEnum>>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ViewUtil.showMessage(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(BaseResp<List<CommEnum>> resp) {
//                        processResponse(resp);
//                    }
//                });
//    }
//
//    private void processResponse(BaseResp<List<CommEnum>> resp) {
//        if (!resp.isSuccess()) {
//            mRecyclerView.getErrorView().setOnClickListener(v -> {
//                mRecyclerView.showProgress();
//                loadData();
//            });
//            mRecyclerView.showError();
//            return;
//        }
//
//        List<Duty> duties = Duty.getDuties(resp.getData());
//        if (duties == null || duties.isEmpty()) {
//            mRecyclerView.getEmptyView().setOnClickListener(v -> {
//                mRecyclerView.showProgress();
//                loadData();
//            });
//            mRecyclerView.showEmpty();
//            return;
//        }
//
//        mRecyclerView.showRecycler();
//        mAdapter.clear();
//        mAdapter.addAll(duties);
//    }

    private class DutyViewHolder extends BaseViewHolder<String> {

        private TextView mTitleView;

        public DutyViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_select);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(String data) {
            super.setData(data);
            mTitleView.setText(data);
        }
    }
}
