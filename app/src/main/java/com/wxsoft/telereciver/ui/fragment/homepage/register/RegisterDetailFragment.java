package com.wxsoft.telereciver.ui.fragment.homepage.register;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.register.RegisterItem;
import com.wxsoft.telereciver.event.PatientOrTagChangedEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterDetailFragment extends BaseFragment {

    public static final String EXTRA_REGISTER_ID = "EXTRA_DISEASECOUNSELING_ID";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private String registerId;
    private RegisterItem register;
    public static void launch(Activity from,String registerId) {
        FragmentArgs args = new FragmentArgs();
        args.add(EXTRA_REGISTER_ID, registerId);
        FragmentContainerActivity.launch(from, RegisterDetailFragment.class,args);
    }

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;


    @BindView(R.id.tv_status)
    TextView mStatusView;
    @BindView(R.id.root)
    LinearLayout mRoot;

    @BindView(R.id.iv_patient_avatar)
    ImageView mPatientAvatarView;

    @BindView(R.id.l_patiment)
    RelativeLayout lPa;

    @BindView(R.id.tv_patient_name)
    TextView mPatientNameView;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_age)
    TextView mAgeView;

    @BindView(R.id.tv_health)
    TextView mHealthView;

    @BindView(R.id.doc1)
    TextView mDoc1;

    @BindView(R.id.doc2)
    TextView mDoc2;

    @BindView(R.id.hos)
    TextView mHos;

    @BindView(R.id.time)
    TextView mTime;

    @BindView(R.id.cost)
    TextView mCost;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_detail;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        registerId =getArguments().getString(EXTRA_REGISTER_ID);
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


    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.register_detail_title);
        setHasOptionsMenu(true);
    }

    private void loadData() {
        showRefreshing(true);
        ApiFactory.getRegisterApi().getDetail(registerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<RegisterItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<RegisterItem> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            register = resp.getData();

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
        if(mRoot.getVisibility()==View.GONE) {
            mRoot.setVisibility(View.VISIBLE);
            mGlide.setDefaultRequestOptions(mOptions.error(register.patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                    .load(register.patient.avatar)
                    .into(mPatientAvatarView);
            mGenderView.setText(register.patient.getFriendlySex());
            mPatientNameView.setText(register.patient.getName());
            mAgeView.setText(String.valueOf(register.patient.getAge()));
            mStatusView.setText(register.statusName);
            mDoc1.setText(register.registerName);
            mDoc2.setText(register.registerDoctorName);
            mHos.setText(register.registerOrgName);
            mTime.setText(register.visitTime);
            mCost.setText(String.valueOf(register.registerFee));
            mStatusView.setText(register.statusName);

            lPa.setOnClickListener(v-> PatientDetailActivity.launch(_mActivity,register.patient));
        }
    }
}
