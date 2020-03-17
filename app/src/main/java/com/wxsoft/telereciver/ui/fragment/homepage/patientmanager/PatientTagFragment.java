package com.wxsoft.telereciver.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.event.PatientOrTagChangedEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.PatientInfoConfirmFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientTagFragment extends BaseFragment {

    public static void launch(Activity from, String title, int patientCount, String tagId, boolean isAll) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_PATIENT_COUNT, patientCount);
        args.add(FRAGMENT_ARGS_TAG_ID, tagId);
        args.add(FRAGMENT_ARGS_TAG_ISALL, isAll);
        FragmentContainerActivity.launch(from, PatientTagFragment.class, args);
    }

    public static void launchForResult(Fragment from, String title, int patientCount, String tagId, boolean isAll,boolean need) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TITLE, title);
        args.add(FRAGMENT_ARGS_PATIENT_COUNT, patientCount);
        args.add(FRAGMENT_ARGS_TAG_ID, tagId);
        args.add(FRAGMENT_ARGS_TAG_ISALL, isAll);
        args.add(FRAGMENT_SELECLT_PATIENT, true);
        args.add(FRAGMENT_SELECLT_NEED, need);
        FragmentContainerActivity.launchForResult(from, PatientTagFragment.class, args,REQUEST_SELECT_PATIENT);
    }


    public static final int REQUEST_SELECT_PATIENT = 248;
    private static final String FRAGMENT_ARGS_TITLE = "FRAGMENT_ARGS_TITLE";
    private static final String FRAGMENT_SELECLT_PATIENT = "FRAGMENT_SELECLT_PATIENT";
    private static final String FRAGMENT_SELECLT_NEED = "FRAGMENT_SELECLT_NEED";
    private static final String FRAGMENT_ARGS_PATIENT_COUNT = "FRAGMENT_ARGS_PATIENT_COUNT";
    private static final String FRAGMENT_ARGS_TAG_ID = "FRAGMENT_ARGS_TAG_ID";
    private static final String FRAGMENT_ARGS_TAG_ISALL = "FRAGMENT_ARGS_TAG_ISALL";

    public static final String KEY_PATIENT = "KEY_PATIENT";

    private static final int STATE_NORMAL = 0;
    private static final int STATE_SEND_SMS = 1;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.ll_menu)
    LinearLayout mMenuView;

    @BindView(R.id.rl_select_patient)
    RelativeLayout mSelectPatientView;

    @BindView(R.id.tv_select_all)
    TextView mSelectAllView;

    @BindView(R.id.tv_ok)
    TextView mOkView;

    private String mTitle;
    private int mPatientCount;
    private String mTagId;
    private boolean isAll,isSelecting,isNeed;
    private RecyclerArrayAdapter<Patient> mAdapter;
    // view state
    private int mCurrentState = STATE_NORMAL;
    // selected patient
    private List<Patient> mSelectedPatients;

    @OnClick(R.id.tv_send_sms)
    void sendSMSClick() {
        openSelectView();
    }

    @OnClick(R.id.tv_cancel)
    void cancelClick() {
        closeSelectView();
    }

    @OnClick(R.id.tv_select_all)
    void selectAllClick() {
        Drawable drawable;
        if (mSelectedPatients.size() == mAdapter.getAllData().size()) {
            mSelectedPatients.clear();
            drawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_off);
            mOkView.setText("群发短信");
            mOkView.setTextColor(ContextCompat.getColor(_mActivity, R.color.comm_btn_disabled));
        } else {
            mSelectedPatients.clear();
            mSelectedPatients.addAll(mAdapter.getAllData());
            mOkView.setText("群发短信(" + mSelectedPatients.size() + ")");
            mOkView.setTextColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
            drawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_on);
        }

        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mSelectAllView.setCompoundDrawables(drawable, null, null, null);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_ok)
    void okClick() {
        if (!mSelectedPatients.isEmpty()) {
            SMSFragment.launch(_mActivity);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_patient_tag;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mTitle = getArguments().getString(FRAGMENT_ARGS_TITLE);
        mPatientCount = getArguments().getInt(FRAGMENT_ARGS_PATIENT_COUNT);
        mTagId = getArguments().getString(FRAGMENT_ARGS_TAG_ID);
        isAll = getArguments().getBoolean(FRAGMENT_ARGS_TAG_ISALL);
        isSelecting = getArguments().getBoolean(FRAGMENT_SELECLT_PATIENT);
        isNeed = getArguments().getBoolean(FRAGMENT_SELECLT_NEED);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(PatientOrTagChangedEvent patientOrTagChangedEvent) {
        loadData();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(mTitle);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Patient>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientTagViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            if(isSelecting){
                PatientInfoConfirmFragment.launch(this, mAdapter.getItem(position),isNeed);
            }else {
                if (mCurrentState == STATE_NORMAL) {
                    PatientDetailActivity.launch(_mActivity, mAdapter.getItem(position));
                } else {
                    Patient patient = mAdapter.getItem(position);
                    if (mSelectedPatients.contains(patient)) {
                        mSelectedPatients.remove(patient);
                    } else {
                        mSelectedPatients.add(patient);
                    }

                    if (mSelectedPatients.isEmpty()) {
                        mOkView.setText("群发短信");
                        mOkView.setTextColor(ContextCompat.getColor(_mActivity, R.color.comm_btn_disabled));
                    } else {
                        mOkView.setText("群发短信(" + mSelectedPatients.size() + ")");
                        mOkView.setTextColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
                    }

                    Drawable drawable;
                    if (mSelectedPatients.size() == mAdapter.getAllData().size()) {
                        drawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_on);
                    } else {
                        drawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_off);
                    }

                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mSelectAllView.setCompoundDrawables(drawable, null, null, null);

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        loadData();
    }

    private void loadData() {
        Observable<BaseResp<List<Patient>>> observable;
        String id = AppContext.getUser().getDoctId();
        if (isAll) {
            observable =  ApiFactory.getPatientManagerApi().getPatientInfosByDoctId(id);
        } else {
            observable = ApiFactory.getPatientManagerApi().getPatientInfoByTag(id, mTagId);
        }
        observable
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PatientInfoConfirmFragment.REQUEST_PATIENT_INFO_CONFIRM) {
                if (data != null) {
                    Patient patient = (Patient) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT);
                    PatientEMR patientEMR = (PatientEMR) data.getSerializableExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_PATIENT, patient);
                    intent.putExtra(PatientInfoConfirmFragment.KEY_PATIENT_EMR, patientEMR);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void processResponse(BaseResp<List<Patient>> resp) {
        List<Patient> patients = resp.getData();

        mAdapter.clear();
        mAdapter.addAll(patients);

        mAdapter.removeAllHeader();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.comm_item_group_title, null);
            }

            @Override
            public void onBindView(View headerView) {
                ((TextView) headerView).setText(mTitle + "(" + mPatientCount + ")");
            }
        });
    }

    private void openSelectView() {
        mCurrentState = STATE_SEND_SMS;
        mMenuView.setVisibility(View.GONE);
        mSelectPatientView.setVisibility(View.VISIBLE);
        if (mSelectedPatients == null) {
            mSelectedPatients = new ArrayList<>();
        } else {
            if (!mSelectedPatients.isEmpty()) {
                mSelectedPatients.clear();
                mOkView.setText("群发短信");
                mOkView.setTextColor(ContextCompat.getColor(_mActivity, R.color.comm_btn_disabled));
                Drawable drawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_off);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mSelectAllView.setCompoundDrawables(drawable, null, null, null);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void closeSelectView() {
        mCurrentState = STATE_NORMAL;
        mMenuView.setVisibility(View.VISIBLE);
        mSelectPatientView.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    private class PatientTagViewHolder extends BaseViewHolder<Patient> {

        private ImageView mCheckBoxView;
        private TextView mHealthView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private LinearLayout mTagsView;


        public PatientTagViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_patient);
            mAvatarView = $(R.id.iv_patient_avatar);
            mCheckBoxView = $(R.id.iv_checkbox);
            mNameView = $(R.id.tv_patient_name);
            mHealthView = $(R.id.tv_health);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mTagsView = $(R.id.ll_tag);
        }

        @Override
        public void setData(Patient data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mAvatarView.setImageResource(data.getAvatarDrawableRes());
            mGenderView.setText(data.getFriendlySex());
            mHealthView.setText(data.getMedicalInsuranceName());
            mAgeView.setText(String.valueOf(data.getAge()));

            mTagsView.removeAllViews();
            List<PatientTag> patientTags = data.getPatientTags();
            if (patientTags == null || patientTags.isEmpty()) {
                mTagsView.setVisibility(View.INVISIBLE);
            } else {
                mTagsView.setVisibility(View.VISIBLE);
                for (PatientTag patientTag : patientTags) {
                    mTagsView.addView(AppUtil.getTagTextView(_mActivity, patientTag.getTagName()));
                }
            }

            if (mCurrentState == STATE_NORMAL) {
                mAvatarView.setVisibility(View.VISIBLE);
                mCheckBoxView.setVisibility(View.GONE);
            } else {
                mAvatarView.setVisibility(View.GONE);
                mCheckBoxView.setVisibility(View.VISIBLE);
                int drawableRes;
                if (mSelectedPatients.contains(data)) {
                    drawableRes = R.drawable.ic_comm_checkbox_on;
                } else {
                    drawableRes = R.drawable.ic_comm_checkbox_off;
                }
                mCheckBoxView.setImageResource(drawableRes);
            }
        }
    }
}
