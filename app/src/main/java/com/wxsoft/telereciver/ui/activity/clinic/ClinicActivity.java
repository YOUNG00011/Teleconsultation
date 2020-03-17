package com.wxsoft.telereciver.ui.activity.clinic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll.ClinicCallTheRollFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.centre.ClinicCentreFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.MyApplyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

//TODO: to be delete
public class ClinicActivity extends SupportBaseActivity {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, ClinicActivity.class));
    }



    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private int mCurrentPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clinic;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        setupToolbar();
        setupViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clinic, menu);
        menu.findItem(R.id.action).setVisible(mCurrentPosition == 0 ? true : false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.call_the_roll:
                ClinicCallTheRollFragment.launch(this);
                return true;

            case R.id.center:
                ClinicCentreFragment.launch(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.consultation_manager_title);
    }

    private void setupViewPager() {
        mViewPager.setAdapter(new ClinicPageAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    private class ClinicPageAdapter extends FragmentPagerAdapter {

        private  final String[] TABS = {
                getString(R.string.consultation_manager_tab_text_1),
                getString(R.string.consultation_manager_tab_text_2)
        };

        public ClinicPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TABS[position];
        }

        @Override
        public Fragment getItem(int position) {
            return MyApplyFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TABS.length;
        }
    }
}
