package com.wxsoft.telereciver.ui.fragment.user.integration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BankAccount;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.event.BankAccountEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BankAccountListFragment extends BaseFragment {


    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, BankAccountListFragment.class, null);
    }


    public static final String NEED_ITEM_CLICK="NEED_ITEM_CLICK";
    public static final String BANK_ACCOUNT="BANK_ACCOUNT";
    public static final int REQUEST_SELECT_BANK_ACCOUNT = 246;
    private boolean itemClickable;
    public static void launch(Fragment from) {

        FragmentArgs args=new FragmentArgs();
        args.add(NEED_ITEM_CLICK,true);
        FragmentContainerActivity.launchForResult(from, BankAccountListFragment.class, args,REQUEST_SELECT_BANK_ACCOUNT);
    }
    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<BankAccount> mAdapter;
    private int mPage = 1;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle(R.string.menu_integral_new_card);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                //commit();
                BankAccountAddFragment.launch(_mActivity);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupRecyclerView();
    }



    private void setupRecyclerView() {
        EventBus.getDefault().register(this);
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.integral_my_bank_account_title);
        setHasOptionsMenu(true);

        Bundle args=getArguments();
        if(args!=null){
            itemClickable=args.getBoolean(NEED_ITEM_CLICK);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<BankAccount>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            loadData();
        });


        mAdapter.setOnItemClickListener(position -> {
            if(itemClickable){
                finish(mAdapter.getItem(position));
            }
        });

        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onEvent(BankAccountEvent uploadPhotoSuccessEvent) {

        loadData();
    }

    private void finish(BankAccount doctor) {
        Intent intent = new Intent();
        intent.putExtra(BANK_ACCOUNT, doctor);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }
    private void loadData() {
        ApiFactory.getCommApi(). queryBankAccounts(AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<BankAccount>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<List<BankAccount>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void processResponse(BaseResp<List<BankAccount>>  resp) {
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
        }

        List<BankAccount> clinics = resp.getData();
        if (clinics == null || clinics.isEmpty()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getEmptyView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadData();
                });
                mRecyclerView.showEmpty();
            } else {
                if (mPage == 1) {
                    mAdapter.clear();
                    mRecyclerView.showEmpty();
                } else {
                    mAdapter.stopMore();
                }
            }
            return;
        }


        List<BankAccount> targetClinics = new ArrayList<>();
        if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
            targetClinics.addAll(mAdapter.getAllData());
        }

        targetClinics.addAll(clinics);
        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(targetClinics);

    }



    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }


    private class ViewHolder extends BaseViewHolder<BankAccount> {

        private TextView bank_name;
        private TextView card_name;
        private TextView tv_name;

        public ViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_card);
            bank_name = $(R.id.bank_name);
            card_name = $(R.id.card_name);
            tv_name = $(R.id.tv_name);
        }

        @Override
        public void setData(BankAccount data) {
            super.setData(data);

            bank_name.setText(data.bankTypeName);
            card_name.setText("**** **** **** "+data.bankCardNo.substring(data.bankCardNo.length()-4,data.bankCardNo.length()));
            tv_name.setText(data.owner);


        }
    }
}
