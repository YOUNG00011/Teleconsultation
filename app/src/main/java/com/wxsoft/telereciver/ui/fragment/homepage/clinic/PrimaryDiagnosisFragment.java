package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Diagnosis;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrimaryDiagnosisFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, PrimaryDiagnosisFragment.class, null, REQUEST_SELECT_PRIMARY_DIAGNOSIS);
    }

    public static final int REQUEST_SELECT_PRIMARY_DIAGNOSIS = 244;
    public static final String KEY_SELECTED_DISEASE = "KEY_SELECTED_DISEASE";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Diagnosis> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_primary_diagnosis;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setupNoSureDiagnosisView(view);
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem( R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.primary_diagnosis_input_hint));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDiagnosis(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mRecyclerView.showEmpty();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_consultation_diagnosis);
        setHasOptionsMenu(true);
    }

    private void setupNoSureDiagnosisView(View view) {
        TextView noSureDiagnosisView = ButterKnife.findById(view, R.id.tv_title);
        noSureDiagnosisView.setText(R.string.primary_diagnosis_undiagnosed);
        noSureDiagnosisView.setOnClickListener(v -> {
            finish(getNoSureDiagnosis());
        });
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Diagnosis>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DiseaseViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            finish(mAdapter.getItem(position));
        });
    }

    private void searchDiagnosis(String text) {
        mRecyclerView.showProgress();
        ApiFactory.getClinicManagerApi().getDiagnosis(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Diagnosis>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.showError();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Diagnosis>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<Diagnosis>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<Diagnosis> diagnoses = resp.getData();
        if (diagnoses == null || diagnoses.isEmpty()) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(diagnoses);
    }

    private Diagnosis getNoSureDiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setName(getString(R.string.primary_diagnosis_undiagnosed));
        return diagnosis;
    }

    private void finish(Diagnosis diagnosis) {
        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_DISEASE, diagnosis);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class DiseaseViewHolder extends BaseViewHolder<Diagnosis> {

        private TextView mTitleView;

        public DiseaseViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_text);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(Diagnosis data) {
            super.setData(data);
            mTitleView.setText(data.getName());
        }
    }
}
