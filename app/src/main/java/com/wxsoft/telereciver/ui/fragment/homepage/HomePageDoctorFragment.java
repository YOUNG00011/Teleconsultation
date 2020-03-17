package com.wxsoft.telereciver.ui.fragment.homepage;

import android.os.Bundle;
import android.view.View;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseFragment;

public class HomePageDoctorFragment extends BaseFragment {

    public static HomePageDoctorFragment newInstance() {
        return new HomePageDoctorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_fragment_container;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, HomePageContentFragment.newInstance());
        }
    }
}
