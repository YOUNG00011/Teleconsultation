package com.wxsoft.teleconsultation.ui.fragment.user.setting;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.helper.SharedPreferencesHelper;
import com.wxsoft.teleconsultation.ui.activity.HomeActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.LocalManageUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LanguageSelectFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, LanguageSelectFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private int mCurrentSelected;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mCurrentSelected = SharedPreferencesHelper.getSelectLanguage();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.language_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        RecyclerArrayAdapter<Language> adapter;
        mRecyclerView.setAdapter(adapter = new RecyclerArrayAdapter<Language>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new LanguageViewHolder(parent);
            }
        });

        adapter.setOnItemClickListener(position -> {
            mCurrentSelected = adapter.getItem(position).getFlag();
            adapter.notifyDataSetChanged();
        });
        adapter.addAll(getLanguages());
    }

    public List<Language> getLanguages() {
        List<Language> languages = new ArrayList<>();
        languages.add(new Language(getString(R.string.language_auto), 0));
        languages.add(new Language(getString(R.string.language_cn), 4));
        languages.add(new Language(getString(R.string.language_en), 1));
        languages.add(new Language(getString(R.string.language_ru), 2));
//        languages.add(new Language(getString(R.string.language_ar), 3));
        return languages;
    }

    private void save() {
        LocalManageUtil.saveSelectLanguage(_mActivity, mCurrentSelected);
        HomeActivity.reStart(_mActivity);
    }

    private class LanguageViewHolder extends BaseViewHolder<Language> {

        private TextView mTitleView;
        private ImageView mSelectView;

        public LanguageViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_one_select);
            mTitleView = $(R.id.tv_title);
            mSelectView = $(R.id.iv_select);
        }

        @Override
        public void setData(Language data) {
            super.setData(data);
            mTitleView.setText(data.getTitle());
            mSelectView.setVisibility(data.getFlag() == mCurrentSelected ? View.VISIBLE : View.GONE);
        }
    }

    private class Language {

        private String title;
        private int flag;

        public Language(String title, int flag) {
            this.title = title;
            this.flag = flag;
        }

        public String getTitle() {
            return title;
        }

        public int getFlag() {
            return flag;
        }
    }
}
