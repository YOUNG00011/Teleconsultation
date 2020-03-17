package com.wxsoft.telereciver.ui.fragment.homepage.patientmanager;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.responsedata.PatientEMRResp;
import com.wxsoft.telereciver.event.UploadPhotoSuccessEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.PreviewPhotoActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.FileUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EMRListFragment extends BaseFragment {

    public static EMRListFragment newInstance(String EMRId, String tabId) {
        Bundle args = new Bundle();
        args.putString(EXTRAS_KEY_EMR_ID, EMRId);
        args.putString(EXTRAS_KEY_TAB_ID, tabId);
        EMRListFragment fragment = new EMRListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String EXTRAS_KEY_EMR_ID = "EXTRAS_KEY_EMR_ID";
    private static final String EXTRAS_KEY_TAB_ID = "EXTRAS_KEY_TAB_ID";

    @BindView(R.id.tv_node_translate)
    TextView mNodeTranslateView;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<PatientEMRResp.PatEMRContentAttachment> mAdapter;
    private String mEMRId;
    private String mTabId;
    private List<PatientEMRResp.PatientEMRTranslate> mPatientEMRTranslates;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_emr_list;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mEMRId = getArguments().getString(EXTRAS_KEY_EMR_ID);
        mTabId = getArguments().getString(EXTRAS_KEY_TAB_ID);
        setupRecyclerView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<PatientEMRResp.PatEMRContentAttachment>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new EMRViewHolder(parent, Glide.with(EMRListFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position ->  {
            String imageUrl = mAdapter.getItem(position).getUrl();
            PreviewPhotoActivity.launch(_mActivity, imageUrl);
        });

        mAdapter.setOnItemLongClickListener(position -> {
            new MaterialDialog.Builder(_mActivity)
                    .items(R.array.emr_photo_option)
                    .itemsCallback((dialog, view2, which, text) -> {
                        if (which == 0) {
                            delete(mAdapter.getItem(position).getAttachmentId());
                        } else if (which == 1) {
                            new RxPermissions(_mActivity)
                                    .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .subscribe(granted -> {
                                        if (granted) {
                                            String url = mAdapter.getItem(position).getUrl();
                                            String fileName = FileUtil.getFileName(url);
                                            Glide.with(this).load(url)
                                                    .into(new SimpleTarget<Drawable>() {
                                                        @Override
                                                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                                            if (AppUtil.saveImageToGallery(_mActivity, fileName, bitmap)) {
                                                                ViewUtil.showMessage("保存成功");
                                                            } else {
                                                                ViewUtil.showMessage("保存失败");
                                                            }
                                                        }
                                                    });
                                        } else {
                                            ViewUtil.showMessage("授权已取消");
                                        }
                                    });
                        }
                    })
                    .show();
            return false;
        });

        loadData();
    }

    private void loadData() {
        ApiFactory.getPatientManagerApi().getPatientEMRById(mEMRId, mTabId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<PatientEMRResp>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<PatientEMRResp> resp) {
                        ViewUtil.dismissProgressDialog();
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<PatientEMRResp> baseResp) {
        if (!baseResp.isSuccess()) {
            ViewUtil.showMessage(baseResp.getMessage());
            return;
        }

        PatientEMRResp patientEMRResp = baseResp.getData();
        mPatientEMRTranslates = patientEMRResp.getPatientEMRTranslates();
        if (mPatientEMRTranslates == null || mPatientEMRTranslates.isEmpty()) {
            mNodeTranslateView.setVisibility(View.GONE);
        } else {
            String nodeTranslateContent = null;
            for (PatientEMRResp.PatientEMRTranslate patientEMRTranslate : mPatientEMRTranslates) {
                String emrContentAttachmentId = patientEMRTranslate.getEmrContentAttachmentId();
                String content = patientEMRTranslate.getContent();
                String tabId = patientEMRTranslate.getEmrNodeType();
                if (TextUtils.isEmpty(patientEMRTranslate.getEmrContentAttachmentId())
                        && !TextUtils.isEmpty(patientEMRTranslate.getContent()) &&
                        tabId.equals(mTabId)) {
                    nodeTranslateContent = patientEMRTranslate.getContent();
                    break;
                }
            }

            if (TextUtils.isEmpty(nodeTranslateContent)) {
                mNodeTranslateView.setVisibility(View.GONE);
            } else {
                mNodeTranslateView.setVisibility(View.VISIBLE);
                mNodeTranslateView.setText(nodeTranslateContent);
            }
        }


        if (patientEMRResp == null ||
                patientEMRResp.getPat_EMRContentAttachments() == null ||
                patientEMRResp.getPat_EMRContentAttachments().isEmpty()) {
            ((TextView) ButterKnife.findById(mRecyclerView.getEmptyView(), R.id.message_info)).setText("没有病历");
            mRecyclerView.showEmpty();
            mRecyclerView.getEmptyView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            return;
        }

        mRecyclerView.showRecycler();

        List<PatientEMRResp.PatEMRContentAttachment> patEMRContentAttachments = patientEMRResp.getPat_EMRContentAttachments();

        if (prevPatEMRContentAttachment != null) {
            prevPatEMRContentAttachment = null;
        }
        mAdapter.clear();
        mAdapter.addAll(patEMRContentAttachments);
    }

    private void delete(String attachmentId) {
        ViewUtil.createProgressDialog(_mActivity, "处理中...");
        ApiFactory.getPatientManagerApi().deleteAttachment(attachmentId)
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
                            loadData();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    @Subscribe
    public void onEvent(UploadPhotoSuccessEvent uploadPhotoSuccessEvent) {
        if (TextUtils.isEmpty(mTabId) || uploadPhotoSuccessEvent.getTabId().equals(mTabId)) {
            loadData();
        }
    }


    private PatientEMRResp.PatEMRContentAttachment prevPatEMRContentAttachment;
    private class EMRViewHolder extends BaseViewHolder<PatientEMRResp.PatEMRContentAttachment> {

        private TextView mMonthDayView;
        private TextView mYearView;
        private RoundedImageView mPhotoView;
        private TextView mPhotoCategoryView;
        private TextView mNameView;
        private TextView mHospitalView;
        private TextView mDepartmentView;
        private TextView mTranslateView;

        private RequestManager mGlide;
        private RequestOptions options;

        public EMRViewHolder(ViewGroup parent, RequestManager requestManager) {
            super(parent, R.layout.item_emr);
            mMonthDayView = $(R.id.tv_month_day);
            mYearView = $(R.id.tv_year);
            mPhotoView = $(R.id.riv_photo);
            mPhotoCategoryView = $(R.id.tv_photo_category);
            mNameView = $(R.id.tv_name);
            mHospitalView = $(R.id.tv_hospital);
            mDepartmentView = $(R.id.tv_department);
            mTranslateView = $(R.id.tv_translate);

            this.mGlide = requestManager;

            options = new RequestOptions();
            options.centerCrop()
                    .dontAnimate();
        }

        @Override
        public void setData(PatientEMRResp.PatEMRContentAttachment data) {
            super.setData(data);

            if (prevPatEMRContentAttachment == null) {
                mYearView.setVisibility(View.VISIBLE);
                mMonthDayView.setVisibility(View.VISIBLE);
                String date = data.getCreatedDate();
                String year = date.substring(0, 4);
                String monthAndDay = date.substring(5, 10);
                mYearView.setText(year);
                mMonthDayView.setText(monthAndDay);
            } else {
                String prevDate = prevPatEMRContentAttachment.getCreatedDate().substring(0, 10);
                String currentDate = data.getCreatedDate().substring(0, 10);
                if (prevDate.equals(currentDate)) {
                    mYearView.setVisibility(View.INVISIBLE);
                    mMonthDayView.setVisibility(View.INVISIBLE);
                } else {
                    mYearView.setVisibility(View.VISIBLE);
                    mMonthDayView.setVisibility(View.VISIBLE);
                    String date = data.getCreatedDate();
                    String year = date.substring(0, 4);
                    String monthAndDay = date.substring(5, 10);
                    mYearView.setText(year);
                    mMonthDayView.setText(monthAndDay);
                }
            }

            prevPatEMRContentAttachment = data;

            mGlide.setDefaultRequestOptions(options.placeholder(R.drawable.ic_emr_placeholder))
                    .load(data.getUrl())
                    .thumbnail(0.5f)
                    .into(mPhotoView);

            PatientEMRResp.PatEMRContentAttachment.EMRContent emrContent = data.getEmrContent();

            mPhotoCategoryView.setText(emrContent.getEmrNodeType());
            mNameView.setText(emrContent.getUploaderName());
            mHospitalView.setText(emrContent.getSourceHospitalName());
            mDepartmentView.setText(emrContent.getSorceDepartmentName());

            String translate = "";
            if (mPatientEMRTranslates != null && !mPatientEMRTranslates.isEmpty()) {
                for (PatientEMRResp.PatientEMRTranslate patientEMRTranslate : mPatientEMRTranslates) {
                    String emrContentAttachmentId = patientEMRTranslate.getEmrContentAttachmentId();
                    if (emrContentAttachmentId != null && emrContentAttachmentId.equals(data.getId()) &&
                            !TextUtils.isEmpty(patientEMRTranslate.getContent())) {
                        translate = patientEMRTranslate.getContent();
                    }
                }
            }

            mTranslateView.setText(translate);
        }
    }
}
