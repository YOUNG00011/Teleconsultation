package com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.calltheroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Department;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.transfertreatment.MessageTemplate;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMent;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMentReceive;
import com.wxsoft.teleconsultation.event.TreatMentStateChangeEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.SelectDepartmentFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.DoctorsDisplayFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.TransferTreatmentModelListFragment;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.teleconsultation.ui.fragment.SelectDepartmentFragment.KEY_DEPARTMENT;
import static com.wxsoft.teleconsultation.ui.fragment.SelectDepartmentFragment.KEY_DOCTOR;
import static com.wxsoft.teleconsultation.ui.fragment.SelectDepartmentFragment.REQUEST_SELECT_DEPARTMENT;
import static com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.DoctorsDisplayFragment.REQUEST_SELECT_DOCTOR;
import static com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.TransferTreatmentModelListFragment.KEY_MODEL_PARAM;

/**
 * 会诊申请
 */
public class TreatmentReceiveFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, TreatmentReceiveFragment.class, null);
    }

    public static void launch(Activity from, TreatMent clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, clinic);
        FragmentContainerActivity.launch(from, TreatmentReceiveFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_CLINIC = "FRAGMENT_ARGS_CLINIC";

    @BindView(R.id.tv_select_dept)
    TextView mSelectDeptView;

    @BindView(R.id.l_select_dept)
    LinearLayout mDeptView;


    @BindView(R.id.tv_select_doc)
    TextView mDoctorView;

    @BindView(R.id.reman_text_size)
    TextView mContentCountView;

    @BindView(R.id.l_surgery_time)
    LinearLayout lSurgeryTime;

    @BindView(R.id.tv_select_time)
    TextView mInTime;

    @BindView(R.id.tv_surgery_time)
    TextView mSurgeryTime;

    @BindView(R.id.check_l1)
    CheckBox check_l1;

    @BindView(R.id.check_l2)
    CheckBox check_l2;

    @BindView(R.id.check_r1)
    CheckBox check_r1;

    @BindView(R.id.check_r2)
    CheckBox check_r2;

    @BindView(R.id.the_money)
    EditText theMoney;
    @BindView(R.id.et_clinic_explain)
    EditText discrip;

    @OnTextChanged(R.id.et_clinic_explain)
    void textchange(){
        int count=discrip.getText().length();
        if (count>0){
            mContentCountView.setText(String.valueOf(count)+"/"+"500");
        }else{
            mContentCountView.setText("500");
        }

    }

    TreatMent mClinic;
    // 医生集合
    private Doctor mDoctors;
    private Department mDepartment;

    @OnClick(R.id.l_select_dept)
    void selectPatientClick() {
        SelectDepartmentFragment.launch(this,AppContext.getUser().getHospitalId(),"TransferTreatment");
    }

    @OnClick(R.id.l_select_doc)
    void inviteDoctorClick() {

        if(mDepartment==null) {
            DoctorsDisplayFragment.launchByHosipitalIdAndDepartmentId(this, mClinic.acceptDoctor.getDepartmentName(),mClinic.acceptDoctor.getHospitalId(),mClinic.acceptDoctor.getDepartmentId(),"TransferTreatment");
        }else{
            DoctorsDisplayFragment.launchByHosipitalIdAndDepartmentId(this, mDepartment.getName(), mDepartment.getOrganizationId(), mDepartment.getId(),"TransferTreatment");
        }
    }

    @OnClick(R.id.l_select_time)
    void inviteTimeClick() {
        showDatePicker(0);

    }

    @OnClick(R.id.l_surgery_time)
    void surgeryTimeClick() {
        showDatePicker(1);

    }
    @OnClick(R.id.get_module)
    void getModel(){
        TransferTreatmentModelListFragment.launch(this);
    }

    @OnClick(R.id.new_module)
    void saveModel(){

        String note=discrip.getText().toString().trim();
        if(TextUtils.isEmpty(note)){
            ViewUtil.showMessage("模版不能为空");
            return;
        }
        MessageTemplate template=new MessageTemplate();
        User mUser=AppContext.getUser();
        template.doctorId=mUser.getDoctId();
        template.name="转诊";
        template.messageNote=note;
        template.messageType="202-0001";
        template.messageTypeName="转诊留言模板";
        ApiFactory.getCommApi().saveTemplate(template)
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
                            ViewUtil.showMessage("添加成功");
                        }
                    }
                });
    }


    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_treat_ment_receive;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mClinic = (TreatMent) getArguments().getSerializable(FRAGMENT_ARGS_CLINIC);
        }

        setupToolbar();

        mDoctors=mClinic.acceptDoctor;
        mDoctorView.setText(mDoctors.getName());
        mSelectDeptView.setText(mDoctors.getDepartmentName());

        check_r1.setOnCheckedChangeListener((buttonView, isChecked) -> lSurgeryTime.setVisibility(isChecked?View.VISIBLE:View.GONE));
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.receive_treatment_title);
    }



    private String mDate;
    private void showDatePicker(int i) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthString = (++monthOfYear) < 10 ? "0" + monthOfYear : "" + monthOfYear;
                    String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    mDate = year + "-" + monthString + "-" + dayString;
                    if(i==0){
                        mInTime.setText(mDate);
                    }else{
                        mSurgeryTime.setText(mDate);
                    }
                    //showTimePicker();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dialog.setMinDate(Calendar.getInstance());
        dialog.show(_mActivity.getFragmentManager(), "DatePickerDialog");
    }

    private void commit() {

        if (mInTime.getText() == null ||"".equals(mInTime.getText()) ) {
            ViewUtil.showMessage("请选择入院日期");
            return;
        }


        if ((mSurgeryTime.getText() == null ||"".equals(mSurgeryTime.getText()) )&& check_r1.isChecked()) {
            ViewUtil.showMessage("请选择手术日期");
            return;
        }


        if ("".equals(theMoney.getText()) ) {
            ViewUtil.showMessage("请输入预交金");
            return;
        }


        TreatMentReceive receive=new TreatMentReceive();
        receive.admissionCharge=theMoney.getText().toString();
        receive.admissionDate=mInTime.getText().toString();

        if(check_l1.isChecked()) {
            receive.admissionOptions = check_l1.getText().toString();
        }
        if(check_l2.isChecked()) {
            receive.admissionOptions=receive.admissionOptions ==null? check_l2.getText().toString():(receive.admissionOptions+";"+check_l2.getText().toString());
        }

        if(check_r1.isChecked()) {
            receive.admissionOptions=receive.admissionOptions ==null? check_r1.getText().toString():(receive.admissionOptions+";"+check_r1.getText().toString());
            receive.operationDate=mSurgeryTime.getText().toString();
        }

        if(check_r2.isChecked()) {
            receive.admissionOptions=receive.admissionOptions ==null? check_r2.getText().toString():(receive.admissionOptions+";"+check_r2.getText().toString());

        }

        receive.departmentId=mDepartment==null?mClinic.acceptDoctor.getDepartmentId():mDepartment.getId();
        receive.departmentName=mDepartment==null?mClinic.acceptDoctor.getDepartmentName():mDepartment.getName();

        receive.doctorId=mDoctors==null?mClinic.acceptDoctor.getId():mDoctors.getId();
        receive.doctorName=mDoctors==null?mClinic.acceptDoctor.getName():mDoctors.getName();

        receive.hospitalId=mClinic.acceptDoctor.getHospitalId();
        receive.hospitalName=mClinic.acceptDoctor.getHospitalName();
        receive.transferTreatmentId=mClinic.id;
        receive.transferType=mClinic.transferType;
        receive.transferTypeName=mClinic.transferTypeName;
        receive.creatorId=AppContext.getUser().getId();
        receive.creatorName=AppContext.getUser().getName();

        receive.note=discrip.getText().toString();

        ApiFactory.getTransferTreatmentApi().receive(receive)
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
                            ViewUtil.showMessage("添加成功");
                              EventBus.getDefault().post(new TreatMentStateChangeEvent());
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {

                switch (requestCode) {
                    case REQUEST_SELECT_DEPARTMENT:
                        mDepartment=(Department)data.getSerializableExtra(KEY_DEPARTMENT);
                        mSelectDeptView.setText(mDepartment.getName());
                        mDoctorView.setText("");
                        mDoctors=null;
                        break;
                    case REQUEST_SELECT_DOCTOR:
                        mDoctors=(Doctor)data.getSerializableExtra(KEY_DOCTOR);
                        mDoctorView.setText(mDoctors.getName());
                        break;
                  case KEY_MODEL_PARAM:
                    MessageTemplate item = (MessageTemplate) data.getSerializableExtra(TransferTreatmentModelListFragment.KEY_MODEL);
                    discrip.setText(item.messageNote);
                    break;
                }
            }
        }
    }

}
