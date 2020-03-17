package com.wxsoft.telereciver.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientTag;
import com.wxsoft.telereciver.entity.RecommendTag;
import com.wxsoft.telereciver.entity.requestbody.PatientTagBody;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyTagFragment extends BaseFragment {

    public static void launch(Fragment from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launchForResult(from, MyTagFragment.class, args, REQUEST_MY_TAG);
    }

    public static void launch(Activity from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launchForResult(from, MyTagFragment.class, args, REQUEST_MY_TAG);
    }

    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";
    public static final int REQUEST_MY_TAG = 78;
    public static final String KEY_TAGS = "KEY_TAGS";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<PatientTag> mAdapter;
    private Patient mPatient;
    private ArrayList<PatientTag> mTags;

    @OnClick(R.id.fabtn_add_tag)
    void addTagClick() {
        ArrayList<String> tagNames = new ArrayList<>();
        for (PatientTag patientTag : mAdapter.getAllData()) {
            tagNames.add(patientTag.getTagName());
        }
        AddTagFragment.launch(this, tagNames);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_tag;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        mTags = new ArrayList<>();
        if (mPatient.getPatientTags() != null && !mPatient.getPatientTags().isEmpty()) {
            mTags.addAll(mPatient.getPatientTags());
        }
        setupTollbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                if (mTags.size() > 10) {
                    ViewUtil.showMessage("最多有10个标签");
                    return false;
                }

                savePatientTags();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AddTagFragment.REQUEST_ADD_TAG) {
                if (data != null) {
                    RecommendTag recommendTag = (RecommendTag) data.getSerializableExtra(AddTagFragment.KEY_TAG);
                    PatientTag patientTag = new PatientTag();
                    patientTag.setTagId(recommendTag.getId());
                    patientTag.setTagName(recommendTag.getTagName());
                    mTags.add(patientTag);
                    mAdapter.insert(patientTag, 0);
                }
            }
        }
    }

    private void setupTollbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.my_tags_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<PatientTag>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyTagViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            PatientTag patientTag = mAdapter.getItem(position);
            if (mTags.contains(patientTag)) {
                mTags.remove(patientTag);
            } else {
                mTags.add(patientTag);
            }
            mAdapter.notifyDataSetChanged();
        });

        mAdapter.addAll(mTags);
    }

    private void savePatientTags() {
        List<Patient> patients = new ArrayList<>();
        patients.add(mPatient);
        PatientTagBody body = new PatientTagBody(mTags, patients);
        ViewUtil.createProgressDialog(_mActivity, "保存中...");
        ApiFactory.getPatientManagerApi().savePatientInfoTag(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        ViewUtil.dismissProgressDialog();
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp resp) {
        if (resp.isSuccess()) {
            Intent intent = new Intent();
            intent.putExtra(KEY_TAGS, mTags);
            _mActivity.setResult(RESULT_OK, intent);
            _mActivity.finish();
        } else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }

    private class MyTagViewHolder extends BaseViewHolder<PatientTag> {

        private TextView mTitleView;

        public MyTagViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_my_tag);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(PatientTag data) {
            super.setData(data);
            mTitleView.setText(data.getTagName());
            Drawable leftDrawable;
            Drawable rightDrawable;
            if (mTags.contains(data)) {
                leftDrawable = getResources().getDrawable(R.drawable.ic_arrow_right);
                rightDrawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_on);
            } else {
                leftDrawable = getResources().getDrawable(R.drawable.ic_arrow_right);
                rightDrawable = getResources().getDrawable(R.drawable.ic_comm_checkbox_off);
            }

            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            mTitleView.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
        }
    }

}
