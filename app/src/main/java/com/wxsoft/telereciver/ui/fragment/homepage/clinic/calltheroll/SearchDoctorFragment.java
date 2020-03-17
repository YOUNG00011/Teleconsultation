package com.wxsoft.telereciver.ui.fragment.homepage.clinic.calltheroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.City;
import com.wxsoft.telereciver.entity.Department;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.PositionTitle;
import com.wxsoft.telereciver.entity.requestbody.QueryDoctorInfoBody;
import com.wxsoft.telereciver.entity.responsedata.QueryCityData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.DoctorsDisplayFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.clinic.SelectCityFragment;
import com.wxsoft.telereciver.ui.fragment.SelectDepartmentFragment;
import com.wxsoft.telereciver.ui.fragment.SelectPositionTitleFragment;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchDoctorFragment extends BaseFragment {

    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, SearchDoctorFragment.class, null, REQUEST_SEARCH_DOCTOR);
    }

    public static final int REQUEST_SEARCH_DOCTOR = 29;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";

    @BindView(R.id.et_name)
    ClearableEditText mNameView;

    @BindView(R.id.et_goodat)
    ClearableEditText mGoodAtView;

    @BindView(R.id.tv_department)
    TextView mDepartmentView;

    @BindView(R.id.tv_position_title)
    TextView mPositionTitleView;

    @BindView(R.id.btn_search)
    Button mSearchView;

    @OnTextChanged(R.id.et_name)
    void nameChanged(CharSequence s, int start, int before, int count) {
        updateSearchView();
    }

    @OnTextChanged(R.id.et_goodat)
    void goodAtChanged(CharSequence s, int start, int before, int count) {
        updateSearchView();
    }

    @OnClick(R.id.ll_root_department)
    void departmentClick() {
        SelectDepartmentFragment.launch(this,AppContext.getUser().getHospitalId(),"Consultation");
    }

    @OnClick(R.id.ll_root_position_title)
    void positionTitleClick() {
        SelectPositionTitleFragment.launch(this, mCurrentPositionTitle);
    }

    @OnClick(R.id.btn_search)
    void searchClick() {
        if (isSearchViewEnabled) {
            searchDoctors();
        } else {
            ViewUtil.showMessage("姓名、擅长疾病请至少输入一项");
        }
    }

    private City mCurrentProvince;
    private City mCurrentCity;
    private City mCurrentArea;
    private Department mDepartment;
    private PositionTitle mCurrentPositionTitle;
    private boolean isSearchViewEnabled = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_doctor;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mCurrentPositionTitle = PositionTitle.getNoLimitPositionTitle();
        setupToolbar();
        if (App.mCities.isEmpty()) {
            loadData();
        }
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
            if (App.mCities.isEmpty()) {
                ViewUtil.showMessage("获取城市数据失败");
                return;
            }
            SelectCityFragment.launch(this);
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectCityFragment.REQUEST_SELECT_CITY) {
                if (data != null) {
                    mCurrentProvince = (City) data.getSerializableExtra(SelectCityFragment.KEY_PROVINCE);
                    mCurrentCity = (City) data.getSerializableExtra(SelectCityFragment.KEY_CITY);
                    mCurrentArea = (City) data.getSerializableExtra(SelectCityFragment.KEY_AREA);
                    _mActivity.invalidateOptionsMenu();
                }
            } else if (requestCode == SelectDepartmentFragment.REQUEST_SELECT_DEPARTMENT) {
                if (data != null) {
                    mDepartment = (Department) data.getSerializableExtra(SelectDepartmentFragment.KEY_DEPARTMENT);
                    mDepartmentView.setText(mDepartment.getName());
                    updateSearchView();
                }
            } else if (requestCode == SelectPositionTitleFragment.REQUEST_SELECT_POSITION_TITLE) {
                if (data != null) {
                    mCurrentPositionTitle = (PositionTitle) data.getSerializableExtra(SelectPositionTitleFragment.KEY_POSITION_TITLE);
                    mPositionTitleView.setText(mCurrentPositionTitle.getPositionTitleName());
                }
            } else if (requestCode == DoctorsDisplayFragment.REQUEST_SELECT_DOCTOR) {
                if (data != null) {
                    Doctor doctor = (Doctor) data.getSerializableExtra(DoctorsDisplayFragment.KEY_DOCTOR);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_DOCTOR, doctor);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.search_doctor_by_condition_title);
        setHasOptionsMenu(true);
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
        _mActivity.invalidateOptionsMenu();
    }

    private void searchDoctors() {
        String provinceId = mCurrentProvince != null ? mCurrentProvince.getId() : "";
        String cityId = mCurrentCity != null ? mCurrentCity.getId() : "";
        String districtId = mCurrentArea != null ? mCurrentArea.getId() : "";
        String name = mNameView.getText().toString().trim();
        String googAt = mGoodAtView.getText().toString().trim();
        String selfHospitalId = AppContext.getUser().getHospitalId();

//        String departmentId = mDepartment == null ? "" : mDepartment.getId();
        String positionTitleId = mCurrentPositionTitle.isNoLimit() ? "" : mCurrentPositionTitle.getPositionTitleEnum();
        QueryDoctorInfoBody queryDoctorInfoBody = new QueryDoctorInfoBody(provinceId, cityId, districtId, name, googAt, positionTitleId, selfHospitalId);
//        DoctorsDisplayFragment.launch(this, "选择医生", queryRequestBody);
        ViewUtil.createProgressDialog(_mActivity, "");
        queryDoctorInfoBody.businessType="Consultation";
        ApiFactory.getClinicManagerApi().queryDoctorInfos(queryDoctorInfoBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {
                        ViewUtil.dismissProgressDialog();
                        processQueryResponse(resp);
                    }
                });
    }

    private void processQueryResponse(BaseResp<List<Doctor>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        DoctorsDisplayFragment.launch(this, getString(R.string.select_doctor_title), (ArrayList<Doctor>) resp.getData());
    }

    private void updateSearchView() {
        if (!TextUtils.isEmpty(mNameView.getText()) ||
                !TextUtils.isEmpty(mGoodAtView.getText()) ||
                mDepartment != null
                ) {
            isSearchViewEnabled = true;
            mSearchView.setBackgroundResource(R.drawable.primary_btn_bg);
        } else {
            isSearchViewEnabled = false;
            mSearchView.setBackgroundResource(R.drawable.disabled_btn_bg);
        }
    }
}
