package com.wxsoft.telereciver.ui.fragment.homepage.register.callandroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
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
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.register.ScheduDateMap;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.register.RegisterSureFragment;
import com.wxsoft.telereciver.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DoctorsDisplayFragment extends BaseFragment {
    private int mPage = 1;

    private int selected;

    public static void launchByHosipitalIdAndDepartmentId(Fragment from, String title, String hospitalId, String departmentId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_HOSPITAL_ID, hospitalId);
        args.add(FRAGMENT_ARGS_DEPARTMENT_ID, departmentId);
        FragmentContainerActivity.launchForResult(from, DoctorsDisplayFragment.class, args, REQUEST_SELECT_DOCTOR);
    }

    private static final String FRAGMENT_ARGS_TITLE= "FRAGMENT_ARGS_TITLE";
    private static final String FRAGMENT_ARGS_DOCTOR_ID = "FRAGMENT_ARGS_DOCTOR_ID";
    private static final String FRAGMENT_ARGS_HOSPITAL_ID = "FRAGMENT_ARGS_HOSPITAL_ID";
    private static final String FRAGMENT_ARGS_DEPARTMENT_ID = "FRAGMENT_ARGS_DEPARTMENT_ID";
    public static final int REQUEST_SELECT_DOCTOR = 66;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";

    private List<WeekDay> nextWeeks;

    @BindView(R.id.recycler)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mTimeRecyclerView;

    private RecyclerArrayAdapter<ScheduDateMap> mAdapter;
    private RecyclerArrayAdapter<WeekDay> adapter;
    private String mTitle;
    private String mDoctorId;
    private String mHospitalId;
    private String mDepartmentId;




    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_doctor_select;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mTitle = getArguments().getString(FRAGMENT_ARGS_TITLE);
        mDoctorId = getArguments().getString(FRAGMENT_ARGS_DOCTOR_ID);
        mHospitalId = getArguments().getString(FRAGMENT_ARGS_HOSPITAL_ID);
        mDepartmentId = getArguments().getString(FRAGMENT_ARGS_DEPARTMENT_ID);
        nextWeeks=getWeekDays();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(mTitle);
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
            loadDataByHosipitalIdAndDepartmentId();

        });

        adapter.addAll(nextWeeks);
        mRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity,3));

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<ScheduDateMap>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MapViewHolder(parent, Glide.with(DoctorsDisplayFragment.this));
            }


        });

        mAdapter.setOnItemClickListener(position ->  {
            SelectTimeFragment.launch(this,mAdapter.getItem(position),nextWeeks.get(selected).date);
//            Intent intent = new Intent();
//            intent.putExtra(KEY_DOCTOR, mAdapter.getItem(position));
//            _mActivity.setResult(RESULT_OK, intent);
//            _mActivity.finish();
        });


        if (!TextUtils.isEmpty(mHospitalId) && !TextUtils.isEmpty(mDepartmentId)) {
            loadDataByHosipitalIdAndDepartmentId();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectTimeFragment.REQUEST_SELECT_SHEDUINGDATE) {
                if (data != null) {
                    boolean doctor = data.getBooleanExtra(RegisterSureFragment.KEY_WORK_DONE, false);
                    Intent intent = new Intent();
                    intent.putExtra(RegisterSureFragment.KEY_WORK_DONE, doctor);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }
    private void loadDataByHosipitalIdAndDepartmentId() {

        String thedate=nextWeeks.get(selected).date;
        QueryRequestBody body=QueryRequestBody.getRegisterScheduDateMapByDate(mDepartmentId,mHospitalId,thedate, AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getRegisterApi().getScheduMapyDate(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<ScheduDateMap>>>() {
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
                    public void onNext(BaseResp<QueryResponseData<ScheduDateMap>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<ScheduDateMap>> resp) {

        if(resp.isSuccess()) {
            List<ScheduDateMap> doctors = resp.getData().getResultData();
            if (doctors == null || doctors.isEmpty()) {
                mRecyclerView.showEmpty();
                return;
            }

            mAdapter.clear();
            mAdapter.addAll(doctors);
        }else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }

    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
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

    private class MapViewHolder extends BaseViewHolder<ScheduDateMap> {

        private ImageView mAvatarView;
        private TextView mScheduName;
        private TextView mNameView;
        private TextView mJob;
        private TextView mResourceNoCount;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public MapViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.item_reg_doctor);
            mGlide = glide;

            mAvatarView = $(R.id.iv_doctor_avatar);
            mScheduName = $(R.id.tv_doctor_schedu_name);
            mNameView = $(R.id.tv_doctor_name);
            mJob = $(R.id.job_name);
            mJob = $(R.id.job_name);
            mResourceNoCount=$(R.id.resource_no_count);
            mOptions = new RequestOptions()
                    .circleCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(ScheduDateMap data) {
            super.setData(data);
            mNameView.setText(data.doctor.name);
            mJob.setText(data.doctor.jobTitleName);
            mScheduName.setText(data.scheduTypeName);
            mResourceNoCount.setText("剩"+String.valueOf(data.resourceNoCount));
            mGlide.setDefaultRequestOptions(mOptions.error(data.doctor.sex.equals("1")? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(data.doctor.userImgUrl)
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
}
