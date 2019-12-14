package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientEMR;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.PatientDetailActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientDetailFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientInfoConfirmFragment extends BaseFragment{

    public static void launch(Fragment from, Patient patient,boolean need) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        args.add(FRAGMENT_ARGS_NEED_SURE, need);
        FragmentContainerActivity.launchForResult(from, PatientInfoConfirmFragment.class, args, REQUEST_PATIENT_INFO_CONFIRM);
    }

    private boolean need=true;
    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";
    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";
    public static final int REQUEST_PATIENT_INFO_CONFIRM = 36;
    public static final String KEY_PATIENT = "KEY_PATIENT";
    public static final String KEY_PATIENT_EMR = "KEY_PATIENT_EMR";

    @BindView(R.id.tv_name)
    TextView mNameView;

    @BindView(R.id.tv_id)
    TextView mIdView;

    @BindView(R.id.tv_phone)
    TextView mPhoneView;

    @BindView(R.id.tv_health_type)
    TextView mHealthTypeView;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_birthday)
    TextView mBirthdayView;

    private Patient mPatient;

    @OnClick(R.id.btn_ok)
    void okClick() {
        sure();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_patient_info_confirm;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        need = getArguments().getBoolean(FRAGMENT_ARGS_NEED_SURE);
        setupToolbar();
        setupViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        menu.findItem(R.id.action).setTitle(R.string.detail);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                PatientDetailActivity.launch(_mActivity, mPatient);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.patient_confirm_title);
        setHasOptionsMenu(true);
    }

    private void setupViews() {
        mNameView.setText(mPatient.getName());
        mIdView.setText(mPatient.getIDC());
        mPhoneView.setText(mPatient.getPhone());
        mHealthTypeView.setText(mPatient.getMedicalInsuranceName());
        mGenderView.setText(mPatient.getFriendlySex());
        if (!TextUtils.isEmpty(mPatient.getBirthday())) {
            mBirthdayView.setText(getBirthday(mPatient.getBirthday()));
        }
    }

    private String getBirthday(String birthday) {

        return birthday.substring(0,10);
//        return new StringBuffer()
//                .append(birthday.substring(0, 4)).append("-")
//                .append(birthday.substring(4, 6)).append("-")
//                .append(birthday.substring(6, 8))
//                .toString();
    }

    private void sure() {
        if(!need){
            finish(null);
        }else {
            new MaterialDialog.Builder(_mActivity)
                    .title(R.string.patient_confirm_select_emr_title)
                    .content(R.string.patient_confirm_select_emr_hint)
                    .cancelable(false)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                        loadPatientEMRs();
                    })
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                        finish(null);
                    })
                    .show();
        }
    }

    private void loadPatientEMRs() {
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getPatientManagerApi().getPatientEMRs(mPatient.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<PatientEMR>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<PatientEMR>> resp) {
                        ViewUtil.dismissProgressDialog();
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<PatientEMR>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<PatientEMR> patientEMRS = resp.getData();
        if (patientEMRS == null || patientEMRS.isEmpty()) {
            ViewUtil.showMessage("该患者暂无病历");
            return;
        }

        List<String> names = new ArrayList<>();
        for (PatientEMR patientEMR : patientEMRS) {
            names.add(patientEMR.getName());
        }

        new MaterialDialog.Builder(_mActivity)
                .items(names)
                .itemsCallback((dialogm, itemView, position, text) -> {
                    finish(patientEMRS.get(position));
                })
                .show();
    }

    private void finish(PatientEMR patientEMR) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PATIENT, mPatient);
        intent.putExtra(KEY_PATIENT_EMR, patientEMR);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

}
