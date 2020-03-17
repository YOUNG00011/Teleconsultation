package com.wxsoft.telereciver.ui.fragment.user.setting;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.BuildConfig;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.helper.SharedPreferencesHelper;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.LoginActivity;
import com.wxsoft.telereciver.ui.base.BaseAppManager;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.GlideCatchUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, SettingFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.tv_version)
    TextView mVersionView;

    private  RecyclerArrayAdapter<Item> mAdapter;

    @OnClick(R.id.btn_logout)
    void logoutClick() {
        AppContext.logout();
        JMessageClient.logout();
        BaseAppManager.getInstance().clear();
        LoginActivity.launch(_mActivity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mVersionView.setText("version  " + BuildConfig.VERSION_NAME);
        setupRecyclerView();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.setting);
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
            if (action == Item.ACTION_NO_DISTURB) {
                updataNoDisturb(position);
            } else if (action == Item.ACTION_MULTI_LANGUAGE) {
                LanguageSelectFragment.launch(_mActivity);
            } else if (action == Item.ACTION_MODIFY_PASSWORD) {
                ModifyPwdFragment.launch(_mActivity);
            } else if (action == Item.ACTION_CLEAR_CACHE) {
                clearCache(position);
            } else {
                ViewUtil.showMessage(mAdapter.getItem(position).getTitle());
            }
        });
        mAdapter.addAll(getItems());
    }

    private void updataNoDisturb(int position) {
        int oldNoDisturbGlobal = SharedPreferencesHelper.getNoDisturb();
        int noDisturbGlobal = oldNoDisturbGlobal == 1 ? 0 : 1;
        ViewUtil.createProgressDialog(_mActivity, "");
        String id = JPushInterface.getRegistrationID(_mActivity);
        ApiFactory.getCommApi().saveJPushAccount(AppContext.getUser().getId(),
                AppContext.getUser().getName(),
                id,
                noDisturbGlobal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            int drawableRes = noDisturbGlobal == 1 ? R.drawable.ic_switch_on : R.drawable.ic_switch_off;
                            SharedPreferencesHelper.setNoDisturb(noDisturbGlobal);
                            mAdapter.getItem(position).setDrawableRes(drawableRes);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void clearCache(int position) {
        new MaterialDialog.Builder(_mActivity)
                .content(R.string.setting_clear_cache_hint)
                .positiveText(R.string.setting_clear_cache_option)
                .negativeText(R.string.cancel)
                .onPositive((materialDialog, dialogAction) -> {
                    materialDialog.dismiss();
                    ViewUtil.createProgressDialog(_mActivity, "正在清除缓存...");
                    GlideCatchUtil.getInstance().clearCacheMemory();
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Glide.get(App.getApplication()).clearDiskCache();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            ViewUtil.dismissProgressDialog();
                            ViewUtil.showMessage("清除缓存成功");
                            mAdapter.getItem(position).setDesc(GlideCatchUtil.getInstance().getCacheSize());
                            mAdapter.notifyDataSetChanged();
                        }
                    }.execute();
                })
                .show();
    }

    private List<Item> getItems() {
        String[] titles = {
                getString(R.string.setting_no_disturb),
//                getString(R.string.setting_features),
                getString(R.string.setting_language),
                getString(R.string.setting_modify_password),
                getString(R.string.setting_clear_cache)
//                getString(R.string.setting_feedback),
//                getString(R.string.setting_contact)
        };
        int[] actions = {
                Item.ACTION_NO_DISTURB,
//                Bank.ACTION_FUNCTION_INTRODUCTION,
                Item.ACTION_MULTI_LANGUAGE,
                Item.ACTION_MODIFY_PASSWORD,
                Item.ACTION_CLEAR_CACHE
//                Bank.ACTION_ADVICE_FEEDBACK,
//                Bank.ACTION_LINK
        };
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            int action = actions[i];
            if (action == Item.ACTION_NO_DISTURB) {
                int noDisturbRes = SharedPreferencesHelper.getNoDisturb() == 0 ? R.drawable.ic_switch_off : R.drawable.ic_switch_on;
                items.add(new Item(titles[i], noDisturbRes, actions[i]));
            } else if (action == Item.ACTION_CLEAR_CACHE) {
                items.add(new Item(titles[i], GlideCatchUtil.getInstance().getCacheSize(), actions[i]));
            } else {
                items.add(new Item(titles[i], actions[i]));
            }
        }
        return items;
    }

    private class SettingViewHolder extends BaseViewHolder<Item> {

        private TextView mTitleView;
        private TextView mDescView;
        private ImageView mIconView;
        private ImageView mRightView;

        public SettingViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_setting);
            mTitleView = $(R.id.tv_title);
            mDescView = $(R.id.tv_desc);
            mIconView = $(R.id.iv_icon);
            mRightView = $(R.id.iv_right);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);
            mTitleView.setText(data.getTitle());
            if (data.getAction() == Item.ACTION_NO_DISTURB) {
                mDescView.setVisibility(View.GONE);
                mRightView.setVisibility(View.GONE);
                mIconView.setVisibility(View.VISIBLE);
                mIconView.setImageResource(data.getDrawableRes());
            } else {
                mDescView.setVisibility(View.VISIBLE);
                mRightView.setVisibility(View.VISIBLE);
                mIconView.setVisibility(View.GONE);
                mDescView.setText(TextUtils.isEmpty(data.getDesc()) ? "" : data.getDesc());
            }
        }
    }

    private class Item {

        public static final int ACTION_NO_DISTURB              = 1;
        // 功能介绍
        public static final int ACTION_FUNCTION_INTRODUCTION   = 2;
        // 多语言
        public static final int ACTION_MULTI_LANGUAGE          = 3;
        // 修改密码
        public static final int ACTION_MODIFY_PASSWORD         = 4;
        // 清除缓存
        public static final int ACTION_CLEAR_CACHE             = 5;
        // 意见反馈
        public static final int ACTION_ADVICE_FEEDBACK         = 6;
        // 联系客服
        public static final int ACTION_LINK                    = 7;

        private String title;
        private String desc;
        private int drawableRes;
        private int action;

        public Item(String title, int action) {
            this.title = title;
            this.action = action;
        }

        public Item(String title, String desc, int action) {
            this.title = title;
            this.desc = desc;
            this.action = action;
        }

        public Item(String title, int drawableRes, int action) {
            this.title = title;
            this.drawableRes = drawableRes;
            this.action = action;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public int getDrawableRes() {
            return drawableRes;
        }

        public int getAction() {
            return action;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void setDrawableRes(int drawableRes) {
            this.drawableRes = drawableRes;
        }
    }
}
