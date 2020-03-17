package com.wxsoft.telereciver.ui.fragment.user.service;

import android.app.Activity;
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
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.diseasecounseling.Cost;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class UserWithPicServiceFragment extends BaseFragment {

    private String mDoctor;

    private Cost cost;

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, UserWithPicServiceFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private  RecyclerArrayAdapter<Item> mAdapter;



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_service_setting;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mDoctor =AppContext.user.getDoctId();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.setting_diseasecounseling_title);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Item>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new SettingViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {

        });
        mAdapter.addAll(getItems());
    }




    private List<Item> getItems() {
        String[] titles = {
                getString(R.string.setting_diseasecounseling_title),
                getString(R.string.setting_phonecall_title)
        };
        int[] actions = {

        };

        String[] dicriptions={
                getString(R.string.setting_diseasecounseling_discription),
                getString(R.string.setting_phonecall_discription)
        };
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            items.add(new Item(titles[i], dicriptions[i], actions[i]));
        }
        return items;
    }

    private class SettingViewHolder extends BaseViewHolder<Item> {

        private TextView mPriceView;
        private ImageView mRightView;

        public SettingViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_service_setting);
            mPriceView = $(R.id.price);
            mRightView = $(R.id.iv_right);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);

            mPriceView.setText(String.valueOf(data.getPrice()));

        }
    }

    private class Item {

        public static final int ACTION_ENABLE = 1;
        // 功能介绍
        public static final int ACTION_PRICE   = 2;

        private int action;
        private float price;
        private boolean enabled;



        public Item( int action,float price) {

            this.action = action;
            this.price=price;
        }

        public Item(String title,String desc, int action) {

            this.action = action;
        }



        public int getAction() {
            return action;
        }


        public void setPrice(float price) {
            this.price = price;
        }

        public float getPrice(){
            return price;
        }
    }
}
