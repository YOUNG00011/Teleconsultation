package com.wxsoft.teleconsultation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.ui.widget.DividerGridItemDecoration;
import com.wxsoft.teleconsultation.util.DensityUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectPhotoCategoryActivity extends SupportBaseActivity {

    public static void launch(Fragment from, ArrayList<String> photoPaths) {
        Intent intent = new Intent(from.getActivity(), SelectPhotoCategoryActivity.class);
        intent.putStringArrayListExtra(EXTRA_KEY_PHOTOPATHS, photoPaths);
        from.startActivityForResult(intent, REQUEST_SELECT_CATEGORY);
    }

    private static final String EXTRA_KEY_PHOTOPATHS = "EXTRA_KEY_PHOTOPATHS";
    public static final int REQUEST_SELECT_CATEGORY = 121;
    public static final String KEY_SELECTED_PHOTOS = "KEY_SELECTED_PHOTOS";
    public static final String KEY_SELECTED_CATEGORY = "KEY_SELECTED_CATEGORY";

    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    @BindView(R.id.recycler_view_category)
    EasyRecyclerView mCategoryRecyclerView;

    private RecyclerArrayAdapter<String> mPhotoAdapter;
    private RecyclerArrayAdapter<EMRTab> mEMRTabAdapter;

    private ArrayList<String> mPhotoPaths;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_category_select;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        setupToolbar();
        mPhotoPaths =  getIntent().getStringArrayListExtra(EXTRA_KEY_PHOTOPATHS);
        setupPhotoRecyclerView();
        setupCategoryRecyclerView();
    }

    private void setupToolbar() {
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.select_picture_type_title);
    }

    private void setupPhotoRecyclerView() {
        int spanCount = 3;
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        mPhotoRecyclerView.setAdapter(mPhotoAdapter = new RecyclerArrayAdapter<String>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PhotoViewHolder(parent, SelectPhotoCategoryActivity.this, Glide.with(SelectPhotoCategoryActivity.this), spanCount, localPath ->  {
                    mPhotoAdapter.remove(localPath);
                    if (mPhotoAdapter.getAllData().isEmpty()) {
                        finish();
                    }
                });
            }
        });

        mPhotoAdapter.addAll(mPhotoPaths);
    }

    private void setupCategoryRecyclerView() {
        mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        DividerGridItemDecoration itemDecoration = new DividerGridItemDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f));
        mCategoryRecyclerView.addItemDecoration(itemDecoration);
        mCategoryRecyclerView.setAdapter(mEMRTabAdapter = new RecyclerArrayAdapter<EMRTab>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CategoryViewHolder(parent);
            }
        });

        mEMRTabAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, (ArrayList<String>) mPhotoAdapter.getAllData());
            intent.putExtra(KEY_SELECTED_CATEGORY, mEMRTabAdapter.getItem(position).getNodeType());
            setResult(RESULT_OK, intent);
            finish();
        });

        mEMRTabAdapter.addAll(App.mEMRTabs);
    }

    private static class PhotoViewHolder extends BaseViewHolder<String> {

        interface OnPhotoRemoveListener {
            void remove(String localPath);
        }

        private ImageView mPhotoView;
        private ImageView mRemoveView;

        private RequestManager mGlide;
        private int mImageSize;
        private RequestOptions options;

        private OnPhotoRemoveListener mOnPhotoRemoveListener;

        public PhotoViewHolder(ViewGroup parent, Context context, RequestManager requestManager, int columnNumber, OnPhotoRemoveListener onPhotoRemoveListener) {
            super(parent, R.layout.item_clinic_category_select_photo);
            mPhotoView = $(R.id.iv_photo);
            mRemoveView = $(R.id.iv_remove);

            this.mGlide = requestManager;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            mImageSize = widthPixels / columnNumber;

            options = new RequestOptions();
            options.centerCrop()
                    .dontAnimate()
                    .override(mImageSize, mImageSize);

            mOnPhotoRemoveListener = onPhotoRemoveListener;
        }

        @Override
        public void setData(String data) {
            super.setData(data);

            mGlide.setDefaultRequestOptions(options)
                    .load(new File(data))
                    .thumbnail(0.5f)
                    .into(mPhotoView);

            mRemoveView.setOnClickListener(v -> {
                if (mOnPhotoRemoveListener != null) {
                    mOnPhotoRemoveListener.remove(data);
                }
            });
        }
    }

    private class CategoryViewHolder extends BaseViewHolder<EMRTab> {

        private TextView mTitleView;

        public CategoryViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_clinic_category);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(EMRTab data) {
            super.setData(data);
            mTitleView.setText(data.getNodeType());
        }
    }
}
