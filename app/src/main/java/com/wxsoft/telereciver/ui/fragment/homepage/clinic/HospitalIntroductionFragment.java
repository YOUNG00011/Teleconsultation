package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HospitalIntroductionFragment extends BaseFragment {

    public static void launch(Activity from, Hospital hospital) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        FragmentContainerActivity.launch(from, HospitalIntroductionFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_HOSPITAL = "FRAGMENT_ARGS_HOSPITAL";

    private Hospital mHospital;

    @OnClick(R.id.iv_call)
    void callClick() {
        if (TextUtils.isEmpty(mHospital.getTelphone())) {
            ViewUtil.showMessage("该医院没有电话");
            return;
        }

        new MaterialDialog.Builder(_mActivity)
                .content(mHospital.getTelphone())
                .positiveText("呼叫")
                .negativeText("取消")
                .onPositive((dialog, which) -> {
                    new RxPermissions(_mActivity)
                            .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    Uri data = Uri.parse("tel:" + mHospital.getTelphone());
                                    intent.setData(data);
                                    startActivity(intent);
                                } else {
                                    ViewUtil.showMessage("授权已取消");
                                }
                            });
                })
                .show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hospital_introduction;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mHospital = (Hospital) getArguments().getSerializable(FRAGMENT_ARGS_HOSPITAL);
        setupToolbar();
        setupHospitalViews(view);
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.hospital_home_title);
    }

    private void setupHospitalViews(View view) {
        ImageView coverView = ButterKnife.findById(view, R.id.iv_cover);
        RequestManager glide = Glide.with(this);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_hospital_cover)
                .error(R.drawable.ic_hospital_cover)
                .centerCrop()
                .dontAnimate();

        glide.setDefaultRequestOptions(options)
                .load(mHospital.getImageUrl())
                .into(coverView);
        ((TextView) ButterKnife.findById(view, R.id.tv_address)).setText(mHospital.getAddress());
        ((TextView) ButterKnife.findById(view, R.id.tv_introduce)).setText(mHospital.getIntroduce());
    }
}
