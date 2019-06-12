package com.wxsoft.teleconsultation.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.Archive;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Department;
import com.wxsoft.teleconsultation.entity.Duty;
import com.wxsoft.teleconsultation.entity.Hospital;
import com.wxsoft.teleconsultation.entity.PositionTitle;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.RegUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrefectInfoFragment extends BaseFragment {

    public static void launch(Activity from, String phone) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_PHONE, phone);
        FragmentContainerActivity.launch(from, PrefectInfoFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_PHONE = "FRAGMENTARGS_KEY_PHONE";

    @BindView(R.id.cet_name)
    ClearableEditText mNameView;

    @BindView(R.id.cet_idcard)
    ClearableEditText mIDCardView;

    @BindView(R.id.cet_password)
    ClearableEditText mPasswordView;

    @BindView(R.id.cet_password_again)
    ClearableEditText mPasswordAgainView;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_hospital)
    TextView mHospitalView;

    @BindView(R.id.tv_department)
    TextView mDepartmentView;

    @BindView(R.id.tv_position_title)
    TextView mPositionTitleView;

    @BindView(R.id.tv_duty)
    TextView mDutyView;

    @BindString(R.string.male)
    String mMale;

    @BindString(R.string.female)
    String mFemale;

    private String mPhone;
    private Hospital mHospital;
    private Department mDepartment;
    private PositionTitle mPositionTitle;
    private Duty mDuty;

    @OnClick(R.id.tv_hospital)
    void hospitalClick() {
        SelectHospitalFragment.launch(this);
    }

    @OnClick(R.id.tv_department)
    void departmentClick() {
        if (mHospital == null) {
            ViewUtil.showMessage("请先选择医院");
            return;
        }

        SelectDepartmentFragment.launch(this, mHospital, false, false,null);
    }

    @OnClick(R.id.tv_gender)
    void genderClick() {
        showSelectGenderDialog();
    }

    @OnClick(R.id.tv_position_title)
    void positionTitleClick() {
        SelectPositionTitleFragment.launch(this);
    }

    @OnClick(R.id.tv_duty)
    void dutyClick() {
        SelectDutyFragment.launch(this);
    }

//    @OnClick(R.id.btn_ok)
//    void okClick() {
//        commit();
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prefect_info;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mPhone = getArguments().getString(FRAGMENTARGS_KEY_PHONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        menu.findItem(R.id.action).setTitle(R.string.ok);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectHospitalFragment.REQUEST_SELECT_HOSPITAL) {
                if (data != null) {
                    mHospital = (Hospital) data.getSerializableExtra(SelectHospitalFragment.KEY_HOSPITAL);
                    mHospitalView.setText(mHospital.getName());
                }
            } else if (requestCode == SelectDepartmentFragment.REQUEST_SELECT_DEPARTMENT) {
                if (data != null) {
                    mDepartment = (Department) data.getSerializableExtra(SelectDepartmentFragment.KEY_DEPARTMENT);
                    mDepartmentView.setText(mDepartment.getName());
                }
            } else if (requestCode == SelectPositionTitleFragment.REQUEST_SELECT_POSITION_TITLE) {
                if (data != null) {
                    mPositionTitle = (PositionTitle) data.getSerializableExtra(SelectPositionTitleFragment.KEY_POSITION_TITLE);
                    mPositionTitleView.setText(mPositionTitle.getPositionTitleName());
                }
            } else if (requestCode == SelectDutyFragment.REQUEST_SELECT_DUTY) {
                if (data != null) {
                    mDuty = (Duty) data.getSerializableExtra(SelectDutyFragment.KEY_DUTY);
                    mDutyView.setText(mDuty.getDutyName());
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.register_title);
        setHasOptionsMenu(true);
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

    private void commit() {
        String name = mNameView.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ViewUtil.showMessage("请输入姓名");
            return;
        }

        String idcard = mIDCardView.getText().toString().trim();
        if (TextUtils.isEmpty(idcard)) {
            ViewUtil.showMessage("请输入证件号");
            return;
        }

        String gender = mGenderView.getText().toString().trim();
        if (TextUtils.isEmpty(gender)) {
            ViewUtil.showMessage("请选择性别");
            return;
        }

        int sex = gender.equals(mMale) ? 1 : 0;

        if (mHospital == null) {
            ViewUtil.showMessage("请选择医院");
            return;
        }

        if (mDepartment == null) {
            ViewUtil.showMessage("请选择专科");
            return;
        }

        if (mPositionTitle == null) {
            ViewUtil.showMessage("请选择职称");
            return;
        }

        if (mDuty == null) {
            ViewUtil.showMessage("请选择职务");
            return;
        }

        String password = mPasswordView.getText().toString().trim();
        String passwordAgain = mPasswordAgainView.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ViewUtil.showMessage("请输入密码");
            return;
        }

        if (TextUtils.isEmpty(passwordAgain)) {
            ViewUtil.showMessage("请确认密码");
            return;
        }

        if (!password.equals(passwordAgain)) {
            ViewUtil.showMessage("两次输入的密码不一致");
            return;
        }

        int passwordLength = password.length();
        if (passwordLength < 4 || passwordLength > 12) {
            ViewUtil.showMessage("密码应在4-12位");
            return;
        }

        Archive archive = Archive.getRegisterArchive(mPhone,
                name,
                sex,
                password,
                idcard,
                mHospital.getId(),
                mHospital.getName(),
                mDepartment.getId(),
                mDepartment.getName(),
                mPositionTitle.getPositionTitleEnum(),
                mPositionTitle.getPositionTitleName(),
                mDuty.getDutyEnum(),
                mDuty.getDutyName());

        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getLoginApi().registerUser(archive)
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
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
