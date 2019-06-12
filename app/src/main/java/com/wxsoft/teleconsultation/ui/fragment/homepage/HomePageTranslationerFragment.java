package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.base.BaseLazyFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.clinic.MyApplyFragment;
import com.wxsoft.teleconsultation.ui.fragment.message.MessageContentFragment;

import butterknife.ButterKnife;

public class HomePageTranslationerFragment extends BaseLazyFragment {

    public static HomePageTranslationerFragment newInstance() {
        return new HomePageTranslationerFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_fragment_container_with_toolbar;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        ((TextView) ButterKnife.findById(view, R.id.toolbar_title)).setText(R.string.home_tab_text_1);
    }

    @Override
    protected void setupLazyView(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, MyApplyFragment.newInstance(1));
        } else { // 这里可能会出现该Fragment没被初始化时,就被强杀导致的没有load子Fragment
            if (findChildFragment(MessageContentFragment.class) == null) {
                loadRootFragment(R.id.fl_container, MyApplyFragment.newInstance(1));
            }
        }
    }
}
