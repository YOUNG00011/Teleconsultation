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
import com.wxsoft.telereciver.event.UserServiceEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserServiceFragment extends BaseFragment {

    private String mDoctor;

    private List<Cost> costs;
    private BusinessSetting setting;

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, UserServiceFragment.class, null);
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
        EventBus.getDefault().register(this);
        mDoctor =AppContext.getUser().getDoctId();
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.service_setting);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object o){
        if(o instanceof UserServiceEvent){
            getCosts();

            getBusinessSetting();
        }
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
            int action = mAdapter.getItem(position).getAction();
            if (action == Item.ACTION_DISEASECOUNSELING) {
                String j=null;
                for(Cost cost:costs){
                    if(cost.diseaseCounselingType.equals("302-0001")){
                        j=(new Gson()).toJson(cost);
                        break;
                    }
                }
                UserServiceTextDetailFragment.launch(_mActivity,action,j);
            } else if (action == Item.ACTION_PHONECALL) {

                String j=null;
                for(Cost cost:costs){
                    if(cost.diseaseCounselingType.equals("302-0002")){
                        j=(new Gson()).toJson(cost);
                        break;
                    }
                }
                UserServicePhoneCallDetailFragment.launch(_mActivity,action,j);
            }else if(action==Item.ACTION_CLOUDCLINC){

                String j=null;
                if(setting!=null)
                    j=(new Gson()).toJson(setting);
                UserServiceCloudClincDetailFragment.launch(_mActivity,action,j);
            }
        });
        mAdapter.addAll(getItems());

        getBusinessSetting();
        getCosts();
    }

    private void getCosts(){
        ApiFactory.getDiseaseCounselingApi().getCost(mDoctor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Cost>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Cost>> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            costs=resp.getData();
                            for(Item item:mAdapter.getAllData()){
                               for(Cost cost:costs){
                                   if(item.action==Item.ACTION_DISEASECOUNSELING && cost.diseaseCounselingType.equals("302-0001")){
                                       item.price=cost.amount;
                                   }else if(item.action==Item.ACTION_PHONECALL && cost.diseaseCounselingType.equals("302-0002")){
                                       item.price=cost.amount;
                                   }
                               }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }


    private void getBusinessSetting(){
        ApiFactory.getCommApi().getBusinessSetting(mDoctor) .subscribeOn(Schedulers.io())
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
    }


    private List<Item> getItems() {
        String[] titles = {
                getString(R.string.setting_transfertreatment_title),
                getString(R.string.setting_clinc_title),
                getString(R.string.setting_cloudclinc_title),
                getString(R.string.setting_diseasecounseling_title),
                getString(R.string.setting_phonecall_title)
        };
        int[] actions = {
                Item.ACTION_TREATMENT,
                Item.ACTION_CLINC,
                Item.ACTION_CLOUDCLINC,
                Item.ACTION_DISEASECOUNSELING,
                Item.ACTION_PHONECALL
        };

        String[] dicriptions={
                getString(R.string.setting_transfertreatment_discription),
                getString(R.string.setting_clinc_discription),
                getString(R.string.setting_cloudclinc_discription),
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
            prices.setVisibility((data.action==Item.ACTION_CLINC||data.action==Item.ACTION_CLOUDCLINC||data.action==Item.ACTION_TREATMENT)?View.GONE:View.VISIBLE);
            marginTip.setVisibility((data.action==Item.ACTION_CLINC||data.action==Item.ACTION_CLOUDCLINC
                    ||data.action==Item.ACTION_TREATMENT||data.action==Item.ACTION_DISEASECOUNSELING)?View.VISIBLE:View.GONE);
            mRightView.setVisibility((data.action==Item.ACTION_CLINC||data.action==Item.ACTION_TREATMENT)?View.GONE:View.VISIBLE);
            switchCompat.setVisibility((data.action==Item.ACTION_CLINC||data.action==Item.ACTION_TREATMENT)?View.VISIBLE:View.GONE);
            switchCompat.setTag(data.action);

            switchCompat.setChecked(data.checked);
            switchCompat.setOnClickListener(l->{
                int act=(int)switchCompat.getTag();
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
