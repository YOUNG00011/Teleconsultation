package com.wxsoft.teleconsultation.ui.fragment.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.BaseLazyFragment;

public class UserFragment extends BaseLazyFragment {

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_fragment_container;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void setupLazyView(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, UserContentFragment.newInstance());
        } else { // 这里可能会出现该Fragment没被初始化时,就被强杀导致的没有load子Fragment
            if (findChildFragment(UserContentFragment.class) == null) {
                loadRootFragment(R.id.fl_container, UserContentFragment.newInstance());
            }
        }
    }
}
