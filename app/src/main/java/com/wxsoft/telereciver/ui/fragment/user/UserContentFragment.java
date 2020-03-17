package com.wxsoft.telereciver.ui.fragment.user;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Doctor;
import com.wxsoft.telereciver.entity.User;
import com.wxsoft.telereciver.event.ModifyUserAvatarEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.fragment.user.favandfan.DoctorsListFragment;
import com.wxsoft.telereciver.ui.fragment.user.info.UserInfoFragment;
import com.wxsoft.telereciver.ui.fragment.user.integration.IntegralFragment;
import com.wxsoft.telereciver.ui.fragment.user.integration.IntegrationFragment;
import com.wxsoft.telereciver.ui.fragment.user.service.UserServiceFragment;
import com.wxsoft.telereciver.ui.fragment.user.setting.SettingFragment;
import com.wxsoft.telereciver.ui.fragment.user.sign.MySignFragment;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserContentFragment extends BaseFragment {

    public static UserContentFragment newInstance() {
        return new UserContentFragment();
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    TextView fan_count,fav_count;

    private List<Doctor> fav,fans;

    LinearLayout l_fav,l_fans;

    private RecyclerArrayAdapter<Item> mAdapter;
    private RequestManager mGlide;
    private RequestOptions mOptions = new RequestOptions()
            .centerCrop()
            .dontAnimate()
            .error(AppContext.getUser().getSex() == 0 ? R.drawable.ic_doctor_women : R.drawable.ic_doctor_man)
            .diskCacheStrategy(DiskCacheStrategy.NONE);

    private RoundedImageView mAvatarView;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        mGlide = Glide.with(UserContentFragment.this);
        setupRecyclerView();

        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Item>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new UserViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            int action = mAdapter.getItem(position).getAction();

            switch (action) {
                case Item.ACTION_SIGN:
                    MySignFragment.launch(_mActivity);
                    break;
                case Item.ACTION_BASE_SETTING:
                    SettingFragment.launch(_mActivity);
                    break;
                case Item.ACTION_SERVICE_SETTING:
                    UserServiceFragment.launch(_mActivity);
                    break;
                case Item.ACTION_PATIENT_EVALUATE:
                    IntegrationFragment.launch(_mActivity);
                    break;
                case Item.ACTION_SCORE:
                    IntegralFragment.launch(_mActivity);
                    break;
            }

        });
        mAdapter.addAll(getItems());
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_user, null);
            }

            @Override
            public void onBindView(View headerView) {
                User user = AppContext.getUser();
                mAvatarView = ButterKnife.findById(headerView, R.id.riv_avatar);
                fan_count = ButterKnife.findById(headerView, R.id.tv_fans_count);
                fav_count = ButterKnife.findById(headerView, R.id.tv_focus_count);
                l_fans = ButterKnife.findById(headerView, R.id.l_fans);
                l_fav = ButterKnife.findById(headerView, R.id.l_fav);
                mAvatarView.setOval(true);
                mGlide.setDefaultRequestOptions(mOptions)
                        .load(user.getUserImgUrl())
                        .into(mAvatarView);

                ((TextView) ButterKnife.findById(headerView, R.id.tv_name)).setText(user.getName());
                ((TextView) ButterKnife.findById(headerView, R.id.tv_department)).setText(user.getDepartmentName());
                ((TextView) ButterKnife.findById(headerView, R.id.tv_hospital)).setText(user.getHospitalName());

                ButterKnife.findById(headerView, R.id.ll_user_info).setOnClickListener(v -> {
                    UserInfoFragment.launch(_mActivity);
                });
                l_fav.setOnClickListener(v->{
                    if (fav != null) {
                        ArrayList<Doctor> doctors=new ArrayList<>();
                        doctors.addAll(fav);
                        DoctorsListFragment.launch(UserContentFragment.this,"关注我的医生",doctors);
                    }

                });

                l_fans.setOnClickListener(v->{
                    if(fans!=null) {
                        ArrayList<Doctor> doctors = new ArrayList<>();
                        doctors.addAll(fans);
                        DoctorsListFragment.launch(UserContentFragment.this, "我关注的医生", doctors);
                    }
                });
            }
        });
    }

    @Subscribe
    public void onEvent(ModifyUserAvatarEvent modifyUserAvatarEvent) {
        mGlide.setDefaultRequestOptions(mOptions)
                .load(AppContext.getUser().getUserImgUrl())
                .into(mAvatarView);
    }

    private List<Item> getItems() {
        int[] icons = {
                R.drawable.ic_sign,
                R.drawable.ic_achievement,
                R.drawable.ic_base_setting,
                R.drawable.ic_base_setting,
                R.drawable.ic_base_setting
        };
        int[] titles = {
                R.string.user_my_sign,
                R.string.service_setting,
                R.string.user_settings,
                R.string.integral_title,
                R.string.patient_integration
        };
        int[] actions = {
//            ,
                Item.ACTION_SIGN,
                Item.ACTION_SERVICE_SETTING,
                Item.ACTION_BASE_SETTING,
                Item.ACTION_SCORE,
                Item.ACTION_PATIENT_EVALUATE
        };
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            items.add(new Item(icons[i], titles[i], actions[i]));
        }
        return items;
    }

    private void loadData(){

        User user = AppContext.getUser();
        ApiFactory.getClinicManagerApi().getFansDoctors(user.getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {

                        if(resp.isSuccess()){
                            fans=resp.getData();
                            fan_count.setText(String.valueOf(fans.size()));
                        }
                    }
                });


        ApiFactory.getClinicManagerApi().getFavoriteDoctors(user.getDoctId(),"")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Doctor>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Doctor>> resp) {

                        if(resp.isSuccess()){
                            fav=resp.getData();
                            fav_count.setText(String.valueOf(fav.size()));
                        }
                    }
                });
    }

    private class UserViewHolder extends BaseViewHolder<Item> {

        private View mSpaceView;
        private ImageView mIconView;
        private TextView mTitleView;

        public UserViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_user);
            mSpaceView = $(R.id.v_space);
            mIconView = $(R.id.iv_icon);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);
            //mSpaceView.setVisibility(showSpace(data.getAction()) ? View.VISIBLE : View.GONE);
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageResource(data.getDrawableRes());
            mTitleView.setText(data.getTitleRes());
        }

        private boolean showSpace(int action) {
            return action == Item.ACTION_SIGN ||
                    action == Item.ACTION_PATIENT_EVALUATE ||
                    action == Item.ACTION_BASE_SETTING;
        }
    }

    private class Item {

        // 我的积分
        public static final int ACTION_SCORE            = 1;
        // 业务设置
        public static final int ACTION_SERVICE_SETTING  = 2;
        // 患者评价
        public static final int ACTION_PATIENT_EVALUATE = 3;
        // 我的执业点
        public static final int ACTION_PRACTICE_POINT   = 4;
        // 我的签名
        public static final int ACTION_SIGN            = 5;
        // 基础设置
        public static final int ACTION_BASE_SETTING     = 6;

        private int drawableRes;
        private int titleRes;
        private int action;

        public Item(int drawableRes, int titleRes, int action) {
            this.drawableRes = drawableRes;
            this.titleRes = titleRes;
            this.action = action;
        }

        public int getDrawableRes() {
            return drawableRes;
        }

        public int getTitleRes() {
            return titleRes;
        }

        public int getAction() {
            return action;
        }
    }
}
