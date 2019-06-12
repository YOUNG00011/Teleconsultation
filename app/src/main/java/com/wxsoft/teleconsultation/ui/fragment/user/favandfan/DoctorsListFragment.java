package com.wxsoft.teleconsultation.ui.fragment.user.favandfan;

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
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.DoctorInfoFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoctorsListFragment extends BaseFragment {

    public static void launch(Fragment from, String title, ArrayList<Doctor> doctors) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_DOCTORS, doctors);
        FragmentContainerActivity.launchForResult(from, DoctorsListFragment.class, args, REQUEST_SELECT_DOCTOR);
    }


    private static final String FRAGMENT_ARGS_TITLE= "FRAGMENT_ARGS_TITLE";
    private static final String FRAGMENT_ARGS_DOCTORS = "FRAGMENT_ARGS_DOCTORS";
    public static final int REQUEST_SELECT_DOCTOR = 66;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Doctor> mAdapter;
    private String mTitle;
    private ArrayList<Doctor> mDoctors;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mTitle = getArguments().getString(FRAGMENT_ARGS_TITLE);
        mDoctors = (ArrayList<Doctor>) getArguments().getSerializable(FRAGMENT_ARGS_DOCTORS);
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
                return new DoctorViewHolder(parent, Glide.with(DoctorsListFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
            DoctorInfoFragment.launch(_mActivity, mAdapter.getItem(position));
        });


            if (mDoctors == null || mDoctors.isEmpty()) {
                mRecyclerView.showEmpty();
            } else {
                mAdapter.addAll(mDoctors);
            }

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
