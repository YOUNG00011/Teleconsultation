package com.wxsoft.telereciver.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.PatientEMR;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PatientEMRFragment extends BaseFragment {

    public static void launch(Activity from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launch(from, PatientEMRFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private Patient mPatient;
    private RecyclerArrayAdapter<PatientEMR> mAdapter;
    private boolean isRefreshSuccess;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem action = menu.findItem(R.id.action);
        action.setTitle(R.string.emr_list_create_title);
        action.setIcon(R.drawable.ic_add_white_24dp);
        action.setVisible(isRefreshSuccess);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                createEMR();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = String.format(getString(R.string.emr_list_title), mPatient.getName());
        activity.getSupportActionBar().setTitle(title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<PatientEMR>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PatientEMRViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            EMRFragment.launch(_mActivity, mPatient, mAdapter.getItem(position).getId());
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getPatientManagerApi().getPatientEMRs(mPatient.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<PatientEMR>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<PatientEMR>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<PatientEMR>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.getErrorView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            return;
        }

        isRefreshSuccess = true;
        _mActivity.invalidateOptionsMenu();

        List<PatientEMR> patientEMRS = resp.getData();
        if (patientEMRS == null || patientEMRS.isEmpty()) {
            mRecyclerView.getEmptyView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(patientEMRS);
    }

    private void createEMR() {
        new MaterialDialog.Builder(_mActivity)
                .title(R.string.emr_list_create_title)
                .input(getString(R.string.emr_list_create_input_hint), null, (dialog, input) -> {})
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    String name = dialog.getInputEditText().getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        ViewUtil.showMessage("请输入病历名称");
                        return;
                    }
                    String patientId = mPatient.getId();
                    ViewUtil.createProgressDialog(_mActivity, "");
                    ApiFactory.getPatientManagerApi().savePatientEMR(PatientEMR.getNewPatientEMRRequestBody(name, patientId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<BaseResp<PatientEMR>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage(e.getMessage());
                                }

                                @Override
                                public void onNext(BaseResp<PatientEMR> resp) {
                                    ViewUtil.dismissProgressDialog();
                                    if (resp.isSuccess()) {
                                        mRecyclerView.showRecycler();
                                        mAdapter.insert(resp.getData(), 0);
                                    } else {
                                        ViewUtil.showMessage(resp.getMessage());
                                    }
                                }
                            });
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private class PatientEMRViewHolder extends BaseViewHolder<PatientEMR> {

        private TextView mTitleView;

        public PatientEMRViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_text);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(PatientEMR data) {
            super.setData(data);
            mTitleView.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.white));
            mTitleView.setText(data.getName());
        }
    }
}
