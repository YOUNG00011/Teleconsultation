package com.wxsoft.telereciver.ui.fragment.homepage.clinic.centre;

import android.app.Activity;
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
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectDoctorFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectDoctorFragment.class, null, REQUEST_SELECT_DOCTOR);
    }

    public static void launch(Activity from) {
        FragmentContainerActivity.launchForResult(from, SelectDoctorFragment.class, null, REQUEST_SELECT_DOCTOR);
    }

    public static final int REQUEST_SELECT_DOCTOR = 40;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    private static final String DEFAULT_HOSPITAL = "全部医院";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.tv_department)
    TextView mDepartmentView;

    @BindView(R.id.iv_remove)
    ImageView mRemoveView;

    private RecyclerArrayAdapter<Doctor> mAdapter;

    @OnClick(R.id.iv_remove)
    void removeClick() {
        updateHospitalView(DEFAULT_HOSPITAL);
    }

    @OnClick(R.id.tv_toggle)
    void toggleClick() {
//        SelectHospitalFragment.launch(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_doctor_centre;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
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
                SearchDoctorFragment.launch(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectHospitalFragment.REQUEST_SELECT_HOSPITAL) {
                if (data != null) {
//                    Hospital hospital = (Hospital) data.getSerializableExtra(SelectHospitalFragment.KEY_HOSPITAL);
//                    Department department = (Department) data.getSerializableExtra(SelectHospitalFragment.KEY_DEPARTMENT);
//                    updateHospitalView(hospital.getName() + department.getName());
//                    searchDoctor(hospital.getId(), department.getId());
                }
            } else if (requestCode == SearchDoctorFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(SearchDoctorFragment.KEY_DOCTOR);
                    finish(doctor);
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("选择医生");
        setHasOptionsMenu(true);
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

        mRecyclerView.showEmpty();
    }

    private void updateHospitalView(String hospital) {
        mDepartmentView.setText(hospital);
        mRemoveView.setVisibility(DEFAULT_HOSPITAL.equals(hospital) ? View.GONE : View.VISIBLE);
    }

    private void searchDoctor(String hospitalId, String departmentId) {
        mRecyclerView.showProgress();
        ApiFactory.getClinicManagerApi().getDoctorsByHospital(hospitalId, departmentId,"Consultation")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.showError();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {
                        processSearchResponse(resp);
                    }
                });
    }

    private void processSearchResponse(BaseResp<List<Doctor>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<Doctor> doctors = resp.getData();
        if (doctors == null || doctors.isEmpty()) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(doctors);
    }

    private void finish(Doctor doctor) {
        Intent intent = new Intent();
        intent.putExtra(KEY_DOCTOR, doctor);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class DoctorViewHolder extends BaseViewHolder<Doctor> {

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
