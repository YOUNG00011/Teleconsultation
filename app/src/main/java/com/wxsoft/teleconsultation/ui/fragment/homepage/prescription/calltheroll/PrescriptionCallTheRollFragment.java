package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.BusinessType;
import com.wxsoft.teleconsultation.entity.Diagnosis;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.Photo;
import com.wxsoft.teleconsultation.entity.prescription.OnlinePrescription;
import com.wxsoft.teleconsultation.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.SelectMedicalFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.PrimaryDiagnosisFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine.select.PrescriptionSelectManageFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 会诊申请
 */
public class PrescriptionCallTheRollFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, PrescriptionCallTheRollFragment.class, null);
    }

    public static void launch(Activity from, OnlinePrescription clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, clinic);
        FragmentContainerActivity.launch(from, PrescriptionCallTheRollFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_CLINIC = "FRAGMENT_ARGS_CLINIC";

    @BindView(R.id.tv_select_patient)
    TextView mSelectPatientView;

    @BindView(R.id.ll_patient)
    LinearLayout mPatientView;

    @BindView(R.id.ll_doctor)
    LinearLayout mDoctorView;

    @BindView(R.id.tv_preliminary_diagnosis)
    TextView mPreliminaryDiagnosisView;

    @BindView(R.id.et_clinic_explain)
    EditText mClinicExplainView;


    @BindView(R.id.med)
    TextView medType;

    @BindView(R.id.tv_add_med)
    TextView mAddMed;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mPhotoRecyclerView;

    private OnlinePrescription mClinic;

    // 患者
    private Patient mPatient;
    // 疾病
    private Diagnosis mDiagnosis;


    @OnClick(R.id.tv_select_patient)
    void clickPatient() {
        SelectPatientFragment.launch(this,BusinessType.PRESCRIPTION);
    }

    @OnClick(R.id.rl_preliminary_diagnosis)
    void diagnosisClick() {
        PrimaryDiagnosisFragment.launch(this);
    }

    @OnClick(R.id.med_type)
    void medTypeClick() {
        SelectMedicalFragment.launch(this);
    }

    @OnClick(R.id.tv_add_med)
    void medSelectClick() {
        PrescriptionSelectManageFragment.launch(_mActivity);
    }

//    @OnClick(R.id.btn_save)
//    void saveClick() {
//        commit();
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prescription_call_the_roll;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mClinic = (OnlinePrescription) getArguments().getSerializable(FRAGMENT_ARGS_CLINIC);
        }

        setupToolbar();
        if (mClinic != null) {

        }else{
            ((TextView) ButterKnife.findById(view, R.id.tv_position)).setText(AppContext.getUser().getHospitalName()+"  "+
              AppContext.getUser().getDepartmentName());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectPatientFragment.REQUEST_SELECT_PATIENT ||
                    requestCode == PatientSearchFragment.REQUEST_SEARCH_PATIENT ||
                    requestCode == PatientAddFragment.REQUEST_ADD_PATIENT) {
                if (data != null) {
                    if (mSelectPatientView.getVisibility() == View.VISIBLE) {
                        mSelectPatientView.setVisibility(View.GONE);
                    }


                    Patient patient = (Patient) data.getSerializableExtra(SelectPatientFragment.KEY_PATIENT);

                    updatePatientView(patient);
                }
            } else if (requestCode == PrimaryDiagnosisFragment.REQUEST_SELECT_PRIMARY_DIAGNOSIS) {
                if (data != null) {
                    mDiagnosis = (Diagnosis) data.getSerializableExtra(PrimaryDiagnosisFragment.KEY_SELECTED_DISEASE);
                    mPreliminaryDiagnosisView.setText(mDiagnosis.getName());
                }
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                if (data != null) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    ArrayList<String> photos = new ArrayList<>();
                    for (LocalMedia localMedia : selectList) {
                        photos.add(localMedia.getPath());
                    }
                    if (!photos.isEmpty()) {
                        SelectPhotoCategoryActivity.launch(this, photos);
                    }
                }
            }else if (requestCode == SelectMedicalFragment.REQUEST_SELECT_MEDICALTYPE) {
                if (data != null) {
                    medType.setText(data.getStringExtra(SelectMedicalFragment.KEY_MED_TYPE));
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_prescription_title);
    }

    private void processResponse(BaseResp<List<EMRTab>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<EMRTab> emrTabs = resp.getData();
        if (emrTabs == null || emrTabs.isEmpty()) {
            ViewUtil.showMessage("获取类型失败");
            return;
        }

        App.mEMRTabs.addAll(emrTabs);
    }

    private void updatePatientView(Patient patient) {
        mPatient = patient;

        if (mSelectPatientView.getVisibility() == View.VISIBLE) {
            mSelectPatientView.setVisibility(View.GONE);
        }

        if (mPatientView.getChildCount() != 0) {
            mPatientView.removeAllViews();
        }

        View patientView = LayoutInflater.from(_mActivity).inflate(R.layout.item_create_clinic_patient, null);
        ImageView avatarView = ButterKnife.findById(patientView, R.id.iv_patient_avatar);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.setMargins(0, marginTop, marginRight, 0);
        avatarView.setLayoutParams(params);

        avatarView.setImageResource(mPatient.getAvatarDrawableRes());
        ((TextView) ButterKnife.findById(patientView, R.id.tv_patient_name)).setText(mPatient.getName());
        ((TextView) ButterKnife.findById(patientView, R.id.tv_gender)).setText(mPatient.getFriendlySex());
        ((TextView) ButterKnife.findById(patientView, R.id.tv_age)).setText(String.valueOf(mPatient.getAge()));
        ((TextView) ButterKnife.findById(patientView, R.id.tv_health)).setText(mPatient.getMedicalInsuranceName());
        ButterKnife.findById(patientView, R.id.tv_emr).setVisibility(View.GONE);
        LinearLayout mTagView = ButterKnife.findById(patientView, R.id.ll_tag);

        if(mPatient.getPatientTags()!=null) {
            for (PatientTag patientTag : mPatient.getPatientTags()) {
                mTagView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
            }
        }

        ButterKnife.findById(patientView, R.id.iv_arrow_right).setVisibility(View.VISIBLE);

        patientView.setOnClickListener(view -> {
            SelectPatientFragment.launch(this,BusinessType.CONSULTATION);
        });

        mPatientView.addView(patientView);
    }

    private void commit() {
        if (mPatient == null) {
            ViewUtil.showMessage("请选择需要会诊的患者");
            return;
        }
        String patientId = mPatient.getId();
        String patientName = mPatient.getName();

    }

    private static class ClinicPhotoViewHolder extends BaseViewHolder<Photo> {

        interface OnPhotoRemoveListener {

            void remove(String localPath);
        }

        private ImageView mPhotoView;
        private ImageView mRemoveView;
        private TextView mCategoryView;

        private RequestManager mGlide;
        private int mImageSize;
        private RequestOptions options;

        private OnPhotoRemoveListener mOnPhotoRemoveListener;

        public ClinicPhotoViewHolder(ViewGroup parent, Context context, RequestManager requestManager, int columnNumber, OnPhotoRemoveListener onPhotoRemoveListener) {
            super(parent, R.layout.item_clinic_photo);
            mPhotoView = $(R.id.iv_photo);
            mRemoveView = $(R.id.iv_remove);
            mCategoryView = $(R.id.tv_category);

            this.mGlide = requestManager;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            mImageSize = widthPixels / columnNumber;

            options = new RequestOptions();
            options.centerCrop()
                    .dontAnimate()
                    .override(mImageSize, mImageSize);

            mOnPhotoRemoveListener = onPhotoRemoveListener;
        }

        @Override
        public void setData(Photo data) {
            super.setData(data);
            if (TextUtils.isEmpty(data.getLocalPath())) {
                mPhotoView.setImageResource(R.drawable.ic_add_photo);
                mRemoveView.setVisibility(View.GONE);
                mCategoryView.setVisibility(View.GONE);
            } else {
                mGlide.setDefaultRequestOptions(options)
                        .load(new File(data.getLocalPath()))
                        .thumbnail(0.5f)
                        .into(mPhotoView);

                mRemoveView.setVisibility(View.VISIBLE);
                mCategoryView.setVisibility(View.VISIBLE);
                mCategoryView.setText(data.getCategory());

                mRemoveView.setOnClickListener(v -> {
                    if (mOnPhotoRemoveListener != null) {
                        mOnPhotoRemoveListener.remove(data.getLocalPath());
                    }
                });
            }
        }
    }
}
