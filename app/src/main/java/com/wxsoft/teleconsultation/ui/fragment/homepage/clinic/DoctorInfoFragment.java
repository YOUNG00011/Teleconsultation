package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Clinic;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.user.info.InputFragment;
import com.wxsoft.teleconsultation.ui.fragment.user.info.UserInfoFragment;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DoctorInfoFragment extends BaseFragment {

    public static void launch(Activity from, Doctor doctor) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_DOCTOR, doctor);
        FragmentContainerActivity.launch(from, DoctorInfoFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_DOCTOR = "FRAGMENTARGS_KEY_DOCTOR";
    private static final int NO_SETUP = 0;
    private static final int NO_FOCUS = 1;
    private static final int HAS_FOCUSED = 2;

    private Doctor mDoctor;

    private int mFocusFlag = NO_SETUP;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_doctor_info;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mDoctor = (Doctor) getArguments().getSerializable(FRAGMENTARGS_KEY_DOCTOR);
        setupToolbar();
        loadFocusStatus();
        setupDoctorInfoViews(view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        boolean isMe = mDoctor.getId().equals(AppContext.getUser().getDoctId());
        if (isMe) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
            String title = "";
            if (mFocusFlag == NO_FOCUS) {
                title = getString(R.string.doctor_detail_follow);
            } else if (mFocusFlag == HAS_FOCUSED) {
                title = getString(R.string.doctor_detail_unfollow);
            }
            menuItem.setTitle(title);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                if (mFocusFlag != NO_SETUP) {
                    commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = String.format(getString(R.string.doctor_detail_title), mDoctor.getName());
        activity.getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    private void loadFocusStatus() {
        ApiFactory.getClinicManagerApi().getAttentionStatus(mDoctor.getId(), AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResp -> {
                    if (baseResp.isSuccess()) {
                        boolean isFocus = baseResp.getData();
                        if (isFocus) {
                            mFocusFlag = HAS_FOCUSED;
                        } else {
                            mFocusFlag = NO_FOCUS;
                        }
                        _mActivity.invalidateOptionsMenu();
                    }
                });
    }

    private void setupDoctorInfoViews(View view) {
        RequestManager glide = Glide.with(DoctorInfoFragment.this);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .error(mDoctor.isMan() ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        RoundedImageView avatarView = ButterKnife.findById(view, R.id.riv_avatar);
        avatarView.setOval(true);
        glide.setDefaultRequestOptions(options)
                .load(mDoctor.getUserImgUrl())
                .thumbnail(0.5f)
                .into(avatarView);

        ((TextView) ButterKnife.findById(view, R.id.tv_name)).setText(mDoctor.getName());
        ((TextView) ButterKnife.findById(view, R.id.tv_hospital_and_department)).setText(mDoctor.getHospitalName() + "    " + mDoctor.getDepartmentName());
        TextView educationAndYearworkView = ButterKnife.findById(view, R.id.tv_education_and_yearwork);
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mDoctor.getEducationName())) {
            sb.append(String.format(getString(R.string.doctor_detail_education), mDoctor.getEducationName()));
        }

        int yearWork = (int) mDoctor.getYearWork();
        if (yearWork > 0) {
            if (sb.length() > 0) {
                sb.append("    ");
            }
            sb.append(String.format(getString(R.string.doctor_detail_work_year), yearWork));
        }

        if (sb.length() > 0) {
            educationAndYearworkView.setVisibility(View.VISIBLE);
            educationAndYearworkView.setText(sb.toString());
        } else {
            educationAndYearworkView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mDoctor.getDynamic())) {
            TextView dynamicView = ButterKnife.findById(view, R.id.tv_dynamic);
            dynamicView.setVisibility(View.VISIBLE);
            dynamicView.setText(mDoctor.getDynamic());
        }

        if (!TextUtils.isEmpty(mDoctor.getIntroduce())) {
            TextView introduceView = ButterKnife.findById(view, R.id.tv_introduce);
            introduceView.setVisibility(View.VISIBLE);
            introduceView.setText(mDoctor.getIntroduce());
        }

        if (!TextUtils.isEmpty(mDoctor.getGoodAt())) {
            TextView goodatView = ButterKnife.findById(view, R.id.tv_goodat);
            goodatView.setVisibility(View.VISIBLE);
            goodatView.setText(mDoctor.getGoodAt());
        }

        if (!TextUtils.isEmpty(mDoctor.getAchievement())) {
            TextView achievementView = ButterKnife.findById(view, R.id.tv_achievement);
            achievementView.setVisibility(View.VISIBLE);
            achievementView.setText(mDoctor.getAchievement());
        }
    }

    private void commit() {
        ViewUtil.createProgressDialog(_mActivity, "");
        int isAttention = 0;
        if (mFocusFlag == NO_FOCUS) {
            isAttention = 1;
        }
        ApiFactory.getClinicManagerApi().saveFavoriteDoctor(AppContext.getUser().getDoctId(),
                AppContext.getUser().getName(),
                mDoctor.getId(),
                mDoctor.getName(),
                isAttention)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<Boolean> booleanBaseResp) {
                        ViewUtil.dismissProgressDialog();
                        if (booleanBaseResp.isSuccess()) {
                            if (mFocusFlag == NO_FOCUS) {
                                mFocusFlag = HAS_FOCUSED;
                            } else if (mFocusFlag ==  HAS_FOCUSED) {
                                mFocusFlag = NO_FOCUS;
                            }
                            _mActivity.invalidateOptionsMenu();
                        }
                    }
                });

    }
}
