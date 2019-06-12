package com.wxsoft.teleconsultation.ui.fragment.user.info;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.CommEnum;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Education;
import com.wxsoft.teleconsultation.entity.PositionTitle;
import com.wxsoft.teleconsultation.entity.requestbody.UpdateUserInfoBody;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectEducationFragment extends BaseFragment {

    public static void launch(Fragment from, Education education) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_EDUCATION, education);
        FragmentContainerActivity.launchForResult(from, SelectEducationFragment.class, args, REQUEST_EDUCATION);
    }

    private static final String FRAGMENTARGS_KEY_EDUCATION = "FRAGMENTARGS_KEY_EDUCATION";
    public static final int REQUEST_EDUCATION = 116;
    public static final String KEY_SELECTED_EDUCATION = "KEY_SELECTED_EDUCATION";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Education> mAdapter;
    private Education mEducation;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mEducation = (Education) getArguments().getSerializable(FRAGMENTARGS_KEY_EDUCATION);
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.information_education);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color),
                DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Education>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new HighDegreeViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            commit(mAdapter.getItem(position));
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getUserApi().getAllEducations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<CommEnum>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<CommEnum>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<CommEnum>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<Education> educations = Education.getEducations(resp.getData());
        mAdapter.addAll(educations);
    }

    private void commit(Education education) {
        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        UpdateUserInfoBody updateUserInfoBody = UpdateUserInfoBody.updateUserEducationBody(AppContext.getUser().getId(), education.getEducationEnum());
        ApiFactory.getUserApi().updateArchives(updateUserInfoBody)
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
                        if (resp.isSuccess()) {
                            Intent intent = new Intent();
                            intent.putExtra(KEY_SELECTED_EDUCATION, education);
                            _mActivity.setResult(RESULT_OK, intent);
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private class HighDegreeViewHolder extends BaseViewHolder<Education> {

        private TextView mTitleView;
        private ImageView mSelectView;

        public HighDegreeViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_select);
            mTitleView = $(R.id.tv_title);
            mSelectView = $(R.id.iv_select);
            mTitleView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mTitleView.setPadding(DensityUtil.dip2px(_mActivity, 16f), 0, DensityUtil.dip2px(_mActivity, 16f), 0);
        }

        @Override
        public void setData(Education data) {
            super.setData(data);
            mTitleView.setText(data.getEducationName());
            mSelectView.setVisibility((mEducation != null && mEducation.equals(data)) ? View.VISIBLE : View.GONE);
        }
    }
}
