package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.prescription.MedicineCategory;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MedicineTreeFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    private RecyclerArrayAdapter<MedicineCategory> mAdapter;
    private int mCurrentPosition = 0;
    private int statusIndex = 0;
    private int mPage = 1;
    public static MedicineTreeFragment newInstance() {
        MedicineTreeFragment fragment = new MedicineTreeFragment();
        return fragment;
    }

    public static final int REQUEST_SELECT_DOCTOR = 41;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    public static final String BUSINESS_TYPE = "BusinessType";

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<MedicineCategory>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MedCategoryViewHolder(parent);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            mAdapter.clear();
            loadData();
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            loadData();
        });

        mAdapter.setOnItemClickListener(position -> {
//            String clinicId = mAdapter.getItem(position).id;
//            TransferTreatmentDetailFragment.launch(_mActivity, clinicId,mCurrentPosition==0);

        });

        loadData();
    }
    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }


    private void loadData() {
        String queryType = mCurrentPosition==0?"0":"1";
        ApiFactory.getPrescriptionApi().getMedTypes("1")
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<BaseResp<List<MedicineCategory>>>() {
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
              public void onNext(BaseResp<List<MedicineCategory>> resp) {
                  showRefreshing(false);
//                        String s=resp.getData().toString();
//                        Log.i(s,s);
                  processResponse(resp);
              }
          });
    }


    private void processResponse(BaseResp<List<MedicineCategory>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    mPage=1;
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }else{

            List<MedicineCategory> clinics = resp.getData();
            if (clinics == null || clinics.isEmpty()) {
                if (mAdapter.getAllData().isEmpty()) {
                    mRecyclerView.getEmptyView().setOnClickListener(v -> {
                        mPage=1;
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


            List<MedicineCategory> targetClinics = new ArrayList<>();
            if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
                targetClinics.addAll(mAdapter.getAllData());
            }

            targetClinics.addAll(clinics);

            mRecyclerView.showRecycler();
            mAdapter.clear();
            mAdapter.addAll(targetClinics);



        }
    }


    private class MedCategoryViewHolder extends BaseViewHolder<MedicineCategory> {

        private TextView title;
        private ImageView icon;
        private EasyRecyclerView items;

        public MedCategoryViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_med_category);

            title = $(R.id.title);
            icon = $(R.id.icon);
            items = $(R.id.items);
            items.setLayoutManager(new LinearLayoutManager(_mActivity));
            DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
            items.addItemDecoration(itemDecoration);


        }

        @Override
        public void setData(MedicineCategory data) {
            super.setData(data);
            title.setText(data.name);
            if(data!=null){
                RecyclerArrayAdapter<MedicineCategory> itemAdapter
                  =
                  new RecyclerArrayAdapter<MedicineCategory>(_mActivity){
                      @Override
                      public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                          return new MedCategoryViewHolder(parent);
                      }
                  };
                items.setAdapter(itemAdapter);
                itemAdapter.addAll(data.medicineCategorys);


            }

        }
    }
}
