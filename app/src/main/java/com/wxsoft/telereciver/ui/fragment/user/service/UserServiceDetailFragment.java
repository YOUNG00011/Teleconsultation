package com.wxsoft.telereciver.ui.fragment.user.service;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessSetting;
import com.wxsoft.telereciver.entity.diseasecounseling.Cost;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserServiceDetailFragment extends BaseFragment {

    private String mDoctor;

    private int action;
    private Cost cost;
    public static final String KEY_JSON="KEY_JSON";
    public static final String KEY_ACTION="KEY_ACTION";
    private BusinessSetting setting;

    public static void launch(Activity from,int action,String json) {

        FragmentArgs args=new FragmentArgs();
        args.add(KEY_ACTION,action);
        args.add(KEY_JSON,json);
        FragmentContainerActivity.launch(from, UserServiceDetailFragment.class, args);
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
        action=getArguments().getInt(KEY_ACTION);
        String json=getArguments().getString(KEY_JSON);
        if(action==Item.ACTION_CLOUDCLINC){
            setting=(new Gson()).fromJson(json,BusinessSetting.class);
        }else{
            cost=(new Gson()).fromJson(json,Cost.class);
        }
        mDoctor =AppContext.user.getDoctId();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.service_setting);
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

//
        Item item=new Item(   getString(R.string.setting_phonecall_title),getString(R.string.setting_phonecall_discription), Item.ACTION_PHONECALL);

        mAdapter.add(item);

    }



    private class SettingViewHolder extends BaseViewHolder<Item> {

        private TextView mTitleView;
        private TextView mDescView;
        private TextView mPriceView;
        private ImageView mRightView;

        private LinearLayout prices;
        private LinearLayout marginTip;

        private SwitchCompat switchCompat;

        public SettingViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_service_setting);
            mTitleView = $(R.id.tv_title);
            mDescView = $(R.id.tv_desc);
            mPriceView = $(R.id.price);
            mRightView = $(R.id.iv_right);
            prices = $(R.id.prices);
            switchCompat = $(R.id.checked);
            marginTip = $(R.id.marginTip);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);
            mTitleView.setText(data.getTitle());
            mDescView.setText(data.getDesc());
            mPriceView.setText(String.valueOf(data.getPrice()));
            prices.setVisibility((data.action== Item.ACTION_CLINC||data.action== Item.ACTION_CLOUDCLINC||data.action== Item.ACTION_TREATMENT)?View.GONE:View.VISIBLE);
            marginTip.setVisibility((data.action== Item.ACTION_CLINC||data.action== Item.ACTION_CLOUDCLINC
                    ||data.action== Item.ACTION_TREATMENT||data.action== Item.ACTION_DISEASECOUNSELING)?View.VISIBLE:View.GONE);
            mRightView.setVisibility((data.action== Item.ACTION_CLINC||data.action== Item.ACTION_TREATMENT)?View.GONE:View.VISIBLE);
            switchCompat.setVisibility((data.action== Item.ACTION_CLINC||data.action== Item.ACTION_TREATMENT)?View.VISIBLE:View.GONE);
            switchCompat.setTag(data.action);

            switchCompat.setChecked(data.checked);
            switchCompat.setOnClickListener(l->{
                int act=action;
                switch (act){
                    case Item.ACTION_CLINC:
                        setting.isConsultation=switchCompat.isChecked();
                        break;
                    case Item.ACTION_CLOUDCLINC:
                        setting.isCloudClinc=switchCompat.isChecked();
                        break;
                    case Item.ACTION_TREATMENT:
                        setting.isTransferTreatment=switchCompat.isChecked();
                        break;
                }

                ApiFactory.getCommApi().setBusinessSetting(setting).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseResp<BusinessSetting>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ViewUtil.dismissProgressDialog();
                                ViewUtil.showMessage(e.getMessage());
                            }

                            @Override
                            public void onNext(BaseResp<BusinessSetting> resp) {
                                ViewUtil.dismissProgressDialog();
                                if (resp.isSuccess()) {
                                    setting=resp.getData();
                                    for(Item item:mAdapter.getAllData()) {

                                        if (item.action == Item.ACTION_TREATMENT) {
                                            item.checked = setting.isTransferTreatment;
                                        } else if (item.action == Item.ACTION_CLINC) {
                                            item.checked = setting.isConsultation;
                                        } else if (item.action == Item.ACTION_CLOUDCLINC) {
                                            item.checked = setting.isCloudClinc;
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    ViewUtil.showMessage(resp.getMessage());
                                }
                            }
                        });
            });

        }

    }

    private class Item {

        public static final int ACTION_TREATMENT = 1;
        // 功能介绍
        public static final int ACTION_CLINC   = 2;
        public static final int ACTION_CLOUDCLINC   = 3;
        public static final int ACTION_DISEASECOUNSELING   = 4;
        public static final int ACTION_PHONECALL   = 5;

        private String title;
        private String desc;
        private int action;
        private float price;
        private boolean checked;



        public Item(String title,String desc, int action,float price) {
            this.title = title;
            this.desc = desc;
            this.action = action;
            this.price=price;
        }

        public Item(String title,String desc, int action) {
            this.title = title;
            this.desc = desc;
            this.action = action;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }


        public int getAction() {
            return action;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getPrice(){
            return price;
        }
        public boolean getChecked(){
            return checked;
        }
    }
}
