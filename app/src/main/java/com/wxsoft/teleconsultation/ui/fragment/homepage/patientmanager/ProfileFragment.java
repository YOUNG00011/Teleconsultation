package com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;

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
