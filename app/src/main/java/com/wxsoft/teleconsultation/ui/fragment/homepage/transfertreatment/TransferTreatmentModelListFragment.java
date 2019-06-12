package com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.transfertreatment.MessageTemplate;
import com.wxsoft.teleconsultation.event.UpdateModelStateEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransferTreatmentModelListFragment extends BaseFragment {


    public static void launch(Fragment from) {

        FragmentContainerActivity.launchForResult(from, TransferTreatmentModelListFragment.class, null,KEY_MODEL_PARAM);
    }

    public static final int KEY_MODEL_PARAM=247;
    public static final String  KEY_MODEL="KEY_MODEL";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<MessageTemplate> mAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setupRecyclerView();
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdateModelStateEvent) {
            loadData();
        }
    }


    private void finish(MessageTemplate template) {
        Intent intent = new Intent();
        intent.putExtra(KEY_MODEL, template);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }
    private void setupRecyclerView() {

        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.model_list_title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));


        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<MessageTemplate>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyApplyViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {

            mAdapter.clear();
            loadData();
        });

        mAdapter.setOnItemClickListener(position -> {

            finish(mAdapter.getItem(position));
        });

        mAdapter.setOnItemLongClickListener(position -> {

            new MaterialDialog.Builder(getContext())
                    .content("是否删除选中模版？")
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ApiFactory.getCommApi().deleteTemplate(mAdapter.getItem(position).id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<BaseResp>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ViewUtil.showMessage(e.getMessage());

                                    }

                                    @Override
                                    public void onNext(BaseResp resp) {
                                        if (resp.isSuccess()&& resp.getData() != null) {

                                            mAdapter.remove(position);

                                        }

                                    }
                                });
                    })
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return false;
        });

        loadData();
    }

    private void loadData() {
        String doctId = AppContext.getUser().getDoctId();
      ApiFactory.getCommApi().getTemplate(doctId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<MessageTemplate>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdapter.getCount() == 0) {
                            ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
                            mRecyclerView.showError();
                            mRecyclerView.getErrorView().setOnClickListener(v -> {
                                mRecyclerView.showProgress();
                                loadData();
                            });
                        } else {
                            showRefreshing(false);
                            ViewUtil.showMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(BaseResp<List<MessageTemplate>> resp) {
                        showRefreshing(false);
//                        String s=resp.getData().toString();
//                        Log.i(s,s);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<MessageTemplate>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }else{

            List<MessageTemplate> clinics = resp.getData();
            if (clinics == null || clinics.isEmpty()) {
                if (mAdapter.getAllData().isEmpty()) {
                    mRecyclerView.getEmptyView().setOnClickListener(v -> {
                        mRecyclerView.showProgress();
                        loadData();
                    });
                    mRecyclerView.showEmpty();
                } else {
                    mAdapter.stopMore();

                }
                return;
            }

            List<MessageTemplate> targetClinics = new ArrayList<>();

            targetClinics.addAll(clinics);
            mRecyclerView.showRecycler();
            mAdapter.clear();
            mAdapter.addAll(targetClinics);
        }
    }


    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (data != null) {
//                MessageTemplate item = (MessageTemplate) data.getSerializableExtra(.K);
//                account.bankType=item.type;
//                account.bankTypeName=item.typeName;
//                tv_bank.setText(item.typeName);
//            }
//        }
//    }

    private class MyApplyViewHolder extends BaseViewHolder<MessageTemplate> {

        private TextView mEditView;
        private TextView mContent;
        private TextView mTimeView;
        private Context mContext;

        public MyApplyViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_model);

            mContext = context;

            mContent = $(R.id.content);
            mTimeView = $(R.id.time);
            mEditView = $(R.id.tv_edit);
        }

        @Override
        public void setData(MessageTemplate data) {
            super.setData(data);

            String time = data.createDate.replace("T", " ").substring(5, 16);
            mTimeView.setText(time);
            mContent.setText(data.messageNote);

            mEditView.setOnClickListener(v->{
                TransferTreatmentModelSubmitFragment.launch(_mActivity,data);
            });
        }
    }



}
