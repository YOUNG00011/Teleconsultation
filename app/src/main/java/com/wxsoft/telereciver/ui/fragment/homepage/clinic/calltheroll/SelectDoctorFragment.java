package com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.DoctorsDisplayFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectDoctorFragment extends BaseFragment {

    private String businessType;
    public static void launch(Fragment from, String type) {
        FragmentArgs args=new FragmentArgs();
        args.add(BUSINESS_TYPE,type);
        FragmentContainerActivity.launchForResult(from, SelectDoctorFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    public static void launch(Activity from, String type) {
        FragmentArgs args=new FragmentArgs();
        args.add(BUSINESS_TYPE,type);
        FragmentContainerActivity.launchForResult(from, SelectDoctorFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    public static final int REQUEST_SELECT_DOCTOR = 41;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    public static final String BUSINESS_TYPE = "BusinessType";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Doctor> mAdapter;
    private QueryRequestBody mQueryRequestBody;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        businessType=getArguments().getString(BUSINESS_TYPE);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SearchDoctorFragment.REQUEST_SEARCH_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(SearchDoctorFragment.KEY_DOCTOR);
                    finish(doctor);
                }
            } else if (requestCode == SelectHospitalFragment.REQUEST_SELECT_HOSPITAL) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(SelectHospitalFragment.KEY_DOCTOR);
                    finish(doctor);
                }
            } else if (requestCode == DoctorsDisplayFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(DoctorsDisplayFragment.KEY_DOCTOR);
                    finish(doctor);
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_doctor_title);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Doctor>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DoctorViewHolder(parent, Glide.with(SelectDoctorFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            finish(mAdapter.getItem(position));
        });

        loadData();
    }

    private void loadData() {
        String doctorId=AppContext.getUser().getDoctId();
        ApiFactory.getClinicManagerApi().getCommonDoctors(doctorId,businessType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<Doctor>> resp) {
        List<Doctor> doctors = resp.getData();
        if (doctors == null || doctors.isEmpty()) {
            doctors.add(new Doctor());
        }

        mAdapter.clear();
        mAdapter.addAll(doctors);

        mAdapter.removeAllHeader();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_select_doctor_call_the_roll, null);
            }

            @Override
            public void onBindView(View headerView) {

                TextView mGroupTitleView = ButterKnife.findById(headerView, R.id.tv_group_title);
                if (TextUtils.isEmpty(doctors.get(0).getId())) {
                    mGroupTitleView.setVisibility(View.GONE);
                } else {
                    mGroupTitleView.setVisibility(View.VISIBLE);
                    mGroupTitleView.setText(R.string.select_doctor_common);
                }

                ButterKnife.findById(headerView, R.id.tv_search_by_condition).setOnClickListener(view -> {
                    SearchDoctorFragment.launch(SelectDoctorFragment.this);
                });

                ButterKnife.findById(headerView, R.id.tv_my_focus).setOnClickListener(view -> {
                    DoctorsDisplayFragment.launchByDoctId(SelectDoctorFragment.this, getString(R.string.select_doctor_my_focus), AppContext.getUser().getDoctId());
                });

                ButterKnife.findById(headerView, R.id.tv_search_by_hospital).setOnClickListener(view -> {
                    SelectHospitalFragment.launch(SelectDoctorFragment.this);
                });
            }
        });
    }

    private void finish(Doctor doctor) {
        Intent intent = new Intent();
        intent.putExtra(KEY_DOCTOR, doctor);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class DoctorViewHolder extends BaseViewHolder<Doctor> {

        private LinearLayout mRootView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mDepartmentView;
        private TextView mHospitalView;
        private TextView mGoodAtView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public DoctorViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.comm_item_doctor);

            mGlide = glide;

            mRootView = $(R.id.ll_root);
            mAvatarView = $(R.id.iv_doctor_avatar);
            mNameView = $(R.id.tv_doctor_name);
            mDepartmentView = $(R.id.tv_department);
            mHospitalView = $(R.id.tv_hospital);
            mGoodAtView = $(R.id.tv_goodat);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(Doctor data) {
            super.setData(data);
            int height;
            if (TextUtils.isEmpty(data.getId())) {
                height = 0;
            } else {
                height = FrameLayout.LayoutParams.WRAP_CONTENT;
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
            mRootView.setLayoutParams(params);

            if (!TextUtils.isEmpty(data.getId())) {
                mNameView.setText(data.getName());
                mDepartmentView.setText(data.getDepartmentName());
                mHospitalView.setText(data.getHospitalName());

                String goodAt = "";
                if (!TextUtils.isEmpty(data.getGoodAt())) {
                    goodAt = "擅长:   " + data.getGoodAt();
                }

                mGoodAtView.setText(goodAt);

                mGlide.setDefaultRequestOptions(mOptions.error(data.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                        .load(data.getUserImgUrl())
                        .into(mAvatarView);
            }
        }
    }
}
