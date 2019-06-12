package com.wxsoft.teleconsultation.ui.activity;

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
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.ModifyPatientDescribeFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager.MyTagFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager.PatientEMRFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;

public class PatientDetailActivity extends SupportBaseActivity{

    public static void launch(Activity from, Patient patient) {
        Intent intent = new Intent(from, PatientDetailActivity.class);
        intent.putExtra(EXTRA_PATIENT, patient);
        from.startActivity(intent);
    }

    private static final String EXTRA_PATIENT = "EXTRA_PATIENT";

    @BindView(R.id.tag)
    TagContainerLayout mTagContainerLayout;

    @BindView(R.id.tv_patient_desc)
    TextView mPatientDescView;

    @BindString(R.string.patient_detail_add_tags)
    String mAddTagsString;

    private Patient mPatient;

    @OnClick(R.id.iv_back)
    void backClick() {
        finish();
    }

    @OnClick(R.id.ll_EMR)
    void EMRClick() {
        PatientEMRFragment.launch(this, mPatient);
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
    protected void setupViews(Bundle savedInstanceState) {

        mPatient = (Patient) getIntent().getSerializableExtra(EXTRA_PATIENT);
        setupPatientViews();
        setupPatientDescribeView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MyTagFragment.REQUEST_MY_TAG) {
                if (data != null) {
                    ArrayList<PatientTag> tags =
                            (ArrayList<PatientTag>) data.getSerializableExtra(MyTagFragment.KEY_TAGS);
                    mPatient.setPatientTags(tags);
                    mTagContainerLayout.removeAllTags();

                    if (mPatient.getPatientTags() == null || mPatient.getPatientTags().isEmpty()) {
                        mTagContainerLayout.setTags(mAddTagsString);
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

    private void setupPatientViews() {
        ((RoundedImageView) ButterKnife.findById(this, R.id.riv_avatar)).setImageResource(mPatient.getAvatarDrawableRes());
        ((TextView) ButterKnife.findById(this, R.id.tv_name)).setText(mPatient.getName());
        ((TextView) ButterKnife.findById(this, R.id.tv_health)).setText(mPatient.getMedicalInsuranceName());
        ((TextView) ButterKnife.findById(this, R.id.tv_gender)).setText(mPatient.getFriendlySex());
        ((TextView) ButterKnife.findById(this, R.id.tv_age)).setText(String.valueOf(mPatient.getAge()));

        List<PatientTag> patientTags = mPatient.getPatientTags();
        if (patientTags == null || patientTags.isEmpty()) {
            mTagContainerLayout.setTags(mAddTagsString);
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
            mPatientDescView.setText(R.string.patient_detail_no_description);
        }
    }
}
