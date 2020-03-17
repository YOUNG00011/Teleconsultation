package com.wxsoft.telereciver.ui.fragment.homepage.clinic.centre;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Department;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.ExpectDepartmentFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.HospitalIntroductionFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchHospitalFragment extends BaseFragment {

    public static void launch(Fragment from, String departmentsJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_DEPARTMENTS_JSON, departmentsJson);
        FragmentContainerActivity.launchForResult(from, SearchHospitalFragment.class, args, REQUEST_SEARCH_HOSPITAL);
    }

    private static final String FRAGMENT_ARGS_DEPARTMENTS_JSON = "FRAGMENT_ARGS_DEPARTMENTS_JSON";
    public static final int REQUEST_SEARCH_HOSPITAL = 55;
    public static final String KEY_DEPARTMENTS = "KEY_DEPARTMENTS";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private Map<String, List<Department>> mDepartments;
    private RecyclerArrayAdapter<Hospital> mAdapter;
    private String mSelectedHospitalName;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        String departmentsJson = getArguments().getString(FRAGMENT_ARGS_DEPARTMENTS_JSON);
        if (!TextUtils.isEmpty(departmentsJson)) {
            mDepartments = new Gson().fromJson(departmentsJson, new TypeToken<Map<String, List<Department>>>() {
            }.getType());
        }
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hospital_input_hint));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHospital(query);
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
            if (requestCode == ExpectDepartmentFragment.REQUEST_EXPEAT_DEPARTMENT) {
                String departmentsJson = data.getStringExtra(ExpectDepartmentFragment.KEY_DEPARTMENTS_JSON);
                List<Department> departments = new Gson().fromJson(departmentsJson, new TypeToken<List<Department>>() {
                }.getType());

                mDepartments.remove(mSelectedHospitalName);
                if (departments != null && !departments.isEmpty()) {
                    mDepartments.put(mSelectedHospitalName, departments);
                }

                Intent intent = new Intent();
                intent.putExtra(KEY_DEPARTMENTS, new Gson().toJson(mDepartments));
                _mActivity.setResult(RESULT_OK, intent);
                _mActivity.finish();
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.search_hospital_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Hospital>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new HospitalViewHolder(parent, Glide.with(SearchHospitalFragment.this), hospital -> {
                    HospitalIntroductionFragment.launch(_mActivity, hospital);
                });
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            mSelectedHospitalName = mAdapter.getItem(position).getName();
            List<Department> departments = null;
            for (String hospital1Name : mDepartments.keySet()) {
                if (mSelectedHospitalName.equals(hospital1Name)) {
                    departments = mDepartments.get(hospital1Name);
                    break;
                }
            }

            if (departments == null) {
                departments = new ArrayList<>();
            }

            ExpectDepartmentFragment.launch(this, mAdapter.getItem(position), new Gson().toJson(departments));
//            Intent intent = new Intent();
//            intent.putExtra(KEY_HOSPITAL, mAdapter.getItem(position));
//            _mActivity.setResult(RESULT_OK, intent);
//            _mActivity.finish();
//            SelectDepartmentFragment.launch(this, mAdapter.getItem(position), false);
        });

        mRecyclerView.showEmpty();
    }

    private void searchHospital(String text) {
        mRecyclerView.showProgress();
        ApiFactory.getClinicManagerApi().getHospitalByName(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Hospital>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.showError();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Hospital>> resp) {
                        processSearchResponse(resp);
                    }
                });
    }

    private void processSearchResponse(BaseResp<List<Hospital>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<Hospital> hospitals = resp.getData();
        if (hospitals == null || hospitals.isEmpty()) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(hospitals);
    }

    private static class HospitalViewHolder extends BaseViewHolder<Hospital> {

        interface OnCoverClickListener {
            void onClick(Hospital hospital);
        }

        private ImageView mCoverView;
        private TextView mNameView;
        private TextView mLevelView;
        private TextView mAddressView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        private OnCoverClickListener mOnCoverClickListener;

        public HospitalViewHolder(ViewGroup parent, RequestManager glide, OnCoverClickListener onCoverClickListener) {
            super(parent, R.layout.comm_item_hospital);

            mGlide = glide;

            mCoverView = $(R.id.iv_cover);
            mNameView = $(R.id.tv_name);
            mLevelView = $(R.id.tv_level);
            mAddressView = $(R.id.tv_address);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();

            mOnCoverClickListener = onCoverClickListener;
        }

        @Override
        public void setData(Hospital data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mLevelView.setText(data.getLevel());
            mAddressView.setText(data.getAddress());

            mGlide.setDefaultRequestOptions(mOptions.placeholder(R.drawable.ic_hospital_placeholder))
                    .load(data.getImageUrl())
                    .into(mCoverView);

            mCoverView.setOnClickListener(v -> {
                if (mOnCoverClickListener != null) {
                    mOnCoverClickListener.onClick(data);
                }
            });
        }
    }
}
