package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientEMR;
import com.wxsoft.telereciver.entity.PatientManagerTag;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.patientmanager.PatientTagFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyFollowPatientFragment extends BaseFragment {

    private boolean need=true;
    private boolean hasResult;

    public static void launch(Fragment from,String businessType,boolean need) {
        FragmentArgs args=new FragmentArgs();
        args.add(KEY_BUSINESSTYPE,businessType);
        args.add(FRAGMENT_ARGS_NEED_SURE,need);
        FragmentContainerActivity.launchForResult(from, MyFollowPatientFragment.class, args, REQUEST_SELECT_PATIENT);
    }

    public static void launchForResult(Fragment from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, true);
        FragmentContainerActivity.launchForResult(from, MyFollowPatientFragment.class, args, REQUEST_SELECT_PATIENT);
    }

    public static final int REQUEST_SELECT_PATIENT = 35;
    public static final String KEY_PATIENT = "KEY_PATIENT";
    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";
    public static final String KEY_BUSINESSTYPE = "KEY_BUSINESSTYPE";


    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Patient> mAdapter;
    private RecyclerArrayAdapter<PatientManagerTag> tagAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        hasResult = getArguments().getBoolean(FRAGMENT_ARGS_HAS_RESULT);
        need=getArguments().getBoolean(FRAGMENT_ARGS_NEED_SURE,true);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PatientInfoConfirmFragment.REQUEST_PATIENT_INFO_CONFIRM
                    ||requestCode==PatientTagFragment.REQUEST_SELECT_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    finish(patient, patientEMR);
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_consultation_follow_patient);
    }

    private void setupRecyclerView() {

         tagAdapter= new RecyclerArrayAdapter<PatientManagerTag>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientManagerViewHolder(parent);
            }
        };

        tagAdapter.setOnItemClickListener(position -> {
            PatientManagerTag patientManagerTag = tagAdapter.getItem(position);
            boolean isAll = false;
            if (position == 0) {
                isAll = true;
            }
            PatientTagFragment.launchForResult(this,
                    patientManagerTag.getTagName(),
                    patientManagerTag.getPatientCount(),
                    patientManagerTag.getId(),
                    isAll,true);
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Patient>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientViewHolder(parent, _mActivity);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            PatientInfoConfirmFragment.launch(this, mAdapter.getItem(position),need);
        });

        mAdapter.removeAllHeader();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_select_patient2, null);
            }

            @Override
            public void onBindView(View headerView) {

                EasyRecyclerView recyclerViewService = ButterKnife.findById(headerView, R.id.recycler_view_service);

                LinearLayoutManager gridLayoutManager=new LinearLayoutManager(_mActivity);
                recyclerViewService.setLayoutManager(gridLayoutManager);

                DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
                recyclerViewService.addItemDecoration(itemDecoration);
                recyclerViewService.setAdapter(tagAdapter);
                TextView textView = ButterKnife.findById(headerView, R.id.tv_group_title);
                textView.setText(R.string.transfer_treatment_list_filter_text_1);

            }
        });
        loadData();
    }

    private void loadData() {
        String doctId = AppContext.getUser().getDoctId();

        ApiFactory.getPatientManagerApi().getPatientTags(AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<PatientManagerTag>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<PatientManagerTag>> resp) {
                        response(resp);
                    }
                });

        ApiFactory.getPatientManagerApi().getPatientInfosByDoctId(doctId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Patient>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Patient>> resp) {
                        processResponse(resp);
                    }
                });
    }


    private void response(BaseResp<List<PatientManagerTag>> resp) {
        List<PatientManagerTag> patientTags = resp.getData();

        for(PatientManagerTag tag : patientTags){
            if(tag.getId().equals("0")){
                patientTags.remove(tag);
                break;
            }
        }
        tagAdapter.clear();
        tagAdapter.addAll(patientTags);
    }

    private void processResponse(BaseResp<List<Patient>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
            return;
        }

        List<Patient> patients = resp.getData();
        if (patients == null || patients.isEmpty()) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(patients);

    }

    private void finish(Patient patient, PatientEMR patientEMR) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PATIENT, patient);
        intent.putExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR, patientEMR);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class PatientViewHolder extends BaseViewHolder<Patient> {

        private Context mContext;

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mHealthTypeView;
        private LinearLayout mTagView;

        public PatientViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.comm_item_patient);
            mContext = context;

            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mHealthTypeView = $(R.id.tv_health);
            mTagView = $(R.id.ll_tag);
        }

        @Override
        public void setData(Patient data) {
            super.setData(data);

            mAvatarView.setImageResource(data.getAvatarDrawableRes());
            mNameView.setText(data.getName());
            mGenderView.setText(data.getFriendlySex());
            mAgeView.setText(String.valueOf(data.getAge()));
            mHealthTypeView.setText(data.getMedicalInsuranceName());
            mTagView.removeAllViews();
            for (PatientTag patientTag : data.getPatientTags()) {
                mTagView.addView(AppUtil.getTagTextView(mContext, patientTag.getTagName()));
            }

        }
    }

    private class PatientManagerViewHolder extends BaseViewHolder<PatientManagerTag> {

        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mCountView;

        public PatientManagerViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_patient_manager);
            mIconView = $(R.id.iv_icon);
            mTitleView = $(R.id.tv_title);
            mCountView = $(R.id.tv_count);
        }

        @Override
        public void setData(PatientManagerTag data) {
            super.setData(data);
            if (data.getId().equals("0")) {
                mIconView.setImageResource(R.drawable.ic_all_patient_tag);
            } else {
                mIconView.setImageResource(R.drawable.ic_custom_tag);
            }
            mTitleView.setText(data.getTagName());
            mCountView.setText(String.valueOf(data.getPatientCount()));
        }
    }

}
