package com.wxsoft.telereciver.ui.activity.cloudclinc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.CloudClincHistoryManagerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

public class CloudClincHistoryActivity extends SupportBaseActivity {

    @BindView(R.id.types)
    SegmentedGroup types;

    @BindView(R.id.tabs_main)
    TabLayout tabs;

    @BindView(R.id.view_pager_main)
    ViewPager mViewPager;


    public static void launch(Activity from) {
        from.startActivity(new Intent(from,CloudClincHistoryActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cloudclinic_history;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        setupToolbar();
        setupView();
    }


    private void setupToolbar() {

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupView();

    }

    private void setupView() {

        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });;
        mViewPager.setCurrentItem(types.getCheckedRadioButtonId()==R.id.pag1?0:1,false);

        tabs.setupWithViewPager(mViewPager);
        types.setOnCheckedChangeListener((group, checkedId) -> mViewPager.setCurrentItem(checkedId==R.id.pag1?0:1,false));
    }

    private class PageAdapter extends FragmentPagerAdapter {

        private  final String[] TABS = {
                getString(R.string.str_cloudclinc_history_text1),
                getString(R.string.str_cloudclinc_history_text2)
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
            return CloudClincHistoryManagerFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TABS.length;
        }


    }
}
