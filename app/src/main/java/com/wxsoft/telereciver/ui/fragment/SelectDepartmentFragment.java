package com.wxsoft.telereciver.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.DoctorsDisplayFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.HospitalIntroductionFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectDepartmentFragment extends BaseFragment {

    public static void launch(Fragment from, Hospital hospital, boolean isSelectDoctor, boolean isShowHospital,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        args.add(FRAGMENT_ARGS_IS_SELECT_DOCTOR, isSelectDoctor);
        args.add(FRAGMENT_ARGS_IS_SHOW_HOSPITAL, isShowHospital);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectDepartmentFragment.class, args, REQUEST_SELECT_DEPARTMENT);
    }

//    public static void launch(Fragment from,String business) {
//        FragmentArgs args = new FragmentArgs();
//        args.add(FRAGMENT_ARGS_BUSINESS, business);
//        FragmentContainerActivity.launchForResult(from, SelectDepartmentFragment.class, args, REQUEST_SELECT_DEPARTMENT);
//    }

    public static void launch(Fragment from,String hospitalId,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL_ID, hospitalId);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectDepartmentFragment.class, args, REQUEST_SELECT_DEPARTMENT);
    }


    public static void launch(Fragment from, Hospital hospital, String departmentsJson,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        args.add(FRAGMENT_ARGS_DEPARTMENTS_JSON, departmentsJson);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectDepartmentFragment.class, args, REQUEST_SELECT_DEPARTMENT);
    }

    private static final String FRAGMENT_ARGS_BUSINESS = "FRAGMENT_ARGS_BUSINESS";
    private static final String FRAGMENT_ARGS_HOSPITAL = "FRAGMENT_ARGS_HOSPITAL";
    private static final String FRAGMENT_ARGS_HOSPITAL_ID = "FRAGMENT_ARGS_HOSPITAL_ID";
    private static final String FRAGMENT_ARGS_IS_SELECT_DOCTOR = "FRAGMENT_ARGS_IS_SELECT_DOCTOR";
    private static final String FRAGMENT_ARGS_DEPARTMENTS_JSON = "FRAGMENT_ARGS_DEPARTMENTS_JSON";
    private static final String FRAGMENT_ARGS_IS_SHOW_HOSPITAL= "FRAGMENT_ARGS_IS_SHOW_HOSPITAL";
    public static final int REQUEST_SELECT_DEPARTMENT = 67;
    public static final String KEY_HOSPITAL = "KEY_HOSPITAL";
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    public static final String KEY_DEPARTMENT = "KEY_DEPARTMENT";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Department> mAdapter;
    private Hospital mHospital;
    private String hospitalId;

    private String getFragmentArgsBusiness;
    private boolean isSelectDoctor;
    private boolean isShowHospiatal;
    private List<Department> mDepartments;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            hospitalId=getArguments().getString(FRAGMENT_ARGS_HOSPITAL_ID);
            getFragmentArgsBusiness = getArguments().getString(FRAGMENT_ARGS_BUSINESS);
            if(hospitalId==null) {
                mHospital = (Hospital) getArguments().getSerializable(FRAGMENT_ARGS_HOSPITAL);
                isSelectDoctor = getArguments().getBoolean(FRAGMENT_ARGS_IS_SELECT_DOCTOR);
                isShowHospiatal = getArguments().getBoolean(FRAGMENT_ARGS_IS_SHOW_HOSPITAL);


                String departmentsJson = getArguments().getString(FRAGMENT_ARGS_DEPARTMENTS_JSON);
                if (!TextUtils.isEmpty(departmentsJson)) {
                    mDepartments = new Gson().fromJson(departmentsJson, new TypeToken<List<Department>>() {
                    }.getType());
                }
            }
        }
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DoctorsDisplayFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(DoctorsDisplayFragment.KEY_DOCTOR);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_DOCTOR, doctor);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_department_title);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        if (isShowHospiatal) {
            itemDecoration.setDrawHeaderFooter(true);
        }
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Department>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DepartmentViewHolder(parent, _mActivity);
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
            if (isSelectDoctor()) {
                Department department = mAdapter.getItem(position);
                DoctorsDisplayFragment.launchByHosipitalIdAndDepartmentId(this, department.getName(), mHospital.getId(), department.getId(),getFragmentArgsBusiness);
            } else {
                Department department = mAdapter.getItem(position);
                if (mDepartments != null && !mDepartments.isEmpty()) {
                    if (mDepartments.contains(department)) {
                        ViewUtil.showMessage("请不要重复选择科室");
                    } else {
                        finish(department);
                    }
                } else {
                    finish(department);
                }
            }
        });

        loadData();
    }

    private void loadData() {
        String organizationId = mHospital == null ? (hospitalId==null?"":hospitalId) : mHospital.getId();
        ApiFactory.getClinicManagerApi().getDepartmentByOrganizationId(organizationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Department>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Department>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private void processResponse(BaseResp<List<Department>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<Department> departments = resp.getData();
        if (departments == null || departments.isEmpty()) {
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(departments);
        if (isShowHospiatal) {
            mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
                @Override
                public View onCreateView(ViewGroup parent) {
                    return View.inflate(_mActivity, R.layout.comm_item_hospital, null);
                }

                @Override
                public void onBindView(View headerView) {

                    ImageView coverView = ButterKnife.findById(headerView, R.id.iv_cover);
                    RequestManager glide = Glide.with(SelectDepartmentFragment.this);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .dontAnimate();

                    glide.setDefaultRequestOptions(options.placeholder(R.drawable.ic_hospital_placeholder))
                            .load(mHospital.getImageUrl())
                            .into(coverView);

                    ((TextView) ButterKnife.findById(headerView, R.id.tv_name)).setText(mHospital.getName());
                    ((TextView) ButterKnife.findById(headerView, R.id.tv_level)).setText(mHospital.getLevel());
                    ((TextView) ButterKnife.findById(headerView, R.id.tv_address)).setText(mHospital.getAddress());
                    headerView.setOnClickListener(view -> {
                        HospitalIntroductionFragment.launch(_mActivity, mHospital);
                    });
                }
            });
        }
    }

    /**
     * 判断是否是选择医生
     * @return
     */
    private boolean isSelectDoctor() {
        return mHospital != null && isSelectDoctor;
    }

    private void finish(Department department) {
        Intent intent = new Intent();
        intent.putExtra(KEY_HOSPITAL, mHospital);
        intent.putExtra(KEY_DEPARTMENT, department);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class DepartmentViewHolder extends BaseViewHolder<Department> {

        private Context mContext;

        private TextView mTitleView;

        public DepartmentViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.comm_item_one_text);

            mContext = context;
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(Department data) {
            super.setData(data);
            mTitleView.setText(data.getName());
            mTitleView.setGravity(Gravity.CENTER);
            mTitleView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }
}
