package com.wxsoft.telereciver.ui.fragment.homepage.diseasecounseling;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wxsoft.telereciver.App;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.diseasecounseling.Attachment;
import com.wxsoft.telereciver.entity.diseasecounseling.CallComment;
import com.wxsoft.telereciver.entity.diseasecounseling.DiseaseCounseling;
import com.wxsoft.telereciver.event.UpdateDiseaseCounselingStatusEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.ChatActivity;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.activity.PreviewPhotoActivity;
import com.wxsoft.telereciver.ui.activity.chat.MessageListActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DiseaseCounselingDetailFragment extends BaseFragment {

    public static final String EXTRA_DISEASECOUNSELING_ID = "EXTRA_DISEASECOUNSELING_ID";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private String diseaseCounselingId;
    private DiseaseCounseling diseaseCounseling;
    private RecyclerArrayAdapter<Attachment> mPhotoAdapter;
    private RecyclerArrayAdapter<CallComment> mCommentAdapter;

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

    @BindView(R.id.tv_double_action_2)
    TextView mDoubleAction2View;

    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mMemoRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_diseasecounseling_detail;
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
        mMemoRecyclerView.setAdapter(mCommentAdapter = new RecyclerArrayAdapter<CallComment>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new CommentViewHolder(parent);
            }
        });
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
        if (object instanceof UpdateDiseaseCounselingStatusEvent) {
            loadData();
        }
    }

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {

         if(diseaseCounseling.type.equals("302-0001")
                &&diseaseCounseling.status.equals("303-0003")) {

            new MaterialDialog.Builder(_mActivity)
                    .title("??????24????????????????????????")
                    .content("???????????????????????????????????????")
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ApiFactory.getDiseaseCounselingApi().complete(diseaseCounselingId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<BaseResp<String>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ViewUtil.dismissProgressDialog();
                                        ViewUtil.showMessage(e.getMessage());
                                    }

                                    @Override
                                    public void onNext(BaseResp<String> resp) {
                                        ViewUtil.dismissProgressDialog();
                                        if (resp.isSuccess()) {

                                            EventBus.getDefault().post(new UpdateDiseaseCounselingStatusEvent(diseaseCounselingId,"303-0006"));

                                        } else {


                                            ViewUtil.showMessage(resp.getMessage());
                                        }
                                    }
                                });
                    })
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
        //SelectDoctorFragment.launch(this, BusinessType.COUNSELING);
    }
    @OnClick(R.id.tv_single_action)
    void singleActionClick() {
        if(diseaseCounseling.type.equals("302-0001")
                &&diseaseCounseling.status.equals("303-0002")
                &&diseaseCounseling.weChatAccount!=null
                &&diseaseCounseling.weChatAccount.jMessagAccount!=null
                &&diseaseCounseling.weChatAccount.jMessagAccount.getjUserName()!=null) {
            Intent intent = new Intent(_mActivity, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, "????????????");
            intent.putExtra(ChatActivity.EXTRA_KEY_DISEASECOUNSELING_ID, diseaseCounselingId);
            intent.putExtra(ChatActivity.EXTRA_KEY_SINGE, true);
            intent.putExtra(ChatActivity.TARGET_APP_ALLOW_EDIT,diseaseCounseling.status.equals("303-0002"));
            startActivity(intent);
        }else if(diseaseCounseling.type.equals("302-0002")
                &&diseaseCounseling.status.equals("303-0002")){
            new MaterialDialog.Builder(_mActivity)
                    .title(R.string.phone_call)
                    .content(diseaseCounseling.patient.getName())
                    .positiveText("??????")
                    .negativeText("??????")
                    .onPositive((dialog, which) -> {
                        new RxPermissions(_mActivity)
                                .request(Manifest.permission.CALL_PHONE)
                                .subscribe(granted -> {
                                    if (granted) {
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        Uri data = Uri.parse("tel:"+diseaseCounseling.patient.getPhone());
                                        intent.setData(data);
                                        startActivity(intent);
                                    } else {
                                        ViewUtil.showMessage("???????????????");
                                    }
                                });
                    }).show();
        }
    }

    @OnClick(R.id.tv_double_action_2)
    void doubleAction2Click() {
        if (diseaseCounseling.type.equals("302-0001")
                && (diseaseCounseling.status.equals("303-0002") || diseaseCounseling.status.equals("303-0003"))
                && diseaseCounseling.weChatAccount != null
                && diseaseCounseling.weChatAccount.jMessagAccount != null
                && diseaseCounseling.weChatAccount.jMessagAccount.getjUserName() != null) {
            if(!diseaseCounseling.status.equals("303-0001")&&diseaseCounseling.weChatAccount!=null&&diseaseCounseling.weChatAccount.jMessagAccount!=null &&diseaseCounseling.weChatAccount.jMessagAccount.getjUserName()!=null) {
                Intent intent = new Intent(_mActivity, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, "????????????");
                intent.putExtra(ChatActivity.EXTRA_KEY_DISEASECOUNSELING_ID, diseaseCounselingId);
                intent.putExtra(ChatActivity.EXTRA_KEY_SINGE, true);
                intent.putExtra(ChatActivity.TARGET_APP_ALLOW_EDIT,diseaseCounseling.status.equals("303-0002"));
                startActivity(intent);
            }else{
                ViewUtil.showMessage("??????????????????");
            }
        }
    }
//

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);
    }

    Menu themenu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phonecall,menu);
        themenu=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void prepare() {
        MenuItem call = themenu.findItem(R.id.call);
        MenuItem text = themenu.findItem(R.id.text);
        MenuItem refuse = themenu.findItem(R.id.refuse);

        if (diseaseCounseling.status.equals("303-0002")||diseaseCounseling.status.equals("303-0003")) {
            text.setVisible(false);
            refuse.setVisible(true);
            call.setVisible(false);
        } else if(diseaseCounseling.status.equals("303-0004")||diseaseCounseling.status.equals("303-0005")
                ||diseaseCounseling.status.equals("303-0008")) {
            text.setVisible(false);
            refuse.setVisible(false);
            call.setVisible(false);
        }else{

            if (diseaseCounseling.type.equals("302-0001")) {
                text.setVisible(true);
                refuse.setVisible(false);
                call.setVisible(false);
            } else {
                call.setVisible(true);
                refuse.setVisible(false);
                text.setVisible(false);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refuse:
                DiseaseCounselingRefuseFragment.launch(_mActivity,diseaseCounselingId);
                return true;
            case R.id.text:
                if(!diseaseCounseling.status.equals("303-0001")&&diseaseCounseling.weChatAccount!=null&&diseaseCounseling.weChatAccount.jMessagAccount!=null &&diseaseCounseling.weChatAccount.jMessagAccount.getjUserName()!=null) {
//                    Intent intent = new Intent(_mActivity, ChatActivity.class);
//                    intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, "????????????");
//                    intent.putExtra(ChatActivity.EXTRA_KEY_DISEASECOUNSELING_ID, diseaseCounselingId);
//                    intent.putExtra(ChatActivity.EXTRA_KEY_SINGE, true);
//                    intent.putExtra(ChatActivity.TARGET_APP_ALLOW_EDIT,diseaseCounseling.status.equals("303-0002"));
//                    startActivity(intent);

                    Intent intent = new Intent(_mActivity, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, "????????????");
                    intent.putExtra(MessageListActivity.DISEASECOUNSELING_ID, diseaseCounselingId);
                    intent.putExtra(ChatActivity.EXTRA_KEY_SINGE, true);
//                    intent.putExtra(ChatActivity.TARGET_APP_ALLOW_EDIT,diseaseCounseling.status.equals("303-0002"));
                    startActivity(intent);
                }else{
                    ViewUtil.showMessage("??????????????????");
                }
                return true;

            case R.id.call:

                new MaterialDialog.Builder(_mActivity)
                        .title(R.string.phone_call)
                        .content(diseaseCounseling.patient.getName())
                        .positiveText("??????")
                        .negativeText("??????")
                        .onPositive((dialog, which) -> {
                            new RxPermissions(_mActivity)
                                    .request(Manifest.permission.CALL_PHONE)
                                    .subscribe(granted -> {
                                        if (granted) {
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            Uri data = Uri.parse("tel:"+diseaseCounseling.patient.getPhone());
                                            intent.setData(data);
                                            startActivity(intent);
                                        } else {
                                            ViewUtil.showMessage("???????????????");
                                        }
                                    });
                        }).show();
                return true;
            default:
                return true;
        }
    }

    private void loadData() {
        showRefreshing(true);
        ApiFactory.getDiseaseCounselingApi().getDetail(diseaseCounselingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<DiseaseCounseling>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<DiseaseCounseling> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            diseaseCounseling = resp.getData();
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


    private void setupPhotoRecyclerView() {

        mPhotoAdapter.clear();
        mPhotoAdapter.addAll(diseaseCounseling.attachments);
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
        ((FragmentContainerActivity) getActivity()).getSupportActionBar().setTitle(diseaseCounseling.typeName);
        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
        }

        mGlide.setDefaultRequestOptions(mOptions.error(diseaseCounseling.patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                .load(diseaseCounseling.patientHeadImage)
                .into(mPatientAvatarView);
        mGenderView.setText(diseaseCounseling.patient.getFriendlySex());
        mPatientNameView.setText(diseaseCounseling.patient.getName());
        mAgeView.setText(String.valueOf(diseaseCounseling.patient.getAge()));
        mDiagnosisView.setText(diseaseCounseling.describe);
        mStatusView.setText(diseaseCounseling.statusName);

        if(diseaseCounseling.type.equals("302-0001")) {
            if (diseaseCounseling.status.equals("303-0004")
                    || diseaseCounseling.status.equals("303-0005")) {
                mSingleActionLayout.setVisibility(View.GONE);
                mCancelReasonLayout.setVisibility(View.VISIBLE);
                mCancelReasonView.setText(diseaseCounseling.memo);
            } else if (diseaseCounseling.status.equals("303-0008")) {
                mSingleActionLayout.setVisibility(View.GONE);
                mDoubleActionLayout.setVisibility(View.GONE);
                mRefuseReasonLayout.setVisibility(View.VISIBLE);
                mRefuseReasonView.setText(diseaseCounseling.memo);
            } else if (diseaseCounseling.status.equals("303-0003")) {
                mDoubleActionLayout.setVisibility(View.VISIBLE);

                mDoubleAction2View.setText(R.string.str_dis_reply);
                mDoubleAction1View.setText(R.string.action_602_0002_tome);
                mSingleActionLayout.setVisibility(View.GONE);
                //mSingleActionView.setText( R.string.str_dis_reply );
            }else if(diseaseCounseling.status.equals("303-0002")){
                mDoubleActionLayout.setVisibility(View.GONE);

                mSingleActionLayout.setVisibility(View.VISIBLE);
                mSingleActionView.setText( R.string.str_dis_reply );
            }else {
                mSingleActionLayout.setVisibility(View.GONE);
                mDoubleActionLayout.setVisibility(View.GONE);
            }
        }

        else if(diseaseCounseling.type.equals("302-0002")){

            mSingleActionLayout.setVisibility(View.VISIBLE);
            mSingleActionView.setText( R.string.str_dis_calling);
            mExceptTimeLayout.setVisibility(View.VISIBLE);
            mExceptTimeView.setText(diseaseCounseling.startTime.replace("T"," ").substring(0,19));
            mMemoLayout.setVisibility(View.VISIBLE);
            mContentView.setText(diseaseCounseling.memo);
        }

        lPa.setOnClickListener(v -> PatientDetailActivity.launch(_mActivity, diseaseCounseling.patient));
        setupPhotoRecyclerView();

        mCommentAdapter.clear();
        if(diseaseCounseling.callComments!=null){
            mCommentAdapter.addAll(diseaseCounseling.callComments);
            if(diseaseCounseling.callComments.size()==3){
                mContentView.setVisibility(View.GONE);
                aa.setVisibility(View.GONE);
            }
        }

    }

    @OnEditorAction(R.id.et_content)
    boolean func(TextView textView, int id, KeyEvent event) {
        if (textView.getId()==R.id.et_content && id == EditorInfo.IME_ACTION_SEND) {
            String content = mContentView.getText().toString();
            if(TextUtils.isEmpty(content))return true;

            CallComment comment = new CallComment();
            comment.commentContent=content;
            comment.diseaseCounselingId=diseaseCounselingId;

           // ViewUtil.createProgressDialog(_mActivity,"");
            ApiFactory.getDiseaseCounselingApi().saveCallingComment(comment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResp>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            showRefreshing(false);
                            ViewUtil.showMessage(e.getMessage());
                        }

                        @Override
                        public void onNext(BaseResp resp) {
                            showRefreshing(false);
                            if (resp.isSuccess()) {
                                loadData();
                            } else {
                                ViewUtil.showMessage(resp.getMessage());
                            }
                        }
                    });
        }
        return true;
    }

}
