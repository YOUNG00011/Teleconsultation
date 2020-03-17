package com.wxsoft.telereciver.ui.fragment.homepage.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.entity.register.ScheduDateItem;
import com.wxsoft.telereciver.entity.register.ScheduDateMap;
import com.wxsoft.telereciver.event.UpdateRegisterEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterSureFragment extends BaseFragment {


    private RequestManager mGlide;
    private RequestOptions mOptions ;
    Patient patient;

    public static void launch(Fragment from, ScheduDateMap map, ScheduDateItem item) {
        FragmentArgs args = new FragmentArgs();
        args.add(KEY_DOCTOR, new Gson().toJson(map));
        args.add(SHEDUINGDATE, new Gson().toJson(item));
        FragmentContainerActivity.launchForResult(from, RegisterSureFragment.class, args, REQUEST_SURE_SHEDUINGDATE);
    }




    @BindView(R.id.iv_avatar)
    ImageView avatar;
    @BindView(R.id.tv_name)
    TextView regDocName;
    @BindView(R.id.tv_dept)
    TextView regDocDept;
    @BindView(R.id.tv_hospital)
    TextView regDocHospital;
    @BindView(R.id.tv_select_dept)
    TextView regDept;
    @BindView(R.id.tv_select_doc)
    TextView regDoc;
    @BindView(R.id.tv_select_time)
    TextView regTime;
    @BindView(R.id.tv_select_money)
    TextView regPrice;
    @BindView(R.id.tv_select_patient)
    TextView regPatient;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.patient_select)
    LinearLayout linPatient;
    @BindView(R.id.null_select)
    LinearLayout nullPatient;
    @BindView(R.id.l_patiment)
    RelativeLayout lPatient;


    @BindView(R.id.iv_patient_avatar)
    ImageView mAvatarView;
    @BindView(R.id.tv_patient_name)
    TextView mNameView;
    @BindView(R.id.tv_health)
    TextView mHealthView;
    @BindView(R.id.tv_gender)
    TextView mGenderView;
    @BindView(R.id.tv_age)
    TextView mAgeView;
    @BindView(R.id.ll_tag)
    LinearLayout mTagsView;

    public static final int REQUEST_SURE_SHEDUINGDATE = 69;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    public static final String KEY_WORK_DONE = "KEY_WORK_DONE";
    public static final String SHEDUINGDATE = "SHEDUING_DATE";

    private ScheduDateMap map;
    private ScheduDateItem item;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_the_roll;
    }


    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.register_new_title);
        setHasOptionsMenu(true);
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        setupToolbar();
        String smap = getArguments().getString(KEY_DOCTOR);
        if (smap != null) {
            map = (new Gson()).fromJson(smap, ScheduDateMap.class);
        }
        String date_day = getArguments().getString(SHEDUINGDATE);

        if (date_day != null) {
            item = (new Gson()).fromJson(date_day, ScheduDateItem.class);
        }
        setupView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupView() {
        mGlide = Glide.with(this);
        regDept.setText(map.doctor.departmentName);
        regDoc.setText(map.doctor.name);
        regDocName.setText(map.doctor.name);
        regDocDept.setText(map.doctor.departmentName);
        regDocHospital.setText(map.doctor.hospitalName);
        regTime.setText(item.schedulingDate.substring(0, 10) + " " + item.schedulingStage);
        regPrice.setText(map.scheduTypeName + " ¥" + String.valueOf(item.registerFee));
        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .error(map.doctor.sex == "0" ? R.drawable.ic_doctor_women : R.drawable.ic_doctor_man)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        mGlide.setDefaultRequestOptions(mOptions)
                .load(map.doctor.userImgUrl)
                .into(avatar);
        linPatient.setOnClickListener(v -> {
            SelectPatientFragment.launch(this, BusinessType.REGISTER,false);
        });

        btnSave.setOnClickListener(v -> {
            if (patient == null) {
                Toast.makeText(getContext(), R.string.toast_no_patient,Toast.LENGTH_SHORT).show();
            } else {
                ApiFactory.getRegisterApi().saveScheding(item.id, AppContext.getUser().getDoctId(), patient.getId())
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

                                    EventBus.getDefault().post(new UpdateRegisterEvent());
                                    Intent intent = new Intent();
                                    intent.putExtra(KEY_WORK_DONE,true);
                                    _mActivity.setResult(RESULT_OK, intent);
                                    _mActivity.finish();
                                } else {
                                    ViewUtil.showMessage(resp.getMessage());
                                }

                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectPatientFragment.REQUEST_SELECT_PATIENT ||
                    requestCode == PatientSearchFragment.REQUEST_SEARCH_PATIENT ||
                    requestCode == PatientAddFragment.REQUEST_ADD_PATIENT) {
                if (data != null) {

                    if(nullPatient.getVisibility()==View.VISIBLE){
                        nullPatient.setVisibility(View.GONE);
                        lPatient.setVisibility(View.VISIBLE);
                    }
                    patient = (Patient) data.getSerializableExtra(SelectPatientFragment.KEY_PATIENT);

                    mNameView.setText(patient.getName());
                    mAvatarView.setImageResource(patient.getAvatarDrawableRes());
                    mGenderView.setText(patient.getFriendlySex());
                    mHealthView.setText(patient.getMedicalInsuranceName());
                    mAgeView.setText(String.valueOf(patient.getAge()));

                    mTagsView.removeAllViews();
                    List<PatientTag> patientTags = patient.getPatientTags();
                    if (patientTags == null || patientTags.isEmpty()) {
                        mTagsView.setVisibility(View.INVISIBLE);
                    } else {
                        mTagsView.setVisibility(View.VISIBLE);
                        for (PatientTag patientTag : patientTags) {
                            mTagsView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
                        }
                    }
                }
            }

        }
    }
}
