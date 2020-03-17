package com.wxsoft.telereciver.ui.fragment.homepage.live;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.HWDeviceAccount;
import com.wxsoft.telereciver.entity.diseasecounseling.DiseaseCounseling;
import com.wxsoft.telereciver.event.PatientOrTagChangedEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll.SelectDoctorFragment;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OnLineDetailFragment extends BaseFragment {

    public static final String EXTRA_DISEASECOUNSELING_ID = "EXTRA_DISEASECOUNSELING_ID";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private String diseaseCounselingId;
    private DiseaseCounseling diseaseCounseling;
    public static void launch(Activity from,String diseaseCounselingId) {
        FragmentArgs args = new FragmentArgs();
        args.add(EXTRA_DISEASECOUNSELING_ID, diseaseCounselingId);
        FragmentContainerActivity.launch(from, OnLineDetailFragment.class,args);
    }

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.ll_root)
    LinearLayout mRootView;

    @BindView(R.id.tv_status)
    TextView mStatusView;

    @BindView(R.id.l_patiment)
    RelativeLayout lPa;

    @BindView(R.id.iv_patient_avatar)
    ImageView mPatientAvatarView;

    @BindView(R.id.tv_patient_name)
    TextView mPatientNameView;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_age)
    TextView mAgeView;

    @BindView(R.id.tv_health)
    TextView mHealthView;

    @BindView(R.id.ll_tag)
    LinearLayout mTagView;

    @BindView(R.id.tv_diagnosis)
    TextView mDiagnosisView;

    @BindView(R.id.rl_video_device)
    RelativeLayout mVideoDeviceRootView;

    @BindView(R.id.tv_video_device)
    TextView mVideoDeviceView;

    @BindView(R.id.iv_video_device_remove)
    ImageView mVideoDeviceRemoveView;

    @BindView(R.id.rl_cancel_reason)
    RelativeLayout mCancelReasonLayout;

    @BindView(R.id.tv_cancel_reason)
    TextView mCancelReasonView;


    @BindView(R.id.ll_single_action)
    LinearLayout mSingleActionLayout;

    @BindView(R.id.tv_single_action)
    TextView mSingleActionView;

    @BindView(R.id.ll_4th_action)
    LinearLayout mDoubleActionLayout;

    @BindView(R.id.tv_double_action_1)
    TextView mDoubleAction1View;

    @BindView(R.id.tv_double_action_2)
    TextView mDoubleAction2View;

//    @BindView(R.id.tv_double_action_3)
//    TextView mDoubleAction3View;
//
//    @BindView(R.id.tv_double_action_4)
//    TextView mDoubleAction4View;
    private String mClinicId;
    private boolean isMyClinic;

    private List<HWAccount> mHWAccounts;
    private HWDeviceAccount mHWDeviceAccount;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diseasecounseling_detail;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        diseaseCounselingId=getArguments().getString(EXTRA_DISEASECOUNSELING_ID);
        setupToolbar();
        mGlide = Glide.with(this);


        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);
        loadData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof PatientOrTagChangedEvent) {
           // loadData();
        }
    }

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {
        SelectDoctorFragment.launch(this, BusinessType.COUNSELING);
    }

    @OnClick(R.id.tv_double_action_2)
    void doubleAction2Click() {

    }
//

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);
    }

    Menu themenu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phonecall,menu);
        themenu=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void prepare()
    {
        MenuItem call = themenu.findItem(R.id.call);
        MenuItem text = themenu.findItem(R.id.text);


            if (diseaseCounseling.type.equals("302-0001")) {
                text.setVisible(true);
            } else {
                call.setVisible(true);
            }

    }

    private void loadData() {
        showRefreshing(true);
        ApiFactory.getDiseaseCounselingApi().getDetail(diseaseCounselingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<DiseaseCounseling>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<DiseaseCounseling> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            diseaseCounseling = resp.getData();
                            prepare();
                            if (diseaseCounseling.doctor.getId().equals(AppContext.getUser().getDoctId())) {
                                isMyClinic = false;
                            } else {
                                isMyClinic = true;
                            }
                            setupViews();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void showRefreshing(final boolean refresh) {
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(refresh);
        });
    }

    private void setupViews() {
        ((FragmentContainerActivity)getActivity()).getSupportActionBar().setTitle(diseaseCounseling.typeName);
        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
            mGlide.setDefaultRequestOptions(mOptions.error(diseaseCounseling.patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                    .load(diseaseCounseling.patient.avatar)
                    .into(mPatientAvatarView);
            mGenderView.setText(diseaseCounseling.patient.getFriendlySex());
            mPatientNameView.setText(diseaseCounseling.patient.getName());
            mAgeView.setText(String.valueOf(diseaseCounseling.patient.getAge()));
            mDiagnosisView.setText(diseaseCounseling.describe);
            mStatusView.setText(diseaseCounseling.statusName);
             if(diseaseCounseling.status.equals("303-0004")
                     || diseaseCounseling.status.equals("303-0005")){
                mCancelReasonLayout.setVisibility(View.VISIBLE);
            }else{
//                mDoubleActionLayout.setVisibility(View.VISIBLE);
//                mDoubleAction1View.setText(R.string.str_recommend);
//                mDoubleAction2View.setText(R.string.str_transfer_treatment);
            }

            lPa.setOnClickListener(v-> PatientDetailActivity.launch(_mActivity,diseaseCounseling.patient));
        }

    }
}
