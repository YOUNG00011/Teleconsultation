package com.wxsoft.telereciver.ui.fragment.user.integration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.Bank;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BankTypeListFragment extends BaseFragment {

    String[] types={
            "131-0001",
            "131-0002",
            "131-0003",
            "131-0004",
            "131-0005",
            "131-0006",
            "131-0007"
    };

    String[] typeNames;
    public static final int REQUEST_SELECT_BANK_TYPE = 245;
    public static final String KEY_BANK="KEY_BANK";
    public static void launch(Fragment from) {
        FragmentContainerActivity.launchForResult(from, BankTypeListFragment.class,null, REQUEST_SELECT_BANK_TYPE);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Bank> mAdapter;
    private int mPage = 1;



    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        typeNames=new String[]{getString(R.string.bank_0001),getString(R.string.bank_0002),
                getString(R.string.bank_0003),getString(R.string.bank_0004),getString(R.string.bank_0005),
                getString(R.string.bank_0006),getString(R.string.bank_0007)};
        setupRecyclerView();
    }



    private void setupRecyclerView() {

        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.bank_type_list_title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Bank>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent);
            }
        });



        mAdapter.setOnItemClickListener(position -> {
            finish(mAdapter.getItem(position));
        });

        mAdapter.addAll(getItems());
        mRecyclerView.getSwipeToRefresh().setRefreshing(false);

    }
    public static final String KEY_WORK_DONE = "KEY_WORK_DONE";
    private void finish(Bank doctor) {
        Intent intent = new Intent();
        intent.putExtra(KEY_BANK, doctor);
        intent.putExtra(KEY_WORK_DONE,true);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private class ViewHolder extends BaseViewHolder<Bank> {

        private ImageView bank_avatar;
        private TextView bank_name;

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_bank);
            bank_name = $(R.id.tv_name);

        }

        @Override
        public void setData(Bank data) {
            super.setData(data);

            bank_name.setText(data.typeName);
        }
    }

    private  List<Bank> getItems(){
        List<Bank> items=new ArrayList<>();
        for(int i=0;i<types.length;i++){
            Bank item=new Bank();
            item.type=types[i];
            item.typeName=typeNames[i];
            items.add(item);
        }

        return items;
    }

}
