package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine.select;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.PrescriptionListFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.PrescriptionCallTheRollFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine.MedicineTreeFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

public class PrescriptionSelectManageFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, PrescriptionSelectManageFragment.class, null);
    }

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private RecyclerArrayAdapter<PatientManagerTag> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diseasecounseling_manager;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setHasOptionsMenu(true);
        setupViewPager();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search2, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                MedicineSearchFragment.launch(_mActivity);
                _mActivity.finish();
                return true;

            default:
                return true;
        }
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
        FragmentContainerActivity activity = (FragmentContainerActivity) _mActivity;
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.prescription_manager_title);
        setHasOptionsMenu(true);
    }


    private void setupViewPager() {
        mViewPager.setAdapter(new PageAdapter(_mActivity.getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                _mActivity.invalidateOptionsMenu();
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
          getString(R.string.title_all_medicine_type)
          ,
          getString(R.string.title_common_medicine)
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

            if(position==0) {
                return MedicineTreeFragment.newInstance();
            }else{
                return CommonUseMedicineFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return TABS.length;
        }
    }

}
