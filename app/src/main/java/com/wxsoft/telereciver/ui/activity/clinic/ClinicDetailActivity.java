package com.wxsoft.telereciver.ui.activity.clinic;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.Clinic;
import com.wxsoft.telereciver.entity.ConsultationFeedbackTranslate;
import com.wxsoft.telereciver.entity.ConsultationTranslate;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.HWDeviceAccount;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.entity.Translationer;
import com.wxsoft.telereciver.entity.conversation.Event;
import com.wxsoft.telereciver.entity.conversation.EventType;
import com.wxsoft.telereciver.entity.requestbody.ClinicAddDoctorRequestBody;
import com.wxsoft.telereciver.event.UpdateClinicStateEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.ChatActivity;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.ClinicAdviceFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.ClinicRefuseFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.ClinicReplyFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.DoctorInfoFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.FinishClinicFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll.ClinicCallTheRollFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll.SelectDoctorFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.centre.ClinicCentreFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.patientmanager.EMRFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ClinicDetailActivity extends SupportBaseActivity {

    public static void launch(Context from, String clinicId) {
        Intent intent = new Intent(from, ClinicDetailActivity.class);
        intent.putExtra(EXTRA_CLINIC_ID, clinicId);
        from.startActivity(intent);
    }

    public static final String EXTRA_IS_MYCLINIC = "EXTRA_IS_MYCLINIC";
    public static final String EXTRA_CLINIC_ID = "EXTRA_CLINIC_ID";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.ll_root)
    LinearLayout mRootView;

    @BindView(R.id.tv_status)
    TextView mStatusView;

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

    @BindView(R.id.tv_describe)
    TextView mDescribeView;

    @BindView(R.id.rl_video_device)
    RelativeLayout mVideoDeviceRootView;

    @BindView(R.id.tv_video_device)
    TextView mVideoDeviceView;

    @BindView(R.id.iv_video_device_remove)
    ImageView mVideoDeviceRemoveView;

    @BindView(R.id.tv_time)
    TextView mTimeView;

    @BindView(R.id.ll_departments_root)
    LinearLayout mDepartmentsRootView;

    @BindView(R.id.tv_department_name)
    TextView mDepartmentView;

    @BindView(R.id.ll_apply_doctor)
    LinearLayout mApplyDoctorView;

    @BindView(R.id.iv_doctor_avatar)
    ImageView mDoctorAvatarView;

    @BindView(R.id.tv_doctor_name)
    TextView mDoctorNameView;

    @BindView(R.id.tv_department)
    TextView mDoctorDepartmentView;

    @BindView(R.id.tv_hospital)
    TextView mHospitalView;

    @BindView(R.id.tv_goodat)
    TextView mGoodatView;

    @BindView(R.id.ll_join_doctor)
    LinearLayout mJoinDoctorView;

    @BindView(R.id.tv_join_doctor_title)
    TextView mJoinDoctorTitleView;

    @BindView(R.id.recycler_view_doctor)
    EasyRecyclerView mJoinDoctorRecyclerView;

    @BindView(R.id.ll_translator)
    LinearLayout mTranslatorView;

    @BindView(R.id.recycler_view_translator)
    EasyRecyclerView mTranslatorRecyclerView;

    @BindView(R.id.rl_cancel_reason)
    RelativeLayout mCancelReasonLayout;

    @BindView(R.id.tv_cancel_reason)
    TextView mCancelReasonView;

    @BindView(R.id.rl_refuse_reason)
    RelativeLayout mRefuseReasonView;

    @BindView(R.id.recycler_view_refuse_reason)
    EasyRecyclerView mRefuseReasonRecyclerView;

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

    private String mClinicId;
    private boolean isMyClinic;
    private Clinic mClinic;
    private List<HWAccount> mHWAccounts;
    private HWDeviceAccount mHWDeviceAccount;

        @OnClick(R.id.rl_patient)
    void patientInfoClick() {
            PatientDetailActivity.launch(this, mClinic.getPatientInfoDTO());
    }

    @OnClick(R.id.rl_video_device)
    void videoDeviceClick() {
        if (mHWAccounts != null && !mHWAccounts.isEmpty()) {
            showSelectHWAccountDialog();
            return;
        }

        ViewUtil.createProgressDialog(this, "");
        ApiFactory.getClinicManagerApi().getHWDeviceAccountByOrgId(AppContext.getUser().getHospitalId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<HWAccount>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<HWAccount>> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            mHWAccounts = resp.getData();
                            if (mHWAccounts == null || mHWAccounts.isEmpty()) {
                                ViewUtil.showMessage("暂时没有华为账号");
                                return;
                            }
                            showSelectHWAccountDialog();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    @OnClick(R.id.iv_video_device_remove)
    void videoDeviceRemoveClick() {
        ViewUtil.createProgressDialog(this, "");
        ApiFactory.getClinicManagerApi().deleteConsultationHWDeviceAccountDTO(mHWDeviceAccount.getId())
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
                            mHWDeviceAccount = null;
                            updateVideoDeviceView();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    @OnClick(R.id.tv_info_more)
    void infoMoreClick() {
        if (mClinic == null || mClinic.getPatientEMRId() == null) {
            return;
        }
        EMRFragment.launch(this, mClinic.getPatientInfoDTO(), mClinic.getPatientEMRId());
    }

    @OnClick(R.id.tv_join_doctor_title)
    void joinDoctorTitleClick() {
        if (isMyClinic || !mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            return;
        }

        SelectDoctorFragment.launch(this, BusinessType.CONSULTATION);
    }

    @OnClick(R.id.tv_single_action)
    void singleActionClick() {
        if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_TODO)) {
            new MaterialDialog.Builder(this)
                    .content(R.string.consultation_details_cancel_hint)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        cancelClinic();
                    }).show();
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            if (!isMyClinic) {
                FinishClinicFragment.launchForFinish(this, (ArrayList<Doctor>) mClinic.getConsultationDoctors(), mClinicId, !isMyClinic, getFeedbackTranslateContentsJson());
            } else {
                ClinicAdviceFragment.launch(this, mClinic, !isMyClinic, getFeedbackTranslateContentsJson());
            }
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_HAS_CONSULTATION)) {
            ClinicAdviceFragment.launch(this, mClinic, !isMyClinic, getFeedbackTranslateContentsJson());
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_FINISHED)) {
            ClinicAdviceFragment.launch(this, mClinic, !isMyClinic, getFeedbackTranslateContentsJson());
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CANCEL)) {
            clinicAgain();
        }
    }

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {
        if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            if (isMyClinic) {
                ClinicRefuseFragment.launch(this, mClinicId);
            } else {
                FinishClinicFragment.launchForFinish(this, (ArrayList<Doctor>) mClinic.getConsultationDoctors(), mClinicId, !isMyClinic, getFeedbackTranslateContentsJson());
            }
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_REFUSE)) {
            if (!isMyClinic) {
                clinicAgain();
            }
        }
    }

    @OnClick(R.id.tv_double_action_2)
    void doubleAction2Click() {
        if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            if (isMyClinic) {
                ClinicReplyFragment.launch(this, mClinicId);
            } else {
                ClinicAdviceFragment.launch(this, mClinic, !isMyClinic, getFeedbackTranslateContentsJson());
            }
        } else if (mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_REFUSE)) {
            if (!isMyClinic) {
                ClinicAdviceFragment.launch(this, mClinic, !isMyClinic, getFeedbackTranslateContentsJson());
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clinic_detail;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        mClinicId = getIntent().getStringExtra(EXTRA_CLINIC_ID);
        setupToolbar();
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_one, menu);
        MenuItem action = menu.findItem(R.id.action);
        action.setTitle(R.string.consultation_details_group);
        boolean isShow = false;
        if (mClinic != null && mClinic.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            isShow = true;
        }
        action.setVisible(isShow);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                conversation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectDoctorFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(SelectDoctorFragment.KEY_DOCTOR);
                    List<Doctor> joinDoctors = mClinic.getConsultationDoctors();
                    if (joinDoctors != null || !joinDoctors.isEmpty()) {
                        if (joinDoctors.contains(doctor)) {
                            ViewUtil.showMessage("该医生已存在");
                            return;
                        }
                    }

                    ViewUtil.createProgressDialog(ClinicDetailActivity.this, "");
                    ApiFactory.getClinicManagerApi().consultationAddDocs(new ClinicAddDoctorRequestBody(mClinicId, doctor.getId()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BaseResp<Boolean>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage(e.getMessage());
                                }

                                @Override
                                public void onNext(BaseResp<Boolean> resp) {
                                    ViewUtil.dismissProgressDialog();
                                    if (resp.isSuccess()) {
                                        loadData();
                                    } else {
                                        ViewUtil.showMessage(resp.getMessage());
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void setupToolbar() {
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.consultation_details_title);
    }

    private void loadData() {
        showRefreshing(true);
        ApiFactory.getClinicManagerApi().getConsultationById(mClinicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Clinic>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<Clinic> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            mClinic = resp.getData();
                            if (mClinic.getApplyDoctorInfoDTO().getId().equals(AppContext.getUser().getDoctId())) {
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

    private void setupViews() {
        invalidateOptionsMenu();
        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
        }

        mStatusView.setText(mClinic.getStatusName());
        ConsultationTranslate consultationTranslate = mClinic.getConsultationTranslateDTO();

        Patient patient = mClinic.getPatientInfoDTO();
        mPatientAvatarView.setImageResource(patient.getAvatarDrawableRes());
        String patientName = patient.getName();
        if (consultationTranslate != null && !TextUtils.isEmpty(consultationTranslate.getPatientName())) {
            patientName = patientName + "  " + consultationTranslate.getPatientName();
        }
        mPatientNameView.setText(patientName);

        String sex = patient.getFriendlySex();
        if (consultationTranslate != null && !TextUtils.isEmpty(consultationTranslate.getSex())) {
            sex = sex + "  " + consultationTranslate.getSex();
        }
        mGenderView.setText(sex);

        String age = String.valueOf(patient.getAge());
        if (consultationTranslate != null && !TextUtils.isEmpty(consultationTranslate.getAge())) {
            age = age + "  " + consultationTranslate.getAge();
        }

        mAgeView.setText(age);
        mHealthView.setText(patient.getMedicalInsuranceName());
        mTagView.removeAllViews();
        List<PatientTag> patientTags = patient.getPatientTags();
        if (patientTags == null || patientTags.isEmpty()) {
            mTagView.setVisibility(View.INVISIBLE);
        } else {
            mTagView.setVisibility(View.VISIBLE);
            for (PatientTag patientTag : patientTags) {
                mTagView.addView(AppUtil.getTagTextView(this, patientTag.getTagName()));
            }
        }

        String diagnosis = mClinic.getDiagnosis();
        if (consultationTranslate != null && !TextUtils.isEmpty(consultationTranslate.getDiagnosis())) {
            diagnosis = diagnosis + "\n" + consultationTranslate.getDiagnosis();
        }
        mDiagnosisView.setText(diagnosis);

        String describe = mClinic.getDescribe();
        if (consultationTranslate != null && !TextUtils.isEmpty(consultationTranslate.getDescribe())) {
            describe = describe + "\n" + consultationTranslate.getDescribe();
        }
        mDescribeView.setText(describe);

        if (!TextUtils.isEmpty(mClinic.getConDate())) {
            String time = mClinic.getConDate().replace("T", " ");
            if (time.contains(".")) {
                int lastPoi = time.lastIndexOf('.');
                time = time.substring(0, lastPoi);
            }
            mTimeView.setText(time);
        }

        List<Clinic.ConsultationHopeOrgAndDept> consultationHopeOrgAndDepts = mClinic.getConsultationHopeOrgAndDepts();
        if (consultationHopeOrgAndDepts != null && !consultationHopeOrgAndDepts.isEmpty()) {
            mDepartmentsRootView.setVisibility(View.VISIBLE);
            List<String> departments = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (Clinic.ConsultationHopeOrgAndDept consultationHopeOrgAndDept : consultationHopeOrgAndDepts) {
                String department = consultationHopeOrgAndDept.getDepartmentName();
                if (!departments.contains(department)) {
                    departments.add(department);
                    sb.append(",").append(department);
                }
            }
            mDepartmentView.setText(sb.substring(1).toString());
        } else {
            mDepartmentsRootView.setVisibility(View.GONE);
        }

        RequestManager glide = Glide.with(this);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate();

        Doctor applyDoctor = mClinic.getApplyDoctorInfoDTO();
        if (applyDoctor != null) {
            mApplyDoctorView.setVisibility(View.VISIBLE);

            mDoctorNameView.setText(applyDoctor.getName());
            mDoctorDepartmentView.setText(applyDoctor.getDepartmentName());
            mHospitalView.setText(applyDoctor.getHospitalName());
            String goodAt = "";
            if (!TextUtils.isEmpty(applyDoctor.getGoodAt())) {
                goodAt = "擅长:   " + applyDoctor.getGoodAt();
            }

            mGoodatView.setText(goodAt);

            glide.setDefaultRequestOptions(options.error(applyDoctor.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(applyDoctor.getUserImgUrl())
                    .into(mDoctorAvatarView);
        } else {
            mApplyDoctorView.setVisibility(View.GONE);
        }

        String status = mClinic.getStatus();
        if (!isMyClinic && status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_arrow_right);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mJoinDoctorTitleView.setCompoundDrawables(null, null, drawable, null);
        } else {
            mJoinDoctorTitleView.setCompoundDrawables(null, null, null, null);
        }

        List<Doctor> joinDoctors = mClinic.getConsultationDoctors();
        if (joinDoctors == null || joinDoctors.isEmpty()) {
            mJoinDoctorView.setVisibility(View.GONE);
        } else {
            mJoinDoctorView.setVisibility(View.VISIBLE);

            mJoinDoctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f), 0, 0);
            mJoinDoctorRecyclerView.addItemDecoration(itemDecoration);
            final RecyclerArrayAdapter<Doctor> adapter;
            mJoinDoctorRecyclerView.setAdapter(adapter = new RecyclerArrayAdapter<Doctor>(this) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new JoinDoctorViewHolder(parent, Glide.with(ClinicDetailActivity.this));
                }
            });

            adapter.setOnItemClickListener(position -> {
                DoctorInfoFragment.launch(ClinicDetailActivity.this, adapter.getItem(position));
            });

            adapter.addAll(joinDoctors);
        }

        if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            List<HWDeviceAccount> hwDeviceAccounts = mClinic.getConsultationHWDeviceAccounts();
            if (hwDeviceAccounts != null && !hwDeviceAccounts.isEmpty()) {
                for (HWDeviceAccount hwDeviceAccount : hwDeviceAccounts) {
                    if (hwDeviceAccount.getUserId().equals(AppContext.getUser().getId())) {
                        mHWDeviceAccount = hwDeviceAccount;
                        break;
                    }
                }
            }
            getHWDeviceAccounts();
        }

        List<Translationer> translationers = mClinic.getConsultationTranslationers();
        if (translationers == null || translationers.isEmpty()) {
            mTranslatorView.setVisibility(View.GONE);
        } else {
            mTranslatorView.setVisibility(View.VISIBLE);

            mTranslatorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f), 0, 0);
            mTranslatorRecyclerView.addItemDecoration(itemDecoration);
            RecyclerArrayAdapter adapter;
            mTranslatorRecyclerView.setAdapter(adapter = new RecyclerArrayAdapter<Translationer>(this) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new TranslatorViewHolder(parent);
                }
            });
            adapter.addAll(translationers);
        }

//        if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CANCEL) && !TextUtils.isEmpty(mClinic.getReason())) {
//            mCancelReasonLayout.setVisibility(View.VISIBLE);
//            mCancelReasonView.setText(mClinic.getReason());
//        } else {
//            mCancelReasonLayout.setVisibility(View.GONE);
//        }

        List<Clinic.ConsultationFeedback> consultationFeedbacks = mClinic.getConsultationFeedbacks();
        if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_REFUSE) && consultationFeedbacks != null && !consultationFeedbacks.isEmpty()) {
            mRefuseReasonView.setVisibility(View.VISIBLE);
            mRefuseReasonRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f), 0, 0);
            mRefuseReasonRecyclerView.addItemDecoration(itemDecoration);
            RecyclerArrayAdapter adapter;
            mRefuseReasonRecyclerView.setAdapter(adapter = new RecyclerArrayAdapter<Clinic.ConsultationFeedback>(this) {
                @Override
                public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                    return new RefuseReasonViewHolder(parent);
                }
            });
            adapter.addAll(consultationFeedbacks);
        } else {
            mRefuseReasonView.setVisibility(View.GONE);
        }

        if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_TODO)) {
            mSingleActionLayout.setVisibility(View.VISIBLE);
            mSingleActionView.setText(R.string.consultation_details_cancel);
            mDoubleActionLayout.setVisibility(View.GONE);
        } else if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CONSULTATION)) {
            if (!isMyClinic) {
                mSingleActionLayout.setVisibility(View.GONE);
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.consultation_details_end);
                mDoubleAction2View.setText(R.string.consultation_details_look_at_advice);
            } else {
                if (isMyClinicTodoOfMe()) {
                    mSingleActionLayout.setVisibility(View.GONE);
                    mDoubleActionLayout.setVisibility(View.VISIBLE);
                    mDoubleAction1View.setText(R.string.consultation_details_refuse);
                    mDoubleAction2View.setText(R.string.consultation_details_reply);
                } else if (isMyClinicReplyOfMe()) {
                    mSingleActionLayout.setVisibility(View.VISIBLE);
                    mDoubleActionLayout.setVisibility(View.GONE);
                    mSingleActionView.setText(R.string.consultation_details_look_at_advice);
                } else {
                    mSingleActionLayout.setVisibility(View.GONE);
                    mDoubleActionLayout.setVisibility(View.GONE);
                }
            }
        } else if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_HAS_CONSULTATION)) {
            if (!isMyClinic) {
                mDoubleActionLayout.setVisibility(View.GONE);
                mSingleActionLayout.setVisibility(View.VISIBLE);
                mSingleActionView.setText(R.string.consultation_details_look_at_advice);
            } else {
                if (isMyClinicReplyOfMe()) {
                    mDoubleActionLayout.setVisibility(View.GONE);
                    mSingleActionLayout.setVisibility(View.VISIBLE);
                    mSingleActionView.setText(R.string.consultation_details_look_at_advice);
                } else {
                    mDoubleActionLayout.setVisibility(View.GONE);
                    mSingleActionLayout.setVisibility(View.GONE);
                }
            }
        } else if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_FINISHED)) {
            if (!isMyClinic) {
                mDoubleActionLayout.setVisibility(View.GONE);
                mSingleActionLayout.setVisibility(View.VISIBLE);
                mSingleActionView.setText(R.string.consultation_details_look_at_advice);
            } else {
                if (isMyClinicReplyOfMe()) {
                    mDoubleActionLayout.setVisibility(View.GONE);
                    mSingleActionLayout.setVisibility(View.VISIBLE);
                    mSingleActionView.setText(R.string.consultation_details_look_at_advice);
                } else {
                    mDoubleActionLayout.setVisibility(View.GONE);
                    mSingleActionLayout.setVisibility(View.GONE);
                }
            }
        } else if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_REFUSE)) {
            mSingleActionLayout.setVisibility(View.GONE);
            if (!isMyClinic) {
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.consultation_details_re_consultation);
                mDoubleAction2View.setText(R.string.consultation_details_look_at_advice);
            } else {
                mDoubleActionLayout.setVisibility(View.GONE);
            }
        } else if (status.equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_STATUS_CANCEL)) {
            mDoubleActionLayout.setVisibility(View.GONE);
            if (!isMyClinic) {
                mSingleActionLayout.setVisibility(View.VISIBLE);
                mSingleActionView.setText(R.string.consultation_details_re_consultation);
            } else {
                mSingleActionLayout.setVisibility(View.GONE);
            }
        }
    }

    private void getHWDeviceAccounts() {
        if (mHWAccounts != null && !mHWAccounts.isEmpty()) {
            updateVideoDeviceView();
            return;
        }
        ApiFactory.getClinicManagerApi().getHWDeviceAccountByOrgId(AppContext.getUser().getHospitalId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<HWAccount>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<HWAccount>> resp) {
                        if (resp.isSuccess()) {
                            mHWAccounts = resp.getData();
                            updateVideoDeviceView();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void updateVideoDeviceView() {
        if (mHWAccounts == null || mHWAccounts.isEmpty()) {
            mVideoDeviceRootView.setVisibility(View.GONE);
            return;
        }

        mVideoDeviceRootView.setVisibility(View.VISIBLE);

        if (mHWDeviceAccount == null) {
            mVideoDeviceView.setText(R.string.consultation_details_none);
            mVideoDeviceRemoveView.setVisibility(View.GONE);
        } else {
            mVideoDeviceView.setText(mHWDeviceAccount.getHwDeviceAccoutName());
            mVideoDeviceRemoveView.setVisibility(View.VISIBLE);
        }
    }

    private void cancelClinic() {
        ViewUtil.createProgressDialog(this, "");
        ApiFactory.getClinicManagerApi().cancelConsultation(mClinicId, "")
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
                            loadData();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });

    }

    private void showSelectHWAccountDialog() {
        List<String> names = new ArrayList<>();
        for (HWAccount hwAccount : mHWAccounts) {
            names.add(hwAccount.getHwNickname());
        }
        new MaterialDialog.Builder(this)
                .title(R.string.consultation_details_select_account)
                .items(names)
                .itemsCallback((dialog, view, which, text) -> {
                    dialog.dismiss();
                    String HWAccountName = mHWAccounts.get(which).getHwNickname();
                    String HWAccountId = mHWAccounts.get(which).getId();
                    HWDeviceAccount hwDeviceAccount = new HWDeviceAccount(mClinicId, HWAccountName, HWAccountId, AppContext.getUser().getId());
                    if (mHWDeviceAccount != null) {
                        hwDeviceAccount.setId(mHWDeviceAccount.getId());
                    }
                    ViewUtil.createProgressDialog(ClinicDetailActivity.this, "");
                    ApiFactory.getClinicManagerApi().saveConsultationHWDeviceAccountDTO(hwDeviceAccount)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BaseResp<HWDeviceAccount>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage(e.getMessage());
                                }

                                @Override
                                public void onNext(BaseResp<HWDeviceAccount> resp) {
                                    ViewUtil.dismissProgressDialog();
                                    if (resp.isSuccess()) {
                                        mHWDeviceAccount = resp.getData();
                                        updateVideoDeviceView();
                                    } else {
                                        ViewUtil.showMessage(resp.getMessage());
                                    }
                                }
                            });

                })
                .show();
    }

    private boolean isMyClinicTodoOfMe() {
        List<Doctor> joinDoctors = mClinic.getConsultationDoctors();
        if (joinDoctors == null || joinDoctors.isEmpty()) {
            return false;
        }

        boolean result = false;
        for (Doctor doctor : joinDoctors) {
            if (doctor.getId().equals(AppContext.getUser().getDoctId()) && doctor.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.JOIN_DOCTOR_STATUS_TODO)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean isMyClinicReplyOfMe() {
        List<Doctor> joinDoctors = mClinic.getConsultationDoctors();
        if (joinDoctors == null || joinDoctors.isEmpty()) {
            return false;
        }

        boolean result = false;
        for (Doctor doctor : joinDoctors) {
            if (doctor.getId().equals(AppContext.getUser().getDoctId()) && doctor.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.JOIN_DOCTOR_STATUS_FINISHED)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean isMyClinicRefuseOfMe() {
        List<Doctor> joinDoctors = mClinic.getConsultationDoctors();
        if (joinDoctors == null || joinDoctors.isEmpty()) {
            return false;
        }

        boolean result = false;
        for (Doctor doctor : joinDoctors) {
            if (doctor.getId().equals(AppContext.getUser().getDoctId()) && doctor.getStatus().equals(AppConstant.REQUEST_TYPE_NAME.JOIN_DOCTOR_STATUS_REFUSE)) {
                result = true;
                break;
            }
        }

        return result;
    }

    private String getFeedbackTranslateContentsJson() {
        List<ConsultationFeedbackTranslate> consultationFeedbackTranslates = mClinic.getConsultationFeedbackTranslates();
        if (consultationFeedbackTranslates == null || consultationFeedbackTranslates.isEmpty()) {
            return null;
        }

        return new Gson().toJson(consultationFeedbackTranslates);
    }

    private void clinicAgain() {
        if (mClinic.getConsultationType().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_TYPE_CALL_THE_ROLL)) {
            ClinicCallTheRollFragment.launch(this, mClinic);
            this.finish();
        } else if (mClinic.getConsultationType().equals(AppConstant.REQUEST_TYPE_NAME.CLINIC_FROM_TYPE_CENTRE)) {
            ClinicCentreFragment.launch(this, mClinic);
            this.finish();
        }
    }

    private void showRefreshing(final boolean refresh) {
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(refresh);
        });
    }

    private void conversation() {
        if (mClinic == null || mClinic.getConsultationChat()==null) {
            return;
        }
        long groupId = mClinic.getConsultationChat().getGroupId();
        Conversation conversation = JMessageClient.getGroupConversation(groupId);
        if (conversation == null) {
            ViewUtil.showMessage("该讨论组不存在");
            return;
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, conversation.getTitle());
        intent.putExtra(ChatActivity.EXTRA_KEY_GROUP_ID, groupId);
        this.startActivity(intent);

//        List<Doctor> doctors = mClinic.getConsultationDoctors();
//        if (doctors == null || doctors.isEmpty()) {
//            ViewUtil.showMessage("没有医生");
//            return;
//        }
//
//        ArrayList<String> doctorNames = new ArrayList<>();
//        doctorNames.add("aaa123");
//        doctorNames.add("caochenglin");
//
//        JMessageClient.createGroup(mClinic.getPatientInfoDTO().getName() + "的会诊", "", new CreateGroupCallback() {
//            @Override
//            public void gotResult(int responseCode, String responseMsg, long groupId) {
//                if (responseCode == 0) {
//                    JMessageClient.addGroupMembers(groupId, doctorNames, new BasicCallback() {
//                        @Override
//                        public void gotResult(int responseCode, String responseMessage) {
//                            if (responseCode == 0) {
//                                //如果创建群组时添加了人,那么就在size基础上加上自己
//                                createGroup(groupId, doctorNames.size() + 1);
//                            } else if (responseCode == 810007) {
//                                ViewUtil.showMessage("不能添加自己");
//                            } else {
//                                ViewUtil.showMessage("添加失败");
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    private void createGroup(long groupId, int groupMembersSize) {
        Conversation groupConversation = JMessageClient.getGroupConversation(groupId);
        if (groupConversation == null) {
            groupConversation = Conversation.createGroupConversation(groupId);
            cn.jpush.im.android.eventbus.EventBus.getDefault().post(new Event.Builder()
                    .setType(EventType.createConversation)
                    .setConversation(groupConversation)
                    .build());
        }

        Intent intent = new Intent(this, ChatActivity.class);
        //设置跳转标志
        intent.putExtra(ChatActivity.EXTRA_KEY_FROM_GROUP, true);
        intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, groupConversation.getTitle());
        intent.putExtra(ChatActivity.EXTRA_KEY_MEMBERS_COUNT, groupMembersSize);
        intent.putExtra(ChatActivity.EXTRA_KEY_GROUP_ID, groupId);
        this.startActivity(intent);
    }

    @Subscribe
    public void onEvent(UpdateClinicStateEvent updateClinicStateEvent) {
        loadData();
    }

    private static class JoinDoctorViewHolder extends BaseViewHolder<Doctor> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mDepartmentView;
        private TextView mHospitalView;
        private TextView mGoodAtView;
        private TextView mStatusView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public JoinDoctorViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.comm_item_doctor);
            mGlide = glide;

            mAvatarView = $(R.id.iv_doctor_avatar);
            mNameView = $(R.id.tv_doctor_name);
            mDepartmentView = $(R.id.tv_department);
            mHospitalView = $(R.id.tv_hospital);
            mGoodAtView = $(R.id.tv_goodat);
            mStatusView = $(R.id.tv_doctor_status);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(Doctor data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mDepartmentView.setText(data.getDepartmentName());
            mHospitalView.setText(data.getHospitalName());

            String goodAt = "";
            if (!TextUtils.isEmpty(data.getGoodAt())) {
                goodAt = "擅长:   " + data.getGoodAt();
            }

            mGoodAtView.setText(goodAt);
            mGlide.setDefaultRequestOptions(mOptions.error(data.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(data.getUserImgUrl())
                    .into(mAvatarView);

            String statusName = data.getStatusName();
            if (TextUtils.isEmpty(statusName)) {
                mStatusView.setVisibility(View.GONE);
            } else {
                mStatusView.setVisibility(View.VISIBLE);
                mStatusView.setText(statusName);
            }
        }
    }

    private static class TranslatorViewHolder extends BaseViewHolder<Translationer> {

        private TextView mTitleView;

        public TranslatorViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_text);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(Translationer data) {
            super.setData(data);
            String userName = data.getUserName();
            mTitleView.setText(userName);
        }
    }

    private static class RefuseReasonViewHolder extends BaseViewHolder<Clinic.ConsultationFeedback> {

        private TextView mNameView;
        private TextView mRefuseReasonView;

        public RefuseReasonViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_clinic_refuse_reason);
            mNameView = $(R.id.tv_doctor_name);
            mRefuseReasonView = $(R.id.tv_refuse_reson);
        }

        @Override
        public void setData(Clinic.ConsultationFeedback data) {
            super.setData(data);
            mNameView.setText(data.getDoctorName() + ":");
            mRefuseReasonView.setText(data.getFeedbackContent());
        }
    }
}
