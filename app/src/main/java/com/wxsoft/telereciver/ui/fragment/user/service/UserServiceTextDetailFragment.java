package com.wxsoft.telereciver.ui.fragment.user.service;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.User;
import com.wxsoft.telereciver.entity.diseasecounseling.Cost;
import com.wxsoft.telereciver.event.UserServiceEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;
import com.wxsoft.telereciver.util.keyboard.TextWatcherAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserServiceTextDetailFragment extends BaseFragment {

    private String mDoctor;

    public static final String KEY_JSON="KEY_JSON";
    public static final String KEY_ACTION="KEY_ACTION";
    private Cost setting;

    boolean allowUpdate=true;


    private User mUser;
    public static void launch(Activity from,int action,String json) {

        FragmentArgs args=new FragmentArgs();
        args.add(KEY_ACTION,action);
        args.add(KEY_JSON,json);
        FragmentContainerActivity.launch(from, UserServiceTextDetailFragment.class, args);
    }



    @BindView(R.id.checked)
    SwitchCompat check;

    @BindView(R.id.price)
    EditText price;
    @BindView(R.id.detail)
    LinearLayout detail;



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_textdisl_setting;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        String json=getArguments().getString(KEY_JSON);

        mUser=AppContext.getUser();
        if(json!=null) {
            setting = (new Gson()).fromJson(json, Cost.class);


            Collections.sort(setting.diseaseCounselingTimes, (obj1, obj2) -> obj1.weekDay.compareTo(obj2.weekDay));
        }else{
            setting=new Cost();
            setting.diseaseCounselingType="302-0001";

            setting.createrId=mUser.getId();
            setting.createrName=mUser.getName();
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

    private void init(){
        if(setting==null)
            setting=new Cost();
        check.setChecked(setting.isEnable);
        detail.setVisibility(setting.isEnable?View.VISIBLE:View.GONE);

        int int_price=(int)setting.amount;
        String new_amount="0";
        if(int_price==setting.amount){
            new_amount = String.valueOf(int_price);
        }else {
            new_amount = String.valueOf(setting.amount);
        }
        if(!price.getText().toString().equals(new_amount)) {
            price.setText(new_amount);
            price.setSelection(new_amount.length());
        }

    }

    private void setupRecyclerView() {

        init();

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {

            detail.setVisibility(check.isChecked()?View.VISIBLE:View.GONE);
            setting.isEnable=check.isChecked();
            if(allowUpdate) {
                saveCost();
            }
        });


        price.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length()==0)return;

                setting.amount=Float.valueOf(s.toString().trim());
                if(allowUpdate) {
                    saveCost();
                }
            }
        });

    }


    private void saveCost(){


            ApiFactory.getDiseaseCounselingApi().saveCost(setting)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResp<Cost>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            allowUpdate = true;
                            ViewUtil.dismissProgressDialog();
                            ViewUtil.showMessage(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseResp<Cost> resp) {
                            allowUpdate = true;
                            ViewUtil.dismissProgressDialog();
                            if (resp.isSuccess()) {
                                setting = resp.getData();
                                EventBus.getDefault().post(new UserServiceEvent());
                                init();
                            } else {
                                ViewUtil.showMessage(resp.getMessage());
                            }
                        }
                    });

            allowUpdate = false;

    }
}
