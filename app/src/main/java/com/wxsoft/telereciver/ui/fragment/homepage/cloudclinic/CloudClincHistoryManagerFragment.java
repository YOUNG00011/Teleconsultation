package com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CloudClincHistoryManagerFragment extends BaseFragment {

    public static CloudClincHistoryManagerFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_POSTION, position);
        CloudClincHistoryManagerFragment fragment = new CloudClincHistoryManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static  final String EXTRA_KEY_POSTION="EXTRA_KEY_POSTION";

    @BindView(R.id.tabs_history)
    TabLayout mPagerSlidingTabStrip;
//    @BindView(R.id.tabs_history)
//    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_pager_history)
    ViewPager mViewPager;

    private int mCurrentPosition = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cloudclinc_history_manager;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        mCurrentPosition=getArguments().getInt(EXTRA_KEY_POSTION);
        setupToolbar();
        setupViewPager();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupToolbar() {

    }


    private void setupViewPager() {
        PageAdapter adapter=new PageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mPagerSlidingTabStrip.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }
    private class PageAdapter extends FragmentStatePagerAdapter {


        List<CloudClincHistoryListFragment> fragments=new ArrayList<>();

        List<TabInfo> tab=new ArrayList<>();

        private final class TabInfo{
            public String status;
            public String title;

            public TabInfo(String status,String title){
                this.status=status;
                this.title=title;
            }
        }

        public PageAdapter(FragmentManager fm) {

            super(fm);

            tab.add(new TabInfo(null,getResources().getString(R.string.status_502_0000)))  ;
            if(mCurrentPosition==0){

                tab.add(new TabInfo("502-0001",getResources().getString(R.string.status_502_0001)));
                tab.add(new TabInfo("502-0002",getResources().getString(R.string.status_502_0002)));
                tab.add(new TabInfo("502-0003",getResources().getString(R.string.status_502_0003)));
                tab.add(new TabInfo("502-0004",getResources().getString(R.string.status_502_0004)));
            }else{
                tab.add(new TabInfo("502-0003",getResources().getString(R.string.status_502_0003)));
                tab.add(new TabInfo("502-0004",getResources().getString(R.string.status_502_0004)));
            }

            for(TabInfo tb:tab){
                CloudClincHistoryListFragment fragment=CloudClincHistoryListFragment.newInstance(mCurrentPosition,tb.status);

                fragments.add(fragment);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab.get(position).title;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return tab.size();
        }


    }


}
