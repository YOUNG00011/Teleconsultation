package com.wxsoft.teleconsultation.ui.fragment.user.service;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.BusinessSetting;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.cloudclinc.ClincSchedulingTime;
import com.wxsoft.teleconsultation.event.UserServiceEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserServiceCloudClincDetailFragment extends BaseFragment {

    private String mDoctor;

    public static final String KEY_JSON="KEY_JSON";
    public static final String KEY_ACTION="KEY_ACTION";
    private BusinessSetting setting;

    List<ClincSchedulingTime> times;


    private User mUser;
    public static void launch(Activity from,int action,String json) {

        FragmentArgs args=new FragmentArgs();
        args.add(KEY_ACTION,action);
        args.add(KEY_JSON,json);
        FragmentContainerActivity.launch(from, UserServiceCloudClincDetailFragment.class, args);
    }



    @BindView(R.id.checked)
    SwitchCompat check;

    @BindView(R.id.detail)
    LinearLayout detail;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cloudclinc_setting;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        String json=getArguments().getString(KEY_JSON);

        mUser=AppContext.getUser();
        if(json!=null) {
            setting = (new Gson()).fromJson(json, BusinessSetting.class);

//
//            Collections.sort(times, (obj1, obj2) -> obj1.weekDay.compareTo(obj2.weekDay));
        }else{
            setting=new BusinessSetting();
            setting.doctorId=mUser.getDoctId();
            setting.doctorName=mUser.getName();
        }
        mDoctor =AppContext.user.getDoctId();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.service_setting);
    }

    private void setupRecyclerView() {

        if(setting==null)
            setting=new BusinessSetting();
        check.setChecked(setting.isCloudClinc);
        detail.setVisibility(setting.isCloudClinc?View.VISIBLE:View.GONE);

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            detail.setVisibility(check.isChecked()?View.VISIBLE:View.GONE);
            setting.isCloudClinc=check.isChecked();
            saveCost();
        });




        getWeeks();
    }

    private void init(){
        if(times!=null) {
            for (ClincSchedulingTime time : times) {
                setTimeShow(time);
            }
        }

    }

    private void setTimeShow(ClincSchedulingTime time) {
        if (time.weekDay.equals("150-0001")) {

            tv1.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0002")) {
            tv2.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0003")) {
            tv3.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0004")) {
            tv4.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0005")) {
            tv5.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0006")) {
            tv6.setText(time.startTime + "~" + time.endTime);
        } else if (time.weekDay.equals("150-0007")) {
            tv7.setText(time.startTime + "~" + time.endTime);
        }

    }

    private void saveCost(){
        ApiFactory.getCommApi().setBusinessSetting(setting)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<BusinessSetting>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<BusinessSetting> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            setting=resp.getData();
                            EventBus.getDefault().post(new UserServiceEvent());
                            setupRecyclerView();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void getWeeks(){
        ApiFactory.getCommApi().getBusinessSettingWeek(mUser.getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<ClincSchedulingTime>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<ClincSchedulingTime>> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            times=resp.getData();
                            init();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void saveTime(){
        ApiFactory.getCommApi().saveBusinessTime(times)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {

                            EventBus.getDefault().post(new UserServiceEvent());
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }


    /**
     * 时间相关
     */
    
    @BindView(R.id.tv_1)
    TextView tv1;

    @BindView(R.id.tv_2)
    TextView tv2;

    @BindView(R.id.tv_3)
    TextView tv3;

    @BindView(R.id.tv_4)
    TextView tv4;

    @BindView(R.id.tv_5)
    TextView tv5;

    @BindView(R.id.tv_6)
    TextView tv6;

    @BindView(R.id.tv_7)
    TextView tv7;

    @BindView(R.id.l1)
    LinearLayout l1;

    @BindView(R.id.l2)
    LinearLayout l2;

    @BindView(R.id.l3)
    LinearLayout l3;

    @BindView(R.id.l4)
    LinearLayout l4;

    @BindView(R.id.l5)
    LinearLayout l5;

    @BindView(R.id.l6)
    LinearLayout l6;

    @BindView(R.id.l7)
    LinearLayout l7;

    @OnClick(R.id.l1)
    void change1(){

        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0001")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0001";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_1);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);
    }

    @OnClick(R.id.l2)
    void change2(){
        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0002")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }

        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0002";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_2);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);
    }


    @OnClick(R.id.l3)
    void change3(){

        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0003")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0003";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_3);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);
    }

    @OnClick(R.id.l4)
    void change4(){

        if (setting==null)return;
        ClincSchedulingTime dtime = null;

        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0004")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0004";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_4);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);

    }

    @OnClick(R.id.l5)
    void change5(){

        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0005")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0005";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_5);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);
    }

    @OnClick(R.id.l6)
    void change6(){

        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0006")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0006";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_6);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);

    }

    @OnClick(R.id.l7)
    void change7(){
        if (setting==null)return;
        ClincSchedulingTime dtime = null;
        if(times!=null) {
            for (ClincSchedulingTime inlist_time : times) {
                if (inlist_time.weekDay.equals("150-0007")) {
                    dtime = inlist_time;
                    break;
                }
            }
        }
        if(dtime==null){
            dtime=new ClincSchedulingTime();
            dtime.weekDay="150-0007";
            dtime.doctorId=mUser.getDoctId();
            dtime.doctorName=mUser.getName();
            dtime.weekDayName=getString(R.string.week_day_7);
            dtime.startTime="00:00";
            dtime.endTime="00:00";
            times.add(dtime);
        }
        showTimePicker(dtime);

    }


    private void showTimePicker(ClincSchedulingTime time) {
        String[] times=new String[2];
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                    String minuteString = minute < 10 ? "0" + minute : "" + minute;
                    // 时间
                    times[0]= hourString + ":" + minuteString ;

                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );

        dialog.setOnDismissListener(dialog12 -> {
            TimePickerDialog dialog1 = TimePickerDialog.newInstance(
                    (view1, hourOfDay1, minute1, second1) -> {
                        String hourString1 = hourOfDay1 < 10 ? "0" + hourOfDay1 : "" + hourOfDay1;
                        String minuteString1 = minute1 < 10 ? "0" + minute1 : "" + minute1;
                        // 时间
                        times[1]= hourString1 + ":" + minuteString1 ;
                    },

                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );

            dialog1.setOnDismissListener(dialog2 -> {
                if(times[0]!=null) {
                    if(times[1]!=null) {
                        if(times[0].compareTo(times[1])>=0) {
                            ViewUtil.showMessage("开始时间不要比结束时间早");
                        }else {
                            time.startTime=times[0];
                            time.endTime=times[1];
                            setTimeShow(time);
                            saveTime();
                        }
                    }else {
                        if(times[0].compareTo(time.endTime)>=0) {
                            ViewUtil.showMessage("开始时间不要比结束时间早");
                        }else {
                            time.startTime=times[0];
                            setTimeShow(time);
                            saveTime();
                        }
                    }
                }else {

                    if(times[1]!=null) {
                        if(time.startTime.compareTo(times[1])>=0) {
                            ViewUtil.showMessage("开始时间不要比结束时间早");
                        }else {
                            time.endTime=times[1];
                            setTimeShow(time);
                            saveTime();
                        }
                    }
                }
            });
            dialog1.show(_mActivity.getFragmentManager(), "TimePickerDialog");


        });

        dialog.show(_mActivity.getFragmentManager(), "TimePickerDialog");

    }
}
