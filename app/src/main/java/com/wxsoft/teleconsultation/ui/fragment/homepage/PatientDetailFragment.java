package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager.MyTagFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager.PatientEMRFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;

public class PatientDetailFragment extends BaseFragment {

    public static void launch(Activity from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launch(from, PatientDetailFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";

    private static final String TEXT_ADD_TAG = "+添加标签";

    @BindView(R.id.tag)
    TagContainerLayout mTagContainerLayout;

    @BindView(R.id.tv_patient_desc)
    TextView mPatientDescView;

    private Patient mPatient;

    @OnClick(R.id.ll_EMR)
    void EMRClick() {
        PatientEMRFragment.launch(_mActivity, mPatient);
    }

    @OnClick(R.id.ll_tag)
    void tagClick() {
        MyTagFragment.launch(this, mPatient);
    }

    @OnClick(R.id.ll_patient_desc)
    void patientDescClick() {
        ModifyPatientDescribeFragment.launch(this, mPatient);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_patient_detail;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        setupToolbar();
        setupPatientViews(view);
        setupPatientDescribeView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyTagFragment.REQUEST_MY_TAG) {
                if (data != null) {
                    ArrayList<PatientTag> tags =
                            (ArrayList<PatientTag>) data.getSerializableExtra(MyTagFragment.KEY_TAGS);
                    mPatient.setPatientTags(tags);
                    mTagContainerLayout.removeAllTags();

                    if (mPatient.getPatientTags() == null || mPatient.getPatientTags().isEmpty()) {
                        mTagContainerLayout.setTags(TEXT_ADD_TAG);
                    } else {
                        List<String> tagNames = new ArrayList<>();
                        for (PatientTag patientTag : mPatient.getPatientTags()) {
                            tagNames.add(patientTag.getTagName());
                        }
                        mTagContainerLayout.setTags(tagNames);
                    }
                    EventBus.getDefault().post(new PatientOrTagChangedEvent());
                }
            } else if (requestCode == ModifyPatientDescribeFragment.REQUEST_MODIFY_PATIENT_DESC) {
                if (data != null) {
                    String describe = data.getStringExtra(ModifyPatientDescribeFragment.KEY_DESCRIBE);
                    mPatient.setDescribe(describe);
                    setupPatientDescribeView();
                    EventBus.getDefault().post(new PatientOrTagChangedEvent());
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(mPatient.getName());
    }

    private void setupPatientViews(View view) {
        ((RoundedImageView) ButterKnife.findById(view, R.id.riv_avatar)).setImageResource(mPatient.getAvatarDrawableRes());
        ((TextView) ButterKnife.findById(view, R.id.tv_name)).setText(mPatient.getName());
        ((TextView) ButterKnife.findById(view, R.id.tv_health)).setText(mPatient.getMedicalInsuranceName());
        ((TextView) ButterKnife.findById(view, R.id.tv_gender)).setText(mPatient.getFriendlySex());
        ((TextView) ButterKnife.findById(view, R.id.tv_age)).setText(mPatient.getAge() + "岁");

        List<PatientTag> patientTags = mPatient.getPatientTags();
        if (patientTags == null || patientTags.isEmpty()) {
            mTagContainerLayout.setTags(TEXT_ADD_TAG);
        } else {
            List<String> tagNames = new ArrayList<>();
            for (PatientTag patientTag : patientTags) {
                tagNames.add(patientTag.getTagName());
            }
            mTagContainerLayout.setTags(tagNames);
        }
    }

    private void setupPatientDescribeView() {
        String describe = mPatient.getDescribe();
        if (!TextUtils.isEmpty(describe)) {
            mPatientDescView.setText(describe);
        } else {
            mPatientDescView.setText("暂无描述");
        }
    }
}
