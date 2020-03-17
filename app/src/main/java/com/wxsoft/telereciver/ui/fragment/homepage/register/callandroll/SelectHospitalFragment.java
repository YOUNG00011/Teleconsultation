package com.wxsoft.telereciver.ui.fragment.homepage.register.callandroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.City;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.entity.responsedata.QueryCityData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.HospitalIntroductionFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.SelectCityFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.register.RegisterSureFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectHospitalFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SelectHospitalFragment.class, null, REQUEST_SELECT_HOSPITAL);
    }

    public static final int REQUEST_SELECT_HOSPITAL = 64;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Hospital> mAdapter;
    private City mCurrentProvince;
    private City mCurrentCity;
    private City mCurrentArea;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setupRecyclerView();
        if (App.mCities.isEmpty()) {
            loadData();
        }
        loadHospitals();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_select_city,menu);
        View view = menu.findItem(R.id.action_address).getActionView();
        String cityName;
        if (mCurrentArea != null) {
            cityName = mCurrentArea.getName();
        } else if (mCurrentCity != null) {
            cityName = mCurrentCity.getName();
        } else if (mCurrentProvince != null) {
            cityName = mCurrentProvince.getName();
        } else {
            cityName = "";
        }
        ((TextView) view).setText(cityName);
        view.setOnClickListener(v ->  {
            SelectCityFragment.launch(this);
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectDepartmentFragment.REQUEST_SELECT_DEPARTMENT) {
                if (data != null) {
                    boolean doctor = data.getBooleanExtra(RegisterSureFragment.KEY_WORK_DONE, false);
                    Intent intent = new Intent();
                    intent.putExtra(RegisterSureFragment.KEY_WORK_DONE, doctor);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            } else if (requestCode == SelectCityFragment.REQUEST_SELECT_CITY) {
                if (data != null) {
                    mCurrentProvince = (City) data.getSerializableExtra(SelectCityFragment.KEY_PROVINCE);
                    mCurrentCity = (City) data.getSerializableExtra(SelectCityFragment.KEY_CITY);
                    mCurrentArea = (City) data.getSerializableExtra(SelectCityFragment.KEY_AREA);
                    _mActivity.invalidateOptionsMenu();

                    ViewUtil.createProgressDialog(_mActivity, "");
                    ApiFactory.getClinicManagerApi().getHospitals(mCurrentProvince != null ? mCurrentProvince.getId() : "",
                            mCurrentCity != null ? mCurrentCity.getId() : "",
                            mCurrentArea != null ? mCurrentArea.getId() : "",
                            AppContext.getUser().getHospitalId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BaseResp<List<Hospital>>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage(e.getMessage());
                                }

                                @Override
                                public void onNext(BaseResp<List<Hospital>> resp) {
                                    ViewUtil.dismissProgressDialog();
                                    processQueryResponse(resp);
                                }
                            });
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.select_doctor_search_by_hospital_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Hospital>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new HospitalViewHolder(parent, Glide.with(SelectHospitalFragment.this), hospital -> {
                    HospitalIntroductionFragment.launch(_mActivity, hospital);
                });
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
            SelectDepartmentFragment.launch(this, mAdapter.getItem(position), true, true);
        });


    }

    private void loadHospitals() {
        ApiFactory.getClinicManagerApi().getHospitals("", "", "",
                AppContext.getUser().getHospitalId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Hospital>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Hospital>> resp) {
                        processQueryResponse(resp);
                    }
                });
    }

    private void loadData() {
        ApiFactory.getCommApi().getCityData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryCityData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<QueryCityData> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryCityData> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        QueryCityData queryCityData = resp.getData();
        App.mCities.addAll(new Gson().fromJson(queryCityData.getCityData(), new TypeToken<ArrayList<City>>() {
        }.getType()));
    }

    private void processQueryResponse(BaseResp<List<Hospital>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(resp.getData());
    }

    private static class HospitalViewHolder extends BaseViewHolder<Hospital> {

        interface OnCoverClickListener {
            void onClick(Hospital hospital);
        }

        private ImageView mCoverView;
        private TextView mNameView;
        private TextView mLevelView;
        private TextView mAddressView;

        private RequestManager mGlide;
        private RequestOptions mOptions;

        private OnCoverClickListener mOnCoverClickListener;

        public HospitalViewHolder(ViewGroup parent, RequestManager glide, OnCoverClickListener onCoverClickListener) {
            super(parent, R.layout.comm_item_hospital);
            mGlide = glide;

            mCoverView = $(R.id.iv_cover);
            mNameView = $(R.id.tv_name);
            mLevelView = $(R.id.tv_level);
            mAddressView = $(R.id.tv_address);

            mOptions = new RequestOptions()
                    .centerCrop()
                    .dontAnimate();

            mOnCoverClickListener = onCoverClickListener;
        }

        @Override
        public void setData(Hospital data) {
            super.setData(data);
            mNameView.setText(data.getName());
            mLevelView.setText(data.getLevel());
            mAddressView.setText(data.getAddress());

            mGlide.setDefaultRequestOptions(mOptions.placeholder(R.drawable.ic_hospital_placeholder))
                    .load(data.getImageUrl())
                    .into(mCoverView);

            mCoverView.setOnClickListener(v -> {
                if (mOnCoverClickListener != null) {
                    mOnCoverClickListener.onClick(data);
                }
            });
        }
    }
}
