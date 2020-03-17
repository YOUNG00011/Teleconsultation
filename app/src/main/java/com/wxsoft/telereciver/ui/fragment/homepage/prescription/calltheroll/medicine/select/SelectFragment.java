package com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll.medicine.select;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.CommEnum;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_HZ;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_AMOUNT;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_COUNT_DAYLY;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_COUNT_DAYS;

public class SelectFragment extends BaseFragment {

    private String end;
    public static void launch(Fragment from,String ty) {
        FragmentArgs arg=new FragmentArgs();
        arg.add(KEY_SELECT,ty);
        FragmentContainerActivity.launchForResult(from, SelectFragment.class, arg, REQUEST_SELECT_DUTY);
    }

    public static void launch(Fragment from,String ty,String end) {
        FragmentArgs arg=new FragmentArgs();
        arg.add(KEY_SELECT,ty);
        arg.add(KEY_END,end);
        FragmentContainerActivity.launchForResult(from, SelectFragment.class, arg, REQUEST_SELECT_DUTY);
    }

    public static final int REQUEST_SELECT_DUTY = 129;
    public static final String KEY_SELECT= "KEY_SELECT";
    public static final String KEY_END= "KEY_END";
    public static final String KEY_DICT= "KEY_DICT";

    private String type;
    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<CommEnum> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {

        type=getArguments().getString(KEY_SELECT);
        end=getArguments().getString(KEY_END);
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title="";
        if(type.equals(MEDICAINE_HZ)){
            title="用药频次";
        }else if(type.equals(MEDICAINE_USING)){
            title="用药途径";
        }else if(type.equals(MEDICAINE_USING_COUNT_DAYS)){
            title="用药天数";
        }else if(type.equals(MEDICAINE_USING_COUNT_DAYLY)){
            title="每次剂量";
        }else if(type.equals(MEDICAINE_USING_AMOUNT)){
            title="药量";
        }
        activity.getSupportActionBar().setTitle(title);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color),
                DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<CommEnum>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new SelectViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.putExtra(KEY_SELECT, type);
            intent.putExtra(KEY_DICT, mAdapter.getItem(position));
            _mActivity.setResult(RESULT_OK, intent);
            _mActivity.finish();
        });

        loadData();
    }

    private void loadData() {

        if(  type.equals(MEDICAINE_HZ) || type.equals(MEDICAINE_USING)) {
            ApiFactory.getPrescriptionApi().getMedicalInsurances(type)
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
        }else if(type.equals(MEDICAINE_USING_AMOUNT) || type.equals(MEDICAINE_USING_COUNT_DAYLY)){
            List<CommEnum> all=new  ArrayList();
            for(int i=1;i<=20;i++){
                CommEnum commEnum=new CommEnum(String.valueOf(i), i +end);
               all.add(commEnum);
            }
            mRecyclerView.showRecycler();
            mAdapter.clear();
            mAdapter.addAll(all);
        }
    }

    private void processResponse(BaseResp<List<CommEnum>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.getErrorView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            mRecyclerView.showError();
            return;
        }

        List<CommEnum> duties =resp.getData();
        if (duties == null || duties.isEmpty()) {
            mRecyclerView.getEmptyView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(duties);
    }

    private class SelectViewHolder extends BaseViewHolder<CommEnum> {

        private TextView mTitleView;

        public SelectViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_select);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(CommEnum data) {
            super.setData(data);
            mTitleView.setText(data.getItemName());
        }
    }
}
