package com.wxsoft.telereciver.ui.fragment.homepage.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.Department;
import com.wxsoft.telereciver.entity.Hospital;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.SelectDepartmentFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class ExpectDepartmentFragment extends BaseFragment {

    public static void launch(Fragment from, Hospital hospital, String departmentsJson) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HOSPITAL, hospital);
        args.add(FRAGMENT_ARGS_DEPARTMENTS_JSON, departmentsJson);
        FragmentContainerActivity.launchForResult(from, ExpectDepartmentFragment.class, args, REQUEST_EXPEAT_DEPARTMENT);
    }

    private static final String FRAGMENT_ARGS_HOSPITAL = "FRAGMENT_ARGS_HOSPITAL";
    private static final String FRAGMENT_ARGS_DEPARTMENTS_JSON = "FRAGMENT_ARGS_DEPARTMENTS_JSON";
    public static final int REQUEST_EXPEAT_DEPARTMENT = 122;
    public static final String KEY_DEPARTMENTS_JSON = "KEY_DEPARTMENTS_JSON";

    @BindView(R.id.tag)
    TagContainerLayout mTagContainerLayout;

    @BindView(R.id.tv_add)
    TextView mAddView;

    private Hospital mHospital;
    private List<Department> mDepartments;

    @OnClick(R.id.tv_add)
    void addClick() {
        if (mDepartments == null || mDepartments.isEmpty()) {
            SelectDepartmentFragment.launch(this, mHospital, false, false,"Consultation");
        } else {
            SelectDepartmentFragment.launch(this, mHospital, new Gson().toJson(mDepartments),"Consultation");
        }
    }

    @OnClick(R.id.btn_ok)
    void okClick() {
        Intent intent = new Intent();
        intent.putExtra(KEY_DEPARTMENTS_JSON, new Gson().toJson(mDepartments));
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_expect_department;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();

        mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                mTagContainerLayout.removeTag(position);
                mDepartments.remove(position);
                if (mDepartments.size() < 5) {
                    mAddView.setVisibility(View.VISIBLE);
                }
            }
        });

        mHospital = (Hospital) getArguments().getSerializable(FRAGMENT_ARGS_HOSPITAL);
        String departmentsJson = getArguments().getString(FRAGMENT_ARGS_DEPARTMENTS_JSON);
        if (!TextUtils.isEmpty(departmentsJson)) {
            mDepartments = new Gson().fromJson(departmentsJson, new TypeToken<List<Department>>() {
            }.getType());
        }

        // 如果有已选科室则显示，否则直接跳到科室选择界面
        if (mDepartments == null) {
            mDepartments = new ArrayList<>();
            SelectDepartmentFragment.launch(this, mHospital, false, false,"Consultation");
        } else {
            if (mDepartments.isEmpty()) {
                SelectDepartmentFragment.launch(this, mHospital, false, false,"Consultation");
            } else {
                List<String> departmentNames = new ArrayList<>();
                for (Department department : mDepartments) {
                    departmentNames.add(department.getName());
                }
                mTagContainerLayout.setTags(departmentNames);
                mAddView.setVisibility(mDepartments.size() > 4 ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SelectDepartmentFragment.REQUEST_SELECT_DEPARTMENT) {
                if (data != null) {
                    Department department = (Department) data.getSerializableExtra(SelectDepartmentFragment.KEY_DEPARTMENT);
                    mDepartments.add(department);
                    mTagContainerLayout.addTag(department.getName());
                    if (mDepartments.size() > 4) {
                        mAddView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.expected_department_title);
    }
}
