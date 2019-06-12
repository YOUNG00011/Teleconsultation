package com.wxsoft.teleconsultation.ui.fragment.homepage.cloudclinic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.BusinessType;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.cloudclinc.ClincRecord;
import com.wxsoft.teleconsultation.event.CloudClincEvent;
import com.wxsoft.teleconsultation.event.UpdateRegisterEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.teleconsultation.ui.fragment.homepage.register.RegisterSureFragment.KEY_WORK_DONE;

public class DoctorInfoFragment extends BaseFragment {

    public static void launch(Activity from, Doctor doctor,String type) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_DOCTOR, doctor);
        args.add(FRAGMENTARGS_KEY_TYPE, type);
        FragmentContainerActivity.launch(from, DoctorInfoFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_DOCTOR = "FRAGMENTARGS_KEY_DOCTOR";
    private static final String FRAGMENTARGS_KEY_TYPE = "FRAGMENTARGS_KEY_TYPE";
    private static final int NO_SETUP = 0;
    private static final int NO_FOCUS = 1;
    private static final int HAS_FOCUSED = 2;

    private Doctor mDoctor;

    private Patient patient;

    private String type;
    private int mFocusFlag = NO_SETUP;

    @BindView(R.id.patient_select)
    LinearLayout linPatient;
    @BindView(R.id.null_select)
    LinearLayout nullPatient;
    @BindView(R.id.l_patiment)
    RelativeLayout lPatient;

    @BindView(R.id.btn_save)
    Button btnSave;
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cloudclinc_doctor_info;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mDoctor = (Doctor) getArguments().getSerializable(FRAGMENTARGS_KEY_DOCTOR);
        type =  getArguments().getString(FRAGMENTARGS_KEY_TYPE);
        setupToolbar();
        loadFocusStatus();
        setupDoctorInfoViews(view);

        btnSave.setOnClickListener(v ->

        {
            if (patient == null) {
                Toast.makeText(getContext(), R.string.toast_no_patient, Toast.LENGTH_SHORT).show();
            }else{

              //  Doctor myself=AppContext.getUser().getDoctId()
                ClincRecord record=new ClincRecord();
                record.patientId=patient.getId();
                record.patientName=patient.getName();
                record.applyDoctorId=AppContext.getUser().getDoctId();
                record.applyDoctorName=AppContext.getUser().getName();
                record.acceptDoctorId=mDoctor.getId();
                record.acceptDoctorName=mDoctor.getName();
                record.creatorId=AppContext.getUser().getId();
                record.creatorName=AppContext.getUser().getName();
                record.status="502-0003";
                record.statusName=getResources().getString(R.string.status_502_0003);
                record.appointDate= DateUtil.getChatTimeStr(System.currentTimeMillis());
                record.clincType=type;
                record.clincTypeName=type.equals("501-0001")?"实时门诊":"预约门诊";


                //record.
                ApiFactory.getCloudClinicApi().saveRecord(record)
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

                                    EventBus.getDefault().post(new CloudClincEvent());
                                    Intent intent = new Intent();
                                    intent.putExtra(KEY_WORK_DONE, true);
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

                    if (nullPatient.getVisibility() == View.VISIBLE) {
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

//    @Override
//    public void onCreateOptionsMenu(MenuItem MenuItem, MenuInflater inflater) {
//        inflater.inflate(R.MenuItem.menu_one,MenuItem);
//        MenuItem menuItem = MenuItem.findItem(R.id.action);
//        boolean isMe = mDoctor.getId().equals(AppContext.getUser().getDoctId());
//        if (isMe) {
//            menuItem.setVisible(false);
//        } else {
//            menuItem.setVisible(true);
//            String title = "";
//            if (mFocusFlag == NO_FOCUS) {
//                title = getString(R.string.doctor_detail_follow);
//            } else if (mFocusFlag == HAS_FOCUSED) {
//                title = getString(R.string.doctor_detail_unfollow);
//            }
//            menuItem.setTitle(title);
//        }
//        super.onCreateOptionsMenu(MenuItem, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action:
//                if (mFocusFlag != NO_SETUP) {
//                    commit();
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = String.format(getString(R.string.doctor_detail_title), mDoctor.getName());
        activity.getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    private void loadFocusStatus() {
        ApiFactory.getClinicManagerApi().getAttentionStatus(mDoctor.getId(), AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResp -> {
                    if (baseResp.isSuccess()) {
                        boolean isFocus = baseResp.getData();
                        if (isFocus) {
                            mFocusFlag = HAS_FOCUSED;
                        } else {
                            mFocusFlag = NO_FOCUS;
                        }
                        _mActivity.invalidateOptionsMenu();
                    }
                });
    }

    private void setupDoctorInfoViews(View view) {
        RequestManager glide = Glide.with(DoctorInfoFragment.this);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .error(mDoctor.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        RoundedImageView avatarView = ButterKnife.findById(view, R.id.riv_avatar);
        avatarView.setOval(true);
        glide.setDefaultRequestOptions(options)
                .load(mDoctor.getUserImgUrl())
                .thumbnail(0.5f)
                .into(avatarView);

        ((TextView) ButterKnife.findById(view, R.id.tv_name)).setText(mDoctor.getName());
        ((TextView) ButterKnife.findById(view, R.id.tv_hospital_and_department)).setText(mDoctor.getHospitalName() + "    " + mDoctor.getDepartmentName());
        TextView educationAndYearworkView = ButterKnife.findById(view, R.id.tv_education_and_yearwork);
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mDoctor.getEducationName())) {
            sb.append(String.format(getString(R.string.doctor_detail_education), mDoctor.getEducationName()));
        }

        int yearWork = (int) mDoctor.getYearWork();
        if (yearWork > 0) {
            if (sb.length() > 0) {
                sb.append("    ");
            }
            sb.append(String.format(getString(R.string.doctor_detail_work_year), yearWork));
        }

        if (sb.length() > 0) {
            educationAndYearworkView.setVisibility(View.VISIBLE);
            educationAndYearworkView.setText(sb.toString());
        } else {
            educationAndYearworkView.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(mDoctor.getGoodAt())) {
            TextView goodatView = ButterKnife.findById(view, R.id.tv_goodat);
            goodatView.setVisibility(View.VISIBLE);
            goodatView.setText(mDoctor.getGoodAt());
        }

        linPatient.setOnClickListener(v -> {
            SelectPatientFragment.launch(this, BusinessType.CLOUDCLINC, false);
        });

    }




}
