package com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.Photo;
import com.wxsoft.teleconsultation.event.UploadPhotoSuccessEvent;
import com.wxsoft.teleconsultation.helper.view.VaryViewHelperController;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.FileUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.lujun.androidtagview.TagContainerLayout;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EMRFragment extends BaseFragment {

    public static void launch(Activity from, Patient patient, String EMRId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_EMR_ID, EMRId);
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launch(from, EMRFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";
    private static final String FRAGMENT_ARGS_EMR_ID = "FRAGMENT_ARGS_EMR_ID";

    @BindView(R.id.tabs)
    PagerSlidingTabStrip mPagerSlidingTabStrip;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private Patient mPatient;
    private String mEMRId;
    private VaryViewHelperController mVaryViewHelperController;
    private int mCurrentPosition = 0;
    private EMRPageAdapter mEMRPageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_emr;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        mEMRId = getArguments().getString(FRAGMENT_ARGS_EMR_ID);

        setupPatientViews(view);

        if (App.mEMRTabs.isEmpty()) {
            mVaryViewHelperController = new VaryViewHelperController(mViewPager);
            loadAllEMRTabs();
        } else {
            setupViewPager();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle("");
        menuItem.setIcon(R.drawable.ic_file_upload_white_24dp);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                if (data != null) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    ArrayList<String> photoLocalPaths = new ArrayList<>();
                    for (LocalMedia localMedia : selectList) {
                        photoLocalPaths.add(localMedia.getPath());
                    }
                    if (!photoLocalPaths.isEmpty()) {
                        if (mCurrentPosition == 0) {
                            SelectPhotoCategoryActivity.launch(this, photoLocalPaths);
                        } else {
                            commit(photoLocalPaths, null);
                        }
                    }
                }
            } else if (requestCode == SelectPhotoCategoryActivity.REQUEST_SELECT_CATEGORY) {
                if (data != null) {
                    ArrayList<String> localPaths =
                            data.getStringArrayListExtra(SelectPhotoCategoryActivity.KEY_SELECTED_PHOTOS);
                    String category = data.getStringExtra(SelectPhotoCategoryActivity.KEY_SELECTED_CATEGORY);
                    for (EMRTab emrTab : mEMRPageAdapter.getEmrTabs()) {
                        if (category.equals(emrTab.getNodeType())) {
                            commit(localPaths, emrTab);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.patient_detail_emr);
        setHasOptionsMenu(true);
    }

    private void setupPatientViews(View view) {
        ((RoundedImageView) ButterKnife.findById(view, R.id.riv_avatar)).setImageResource(mPatient.getAvatarDrawableRes());
        ((TextView) ButterKnife.findById(view, R.id.tv_name)).setText(mPatient.getName());
        ((TextView) ButterKnife.findById(view, R.id.tv_health)).setText(mPatient.getMedicalInsuranceName());
        ((TextView) ButterKnife.findById(view, R.id.tv_gender)).setText(mPatient.getFriendlySex());
        ((TextView) ButterKnife.findById(view, R.id.tv_age)).setText(mPatient.getAge() + "岁");

        List<PatientTag> patientTags = mPatient.getPatientTags();
        if (patientTags != null && !patientTags.isEmpty()) {
            TagContainerLayout tagContainerLayout = ButterKnife.findById(view, R.id.tag);

            List<String> tagNames = new ArrayList<>();
            for (PatientTag patientTag : patientTags) {
                tagNames.add(patientTag.getTagName());
            }
            tagContainerLayout.setTags(tagNames);
        }
    }

    private void loadAllEMRTabs() {
        mVaryViewHelperController.showLoading("");
        ApiFactory.getPatientManagerApi().getAllEMRNode("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<EMRTab>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mVaryViewHelperController.showError(e.getMessage(), v -> {
                            loadAllEMRTabs();
                        });
                    }

                    @Override
                    public void onNext(BaseResp<List<EMRTab>> resp) {
                        mVaryViewHelperController.restore();
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<EMRTab>> resp) {
        if (!resp.isSuccess()) {
            mVaryViewHelperController.showEmpty("获取数据失败", v -> {
                loadAllEMRTabs();
            });
            return;
        }

        List<EMRTab> tabs = resp.getData();
        if (tabs == null || tabs.isEmpty()) {
            mVaryViewHelperController.showEmpty("获取数据失败", v -> {
                loadAllEMRTabs();
            });
            return;
        }

        App.mEMRTabs.addAll(tabs);
        setupViewPager();
    }

    private void setupViewPager() {
        mViewPager.setAdapter(mEMRPageAdapter = new EMRPageAdapter(getChildFragmentManager()));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    private void commit(List<String> photoLocalPaths, EMRTab emrTab) {
        String content = "";
        String contentType = "102-0002";
        if (emrTab == null) {
            emrTab = mEMRPageAdapter.getEmrTabs().get(mCurrentPosition);
        }
        String EMRNodeType = emrTab.getNodeType();
        String EMRNodeypeId = emrTab.getId();
        String uploaderId = AppContext.getUser().getDoctId();
        String uploaderName = AppContext.getUser().getName();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("Content", content)
                .addFormDataPart("ContentType", contentType)
                .addFormDataPart("EMRNodeType", EMRNodeType)
                .addFormDataPart("EMRNodeypeId", EMRNodeypeId)
                .addFormDataPart("PatientEMRId", mEMRId)
                .addFormDataPart("UploaderId", uploaderId)
                .addFormDataPart("UploaderName", uploaderName);



        if (photoLocalPaths != null && !photoLocalPaths.isEmpty()) {
            for (int i = 0; i < photoLocalPaths.size(); i++) {
                File file = new File(photoLocalPaths.get(i));
                String fileExtension = FileUtil.getFileExtension(file);
                String fileName = System.currentTimeMillis() + fileExtension;
                builder.addFormDataPart("file" + i, fileName, RequestBody.create(MediaType.parse("image/*"), file));
            }
        }

        String url = AppConstant.BASE_URL + "api/PatientInfo/SavePatientEMRContent?isMobile=true";
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())//传参数、文件或者混合，改一下就行请求体就行
                .build();

        ViewUtil.createProgressDialog(_mActivity, "上传中...");
        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _mActivity.runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {

                Log.d(response.isSuccessful()?"":"","");
                _mActivity.runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    EventBus.getDefault().post(new UploadPhotoSuccessEvent(mEMRPageAdapter.getEmrTabs().get(mCurrentPosition).getId()));
                });
            }
        });
    }

    private class EMRPageAdapter extends FragmentPagerAdapter {

        private List<EMRTab> emrTabs;

        public EMRPageAdapter(FragmentManager fm) {
            super(fm);
            emrTabs = new ArrayList<>();
            emrTabs.add(EMRTab.getAll());
            emrTabs.addAll(App.mEMRTabs);
        }

        public List<EMRTab> getEmrTabs() {
            return emrTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return emrTabs.get(position).getNodeType();
        }

        @Override
        public Fragment getItem(int position) {
            return EMRListFragment.newInstance(mEMRId, emrTabs.get(position).getId());
        }

        @Override
        public int getCount() {
            return emrTabs.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        }
    }
}
