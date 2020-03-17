package com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessType;
import com.wxsoft.telereciver.entity.Diagnosis;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.prescription.OnlinePrescription;
import com.wxsoft.telereciver.entity.prescription.Recipe;
import com.wxsoft.telereciver.event.UpdatePrescriptionStatusEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.Chat2Activity;
import com.wxsoft.telereciver.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.SelectMedicalFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.PrimaryDiagnosisFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.SelectPatientFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll.medicine.select.PrescriptionSelectManageFragment;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.telereciver.ui.activity.Chat2Activity.EXTRA_KEY_DISEASECOUNSELING_ID;

/**
 * 会诊申请
 */
public class PrescriptionCallTheRollFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, PrescriptionCallTheRollFragment.class, null);
    }

    public static void launch(Activity from, OnlinePrescription onlinePrescription,boolean allowAudit) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, onlinePrescription);
        args.add(ALLOW_AUDIT, allowAudit);
        FragmentContainerActivity.launch(from, PrescriptionCallTheRollFragment.class, args);
    }
    private static final String ALLOW_AUDIT = "ALLOW_AUDIT";
    private boolean allowAudit;
    private static final String FRAGMENT_ARGS_CLINIC = "FRAGMENT_ARGS_CLINIC";
    @BindView(R.id.ll_root)
    LinearLayout mRootView;

    @BindView(R.id.ll_2th_action)
    LinearLayout mActionView;

    @BindView(R.id.tv_select_patient)
    TextView mSelectPatientView;

    @BindView(R.id.tv_status)
    TextView mStatusView;

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

    @BindView(R.id.create_time)
    TextView tvCreateTimeView;

    @BindView(R.id.tv_doctor)
    TextView tvDoctorView;

    @BindView(R.id.sign)
    LinearLayout mSignLayout;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.btn_save)
    Button btnSubmit;

    private OnlinePrescription onlinePrescription;
    private String preId;

    private RecyclerArrayAdapter<Recipe> mAdapter;


    // 疾病
    private Diagnosis mDiagnosis;
    private RequestManager mGlide;
    private RequestOptions mOptions;@OnClick(R.id.tv_status)
    void clickPatient() {
        SelectPatientFragment.launch(this,BusinessType.PRESCRIPTION);
    }

    @OnClick(R.id.rl_preliminary_diagnosis)
    void diagnosisClick() {

        if(onlinePrescription!=null&& onlinePrescription.status.equals("901-0001"))
            PrimaryDiagnosisFragment.launch(this);
    }

    @OnClick(R.id.med_type)
    void medTypeClick() {

        if(onlinePrescription!=null&& onlinePrescription.status.equals("901-0001"))
            SelectMedicalFragment.launch(this);
    }

    @OnClick(R.id.tv_add_med)
    void medSelectClick() {
        if(onlinePrescription!=null&& onlinePrescription.status.equals("901-0001"))
            PrescriptionSelectManageFragment.launch(_mActivity);
    }

    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @OnClick(R.id.tv_double_action_1)
    void saveClick1() {
        pharmacist("901-0004");
    }

    @OnClick(R.id.tv_double_action_2)
    void saveClick2() {
        pharmacist("901-0003");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prescription_call_the_roll;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        mGlide = Glide.with(this);


        mOptions = new RequestOptions()
          .centerCrop()
          .dontAnimate();
        setupToolbar();
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity){
            @Override
            public boolean canScrollVertically() {
                // 直接禁止垂直滑动
                return false;
            }

        });
        mRecyclerView.setRefreshingColorResources(R.color.colorPrimary);
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Recipe>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecipeViewHolder(parent);
            }
        });
        if (onlinePrescription != null && onlinePrescription.recipes!=null) {
            mAdapter.addAll(onlinePrescription.recipes);
        }else{
            ((TextView) ButterKnife.findById(view, R.id.tv_position)).setText(AppContext.getUser().getHospitalName()+"  "+
              AppContext.getUser().getDepartmentName());
        }


        if (getArguments() != null) {
            preId =( (OnlinePrescription) getArguments().getSerializable(FRAGMENT_ARGS_CLINIC)).id;
            allowAudit = getArguments().getBoolean(ALLOW_AUDIT,false);

            loadData();

        }

    }

    @Subscribe
    public void onEvent(Recipe recipe) {

        if(onlinePrescription !=null ){
            if(onlinePrescription.recipes==null){
                onlinePrescription.recipes=new ArrayList();
            }else {
                for (Recipe re : onlinePrescription.recipes) {
                    if (re.medicineId == recipe.medicineId)
                        return;
                }
            }

            onlinePrescription.recipes.add(recipe);
            mAdapter.add(recipe);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void loadData() {
        ApiFactory.getPrescriptionApi().getPrescription(preId)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<BaseResp< OnlinePrescription>>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  ViewUtil.showMessage(e.getMessage());
              }

              @Override
              public void onNext(BaseResp< OnlinePrescription> resp) {

                  processResponse(resp);
              }
          });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectPatientFragment.REQUEST_SELECT_PATIENT ||
                    requestCode == PatientSearchFragment.REQUEST_SEARCH_PATIENT ||
                    requestCode == PatientAddFragment.REQUEST_ADD_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(SelectPatientFragment.KEY_PATIENT);

//                    updatePatientView(patient);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(preId ==  null || preId.equals("")) return false;
        switch (item.getItemId()) {
            case R.id.text:
                Intent intent = new Intent(_mActivity, Chat2Activity.class);
                intent.putExtra(Chat2Activity.EXTRA_KEY_CONV_TITLE, "咨询详情");
                intent.putExtra(EXTRA_KEY_DISEASECOUNSELING_ID, preId);
                intent.putExtra(Chat2Activity.EXTRA_KEY_SINGE, true);
                intent.putExtra(Chat2Activity.EXTRA_KEY_MODULE, "KaiFang");
                startActivity(intent);
                return true;
        }
        return  false;
    }
    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_prescription_title);
        setHasOptionsMenu(true);
    }

    private void processResponse(BaseResp<OnlinePrescription> resp) {
        if (resp.isSuccess()) {
            onlinePrescription=resp.getData();
            if(onlinePrescription.status.compareTo("901-0002")<0) {
                ((FragmentContainerActivity) getActivity()).getSupportActionBar().setTitle(R.string.create_prescription_title);
            }else{
                ((FragmentContainerActivity) getActivity()).getSupportActionBar().setTitle(R.string.prescription_detail);
            }
            if(onlinePrescription.weChatAccount!=null )
                updatePatientView(onlinePrescription);
            mPreliminaryDiagnosisView.setText(onlinePrescription.diagnosis);
            if(onlinePrescription.memo!=null)
                mClinicExplainView.setText(onlinePrescription.memo);
            if(onlinePrescription.recipes!=null) {
                mAdapter.clear();
                mAdapter.addAll(onlinePrescription.recipes);
            }

            mStatusView.setText(onlinePrescription.statusName);
            btnSubmit.setVisibility(onlinePrescription.status.compareTo("901-0002")<0 ?View.VISIBLE:View.GONE);
            if(mRootView.getVisibility()==View.GONE)
                mRootView.setVisibility(View.VISIBLE);

            if(onlinePrescription.status.compareTo("901-0002")==0 && allowAudit){
                mActionView.setVisibility(View.VISIBLE);
            }else{
                mActionView.setVisibility(View.GONE);
            }
            if(onlinePrescription.doctorName!=null)
                tvDoctorView.setText(onlinePrescription.doctorName);

            if(onlinePrescription.createdDate!=null){
                tvCreateTimeView.setText("开具时间:("+onlinePrescription.createdDate.substring(0,19).replace("T"," ")+")");
            }

            mSignLayout.setVisibility(onlinePrescription.status.compareTo("901-0001")>0?View.VISIBLE:View.GONE);


        }else{
            ViewUtil.showMessage(resp.getMessage());
        }



    }

    private void updatePatientView(OnlinePrescription patient) {
//        mPatient = patient;

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
        mGlide .load(patient.weChatAccount.headimgurl)
          .into(avatarView);
//        avatarView.setImageResource(patient.weChatAccount..getAvatarDrawableRes());
        ((TextView) ButterKnife.findById(patientView, R.id.tv_patient_name)).setText(patient.weChatAccount.name);
        ((TextView) ButterKnife.findById(patientView, R.id.tv_gender)).setText(patient.weChatAccount.sex==1?"男":"女");
        ButterKnife.findById(patientView, R.id.tv_emr).setVisibility(View.GONE);


        ButterKnife.findById(patientView, R.id.iv_arrow_right).setVisibility(View.VISIBLE);

        mPatientView.addView(patientView);
    }


    private void commit() {

        ApiFactory.getPrescriptionApi().savePrescriptionRecipe(onlinePrescription)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<BaseResp< OnlinePrescription>>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  ViewUtil.showMessage(e.getMessage());
              }

              @Override
              public void onNext(BaseResp<OnlinePrescription> resp) {

                  EventBus.getDefault().post(new UpdatePrescriptionStatusEvent());
                  Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
                  _mActivity.finish();
//                  processResponse(resp);

              }
          });
    }

    private void pharmacist(String status) {
        onlinePrescription.status=status;
        onlinePrescription.pharmacistId= AppContext.getUser().getDoctId();
        onlinePrescription.pharmacistName= AppContext.getUser().getName();
        ApiFactory.getPrescriptionApi().pharmacistPrescription(onlinePrescription)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<BaseResp< OnlinePrescription>>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  ViewUtil.showMessage(e.getMessage());
              }

              @Override
              public void onNext(BaseResp<OnlinePrescription> resp) {

                  processResponse(resp);
              }
          });
    }
    private static class RecipeViewHolder extends BaseViewHolder<Recipe> {

        private TextView name;
        private TextView detail;
        private TextView count;
        private TextView memo;


        public RecipeViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_prescription_recipe);
            name = $(R.id.med_name);
            detail = $(R.id.detail);
            count = $(R.id.count);
            memo = $(R.id.memo);


        }

        @Override
        public void setData(Recipe data) {
            super.setData(data);

            name.setText(data.medicineProductName);
            detail.setText(data.usage);
            count.setText(data.count+data.saleUnit);
            memo.setText(data.tags);

        }
    }

    Menu menuCall;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_call,menu);
        menuCall =menu;
        super.onCreateOptionsMenu(menu, inflater);
    }
}
