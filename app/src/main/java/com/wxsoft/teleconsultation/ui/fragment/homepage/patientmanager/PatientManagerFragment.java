package com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientManagerTag;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientAddFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.PatientSearchFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientManagerFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, PatientManagerFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<PatientManagerTag> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_two_icon,menu);
        MenuItem action1 = menu.findItem(R.id.action_1);
        action1.setTitle("搜索患者");
        action1.setIcon(R.drawable.ic_search_white_24dp);

        MenuItem action2 = menu.findItem(R.id.action_2);
        action2.setTitle("添加患者");
        action2.setIcon(R.drawable.ic_add_white_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_1:
                PatientSearchFragment.launch(_mActivity);
                return true;

            case R.id.action_2:
                PatientAddFragment.launch(_mActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof PatientOrTagChangedEvent) {
            loadData();
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.patient_manager_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        // 设置上边距10dp
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, DensityUtil.dip2px(_mActivity, 16), 0, 0);
        mRecyclerView.setLayoutParams(params);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<PatientManagerTag>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientManagerViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            PatientManagerTag patientManagerTag = mAdapter.getItem(position);
            boolean isAll = false;
            if (position == 0) {
                isAll = true;
            }
            PatientTagFragment.launch(_mActivity,
                    patientManagerTag.getTagName(),
                    patientManagerTag.getPatientCount(),
                    patientManagerTag.getId(),
                    isAll);
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getPatientManagerApi().getPatientTags(AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<PatientManagerTag>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<PatientManagerTag>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<PatientManagerTag>> resp) {
        List<PatientManagerTag> patientTags = resp.getData();

        mAdapter.clear();
        mAdapter.addAll(patientTags);

    }

    private class PatientManagerViewHolder extends BaseViewHolder<PatientManagerTag> {

        private ImageView mIconView;
        private TextView mTitleView;
        private TextView mCountView;

        public PatientManagerViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_patient_manager);
            mIconView = $(R.id.iv_icon);
            mTitleView = $(R.id.tv_title);
            mCountView = $(R.id.tv_count);
        }

        @Override
        public void setData(PatientManagerTag data) {
            super.setData(data);
            if (data.getId().equals("0")) {
                mIconView.setImageResource(R.drawable.ic_all_patient_tag);
            } else {
                mIconView.setImageResource(R.drawable.ic_custom_tag);
            }
            mTitleView.setText(data.getTagName());
            mCountView.setText(String.valueOf(data.getPatientCount()));
        }
    }
}
