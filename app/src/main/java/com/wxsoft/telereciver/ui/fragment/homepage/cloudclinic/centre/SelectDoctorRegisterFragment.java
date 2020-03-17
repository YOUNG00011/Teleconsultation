package com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.centre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.entity.cloudclinc.CloudClincOnDutyDoctor;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.DoctorInfoFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectDoctorRegisterFragment extends BaseFragment {

    private int mPage=1;
    private int selected;
    Hospital hospital;
    private List<WeekDay> nextWeeks;

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectDoctorRegisterFragment.class, null, REQUEST_SELECT_DOCTOR);
    }

    public static void launch(Activity from) {
        FragmentContainerActivity.launchForResult(from, SelectDoctorRegisterFragment.class, null, REQUEST_SELECT_DOCTOR);
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

    @BindView(R.id.week_days)
    EasyRecyclerView mTimeRecyclerView;

    private RecyclerArrayAdapter<CloudClincOnDutyDoctor> mAdapter;

    private RecyclerArrayAdapter<WeekDay> adapter;

    @OnClick(R.id.iv_remove)
    void removeClick() {
        updateHospitalView(DEFAULT_HOSPITAL);
    }

    @OnClick(R.id.tv_toggle)
    void toggleClick() {
        com.wxsoft.telereciver.ui.fragment.SelectHospitalFragment.launch(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_doctor_centre;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        nextWeeks=getWeekDays();
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
            if (requestCode == 61) {
                if (data != null) {
                    hospital = (Hospital) data.getSerializableExtra(SelectHospitalFragment.KEY_HOSPITAL);
                    updateHospitalView(hospital.getName());
                    searchDoctor(hospital==null?null:hospital.getId() );
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

        mTimeRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity,7));
        mTimeRecyclerView.setAdapter(adapter=new RecyclerArrayAdapter<WeekDay>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new TimeViewHolder(parent);
            }

            @Override
            public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);

                TimeViewHolder timeViewHolder=(TimeViewHolder)holder;
                assert timeViewHolder!=null;
                if(position==selected){
                    timeViewHolder.mContainer.setBackgroundResource(R.color.colorPrimary);
                    timeViewHolder. mWeek.setTextColor(getResources().getColor(R.color.white));
                    timeViewHolder.mDay.setTextColor(getResources().getColor(R.color.white));
                }else{
                    timeViewHolder.mContainer.setBackgroundResource(R.color.white);
                    timeViewHolder.mWeek.setTextColor(getResources().getColor(R.color.colorPrimary));
                    timeViewHolder.mDay.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
        adapter.setOnItemClickListener(position -> {
            if(selected!=position) {
                selected = position;
                adapter.notifyDataSetChanged();

            }
            searchDoctor(   hospital==null?null:hospital.getId());
           // loadDataByHosipitalIdAndDepartmentId();

        });

        adapter.addAll(nextWeeks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<CloudClincOnDutyDoctor>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DoctorViewHolder(parent, Glide.with(SelectDoctorRegisterFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            finish(mAdapter.getItem(position).doctorInfo);
        });

        mRecyclerView.showEmpty();

        searchDoctor(null);
    }

    private void updateHospitalView(String hospital) {
        mDepartmentView.setText(hospital);
        mRemoveView.setVisibility(DEFAULT_HOSPITAL.equals(hospital) ? View.GONE : View.VISIBLE);
        if(hospital.equals("全部医院")){
            searchDoctor(null);
        }
    }


    private void searchDoctor(String hospitalId) {
        searchDoctor(hospitalId,"");
    }
    private void searchDoctor(String hospitalId,String doctorId) {
        mRecyclerView.showProgress();

        QueryRequestBody body=QueryRequestBody.getOnlineDutyBody(doctorId,nextWeeks.get(selected).date,hospitalId, AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getCloudClinicApi().getDoctorDuties(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<CloudClincOnDutyDoctor>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRecyclerView.showError();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<CloudClincOnDutyDoctor>> resp) {
                        processSearchResponse(resp);
                    }
                });
    }

    private void processSearchResponse(BaseResp<QueryResponseData<CloudClincOnDutyDoctor>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<CloudClincOnDutyDoctor> doctors = resp.getData().getResultData();
        if (doctors == null || doctors.isEmpty()) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(doctors);
    }

    private void finish(Doctor doctor) {
        DoctorInfoFragment.launch(_mActivity,doctor,"501-0002");
    }

    private class DoctorViewHolder extends BaseViewHolder<CloudClincOnDutyDoctor> {

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
        public void setData(CloudClincOnDutyDoctor data) {
            super.setData(data);
            mNameView.setText(data.doctorInfo.getName());
            mDepartmentView.setText(data.doctorInfo.getDepartmentName());
            mHospitalView.setText(data.doctorInfo.getHospitalName());

            String goodAt = "";
            if (!TextUtils.isEmpty(data.doctorInfo.getGoodAt())) {
                goodAt = "擅长:   " + data.doctorInfo.getGoodAt();
            }

            mGoodAtView.setText(goodAt);
            mGlide.setDefaultRequestOptions(mOptions.error(data.doctorInfo.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(data.doctorInfo.getUserImgUrl())
                    .into(mAvatarView);
        }
    }


    class WeekDay{
        public String date;
        public String day;
        public String week;

    }

    private List<WeekDay> getWeekDays(){

        String[] weekdays=new String[]{getString(R.string.sun),getString(R.string.mon),getString(R.string.tue),getString(R.string.wen),getString(R.string.thu),getString(R.string.fri),getString(R.string.san)};
        List<WeekDay> weekDays=new ArrayList<>();
        Calendar now = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd");

        for (int i = 0; i < 7; i++)
        {
            now.add(Calendar.DAY_OF_MONTH, 1);
            WeekDay weekDay=new WeekDay();
            weekDay.date = format.format(now.getTime());
            weekDay.day=format1.format(now.getTime());
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            weekDay.week=weekdays[dayOfWeek-1];
            weekDays.add(weekDay);

        }
        return weekDays;
    }
    private class TimeViewHolder extends BaseViewHolder<WeekDay>{

        private LinearLayout mContainer;
        private TextView mDate;
        private TextView mWeek;
        private TextView mDay;
        public TimeViewHolder(ViewGroup parent) {
            super(parent,R.layout.item_week_date);
            mDate = $(R.id.date);
            mWeek = $(R.id.weekday);
            mDay = $(R.id.time);
            mContainer = $(R.id.container);
        }



        @Override
        public void setData(WeekDay data) {
            super.setData(data);

            mDate.setText(data.date);
            mWeek .setText(data.week);
            mDay .setText(data.day);

        }
    }

}
