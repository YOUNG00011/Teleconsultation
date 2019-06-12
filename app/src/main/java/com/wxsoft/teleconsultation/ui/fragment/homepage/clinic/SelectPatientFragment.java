package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientEMR;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectPatientFragment extends BaseFragment {

    private boolean need=true;
    public static void launch(Fragment from,String businessType,boolean need) {
        FragmentArgs args=new FragmentArgs();
        args.add(KEY_BUSINESSTYPE,businessType);
        args.add(FRAGMENT_ARGS_NEED_SURE,need);
        FragmentContainerActivity.launchForResult(from, SelectPatientFragment.class, args, REQUEST_SELECT_PATIENT);
    }

    public static void launch(Fragment from,String businessType) {
        launch(from,businessType,true);
    }

    public static final int REQUEST_SELECT_PATIENT = 35;
    public static final String KEY_PATIENT = "KEY_PATIENT";

    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";
    public static final String KEY_BUSINESSTYPE = "KEY_BUSINESSTYPE";


    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Patient> mAdapter;

    private String businesstype;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        businesstype=getArguments().getString(KEY_BUSINESSTYPE);
        need=getArguments().getBoolean(FRAGMENT_ARGS_NEED_SURE,true);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_two_icon,menu);
        MenuItem action1 = menu.findItem(R.id.action_1);
        action1.setTitle("搜索患者");
        action1.setIcon(R.drawable.ic_search_white_24dp);

        MenuItem action2 = menu.findItem(R.id.action_2);
        action2.setTitle("添加患者");
        action2.setIcon(R.drawable.ic_add_white_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_1:
                PatientSearchFragment.launchForResult(this);
                return true;

            case R.id.action_2:
                PatientAddFragment.launchForResult(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PatientInfoConfirmFragment.REQUEST_PATIENT_INFO_CONFIRM
                    ||requestCode == REQUEST_SELECT_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    finish(patient, patientEMR);
                }
            } else if (requestCode == PatientSearchFragment.REQUEST_SEARCH_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientSearchFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    finish(patient, patientEMR);
                }
            } else if (requestCode == PatientAddFragment.REQUEST_ADD_PATIENT) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientAddFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    finish(patient, patientEMR);
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_consultation_select_patient);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
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

        loadData();
    }

    private void loadData() {
        String doctId = AppContext.getUser().getDoctId();
        //ApiFactory.getPatientManagerApi().getHistoryPatient(doctId,businesstype)
        ApiFactory.getPatientManagerApi().getHistoryPatient(doctId)
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

    private void processResponse(BaseResp<List<Patient>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.showError();
        }

        List<Patient> patients = resp.getData();
        if (patients == null ) {
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(patients);

        mAdapter.removeAllHeader();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_select_patient, null);
            }

            @Override
            public void onBindView(View headerView) {
                //((TextView) headerView).setText(R.string.select_patient_history);
                TextView textView = headerView.findViewById(R.id.tv_group_title);
                textView.setText(R.string.select_patient_history);
                headerView.findViewById(R.id.ll_root).setOnClickListener(v ->{
                    MyFollowPatientFragment.launchForResult(SelectPatientFragment.this);
                });
            }
        });
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

}
