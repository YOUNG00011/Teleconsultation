package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.diseasecounse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.diseasecounseling.Attachment;
import com.wxsoft.teleconsultation.entity.diseasecounseling.CallComment;
import com.wxsoft.teleconsultation.entity.prescription.PrescriptionCon;
import com.wxsoft.teleconsultation.event.UpdatePrescriptionConStatusEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.Chat2Activity;
import com.wxsoft.teleconsultation.ui.activity.PreviewPhotoActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.teleconsultation.ui.activity.Chat2Activity.EXTRA_KEY_DISEASECOUNSELING_ID;

public class DiseaseCounselingDetailFragment extends BaseFragment {

    public static final String EXTRA_DISEASECOUNSELING_ID = "EXTRA_DISEASECOUNSELING_ID";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private String diseaseCounselingId;
    private PrescriptionCon con;
    private RecyclerArrayAdapter<Attachment> mPhotoAdapter;
//    private RecyclerArrayAdapter<CallComment> mCommentAdapter;

    public static void launch(Activity from,String diseaseCounselingId) {
        FragmentArgs args = new FragmentArgs();
        args.add(EXTRA_DISEASECOUNSELING_ID, diseaseCounselingId);
        FragmentContainerActivity.launch(from, DiseaseCounselingDetailFragment.class,args);
    }

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.ll_root)
    LinearLayout mRootView;

    @BindView(R.id.tv_status)
    TextView mStatusView;

    @BindView(R.id.l_patiment)
    RelativeLayout lPa;

    @BindView(R.id.iv_patient_avatar)
    ImageView mPatientAvatarView;

    @BindView(R.id.tv_patient_name)
    TextView mPatientNameView;

    @BindView(R.id.tv_gender)
    TextView mGenderView;

    @BindView(R.id.tv_age)
    TextView mAgeView;

    @BindView(R.id.tv_health)
    TextView mHealthView;

    @BindView(R.id.ll_tag)
    LinearLayout mTagView;

    @BindView(R.id.tv_diagnosis)
    TextView mDiagnosisView;



    @BindView(R.id.rl_cancel_reason)
    RelativeLayout mCancelReasonLayout;

    @BindView(R.id.tv_cancel_reason)
    TextView mCancelReasonView;


    @BindView(R.id.rl_refuse_reason)
    RelativeLayout mRefuseReasonLayout;

    @BindView(R.id.tv_refuse_reason)
    TextView mRefuseReasonView;

    @BindView(R.id.rl_except_time)
    RelativeLayout mExceptTimeLayout;

    @BindView(R.id.tv_except_time)
    TextView mExceptTimeView;
    @BindView(R.id.aa)
    TextView aa;

    @BindView(R.id.lr_memo)
    LinearLayout mMemoLayout;

    @BindView(R.id.et_content)
    EditText mContentView;


    @BindView(R.id.ll_single_action)
    LinearLayout mSingleActionLayout;

    @BindView(R.id.tv_single_action)
    TextView mSingleActionView;

    @BindView(R.id.ll_4th_action)
    LinearLayout mDoubleActionLayout;

    @BindView(R.id.tv_double_action_1)
    TextView mDoubleAction1View;

    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mMemoRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prescription_con;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        diseaseCounselingId=getArguments().getString(EXTRA_DISEASECOUNSELING_ID);
        setupToolbar();
        mGlide = Glide.with(this);


        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity, 4));
        mPhotoRecyclerView.setAdapter(mPhotoAdapter = new RecyclerArrayAdapter<Attachment>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PhotoViewHolder(parent);
            }
        });

        mPhotoAdapter.setOnItemClickListener(position ->  {
            String imageUrl = mPhotoAdapter.getItem(position).url;
            PreviewPhotoActivity.launch(_mActivity, imageUrl);
        });

        mMemoRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);
        loadData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof UpdatePrescriptionConStatusEvent) {
            loadData();
        }
    }

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {

        Intent intent = new Intent(_mActivity, Chat2Activity.class);
        intent.putExtra(Chat2Activity.EXTRA_KEY_CONV_TITLE, "咨询详情");
        intent.putExtra(EXTRA_KEY_DISEASECOUNSELING_ID, diseaseCounselingId);
        intent.putExtra(Chat2Activity.EXTRA_KEY_SINGE, true);
        startActivity(intent);
//         if(con.type.equals("302-0001")
//                &&con.status.equals("303-0003")) {
//
//            new MaterialDialog.Builder(_mActivity)
//                    .title("咨询24小时后自动完成。")
//                    .content("已解决患者问题，提前完成？")
//                    .positiveText(R.string.ok)
//                    .negativeText(R.string.cancel)
//                    .onPositive((dialog, which) -> {
//                        ApiFactory.getDiseaseCounselingApi().complete(diseaseCounselingId)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Observer<BaseResp<String>>() {
//                                    @Override
//                                    public void onCompleted() {
//
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        ViewUtil.dismissProgressDialog();
//                                        ViewUtil.showMessage(e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onNext(BaseResp<String> resp) {
//                                        ViewUtil.dismissProgressDialog();
//                                        if (resp.isSuccess()) {
//
//                                            EventBus.getDefault().post(new UpdateDiseaseCounselingStatusEvent(diseaseCounselingId,"303-0006"));
//
//                                        } else {
//
//
//                                            ViewUtil.showMessage(resp.getMessage());
//                                        }
//                                    }
//                                });
//                    })
//                    .onNegative((dialog, which) -> {
//                        dialog.dismiss();
//                    })
//                    .show();
//        }
        //SelectDoctorFragment.launch(this, BusinessType.COUNSELING);
    }
    @OnClick(R.id.tv_single_action)
    void singleActionClick() {
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("用药咨询");
        setHasOptionsMenu(true);
    }

    Menu themenu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refuse,menu);
        themenu=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void prepare() {

        MenuItem refuse = themenu.findItem(R.id.refuse);

        if (con.status.compareTo("906-0002")<=0) {
            refuse.setVisible(true);
        } else{
            refuse.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refuse:
                DiseaseCounselingRefuseFragment.launch(_mActivity,diseaseCounselingId);
                return true;
            default:
                return true;
        }
    }

    private void loadData() {
        showRefreshing(true);
        ApiFactory.getPrescriptionApi().getConsultationDetail(diseaseCounselingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<PrescriptionCon>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<PrescriptionCon> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            con = resp.getData();
                            prepare();
                            setupViews();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void showRefreshing(final boolean refresh) {
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(refresh);
        });
    }


    private class PhotoViewHolder extends BaseViewHolder<Attachment> {

        private ImageView mTitleView;

        public PhotoViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_photo);
            mTitleView = $(R.id.iv_photo);
        }

        @Override
        public void setData(Attachment data) {
            super.setData(data);

            mGlide.load(data.url).into(mTitleView);
        }
    }

    private class CommentViewHolder extends BaseViewHolder<CallComment> {

        private TextView name;
        private TextView time;

        public CommentViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_memo_dis);
            name = $(R.id.tv_name);
            time = $(R.id.tv_time);
        }

        @Override
        public void setData(CallComment data) {
            super.setData(data);

            name.setText(data.commentContent);
            time.setText(data.commentDateTime.replace("T"," ").substring(0,10));
        }
    }


    private void setupViews() {
//        ((FragmentContainerActivity) getActivity()).getSupportActionBar().setTitle(con.patientName);
        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
        }



        mGlide.setDefaultRequestOptions(mOptions.error(con.weChatAccount.sex==1 ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                .load(con.patientHeadImage)
                .into(mPatientAvatarView);
        mGenderView.setText(con.weChatAccount.sex==1?"男":"女");
        mPatientNameView.setText(con.weChatAccount.name);
//        mAgeView.setText(String.valueOf(con.patientInfo.getAge()));
        mDiagnosisView.setText(con.describe);
        mStatusView.setText(con.getStatusName());
        mDoubleActionLayout.setVisibility(View.VISIBLE);

    }

    @OnEditorAction(R.id.et_content)
    boolean func(TextView textView, int id, KeyEvent event) {
        if (textView.getId()==R.id.et_content && id == EditorInfo.IME_ACTION_SEND) {
            String content = mContentView.getText().toString();
            if(TextUtils.isEmpty(content))return true;

            CallComment comment = new CallComment();
            comment.commentContent=content;
            comment.diseaseCounselingId=diseaseCounselingId;

          
        }
        return true;
    }

}
