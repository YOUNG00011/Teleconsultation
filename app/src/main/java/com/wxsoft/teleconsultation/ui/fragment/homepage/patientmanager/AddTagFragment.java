package com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.RecommendTag;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddTagFragment extends BaseFragment {

    public static void launch(Fragment from, ArrayList<String> tags) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_TAGS, tags);
        FragmentContainerActivity.launchForResult(from, AddTagFragment.class, args, REQUEST_ADD_TAG );
    }

    private static final String FRAGMENT_ARGS_TAGS = "FRAGMENT_ARGS_TAGS";
    public static final int REQUEST_ADD_TAG = 88;
    public static final String KEY_TAG = "KEY_TAG";

    @BindView(R.id.cet_tag)
    ClearableEditText mEditTagView;

    @BindView(R.id.tag)
    TagContainerLayout mTagContainerLayout;

    private ArrayList<String> mTags;
    private ArrayList<RecommendTag> mAdminPatientTags;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_tag;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mTags = getArguments().getStringArrayList(FRAGMENT_ARGS_TAGS);
        setupToolbar();
        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                String tagName = mEditTagView.getText().toString();
                if (TextUtils.isEmpty(tagName)) {
                    ViewUtil.showMessage("请输入标签");
                    return false;
                }

                if (mTags.contains(tagName)) {
                    ViewUtil.showMessage("标签已存在");
                    return false;
                }

                saveTag(tagName);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.add_tags_title);
        setHasOptionsMenu(true);
    }

    private void loadData() {
        ApiFactory.getPatientManagerApi().getRecommendTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<RecommendTag>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<RecommendTag>> resp) {
                        processLoadDataResponse(resp);
                    }
                });
    }

    private void processLoadDataResponse(BaseResp<List<RecommendTag>> resp) {
        List<RecommendTag> recommendTags = resp.getData();
        if (recommendTags != null && !recommendTags.isEmpty()) {
            mAdminPatientTags = new ArrayList<>();
            List<String> adminPatientTagNames = new ArrayList<>();
            for (RecommendTag recommendTag : recommendTags) {
                mAdminPatientTags.add(recommendTag);
                adminPatientTagNames.add(recommendTag.getTagName());
            }

            if (!adminPatientTagNames.isEmpty()) {
                mTagContainerLayout.setTags(adminPatientTagNames);
                mTagContainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(int position, String text) {
                        mEditTagView.setText(text);
                        mEditTagView.setSelection(text.length());
                    }

                    @Override
                    public void onTagLongClick(int position, String text) {

                    }

                    @Override
                    public void onTagCrossClick(int position) {

                    }
                });
            }
        }
    }

    private void saveTag(String tagName) {
        RecommendTag recommendTag = null;
        if (mAdminPatientTags != null && !mAdminPatientTags.isEmpty()) {
            for (RecommendTag targetRecommendTag : mAdminPatientTags) {
                if (targetRecommendTag.getTagName().equals(tagName)) {
                    recommendTag = targetRecommendTag;
                    break;
                }
            }
        }

        if (recommendTag == null) {
            recommendTag = new RecommendTag();
            recommendTag.setTagType(PatientTag.TAG_NORMAL);
            recommendTag.setTagName(tagName);
            ViewUtil.createProgressDialog(_mActivity, "");
            ApiFactory.getPatientManagerApi().saveTag(recommendTag)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResp<RecommendTag>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ViewUtil.dismissProgressDialog();
                            ViewUtil.showMessage(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseResp<RecommendTag> resp) {
                            ViewUtil.dismissProgressDialog();
                            processSaveTagResponse(resp);
                        }
                    });
        } else {
            finish(recommendTag);
        }
    }

    private void processSaveTagResponse(BaseResp<RecommendTag> resp) {
        if (resp.isSuccess()) {
            finish(resp.getData());
        } else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }

    private void finish(RecommendTag recommendTag) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TAG, recommendTag);
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }
}
