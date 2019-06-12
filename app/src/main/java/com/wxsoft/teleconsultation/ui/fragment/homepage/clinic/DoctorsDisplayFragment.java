package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoctorsDisplayFragment extends BaseFragment {

    public static void launch(Fragment from, String title, ArrayList<Doctor> doctors) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_DOCTORS, doctors);
        args.add(FRAGMENT_ARGS_BUSINESS, "Consultation");
        FragmentContainerActivity.launchForResult(from, DoctorsDisplayFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    public static void launchByDoctId(Fragment from, String title, String doctId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_DOCTOR_ID, doctId);
        args.add(FRAGMENT_ARGS_BUSINESS, "Consultation");
        FragmentContainerActivity.launchForResult(from, DoctorsDisplayFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    public static void launchByHosipitalIdAndDepartmentId(Fragment from, String title, String hospitalId, String departmentId,String business) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_HOSPITAL_ID, hospitalId);
        args.add(FRAGMENT_ARGS_DEPARTMENT_ID, departmentId);
        args.add(FRAGMENT_ARGS_BUSINESS, business);
        FragmentContainerActivity.launchForResult(from, DoctorsDisplayFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    private static final String FRAGMENT_ARGS_BUSINESS= "FRAGMENT_ARGS_BUSINESS";
    private static final String FRAGMENT_ARGS_TITLE= "FRAGMENT_ARGS_TITLE";
    private static final String FRAGMENT_ARGS_DOCTORS = "FRAGMENT_ARGS_DOCTORS";
    private static final String FRAGMENT_ARGS_DOCTOR_ID = "FRAGMENT_ARGS_DOCTOR_ID";
    private static final String FRAGMENT_ARGS_HOSPITAL_ID = "FRAGMENT_ARGS_HOSPITAL_ID";
    private static final String FRAGMENT_ARGS_DEPARTMENT_ID = "FRAGMENT_ARGS_DEPARTMENT_ID";
    public static final int REQUEST_SELECT_DOCTOR = 66;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Doctor> mAdapter;
    private String mTitle;
    private ArrayList<Doctor> mDoctors;
    private String mDoctorId;
    private String mHospitalId;
    private String mDepartmentId;
    private String business;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mTitle = getArguments().getString(FRAGMENT_ARGS_TITLE);
        mDoctors = (ArrayList<Doctor>) getArguments().getSerializable(FRAGMENT_ARGS_DOCTORS);
        mDoctorId = getArguments().getString(FRAGMENT_ARGS_DOCTOR_ID);
        mHospitalId = getArguments().getString(FRAGMENT_ARGS_HOSPITAL_ID);
        mDepartmentId = getArguments().getString(FRAGMENT_ARGS_DEPARTMENT_ID);
        business = getArguments().getString(FRAGMENT_ARGS_BUSINESS);
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(mTitle);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Doctor>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DoctorViewHolder(parent, Glide.with(DoctorsDisplayFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
            Intent intent = new Intent();
            intent.putExtra(KEY_DOCTOR, mAdapter.getItem(position));
            _mActivity.setResult(RESULT_OK, intent);
            _mActivity.finish();
        });

        if (!TextUtils.isEmpty(mDoctorId)) {
            loadDataByDoctorId();
        } else if (!TextUtils.isEmpty(mHospitalId) && !TextUtils.isEmpty(mDepartmentId)) {
            loadDataByHosipitalIdAndDepartmentId();
        }else {
            if (mDoctors == null || mDoctors.isEmpty()) {
                mRecyclerView.showEmpty();
            } else {
                mAdapter.addAll(mDoctors);
            }
        }
    }

    private void loadDataByDoctorId() {
        ApiFactory.getClinicManagerApi().getFavoriteDoctors(mDoctorId,"Consultation")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {
                        showRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void loadDataByHosipitalIdAndDepartmentId() {
        ApiFactory.getClinicManagerApi().getDoctorsByHospital(mHospitalId, mDepartmentId,business)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {
                        showRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
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
            mRecyclerView.showEmpty();
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(doctors);
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private class DoctorViewHolder extends BaseViewHolder<Doctor> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mPositionTitleView;
        private TextView mDepartmentView;
        private TextView mHospitalView;
        private TextView mGoodAtView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public DoctorViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.comm_item_doctor);
            mGlide = glide;

            mAvatarView = $(R.id.iv_doctor_avatar);
            mNameView = $(R.id.tv_doctor_name);
            mPositionTitleView = $(R.id.tv_position_title);
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
            mNameView.setText(data.getName());
            mPositionTitleView.setText(data.getPositionTitle().getPositionTitleName());
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
