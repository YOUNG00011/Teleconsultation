package com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.calltheroll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.BusinessType;
import com.wxsoft.teleconsultation.entity.Clinic;
import com.wxsoft.teleconsultation.entity.Diagnosis;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientEMR;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.Photo;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMent;
import com.wxsoft.teleconsultation.event.TreatMentStateChangeEvent;
import com.wxsoft.teleconsultation.event.UpdateClinicStateEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.teleconsultation.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.DoctorInfoFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.PatientInfoConfirmFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.PrimaryDiagnosisFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.calltheroll.SelectDoctorFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.FileUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 会诊申请
 */
public class TreatmentCallTheRollFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, TreatmentCallTheRollFragment.class, null);
    }

    public static void launch(Activity from, TreatMent clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, clinic);
        FragmentContainerActivity.launch(from, TreatmentCallTheRollFragment.class, args);
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



    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    private TreatMent mClinic;

    // 患者
    private Patient mPatient;
    // 医生集合
    private Doctor mDoctors;
    // 疾病
    private Diagnosis mDiagnosis;

    private RecyclerArrayAdapter<Photo> mPhotoAdapter;
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mEMRTabs;
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private PatientEMR mPatientEMR;

    @OnClick(R.id.tv_select_patient)
    void selectPatientClick() {
        SelectPatientFragment.launch(this,BusinessType.TRANSFERTREATMENT,false);
    }

    @OnClick(R.id.tv_invite_doctor)
    void inviteDoctorClick() {
        SelectDoctorFragment.launch(this, BusinessType.TRANSFERTREATMENT);
    }

    @OnClick(R.id.rl_preliminary_diagnosis)
    void preliminaryDiagnosisClick() {
        PrimaryDiagnosisFragment.launch(this);
    }


    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_treatment_call_the_roll;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        mGlide = Glide.with(TreatmentCallTheRollFragment.this);
        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();
        if (getArguments() != null) {
            mClinic = (TreatMent) getArguments().getSerializable(FRAGMENT_ARGS_CLINIC);

            mDoctors=mClinic.acceptDoctor;
            updateDoctorView();

            mPreliminaryDiagnosisView.setText(mClinic.diagnosis);
        }

        setupToolbar();
        setupPhotoRecyclerView();
        if (App.mEMRTabs.isEmpty()) {
            loadAllEMRTabs();
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
                    mPatientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    String patientEMRName = null;
                    if (mPatientEMR != null) {
                        patientEMRName = mPatientEMR.getName();
                    }
                    updatePatientView(patient, patientEMRName);
                }
            } else if (requestCode == SelectDoctorFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(SelectDoctorFragment.KEY_DOCTOR);

                    if (doctor.getId().equals(AppContext.getUser().getDoctId())) {
                        ViewUtil.showMessage("不能选择自己");
                        return;
                    }


                    mDoctors=doctor;
                    updateDoctorView();
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
            } else if (requestCode == SelectPhotoCategoryActivity.REQUEST_SELECT_CATEGORY) {
                if (data != null) {
                    ArrayList<String> localPaths =
                            data.getStringArrayListExtra(SelectPhotoCategoryActivity.KEY_SELECTED_PHOTOS);
                    String category = data.getStringExtra(SelectPhotoCategoryActivity.KEY_SELECTED_CATEGORY);
                    mPhotoAdapter.clear();
                    for (String localPath : localPaths) {
                        Photo photo = new Photo(localPath, category);
                        mPhotos.add(0, photo);
                    }
                    mPhotoAdapter.addAll(mPhotos);
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_treatment_title);
    }

    private void setupPhotoRecyclerView() {
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity, 4));
        mPhotoRecyclerView.setAdapter(mPhotoAdapter = new RecyclerArrayAdapter<Photo>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ClinicPhotoViewHolder(parent, _mActivity, Glide.with(TreatmentCallTheRollFragment.this), 4, localPath -> {
                    for (Photo photo : mPhotos) {
                        if (localPath.equals(photo.getLocalPath())) {
                            mPhotos.remove(photo);
                            mPhotoAdapter.remove(photo);
                            break;
                        }
                    }
                });
            }
        });

        mPhotoAdapter.setOnItemClickListener(position -> {
            if (TextUtils.isEmpty(mPhotoAdapter.getItem(position).getLocalPath())) {
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            } else {
                if (mEMRTabs == null) {
                    mEMRTabs = new ArrayList<>();
                    for (EMRTab emrTab : App.mEMRTabs) {
                        mEMRTabs.add(emrTab.getNodeType());
                    }
                }
                new MaterialDialog.Builder(_mActivity)
                        .title(R.string.select_picture_type_title)
                        .items(mEMRTabs)
                        .itemsCallback((dialog, view, which, text) -> {
                            Photo targetPhoto = mPhotoAdapter.getAllData().get(position);
                            targetPhoto.setCategory(text.toString());
                            mPhotoAdapter.update(targetPhoto, position);
                            for (Photo photo : mPhotos) {
                                if (photo.getLocalPath().equals(targetPhoto.getLocalPath())) {
                                    photo.setCategory(text.toString());
                                    break;
                                }
                            }
                        })
                        .show();
            }
        });

        mPhotos = new ArrayList<>();
        mPhotos.add(new Photo(null, null));
        mPhotoAdapter.addAll(mPhotos);
    }

    private void loadAllEMRTabs() {
        ApiFactory.getPatientManagerApi().getAllEMRNode("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<EMRTab>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<EMRTab>> resp) {
                        processResponse(resp);
                    }
                });
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

    private void updatePatientView(Patient patient, String emrName) {
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
        LinearLayout mTagView = ButterKnife.findById(patientView, R.id.ll_tag);

        for (PatientTag patientTag : mPatient.getPatientTags()) {
            mTagView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
        }

        ButterKnife.findById(patientView, R.id.iv_arrow_right).setVisibility(View.VISIBLE);

        patientView.setOnClickListener(view -> {
            SelectPatientFragment.launch(this,BusinessType.TRANSFERTREATMENT,false);
        });

        TextView mEMRView = ButterKnife.findById(patientView, R.id.tv_emr);
        if (TextUtils.isEmpty(emrName)) {
            mEMRView.setText(R.string.create_consultation_emr_system_create_text);
        } else {
            mEMRView.setText(String.format(getString(R.string.create_consultation_emr_text), emrName));
        }

        mPatientView.addView(patientView);
    }

    private void updateDoctorView() {
        if (mDoctorView.getChildCount() != 0) {
            mDoctorView.removeAllViews();
        }

        if (mDoctors==null) return;

        LayoutInflater inflater = LayoutInflater.from(_mActivity);

            Doctor doctor = mDoctors;
            View doctorView = inflater.inflate(R.layout.comm_item_doctor, null);
            ImageView mAvatarView = ButterKnife.findById(doctorView, R.id.iv_doctor_avatar);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(0, 0, margin, 0);
            mAvatarView.setLayoutParams(params);

            ImageView avatarView = ButterKnife.findById(doctorView, R.id.iv_doctor_avatar);
            mGlide.setDefaultRequestOptions(mOptions.error(doctor.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(doctor.getUserImgUrl())
                    .into(avatarView);

            ((TextView) ButterKnife.findById(doctorView, R.id.tv_doctor_name)).setText(doctor.getName());
            ((TextView) ButterKnife.findById(doctorView, R.id.tv_department)).setText(doctor.getDepartmentName());
            ((TextView) ButterKnife.findById(doctorView, R.id.tv_hospital)).setText(doctor.getHospitalName());
            ButterKnife.findById(doctorView, R.id.tv_goodat).setVisibility(View.GONE);

            doctorView.setOnClickListener(view -> {
                DoctorInfoFragment.launch(_mActivity, doctor);
            });

            mDoctorView.addView(doctorView);

            View lineView = new View(_mActivity);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            lineView.setLayoutParams(lineParams);
            lineView.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color));

            mDoctorView.addView(lineView);

    }



    private void commit() {
        if (mPatient == null) {
            ViewUtil.showMessage("请选择需要转诊的患者");
            return;
        }
        String patientId = mPatient.getId();
        String patientName = mPatient.getName();

        if (mDoctors == null ) {
            ViewUtil.showMessage("请选择转诊的医生");
            return;
        }


        if (mPreliminaryDiagnosisView.getText().equals("")) {
            ViewUtil.showMessage("请填写初步诊断");
            return;
        }

        String diagnosis = mPreliminaryDiagnosisView.getText().toString();

        String describe = mClinicExplainView.getText().toString().trim();
        if (TextUtils.isEmpty(describe)) {
            ViewUtil.showMessage("请填写会诊说明");
            return;
        }

        if (mPatientEMR == null && (mPhotos == null || mPhotos.isEmpty())) {
            ViewUtil.showMessage("没有选择已经存在的病历则必须添加图片");
            return;
        }

        String applyDoctorId = AppContext.getUser().getDoctId();
        String applyDoctorName = AppContext.getUser().getName();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("PatientId", patientId)
                .addFormDataPart("PatientName", patientName)
                .addFormDataPart("Diagnosis", diagnosis)
                .addFormDataPart("Describe", describe)
                .addFormDataPart("AcceptDoctorId", mDoctors.getId())
                .addFormDataPart("AcceptDoctorName", mDoctors.getName())
                .addFormDataPart("ApplyDoctorId", applyDoctorId)
                .addFormDataPart("TransferType", "601-0002")
                .addFormDataPart("status", "602-0001")
                .addFormDataPart("ApplyDoctorName", applyDoctorName);
        if (mPhotos != null && !mPhotos.isEmpty()) {
            for (int i = 0; i < mPhotos.size() - 1; i++) {
                Photo photo = mPhotos.get(i);
                File file = new File(photo.getLocalPath());
                String fileExtension = FileUtil.getFileExtension(file);
                String fileName = photo.getCategory() + "|" + System.currentTimeMillis() + fileExtension;
                builder.addFormDataPart("file" + i, fileName, RequestBody.create(MediaType.parse("image/*"), file));
            }
        }

        String url = AppConstant.BASE_URL + "api/TransferTreatment/SaveTransferTreatment?isMobile=true";
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())//传参数、文件或者混合，改一下就行请求体就行
                .build();

        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _mActivity.runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    String data = jsonObject.getString("data");
                    TreatMent clinic = new Gson().fromJson(data, TreatMent.class);
                    _mActivity.runOnUiThread(() -> {
                        ViewUtil.dismissProgressDialog();
                        TreatMentStateChangeEvent event=new TreatMentStateChangeEvent();
                        event.treatMent=clinic;
                        EventBus.getDefault().post(event);
                        _mActivity.finish();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
