package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.centre;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.huawei.ecs.mtk.json.Json;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Department;
import com.wxsoft.teleconsultation.entity.Hospital;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.ExpectDepartmentFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.HospitalIntroductionFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectHospitalFragment extends BaseFragment {

    public static void launch(Fragment from, String departmentsJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_DEPARTMENTS_JSON, departmentsJson);
        FragmentContainerActivity.launchForResult(from, SelectHospitalFragment.class, args, REQUEST_SELECT_HOSPITAL);
    }

    private static final String FRAGMENT_ARGS_DEPARTMENTS_JSON = "FRAGMENT_ARGS_DEPARTMENTS_JSON";
    public static final int REQUEST_SELECT_HOSPITAL = 67;
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
        inflater.inflate(R.menu.menu_one, menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle("");
        menuItem.setIcon(R.drawable.ic_search_white_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                SearchHospitalFragment.launch(this, new Gson().toJson(mDepartments));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
             if (requestCode == SearchHospitalFragment.REQUEST_SEARCH_HOSPITAL) {
                if (data != null) {
                    String departmentsJson = data.getStringExtra(SearchHospitalFragment.KEY_DEPARTMENTS);
                    mDepartments = new Gson().fromJson(departmentsJson, new TypeToken<Map<String, List<Department>>>() {
                    }.getType());

                    finish();
                }
             } else if (requestCode == ExpectDepartmentFragment.REQUEST_EXPEAT_DEPARTMENT) {
                 String departmentsJson = data.getStringExtra(ExpectDepartmentFragment.KEY_DEPARTMENTS_JSON);
                 List<Department> departments = new Gson().fromJson(departmentsJson, new TypeToken<List<Department>>() {
                     }.getType());

                 mDepartments.remove(mSelectedHospitalName);
                 if (departments != null && !departments.isEmpty()) {
                     mDepartments.put(mSelectedHospitalName, departments);
                 }

                 finish();
             }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_doctor_search_by_hospital_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Hospital>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new HospitalViewHolder(parent, Glide.with(SelectHospitalFragment.this), hospital -> {
                    HospitalIntroductionFragment.launch(_mActivity, hospital);
                });
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
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
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getClinicManagerApi().getHospitals("", "", "", AppContext.getUser().getHospitalId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Hospital>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Hospital>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<Hospital>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(resp.getData());
    }

    private void finish() {
        Intent intent = new Intent();
        intent.putExtra(KEY_DEPARTMENTS, new Gson().toJson(mDepartments));
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
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
