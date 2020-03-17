package com.wxsoft.telereciver.ui.fragment.homepage.clinic.centre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.Clinic;
import com.wxsoft.telereciver.entity.Department;
import com.wxsoft.telereciver.entity.Diagnosis;
import com.wxsoft.telereciver.entity.EMRTab;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientEMR;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.entity.Photo;
import com.wxsoft.telereciver.entity.PositionTitle;
import com.wxsoft.telereciver.event.UpdateClinicStateEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.telereciver.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.PatientInfoConfirmFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.PrimaryDiagnosisFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.telereciver.ui.fragment.SelectPositionTitleFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DateUtil;
import com.wxsoft.telereciver.util.FileUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.eventbus.EventBus;
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

public class ClinicCentreFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, ClinicCentreFragment.class, null);
    }

    public static void launch(Activity from, Clinic clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, clinic);
        FragmentContainerActivity.launch(from, ClinicCentreFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_CLINIC = "FRAGMENT_ARGS_CLINIC";

    @BindView(R.id.tv_select_patient)
    TextView mSelectPatientView;

    @BindView(R.id.ll_patient)
    LinearLayout mPatientView;

//    @BindView(R.id.tv_select_doctor)
//    TextView mSelectDoctorView;

//    @BindView(R.id.ll_doctor)
//    LinearLayout mDoctorView;

    @BindView(R.id.ll_departments)
    LinearLayout mDepartmentsView;

    @BindView(R.id.tv_preliminary_diagnosis)
    TextView mPreliminaryDiagnosisView;

    @BindView(R.id.et_clinic_explain)
    EditText mClinicExplainView;

    @BindView(R.id.tv_time)
    TextView mTimeView;

//    @BindView(R.id.tv_hospital)
//    TextView mHospitalView;

    @BindView(R.id.tv_department)
    TextView mDepartmentView;

    @BindView(R.id.tv_position_title)
    TextView mPositionTitleView;

    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    private Clinic mClinic;

    // 患者
    private Patient mPatient;
//    // 医生
//    private Doctor mDoctor;
//    // 医院
//    private Hospital mHospital;
    // 疾病
    private Diagnosis mDiagnosis;
//    // 期望科室
//    private List<Department> mDepartments;
    private Map<String, List<Department>> mDepartments;

    private RecyclerArrayAdapter<Photo> mPhotoAdapter;
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mEMRTabs;
    // 期望时间
    private String mExpectTime;
    private PositionTitle mCurrentPositionTitle;

    private RequestManager mGlide;
    private RequestOptions mOptions;
    private PatientEMR mPatientEMR;

    @OnClick(R.id.tv_select_patient)
    void selectPatientClick() {
        SelectPatientFragment.launch(this, BusinessType.CONSULTATION);
    }

//    @OnClick(R.id.tv_select_doctor)
//    void selectDoctorClick() {
//        SelectDoctorFragment.launch(this);
//    }

    @OnClick(R.id.rl_preliminary_diagnosis)
    void preliminaryDiagnosisClick() {
        PrimaryDiagnosisFragment.launch(this);
    }

    @OnClick(R.id.rl_time)
    void timeClick() {
        showDatePicker();
    }

//    @OnClick(R.id.rl_hospital)
//    void hospitalClick() {
//        SelectHospitalFragment.launch(this);
//    }

    @OnClick(R.id.rl_department)
    void departmentClick() {
        if (mDepartments == null) {
            mDepartments = new HashMap<>();
        }
        SelectHospitalFragment.launch(this, new Gson().toJson(mDepartments));
    }

    @OnClick(R.id.rl_doctor_title)
    void doctorTitleClick() {
        SelectPositionTitleFragment.launch(this, mCurrentPositionTitle);
    }

    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_clinic_centre;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mClinic = (Clinic) getArguments().getSerializable(FRAGMENT_ARGS_CLINIC);
        }
        mCurrentPositionTitle = PositionTitle.getNoLimitPositionTitle();
        mGlide = Glide.with(ClinicCentreFragment.this);
        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();

        setupToolbar();
        if (mClinic != null) {
            String emrName = null;
            if (mClinic.getPatientEMR() != null) {
                emrName = mClinic.getPatientEMR().getName();
            }
            updatePatientView(mClinic.getPatientInfoDTO(), emrName);
            mClinicExplainView.setText(mClinic.getDescribe());
        }
        setupPhotoRecyclerView();
        if (App.mEMRTabs.isEmpty()) {
            loadAllEMRTabs();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == SelectPatientFragment.REQUEST_SELECT_PATIENT ||
                    requestCode == PatientSearchFragment.REQUEST_SEARCH_PATIENT ||
                    requestCode == PatientAddFragment.REQUEST_ADD_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(SelectPatientFragment.KEY_PATIENT);
                    mPatientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    String patientEMRName = null;
                    if (mPatientEMR != null) {
                        patientEMRName = mPatientEMR.getName();
                    }
                    updatePatientView(patient, patientEMRName);
                }
            } else if (requestCode == SelectHospitalFragment.REQUEST_SELECT_HOSPITAL) {
                if (data != null) {
                    String departmentsJson = data.getStringExtra(SelectHospitalFragment.KEY_DEPARTMENTS);
                    Map<String, List<Department>> map = null;

                    if (!TextUtils.isEmpty(departmentsJson)) {
                        map = new Gson().fromJson(departmentsJson, new TypeToken<Map<String, List<Department>>>() {
                        }.getType());
                    }

                    updateDepartmentsView(map);
                }

            } else if (requestCode == PrimaryDiagnosisFragment.REQUEST_SELECT_PRIMARY_DIAGNOSIS) {
                if (data != null) {
                    mDiagnosis = (Diagnosis) data.getSerializableExtra(PrimaryDiagnosisFragment.KEY_SELECTED_DISEASE);
                    mPreliminaryDiagnosisView.setText(mDiagnosis.getName());
                }
            } else if (requestCode == SelectHospitalFragment.REQUEST_SELECT_HOSPITAL) {
                if (data != null) {
//                   String departmentsJson = data.getStringExtra(SelectHospitalFragment.KEY_DEPARTMENTS);
//                   Map<String, List<Department>> map = null;
//
//                   if (!TextUtils.isEmpty(departmentsJson)) {
//                       map = new Gson().fromJson(departmentsJson, new TypeToken<Map<String, List<Department>>>() {
//                       }.getType());
//                   }
//
//                   updateDepartmentsView(map);
                }
            } else if (requestCode == SelectPositionTitleFragment.REQUEST_SELECT_POSITION_TITLE) {
                if (data != null) {
                    mCurrentPositionTitle = (PositionTitle) data.getSerializableExtra(SelectPositionTitleFragment.KEY_POSITION_TITLE);
                    mPositionTitleView.setText(mCurrentPositionTitle.getPositionTitleName());
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
        activity.getSupportActionBar().setTitle(R.string.create_consultation_title);
    }

    private void setupPhotoRecyclerView() {
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity, 4));
        mPhotoRecyclerView.setAdapter(mPhotoAdapter = new RecyclerArrayAdapter<Photo>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ClinicPhotoViewHolder(parent, _mActivity, Glide.with(ClinicCentreFragment.this), 4, localPath -> {
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
        ButterKnife.findById(patientView, R.id.iv_arrow_right).setVisibility(View.VISIBLE);
        LinearLayout mTagView = ButterKnife.findById(patientView, R.id.ll_tag);
        for (PatientTag patientTag : mPatient.getPatientTags()) {
            mTagView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
        }

        patientView.setOnClickListener(view -> {
            SelectPatientFragment.launch(this,BusinessType.CONSULTATION);
        });

        TextView mEMRView = ButterKnife.findById(patientView, R.id.tv_emr);
        if (TextUtils.isEmpty(emrName)) {
            mEMRView.setText(R.string.create_consultation_emr_system_create_text);
        } else {
            mEMRView.setText(String.format(getString(R.string.create_consultation_emr_text), emrName));
        }

        mPatientView.addView(patientView);
    }

    private void updateDepartmentsView(Map<String, List<Department>> map) {
        mDepartments = map;

        if (mDepartmentsView.getChildCount() != 0) {
            mDepartmentsView.removeAllViews();
        }

        if (mDepartments == null || mDepartments.isEmpty()) {
            mDepartmentView.setHint(R.string.unlimited);
            return;
        }

        mDepartmentView.setHint("");

        for (String name : mDepartments.keySet()) {
            List<Department> departments = mDepartments.get(name);
            StringBuilder sb = new StringBuilder();
            for (Department department : departments) {
                sb.append(",").append(department.getName());
            }
            String content = sb.substring(1).toString();
            View departmentView = LayoutInflater.from(_mActivity).inflate(R.layout.item_clinic_centre_department, null);
            TextView hospitalNameView = ButterKnife.findById(departmentView, R.id.tv_hospital_name);
            TextView contentView = ButterKnife.findById(departmentView, R.id.tv_departments);
            hospitalNameView.setText(name);
            contentView.setText(content);

            departmentView.setOnLongClickListener(view -> {
                new MaterialDialog.Builder(_mActivity)
                        .items(R.array.delete_option)
                        .itemsCallback((dialog, view2, which, text) -> {
                            mDepartments.remove(name);
                            updateDepartmentsView(mDepartments);
                        })
                        .show();
                return false;
            });

            mDepartmentsView.addView(departmentView);
        }
    }

    private String mDate;
    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthString =  (++monthOfYear) < 10 ? "0" + monthOfYear : "" + monthOfYear;
                    String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    mDate = year + "-" + monthString + "-" + dayString;
                    showTimePicker();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dialog.setMinDate(Calendar.getInstance());
        dialog.show(_mActivity.getFragmentManager(), "DatePickerDialog");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    String hourString = hourOfDay < 10  ? "0" + hourOfDay : "" + hourOfDay;
                    String minuteString = minute  < 10  ? "0" + minute    : "" + minute;
                    String secondString = second  < 10  ? "0" + second    : "" + second;
                    // 期望的时间
                    mExpectTime = mDate + " " + hourString + ":" + minuteString + ":" + secondString;
                    // 获取当前时间
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowTime = sdf.format(date);
                    if (DateUtil.isDateBefore(nowTime, mExpectTime)) {
                        mTimeView.setText(mExpectTime);
                    } else {
                        new MaterialDialog.Builder(_mActivity)
                                .content("期望时间需大于当前时间！")
                                .positiveText("我知道了")
                                .show();
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        dialog.show(_mActivity.getFragmentManager(), "TimePickerDialog");
    }

    private void commit() {
        if (mPatient == null) {
            ViewUtil.showMessage("请选择需要会诊的患者");
            return;
        }
        String patientId = mPatient.getId();
        String patientName = mPatient.getName();

        String consultationDoctorIds = "";

        if (mDiagnosis == null) {
            ViewUtil.showMessage("请填写初步诊断");
            return;
        }

        String diagnosis = mDiagnosis.getName();

        String describe = mClinicExplainView.getText().toString().trim();
        if (TextUtils.isEmpty(describe)) {
            ViewUtil.showMessage("请填写会诊说明");
            return;
        }

        if (mPatientEMR == null && (mPhotos == null || mPhotos.isEmpty())) {
            ViewUtil.showMessage("没有选择已经存在的病历则必须添加图片");
            return;
        }

        String conDate = mExpectTime == null ? "" : mExpectTime;

        String departmentIds = "";
        if (mDepartments != null && !mDepartments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String name : mDepartments.keySet()) {
                List<Department> departments = mDepartments.get(name);
                for (Department department : departments) {
                    sb.append("|").append(department.getId());
                }
            }
            departmentIds = sb.substring(1).toString();
        }

        String doctorType = "";
        if (!mCurrentPositionTitle.isNoLimit()) {
            doctorType = mCurrentPositionTitle.getPositionTitleEnum();
        }

        String applyDoctorId = AppContext.getUser().getDoctId();
        String applyDoctorName = AppContext.getUser().getName();
        String consultationType = AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_TYPE_CENTRE;
        String consultationId = "";
        String patientEMRId = mPatientEMR == null ? "" : mPatientEMR.getId();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("PatientId", patientId)
                .addFormDataPart("PatientName", patientName)
                .addFormDataPart("ConsultationDoctorIds", consultationDoctorIds)
                .addFormDataPart("hospitalId", "")
                .addFormDataPart("ConsultationType", consultationType)
                .addFormDataPart("DoctorType", doctorType)
                .addFormDataPart("DepartmentIds", departmentIds)
                .addFormDataPart("ConDate", conDate)
                .addFormDataPart("Diagnosis", diagnosis)
                .addFormDataPart("Describe", describe)
                .addFormDataPart("ApplyDoctorId", applyDoctorId)
                .addFormDataPart("ApplyDoctorName", applyDoctorName)
                .addFormDataPart("ConsultationId", consultationId)
                .addFormDataPart("PatientEMRId", patientEMRId);
        if (mPhotos != null && !mPhotos.isEmpty()) {
            for (int i = 0; i < mPhotos.size() - 1; i++) {
                Photo photo = mPhotos.get(i);
                File file = new File(photo.getLocalPath());
                String fileExtension = FileUtil.getFileExtension(file);
                String fileName = photo.getCategory() + "|" + System.currentTimeMillis() + fileExtension;
                builder.addFormDataPart("file" + i, fileName, RequestBody.create(MediaType.parse("image/*"), file));
            }
        }

        String url = AppConstant.BASE_URL + "api/ConsultationManager/SaveConsultation?isMobile=true";
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())//传参数、文件或者混合，改一下就行请求体就行
                .build();

        ViewUtil.createProgressDialog(_mActivity, "");
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
                    Clinic clinic = new Gson().fromJson(data, Clinic.class);
                    _mActivity.runOnUiThread(() -> {
                        ViewUtil.dismissProgressDialog();
                        EventBus.getDefault().post(new UpdateClinicStateEvent());
                        ClinicDetailActivity.launch(_mActivity, clinic.getId());
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
