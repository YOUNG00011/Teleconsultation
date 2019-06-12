package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.CommEnum;
import com.wxsoft.teleconsultation.entity.MedicalInsurance;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientAddFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, false);
        FragmentContainerActivity.launch(from, PatientAddFragment.class, args);
    }

    public static void launchForResult(Fragment from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, true);
        FragmentContainerActivity.launchForResult(from, PatientAddFragment.class, args, REQUEST_ADD_PATIENT);
    }

    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_ADD_PATIENT = 39;
    public static final String KEY_PATIENT = "KEY_PATIENT";

    @BindView(R.id.et_id)
    ClearableEditText mIdView;

    @BindView(R.id.et_name)
    ClearableEditText mNameView;

    @BindView(R.id.et_phone)
    ClearableEditText mPhoneView;

    @BindView(R.id.tv_health_type)
    TextView mHealthType;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_birthday)
    TextView mBirthdayView;

    @BindView(R.id.btn_save)
    Button mSaveView;

    @BindString(R.string.male)
    String mMale;

    @BindString(R.string.female)
    String mFemale;

    private boolean hasResult;
    private MedicalInsurance mCurrentMedicalInsurance;


    @OnClick(R.id.ll_health_card)
    void healthCardClick() {
        showSelectMedicalInsuranceDialog();
    }

    @OnClick(R.id.ll_gender)
    void genderClick() {
        showSelectGenderDialog();
    }

    @OnClick(R.id.ll_birthday)
    void birthdayClick() {
        showSelectBirthdayDialog();
    }

    @OnClick(R.id.btn_save)
    void saveClick() {
        savePatient();
    }

    @OnTextChanged(R.id.et_id)
    void idChanged(CharSequence s, int start, int before, int count) {
//        String idStr = s.toString();
//        String gender = "";
//        String birthday = "";
//        if (idStr.length() == 18) {
//            gender = Integer.parseInt(idStr.substring(16).substring(0, 1)) % 2 == 0 ? "女" : "男";
//            birthday = idStr.substring(6, 10) + "-" +
//                    idStr.substring(10, 12) + "-" +
//                    idStr.substring(12, 14);
//            mAge = getAge(Integer.parseInt(birthday.substring(0, 4)),
//                    Calendar.getInstance().get(Calendar.YEAR));
//        }
//        mGenderView.setText(gender);
//        mBirthdayView.setText(birthday);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_patient_add;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        hasResult = getArguments().getBoolean(FRAGMENT_ARGS_HAS_RESULT);
        setupToolbar();
        mSaveView.setText(R.string.save_button_text);
        loadHealthData();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.new_patient_title);
    }

    private void loadHealthData() {
        if (!App.mMedicalInsurances.isEmpty()) {
            mCurrentMedicalInsurance = App.mMedicalInsurances.get(0);
            mHealthType.setText(mCurrentMedicalInsurance.getMedicalInsuranceName());
            return;
        }

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getPatientManagerApi().getMedicalInsurances()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<CommEnum>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<CommEnum>> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            List<MedicalInsurance> medicalInsurances = MedicalInsurance.getMedicalInsurances(resp.getData());
                            App.mMedicalInsurances.clear();
                            App.mMedicalInsurances.addAll(medicalInsurances);
                            mCurrentMedicalInsurance = App.mMedicalInsurances.get(0);
                            mHealthType.setText(mCurrentMedicalInsurance.getMedicalInsuranceName());
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void showSelectMedicalInsuranceDialog() {
        if (App.mMedicalInsurances.isEmpty()) {
            ViewUtil.showMessage("医保类型不存在");
            return;
        }

        int index = -1;
        List<String> names = new ArrayList<>();
        for (int i = 0; i < App.mMedicalInsurances.size(); i++) {
            MedicalInsurance medicalInsurance = App.mMedicalInsurances.get(i);
            names.add(medicalInsurance.getMedicalInsuranceName());
            if (mCurrentMedicalInsurance != null && mCurrentMedicalInsurance.equals(medicalInsurance)) {
                index = i;
            }
        }

        new MaterialDialog.Builder(_mActivity)
                .items(names)
                .itemsCallbackSingleChoice(index, (dialog, view, which, text) -> {
                    mCurrentMedicalInsurance = App.mMedicalInsurances.get(which);
                    mHealthType.setText(mCurrentMedicalInsurance.getMedicalInsuranceName());
                    return true;
                })
                .show();

    }

    private void showSelectGenderDialog() {
        String gender = mGenderView.getText().toString();
        int selectedIndex = -1;
        if (!TextUtils.isEmpty(gender)) {
            selectedIndex = gender.equals(mMale) ? 0 : 1;
        }
        new MaterialDialog.Builder(_mActivity)
                .items(R.array.patient_gender_option)
                .itemsCallbackSingleChoice(selectedIndex, (dialog, view, which, text) -> {
                    String selectGender;
                    switch (which) {

                        case 0:
                            selectGender = mMale;
                            break;
                        case 1:
                            selectGender = mFemale;
                            break;
                        default:
                            selectGender = "";
                            break;
                    }
                    mGenderView.setText(selectGender);
                    return true;
                })
                .show();
    }

    private void showSelectBirthdayDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthString = (++monthOfYear) < 10 ? "0" + monthOfYear : "" + monthOfYear;
                    String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    String date = year + "-" + monthString + "-" + dayString;
                    mBirthdayView.setText(date);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dialog.setMaxDate(Calendar.getInstance());
        dialog.show(_mActivity.getFragmentManager(), "DatePickerDialog");
    }


    private void savePatient() {
        String id = mIdView.getText().toString();
        if (TextUtils.isEmpty(id)) {
            ViewUtil.showMessage("请输入身份证号");
            return;
        }

        String name = mNameView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ViewUtil.showMessage("请输入姓名");
            return;
        }

        String phone = mPhoneView.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ViewUtil.showMessage("请输入手机号码");
            return;
        }

        String gender = mGenderView.getText().toString();
        if (TextUtils.isEmpty(gender)) {
            ViewUtil.showMessage("请选择性别");
            return;
        }

        String sex = mGenderView.getText().toString().equals("男") ? "1" : "0";

        String birthday = mBirthdayView.getText().toString();
        if (TextUtils.isEmpty(birthday)) {
            ViewUtil.showMessage("请选择出生日期");
            return;
        }

        int age = getAge(Integer.parseInt(birthday.substring(0, 4)),
                    Calendar.getInstance().get(Calendar.YEAR));
        //birthday = birthday.replace("-", "");

        User user=AppContext.getUser();
        Patient patient = new Patient(user.getDoctId(),name, age, birthday, sex, id, phone, AppContext.getUser().getDoctId(), mCurrentMedicalInsurance.getMedicalInsuranceEnum(), mCurrentMedicalInsurance.getMedicalInsuranceName());

        patient.createrId=user.getId();
        patient.createrName=user.getName();

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getPatientManagerApi().savePatientInfo(patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Patient>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<Patient> resp) {
                        ViewUtil.dismissProgressDialog();
                        processResponse(resp);
                    }
                });
    }

    public void processResponse(BaseResp<Patient> resp) {
        if (resp.isSuccess()) {
            if (hasResult) {
                Patient patient = resp.getData();
                Intent intent = new Intent();
                intent.putExtra(KEY_PATIENT, patient);
                _mActivity.setResult(RESULT_OK, intent);
                _mActivity.finish();
            } else {
                EventBus.getDefault().post(new PatientOrTagChangedEvent());
                _mActivity.finish();
            }
        } else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }

    private int getAge(int yearBirth, int yearNow) {
        return yearNow - yearBirth;
    }
}
