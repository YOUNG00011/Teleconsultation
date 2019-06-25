package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine.select;

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
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Department;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Hospital;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.DoctorsDisplayFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.HospitalIntroductionFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectMedicalTypesFragment extends BaseFragment {

    public static void launch(Fragment from, Hospital hospital, boolean isSelectDoctor, boolean isShowHospital,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        args.add(FRAGMENT_ARGS_IS_SELECT_DOCTOR, isSelectDoctor);
        args.add(FRAGMENT_ARGS_IS_SHOW_HOSPITAL, isShowHospital);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectMedicalTypesFragment.class, args, REQUEST_SELECT_DEPARTMENT);
    }

   public static SelectMedicalTypesFragment newInstance(int position) {
       SelectMedicalTypesFragment fragment = new SelectMedicalTypesFragment();
        return fragment;
    }
    public static void launch(Fragment from,String hospitalId,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL_ID, hospitalId);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectMedicalTypesFragment.class, args, REQUEST_SELECT_DEPARTMENT);
    }


    public static void launch(Fragment from, Hospital hospital, String departmentsJson,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        args.add(FRAGMENT_ARGS_DEPARTMENTS_JSON, departmentsJson);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, SelectMedicalTypesFragment.class, args, REQUEST_SELECT_DEPARTMENT);
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
//    private Hospital mHospital;
//    private String hospitalId;
//
//    private String getFragmentArgsBusiness;
//    private boolean isSelectDoctor;
//    private boolean isShowHospiatal;
//    private List<Department> mDepartments;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
//        if (getArguments() != null) {
//            hospitalId=getArguments().getString(FRAGMENT_ARGS_HOSPITAL_ID);
//            getFragmentArgsBusiness = getArguments().getString(FRAGMENT_ARGS_BUSINESS);
//            if(hospitalId==null) {
//                mHospital = (Hospital) getArguments().getSerializable(FRAGMENT_ARGS_HOSPITAL);
//                isSelectDoctor = getArguments().getBoolean(FRAGMENT_ARGS_IS_SELECT_DOCTOR);
//                isShowHospiatal = getArguments().getBoolean(FRAGMENT_ARGS_IS_SHOW_HOSPITAL);
//
//
//                String departmentsJson = getArguments().getString(FRAGMENT_ARGS_DEPARTMENTS_JSON);
//                if (!TextUtils.isEmpty(departmentsJson)) {
//                    mDepartments = new Gson().fromJson(departmentsJson, new TypeToken<List<Department>>() {
//                    }.getType());
//                }
//            }
//        }
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
//        if (isShowHospiatal) {
//            itemDecoration.setDrawHeaderFooter(true);
//        }
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Department>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DepartmentViewHolder(parent, _mActivity);
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
//            if (isSelectDoctor()) {
//                Department department = mAdapter.getItem(position);
//                DoctorsDisplayFragment.launchByHosipitalIdAndDepartmentId(this, department.getName(), mHospital.getId(), department.getId(),getFragmentArgsBusiness);
//            } else {
//
//            }
        });

        loadData();
    }

    private void loadData() {
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
