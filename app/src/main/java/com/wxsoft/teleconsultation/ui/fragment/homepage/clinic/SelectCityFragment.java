package com.wxsoft.teleconsultation.ui.fragment.homepage.clinic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.City;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;

import java.util.ArrayList;

import butterknife.BindView;

public class SelectCityFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectCityFragment.class, null, REQUEST_SELECT_CITY);
    }

    public static final int REQUEST_SELECT_CITY = 33;
    public static final String KEY_PROVINCE = "KEY_PROVINCE";
    public static final String KEY_CITY = "KEY_CITY";
    public static final String KEY_AREA = "KEY_AREA";

    @BindView(R.id.tv_location)
    TextView mLocationView;

    @BindView(R.id.recycler_view_province)
    EasyRecyclerView mProvinceRecyclerView;

    @BindView(R.id.recycler_view_city)
    EasyRecyclerView mCityRecyclerView;

    @BindView(R.id.recycler_view_area)
    EasyRecyclerView mAreaRecyclerView;

    private RecyclerArrayAdapter<City> mProvinceAdapter;
    private RecyclerArrayAdapter<City> mCityAdapter;
    private RecyclerArrayAdapter<City> mAreaAdapter;

    private City mProvince;
    private City mCity;
    private City mArea;
    private ArrayList<City> mCities = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_city;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mCities.addAll(App.mCities);
        setupProvinceRecyclerView();
        setupCityRecyclerView();
        setupAreaRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_city_title);
    }

    private void setupProvinceRecyclerView() {
        mProvinceRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mProvinceRecyclerView.addItemDecoration(itemDecoration);

        mProvinceRecyclerView.setAdapter(mProvinceAdapter = new RecyclerArrayAdapter<City>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CityViewHolder(parent, _mActivity);
            }
        });

        mProvinceAdapter.setOnItemClickListener(position -> {
            mProvince = mProvinceAdapter.getItem(position);
            mProvinceAdapter.notifyDataSetChanged();

            // 清空区域列表
            mAreaAdapter.clear();

            // 更新城市列表
            mCityAdapter.clear();
            mCityAdapter.addAll(mProvince.getCityList());
            mCityAdapter.removeAllHeader();
            mCityAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
                @Override
                public View onCreateView(ViewGroup parent) {
                    return View.inflate(_mActivity, R.layout.comm_item_one_text, null);
                }

                @Override
                public void onBindView(View headerView) {
                    TextView textView = (TextView) headerView;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(_mActivity, 48f));
                    textView.setLayoutParams(params);
                    textView.setText("不限");
                    textView.setGravity(Gravity.CENTER);

                    textView.setOnClickListener(view -> {
                        if (mCity != null) {
                            mCity = null;
                        }
                        finish();
                    });
                }
            });
        });

        mProvinceAdapter.addAll(mCities);
    }

    private void setupCityRecyclerView() {
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        itemDecoration.setDrawHeaderFooter(true);
        mCityRecyclerView.addItemDecoration(itemDecoration);

        mCityRecyclerView.setAdapter(mCityAdapter = new RecyclerArrayAdapter<City>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CityViewHolder(parent, _mActivity);
            }
        });

        mCityAdapter.setOnItemClickListener(position -> {
            mCity = mCityAdapter.getItem(position);
            mCityAdapter.notifyDataSetChanged();

            // 更新区域列表
            mAreaAdapter.clear();
            mAreaAdapter.addAll(mCity.getCityList());
            mAreaAdapter.removeAllHeader();
            mAreaAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
                @Override
                public View onCreateView(ViewGroup parent) {
                    return View.inflate(_mActivity, R.layout.comm_item_one_text, null);
                }

                @Override
                public void onBindView(View headerView) {
                    TextView textView = (TextView) headerView;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(_mActivity, 48f));
                    textView.setLayoutParams(params);
                    textView.setText("不限");
                    textView.setGravity(Gravity.CENTER);

                    textView.setOnClickListener(view -> {
                        if (mArea != null) {
                            mArea = null;
                        }
                        finish();
                    });
                }
            });
        });
    }

    private void setupAreaRecyclerView() {
        mAreaRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        itemDecoration.setDrawHeaderFooter(true);
        mAreaRecyclerView.addItemDecoration(itemDecoration);

        mAreaRecyclerView.setAdapter(mAreaAdapter = new RecyclerArrayAdapter<City>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CityViewHolder(parent, _mActivity);
            }
        });

        mAreaAdapter.setOnItemClickListener(position -> {
            mArea = mAreaAdapter.getItem(position);
            finish();
        });
    }

    private void finish() {
        Intent intent = new Intent();
        intent.putExtra(KEY_PROVINCE, mProvince);
        intent.putExtra(KEY_CITY, mCity);
        intent.putExtra(KEY_AREA, mArea);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class CityViewHolder extends BaseViewHolder<City> {

        private Context mContext;
        private TextView mTitleView;

        public CityViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.comm_item_one_text);

            mContext = context;

            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(City data) {
            super.setData(data);
            mTitleView.setText(data.getName());
            mTitleView.setGravity(Gravity.CENTER);

            if ((mProvince != null && data.getId().equals(mProvince.getId())) ||
                    (mCity != null && data.getId().equals(mCity.getId()))) {
                mTitleView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                mTitleView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mTitleView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                mTitleView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
    }
}
