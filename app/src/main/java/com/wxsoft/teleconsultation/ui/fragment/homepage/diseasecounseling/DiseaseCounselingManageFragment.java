package com.wxsoft.teleconsultation.ui.fragment.homepage.diseasecounseling;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class DiseaseCounselingManageFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, DiseaseCounselingManageFragment.class, null);
    }



    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private int mCurrentPosition = 0;

    private RecyclerArrayAdapter<PatientManagerTag> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diseasecounseling_manager;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setupViewPager();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof PatientOrTagChangedEvent) {
           // loadData();
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.diseasecounseling_manager_title);
        setHasOptionsMenu(true);
    }


    private void setupViewPager() {
        mViewPager.setAdapter(new PageAdapter(getActivity().getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }
    private class PageAdapter extends FragmentPagerAdapter {

        private  final String[] TABS = {
                getString(R.string.diseasecounseling_manager_tab_text_1),
                getString(R.string.diseasecounseling_manager_tab_text_2)
        };

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TABS[position];
        }

        @Override
        public Fragment getItem(int position) {
            return DiseaseCounselingListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TABS.length;
        }
    }


}
