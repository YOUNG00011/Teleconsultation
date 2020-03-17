package com.wxsoft.telereciver.ui.fragment.homepage.transfertreatment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.diseasecounseling.Attachment;
import com.wxsoft.telereciver.entity.transfertreatment.TreatMent;
import com.wxsoft.telereciver.event.TreatMentStateChangeEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.activity.PatientDetailActivity;
import com.wxsoft.telereciver.ui.activity.PreviewPhotoActivity;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.patientmanager.EMRFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.transfertreatment.calltheroll.TreatmentCallTheRollFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.transfertreatment.calltheroll.TreatmentReceiveFragment;
import com.wxsoft.telereciver.ui.widget.CallPopWindow;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransferTreatmentDetailFragment extends BaseFragment {

    public static final String EXTRA_DISEASECOUNSELING_ID = "EXTRA_DISEASECOUNSELING_ID";
    public static final String EXTRA_DISEASECOUNSELING_MINE = "EXTRA_DISEASECOUNSELING_MINE";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private String diseaseCounselingId;
    private boolean mine;
    private TreatMent treatMent;
    private RecyclerArrayAdapter<Attachment> mPhotoAdapter;
    CallPopWindow popWindow;

    String[] statuses=new String[]{"602-0001","602-0002","602-0003","602-0004","602-0005"};
    String[] statusNames;
    public static void launch(Activity from,String diseaseCounselingId,boolean mine) {
        FragmentArgs args = new FragmentArgs();
        args.add(EXTRA_DISEASECOUNSELING_ID, diseaseCounselingId);
        args.add(EXTRA_DISEASECOUNSELING_MINE, mine);
        FragmentContainerActivity.launch(from, TransferTreatmentDetailFragment.class,args);
    }

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_describe)
    TextView mDescribe;

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

    @BindView(R.id.ll_apply_doctor)
    LinearLayout laccept;
    @BindView(R.id.detail)
    LinearLayout detail;

    @BindView(R.id.ll_apply_doctor1)
    LinearLayout lapply;

    @BindView(R.id.tv_diagnosis)
    TextView mDiagnosisView;

    @BindView(R.id.iv_doctor_avatar)
    ImageView mDocAvatar;

    @BindView(R.id.tv_doctor_name)
    TextView mDocName;

    @BindView(R.id.tv_doc_describe)
    TextView mDocDescribe;

    @BindView(R.id.iv_doctor_avatar1)
    ImageView mDocAvatar1;

    @BindView(R.id.tv_doctor_name1)
    TextView mDocName1;

    @BindView(R.id.tv_doc_describe1)
    TextView mDocDescribe1;


    @BindView(R.id.ll_4th_action)
    LinearLayout mDoubleActionLayout;

    @BindView(R.id.tv_double_action_1)
    TextView mDoubleAction1View;
    @BindView(R.id.tv_double_action_2)
    TextView mDoubleAction2View;
    @BindView(R.id.mid)
    View mid;
    @BindView(R.id.recycler_view_photo)
    EasyRecyclerView mPhotoRecyclerView;

    @BindView(R.id.doc1)
    TextView mDoc1;

    @BindView(R.id.dept)
    TextView mDoc2;

    @BindView(R.id.hos)
    TextView mHos;

    @BindView(R.id.time)
    TextView mTime;

    @BindView(R.id.cost)
    TextView mCost;
    @BindView(R.id.memo)
    TextView memo;

    @BindView(R.id.rl_cancel_reason)
    RelativeLayout mCancelReasonLayout;

    @BindView(R.id.tv_cancel_reason)
    TextView mCancelReasonView;


    @BindView(R.id.rl_refuse_reason)
    RelativeLayout mRefuseReasonLayout;


    @BindView(R.id.ll_my_memo)
    LinearLayout mMemoLayout;

    @BindView(R.id.tv_refuse_reason)
    TextView mRefuseReasonView;

    @OnClick(R.id.tv_double_action_1)
    void doubleAction1Click() {
        if(mine){
            if(treatMent.status.equals("602-0001")){

                TransferTreatmentCancelFragment.launch(_mActivity,treatMent.id);
            }else if(treatMent.status.equals("602-0002")){


            }else if(treatMent.status.equals("602-0003")){

                TreatmentCallTheRollFragment.launch(_mActivity,treatMent);
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0003_mine);

            }else if(treatMent.status.equals("602-0004")){

            }else if(treatMent.status.equals("602-0005")){

            }
        } else{
            if(treatMent.status.equals("602-0001")){

                TransferTreatmentRefuceFragment.launch(_mActivity,treatMent.id);
            }else if(treatMent.status.equals("602-0002")){

                TransferTreatmentSubmitFragment.launch(_mActivity,treatMent.id);

            }else if(treatMent.status.equals("602-0003")){

                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0003_mine);

            }else if(treatMent.status.equals("602-0004")){

            }else if(treatMent.status.equals("602-0005")){

            }
        }
    }



    @OnClick(R.id.tv_double_action_2)
    void doubleAction2Click() {
        if(mine){
            if(treatMent.status.equals("602-0001")){

                TransferTreatmentCancelFragment.launch(_mActivity,treatMent.id);
            }else if(treatMent.status.equals("602-0002")){

            }else if(treatMent.status.equals("602-0003")){

                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0003_mine);

            }else if(treatMent.status.equals("602-0004")){

            }else if(treatMent.status.equals("602-0005")){

            }
        } else{
            if(treatMent.status.equals("602-0001")){

                TreatmentReceiveFragment.launch(_mActivity,treatMent);
            }else if(treatMent.status.equals("602-0002")){

            }else if(treatMent.status.equals("602-0003")){

                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0003_mine);

            }else if(treatMent.status.equals("602-0004")){

            }else if(treatMent.status.equals("602-0005")){

            }
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_treat_ment_detail;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        statusNames =new String[]{getResources().getString(R.string.status_602_0001),
                getResources().getString(R.string.status_602_0002),
                getResources().getString(R.string.status_602_0003),
                getResources().getString(R.string.status_602_0004),
                getResources().getString(R.string.status_602_0005)};
        EventBus.getDefault().register(this);
        diseaseCounselingId=getArguments().getString(EXTRA_DISEASECOUNSELING_ID);
        mine=getArguments().getBoolean(EXTRA_DISEASECOUNSELING_MINE);
        setupToolbar();
        mGlide = Glide.with(this);


        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);

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
        loadData();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof TreatMentStateChangeEvent) {
           loadData();
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.treatment_detail_title);
        setHasOptionsMenu(true);
    }

    Menu themenu;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phonecall,menu);
        themenu=menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.text:

                new MaterialDialog.Builder(_mActivity)
                        .title(R.string.phone_call)
                        .items(new String[]{treatMent.patient.getName(),treatMent.applyDoctor.getName()})
                        .itemsCallback((dialog, itemView, position, text) -> new RxPermissions(_mActivity)
                                .request(Manifest.permission.CALL_PHONE)
                                .subscribe(granted -> {
                                    if (granted) {
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        Uri data = Uri.parse("tel:" + (position==0?treatMent.patient.getPhone():""));
                                        intent.setData(data);
                                        startActivity(intent);
                                    } else {
                                        ViewUtil.showMessage("授权已取消");
                                    }
                                })).show();
                return true;

            default:
                return true;
        }
    }

    public void prepare()
    {
        MenuItem text = themenu.findItem(R.id.text);
        text.setVisible(true);

    }


    private void loadData() {
        showRefreshing(true);
        ApiFactory.getTransferTreatmentApi().getDetail(diseaseCounselingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<TreatMent>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<TreatMent> resp) {
                        showRefreshing(false);
                        if (resp.isSuccess()) {
                            treatMent = resp.getData();
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

    private void setupViews() {

        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
        }
        mGlide.setDefaultRequestOptions(mOptions.error(treatMent.patient.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_patient_man : R.drawable.ic_patient_women))
                .load(treatMent.patient.avatar)
                .into(mPatientAvatarView);
        mGenderView.setText(treatMent.patient.getFriendlySex());
        mPatientNameView.setText(treatMent.patient.getName());
        mAgeView.setText(String.valueOf(treatMent.patient.getAge()));
        mDiagnosisView.setText(treatMent.diagnosis);
        mStatusView.setText(treatMent.statusName);

        mDescribe.setText(treatMent.describe);
        lPa.setOnClickListener(v-> PatientDetailActivity.launch(_mActivity, treatMent.patient));


        mGlide.setDefaultRequestOptions(mOptions.error(treatMent.acceptDoctor.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                .load(treatMent.acceptDoctor.getUserImgUrl())
                .into(mDocAvatar);

        mGlide.setDefaultRequestOptions(mOptions.error(treatMent.applyDoctor.getFriendlySex().equals(App.getApplication().getString(R.string.male)) ? R.drawable.ic_doctor_man : R.drawable.ic_doctor_women))
                .load(treatMent.acceptDoctor.getUserImgUrl())
                .into(mDocAvatar1);

        mDocName.setText(treatMent.acceptDoctor.getName());
        mDocName1.setText(treatMent.applyDoctor.getName());
        mDocDescribe.setText(treatMent.acceptDoctor.getDepartmentName()+" "+
                treatMent.acceptDoctor.getHospitalName());
        mDocDescribe1.setText(treatMent.applyDoctor.getDepartmentName()+" "+
        treatMent.applyDoctor.getHospitalName());

        mRefuseReasonView.setText(treatMent.describe);
        mCancelReasonView.setText(treatMent.describe);


        if((treatMent.status.equals("602-0002")&& !mine) ||treatMent.status.equals("602-0005")) {
            detail.setVisibility(View.VISIBLE);
            mDoc1.setText(treatMent.acceptDoctor.getName());
            mDoc2.setText(treatMent.acceptDoctor.getDepartmentName());
            mHos.setText(treatMent.acceptDoctor.getHospitalName());
            mTime.setText(treatMent.transferTreatmentRecivedRecord.admissionDate.substring(0, 19).replace("T", " "));
            mCost.setText(String.valueOf(treatMent.transferTreatmentRecivedRecord.admissionCharge));
            mStatusView.setText(treatMent.statusName);
            mine = treatMent.applyDoctorId.equals(AppContext.getUser().getId());
            mCancelReasonLayout.setVisibility(View.GONE);
            mRefuseReasonLayout.setVisibility(View.GONE);
        }else if(treatMent.status.equals("602-0003")){
            detail.setVisibility(View.GONE);
            mCancelReasonLayout.setVisibility(View.VISIBLE);
            mRefuseReasonLayout.setVisibility(View.GONE);
        }else if(treatMent.status.equals("602-0004")){
            detail.setVisibility(View.GONE);
            mCancelReasonLayout.setVisibility(View.GONE);
            mRefuseReasonLayout.setVisibility(View.VISIBLE);
        }

        if(treatMent.transferTreatmentAttachments!=null){
            mPhotoAdapter.clear();
            mPhotoAdapter.addAll(treatMent.transferTreatmentAttachments);
        }
        changeActionView();


    }

    private void changeActionView(){

        if(mine){

            laccept.setVisibility(View.GONE);

            if(treatMent.status.equals("602-0001")){
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0001_mine);

            }else if(treatMent.status.equals("602-0002")){
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0002_tome);
                mid.setVisibility(View.GONE);
                mDoubleAction2View.setVisibility(View.GONE);
            }else if(treatMent.status.equals("602-0003")){

                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0003_mine);

            }else if(treatMent.status.equals("602-0004")){

            }else if(treatMent.status.equals("602-0005")){

            }
        }else{
            lapply.setVisibility(View.GONE);
            if(treatMent.status.equals("602-0001")){
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0001_tome_1);
                mid.setVisibility(View.VISIBLE);
                mDoubleAction2View.setText(R.string.action_602_0001_tome_2);
                mDoubleAction2View.setVisibility(View.VISIBLE);
            }else if(treatMent.status.equals("602-0002")){
                mDoubleActionLayout.setVisibility(View.VISIBLE);
                mDoubleAction1View.setText(R.string.action_602_0002_tome);
                mDoubleAction2View.setVisibility(View.GONE);
                mid.setVisibility(View.GONE);
            }else if(treatMent.status.equals("602-0003")){
                mDoubleActionLayout.setVisibility(View.GONE);

            }else if(treatMent.status.equals("602-0004")){
                mDoubleActionLayout.setVisibility(View.GONE);
            }else if(treatMent.status.equals("602-0005")){
                mDoubleActionLayout.setVisibility(View.GONE);
                mMemoLayout.setVisibility(View.VISIBLE);
                memo.setText(treatMent.transferTreatmentRecivedRecord.note);
            }
        }
    }


    @OnClick(R.id.tv_info_more)
    void infoMoreClick() {

        EMRFragment.launch(getActivity(), treatMent.patient, null);
    }
}
