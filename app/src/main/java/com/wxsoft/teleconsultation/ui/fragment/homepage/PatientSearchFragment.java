package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientEMR;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.PatientDetailActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.PatientInfoConfirmFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientSearchFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, false);
        FragmentContainerActivity.launch(from, PatientSearchFragment.class, args);
    }

    public static void launchForResult(Fragment from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, true);
        FragmentContainerActivity.launchForResult(from, PatientSearchFragment.class, args, REQUEST_SEARCH_PATIENT);
    }

    private boolean need;
    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_SEARCH_PATIENT = 37;
    public static final String KEY_PATIENT = "KEY_PATIENT";

    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";

    private static final int SIZE_OF_PAGE = 10;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Patient> mAdapter;
    private int mPage = 1;
    private String mQueryText;
    private boolean hasResult;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        hasResult = getArguments().getBoolean(FRAGMENT_ARGS_HAS_RESULT);
        need = getArguments().getBoolean(FRAGMENT_ARGS_NEED_SURE);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem( R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.select_patient_search_input_hint));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPage = 1;
                mQueryText = query;
                mRecyclerView.showProgress();
                searchPatient();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PatientInfoConfirmFragment.REQUEST_PATIENT_INFO_CONFIRM) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_PATIENT, patient);
                    intent.putExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR, patientEMR);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Patient>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientViewHolder(parent);
            }
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            searchPatient();
        });

        mAdapter.setOnItemClickListener(position -> {
            if (hasResult) {
                PatientInfoConfirmFragment.launch(this, mAdapter.getItem(position),need);
            } else {
                PatientDetailActivity.launch(_mActivity, mAdapter.getItem(position));
            }
        });

        mRecyclerView.showEmpty();
    }

    private void searchPatient() {
        QueryRequestBody body = QueryRequestBody.getPatientsRequestBody(AppContext.getUser().getDoctId(), mQueryText, SIZE_OF_PAGE, mPage);
        ApiFactory.getPatientManagerApi().queryPatientInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<Patient>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<Patient>> resp) {
                        processSearchResponse(resp);
                    }
                });
    }

    private void processSearchResponse(BaseResp<QueryResponseData<Patient>> resp) {
        List<Patient> patients = resp.getData().getResultData();
        if (patients == null || patients.isEmpty()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.showEmpty();
            } else {
                mAdapter.stopMore();
            }
            return;
        }

        if (mPage == 1) {
            mAdapter.clear();
        }

        mAdapter.addAll(patients);
    }

    private class PatientViewHolder extends BaseViewHolder<Patient> {

        private TextView mHealthView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private LinearLayout mTagsView;

        public PatientViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_patient);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mHealthView = $(R.id.tv_health);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mTagsView = $(R.id.ll_tag);
        }

        @Override
        public void setData(Patient data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mAvatarView.setImageResource(data.getAvatarDrawableRes());
            mGenderView.setText(data.getFriendlySex());
            mHealthView.setText(data.getMedicalInsuranceName());
            mAgeView.setText(String.valueOf(data.getAge()));

            mTagsView.removeAllViews();
            List<PatientTag> patientTags = data.getPatientTags();
            if (patientTags == null || patientTags.isEmpty()) {
                mTagsView.setVisibility(View.INVISIBLE);
            } else {
                mTagsView.setVisibility(View.VISIBLE);
                for (PatientTag patientTag : patientTags) {
                    mTagsView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
                }
            }
        }
    }
}
