package com.wxsoft.telereciver.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;

public class ProfileFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, ProfileFragment.class, null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

    }
}
