package com.wxsoft.telereciver.ui.fragment.user.integration;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.DrwaMoneyApplyRecord;
import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;
import com.wxsoft.telereciver.entity.responsedata.QueryResponseData;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IntegralOutListFragment extends BaseFragment {


    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, IntegralOutListFragment.class, null);
    }
    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<DrwaMoneyApplyRecord> mAdapter;
    private int mPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupRecyclerView();
    }



    private void setupRecyclerView() {

        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.integral_out_title);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<DrwaMoneyApplyRecord>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            loadData();
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            loadData();
        });

        loadData();
    }


    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getIntegralHistory1RequestBody(AppContext.getUser().getDoctId(), AppConstant.SIZE_OF_PAGE, mPage);
        ApiFactory.getCommApi(). getDrwaMoneyHistory(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<DrwaMoneyApplyRecord>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<DrwaMoneyApplyRecord>> stringBaseResp) {
                        showRefreshing(false);
                        processResponse(stringBaseResp);

                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<DrwaMoneyApplyRecord>>  resp) {
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

        List<DrwaMoneyApplyRecord> clinics = resp.getData().getResultData();
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


        List<DrwaMoneyApplyRecord> targetClinics = new ArrayList<>();
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


    private class ViewHolder extends BaseViewHolder<DrwaMoneyApplyRecord> {

        private TextView time;
        private TextView start_time;
        private TextView money;
        private TextView person;
        private TextView type;
        private TextView bank;
        private TextView account;


        public ViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_integral_history_out);

            account = $(R.id.account);
            type = $(R.id.type);
            bank = $(R.id.bank);
            time = $(R.id.time);
            start_time = $(R.id.start_time);
            money = $(R.id.money);
            person = $(R.id.person);
        }

        @Override
        public void setData(DrwaMoneyApplyRecord data) {
            super.setData(data);

            try {

                String the_time=data.applyDate.substring(0,19).replace("T"," ");
                time.setText(dateToShort(strToDateLong(the_time)));
                start_time.setText(dateToLong(strToDateLong(the_time)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DecimalFormat df = new DecimalFormat("#.##");
            type.setText(R.string.integral_out_type1);
            account.setText( "**** **** **** "+data.bankAccount.substring(data.bankAccount.length()-4,data.bankAccount.length()));

            bank.setText(data.bankName);
            money.setText(df.format(data.applyAmount));
            person.setText(data.payee);


        }

        public Date strToDateLong(String strDate) throws ParseException {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date strtodate = formatter.parse(strDate);
            return strtodate;
        }

        public String dateToLong(Date date)  {
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.string_mm_dd_hh_mm));

            String strtodate = formatter.format(date);
            return strtodate;
        }

        public String dateToShort(Date date)  {
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.string_mm_dd));

            String strtodate = formatter.format(date);
            return strtodate;
        }
    }
}
