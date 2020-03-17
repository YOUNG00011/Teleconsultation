package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.event.UpdateClinicStateEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FinishClinicFragment extends BaseFragment {

    public static void launchForFinish(Activity from,
                                       ArrayList<Doctor> doctors,
                                       String clinicId,
                                       boolean isMyApply,
                                       String consultationFeedbackTranslatesJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_ACTION, ACTION_FINISH);
        args.add(FRAGMENTARGS_KEY_DOCTORS, doctors);
        args.add(FRAGMENTARGS_KEY_CLINIC_ID, clinicId);
        args.add(FRAGMENTARGS_KEY_IS_MYAPPLY, isMyApply);
        args.add(FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON, consultationFeedbackTranslatesJson);
        FragmentContainerActivity.launch(from, FinishClinicFragment.class, args);
    }

    public static void launchForCancel(Activity from,
                                       ArrayList<Doctor> doctors,
                                       String clinicId,
                                       boolean isMyApply,
                                       String consultationFeedbackTranslatesJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_ACTION, ACTION_CANCEL);
        args.add(FRAGMENTARGS_KEY_DOCTORS, doctors);
        args.add(FRAGMENTARGS_KEY_CLINIC_ID, clinicId);
        args.add(FRAGMENTARGS_KEY_IS_MYAPPLY, isMyApply);
        args.add(FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON, consultationFeedbackTranslatesJson);
        FragmentContainerActivity.launch(from, FinishClinicFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_ACTION = "FRAGMENTARGS_KEY_ACTION";
    private static final String FRAGMENTARGS_KEY_DOCTORS = "FRAGMENTARGS_KEY_DOCTORS";
    private static final String FRAGMENTARGS_KEY_CLINIC_ID = "FRAGMENTARGS_KEY_CLINIC_ID";
    private static final String FRAGMENTARGS_KEY_IS_MYAPPLY = "FRAGMENTARGS_KEY_IS_MYAPPLY";
    private static final String FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON = "FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON";

    private static final int ACTION_FINISH = 1;
    private static final int ACTION_CANCEL = 2;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.et_remark)
    EditText mRemarkView;

    private RecyclerArrayAdapter<Doctor> mAdapter;
    private int mAction;
    private ArrayList<Doctor> mDoctors;
    private String mClinicId;
    private boolean isMyApply;
    private String mConsultationFeedbackTranslatesJson;

//    @OnClick(R.id.tv_detail)
//    void detailClick() {
//        ClinicAdviceFragment.launch(_mActivity, mClinicId, isMyApply, mConsultationFeedbackTranslatesJson);
//    }

    @OnTextChanged(R.id.et_remark)
    void remarkChanged(CharSequence s, int start, int before, int count) {
        _mActivity.invalidateOptionsMenu();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_finish_clinic;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mAction = bundle.getInt(FRAGMENTARGS_KEY_ACTION);
        mDoctors = (ArrayList<Doctor>) bundle.getSerializable(FRAGMENTARGS_KEY_DOCTORS);
        mClinicId = bundle.getString(FRAGMENTARGS_KEY_CLINIC_ID);
        isMyApply = bundle.getBoolean(FRAGMENTARGS_KEY_IS_MYAPPLY);
        mConsultationFeedbackTranslatesJson = bundle.getString(FRAGMENTARGS_KEY_CONSULTATION_FEEDBACK_TRANSLATES_JSON);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one, menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle(R.string.ok);
        menuItem.setVisible(mRemarkView.getText().length() > 0 ? true : false);
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

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.end_consultation_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        if (mDoctors == null || mDoctors.isEmpty()) return;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Doctor>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new DoctorViewHolder(parent, Glide.with(FinishClinicFragment.this));
            }
        });

        mAdapter.addAll(mDoctors);
    }

    private void commit() {
        String content = mRemarkView.getText().toString();
        ViewUtil.createProgressDialog(_mActivity, "");
        Observable<BaseResp> observable = null;
        if (mAction == ACTION_FINISH) {
            observable =  ApiFactory.getClinicManagerApi().completedConsultation(mClinicId, content);
        } else if (mAction == ACTION_CANCEL) {
            observable =  ApiFactory.getClinicManagerApi().cancelConsultation(mClinicId, content);
        }

        if (observable == null) return;

        observable
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
                            if (mAction == ACTION_FINISH ||
                                    mAction == ACTION_CANCEL) {
                                EventBus.getDefault().post(new UpdateClinicStateEvent());
                                _mActivity.finish();
                            }
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private class DoctorViewHolder extends BaseViewHolder<Doctor> {

        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mStatusView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        public DoctorViewHolder(ViewGroup parent, RequestManager glide) {
            super(parent, R.layout.item_finish_clinic_doctor);
            mGlide = glide;

            mAvatarView = $(R.id.iv_avatar);
            mNameView = $(R.id.tv_name);
            mStatusView = $(R.id.tv_status);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(Doctor data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mStatusView.setText(data.getStatusName());

            mGlide.setDefaultRequestOptions(mOptions.error(data.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                    .load(data.getUserImgUrl())
                    .into(mAvatarView);
        }
    }
}
