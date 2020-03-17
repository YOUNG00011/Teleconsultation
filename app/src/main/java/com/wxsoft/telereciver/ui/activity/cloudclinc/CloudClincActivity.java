package com.wxsoft.telereciver.ui.activity.cloudclinc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.BusinessSetting;
import com.wxsoft.telereciver.entity.Confrence;
import com.wxsoft.telereciver.entity.HWAccount;
import com.wxsoft.telereciver.entity.Patient;
import com.wxsoft.telereciver.entity.cloudclinc.ClincCallRecord;
import com.wxsoft.telereciver.entity.cloudclinc.CloudClincDuty;
import com.wxsoft.telereciver.event.CloudClincEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.CloudClincSubmitFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.centre.SelectDoctorFragment;
import com.wxsoft.telereciver.ui.fragment.homepage.cloudclinic.centre.SelectDoctorRegisterFragment;
import com.wxsoft.telereciver.util.DateUtil;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;
import com.wxsoft.telereciver.vc.ui.activity.CallActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import object.KickOutInfo;
import object.TupRegisterResult;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tupsdk.TupCall;

public class CloudClincActivity extends SupportBaseActivity {


    CloudClincDuty duty;
    private TimeCount timeCount;
    @BindView(R.id.bottom_sheet)
    BottomSheetLayout mBottomSheet;

    private boolean lastState;
    @BindView(R.id.doc_online_status)
    SwitchCompat status;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;


    private RecyclerArrayAdapter<CloudClincDuty> mAdapter;

    @OnClick(R.id.fabtn_more)
    void moreClick() {
        showMenuSheet(MenuSheetView.MenuType.GRID);
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(this, menuType, "", item -> {
                    if (mBottomSheet.isSheetShowing()) {
                        mBottomSheet.dismissSheet();
                    }

                    int itemId = item.getItemId();
                    if (itemId == R.id.next) {
                        SelectDoctorRegisterFragment.launch(this);
                    } else if (itemId == R.id.current) {
                        SelectDoctorFragment.launch(this,"501-0001");
                    }
                    return true;
                });
        menuSheetView.inflateMenu(R.menu.menu_cloudclinic_buttom);
        mBottomSheet.showWithSheetView(menuSheetView);
    }

    @OnClick(R.id.doc_online_status)
    void changeStatus(){
        ApiFactory.getCloudClinicApi().setDocOnlineStatus(AppContext.getUser().getDoctId(),!lastState)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // showRefreshing(false);
                        status.setChecked(lastState);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<Boolean> resp) {

                        lastState=status.isChecked();
                        //status.setChecked(resp.getData());
                    }
                });
    }

    public static void launch(Activity from) {
        from.startActivity(new Intent(from,CloudClincActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cloudclinic_manager;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        setupToolbar();
        setupView();


        //LoginService.getInstance().registerTupNotify(this);

        long time=5*60*1000;
        timeCount=new TimeCount(time,time);
        timeCount.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloudclinic, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressedSupport();
                return true;
            case R.id.action:
                Intent it=new Intent(this,CloudClincHistoryActivity.class);
                startActivity(it);
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof CloudClincEvent) {
            loadTodaysWork();
        }
    }

    private void setupToolbar() {

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //setupView();

    }

    private void setupView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(this, R.color.comm_list_divider_color), DensityUtil.dip2px(this, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<CloudClincDuty>(this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new  ViewHolder(parent);
            }
        });
        StickyHeaderDecoration decoration = new StickyHeaderDecoration(new StickyHeaderAdapter(this));
        decoration.setIncludeHeader(false);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setRefreshListener(() -> {
            loadTodaysWork();
        });
        getBusinessSetting();
        //loadMyStatus();

        loadTodaysWork();
    }

    private void getBusinessSetting(){
        ApiFactory.getCommApi().getBusinessSetting(AppContext.getUser().getDoctId()) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<BusinessSetting>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<BusinessSetting> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            BusinessSetting setting=resp.getData();
                            status.setVisibility(setting.isCloudClinc?View.VISIBLE:View.INVISIBLE);
                            loadMyStatus();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void loadMyStatus(){
        ApiFactory.getCloudClinicApi().getDocOnlineStatus(AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<Boolean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       // showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<Boolean> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            status.setChecked(resp.getData());
                           // loadMyStatus();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }

                    }
                });
    }

    private void loadTodaysWork(){
        ApiFactory.getCloudClinicApi().getCurrentDuty(AppContext.getUser().getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<CloudClincDuty>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // showRefreshing(false);
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<CloudClincDuty>> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            processResponse(resp);
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                        //showRefreshing(false);

                    }
                });
    }

    private void processResponse(BaseResp<List<CloudClincDuty>> resp) {
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadTodaysWork();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }

        List<CloudClincDuty> clinics = resp.getData();
        if (clinics == null || clinics.isEmpty()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getEmptyView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    loadTodaysWork();
                });
                mRecyclerView.showEmpty();
            } else {
                mAdapter.stopMore();
            }
            return;
        }



        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(clinics);

    }

//    @Override
//    public void onRegisterNotify(int registerResult, int errorCode) {
//
//    }
//
//    @Override
//    public void onSMCLogin(int smcAuthorizeResult, String errorReason) {
//
//    }
//
//    @Override
//    public void onCallNotify(int code, Object object) {
//        switch (code){
//            case CallConstants.BACK_CAMERA:
//
//                int i=CallConstants.VIDEO_CONTROL_START;
//                break;
//        }
//    }

    @Override
    public void onRegisterResult(TupRegisterResult regRet) {

    }

    @Override
    public void onBeKickedOut(KickOutInfo kickOutInfo) {

    }

    @Override
    public void onCallComing(TupCall call) {

    }

    @Override
    public void onCallGoing(TupCall tupCall) {

    }

    @Override
    public void onCallRingBack(TupCall tupCall) {

    }

    int the_call_count=0;
    @Override
    public void onCallConnected(TupCall call) {

        if(the_call_count==0) {
            the_call_count++;
            ClincCallRecord record = new ClincCallRecord();
            record.clincRecordId = duty.clincRecord.id;
            record.startDate= DateUtil.getTimeStr(System.currentTimeMillis());
            record.endDate= DateUtil.getTimeStr(System.currentTimeMillis()+300*1000);

            ApiFactory.getCloudClinicApi().saveCallRecord(record)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResp>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ViewUtil.showMessage(e.getMessage());

                        }

                        @Override
                        public void onNext(BaseResp resp) {
                            if (resp.isSuccess() && resp.getData() != null) {

                            }

                        }
                    });
        }
    }


    private class ViewHolder extends BaseViewHolder<CloudClincDuty> {

        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mDocView;
        private TextView mTimeView;
        private TextView mWait;
        private TextView mCall;
        private TextView mDocRole;


        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_cloud_clinc_home);

            mTimeView = $(R.id.tv_duty);
            mDocRole = $(R.id.doc_role);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.sex);
            mAgeView = $(R.id.age);
            mDocView=$(R.id.tv_doc);
            mWait=$(R.id.wait_for);
            mCall=$(R.id.call);

        }

        @Override
        public void setData(CloudClincDuty data) {
            super.setData(data);

            the_call_count=0;
            try {


                Patient patient = data.patient;
                mNameView.setText(patient.getName());
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mDocView.setText(data.doctor.getName());;
                mTimeView.setText(data.doctor.getDepartmentName());
                mWait.setText(String.valueOf(data.waitNumber));

                if(data.clincRecord.clincType.equals("501-0002")){
                    mCall.setText("接受预约");
                }
                else {
                    mWait.setVisibility(View.GONE);
                    mDocRole.setText("申请医生");
                    if (data.itemType == 1) {

                        if (data.clincRecord.status.equals("502-0003")) {
                            mCall.setText("视频呼叫");
                        } else if (data.clincRecord.status.equals("502-0004")) {
                            mCall.setText("是否结束");
                        }
                    } else {

                        mCall.setText("等待呼叫");


                    }
                }
                mCall.setOnClickListener(l->{


                    if(data.clincRecord.clincType.equals("501-0002")){
                        new MaterialDialog.Builder(getContext())
                                .title("接受预约")
                                .content("是否接收云门诊预约？")
                                .positiveText("拒绝")
                                .negativeText("接收")
                                .neutralText("取消")
                                .onPositive((d,w)->{
                                    ApiFactory.getCloudClinicApi().delete(data.clincRecord.id)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<BaseResp>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    // showRefreshing(false);
                                                    status.setChecked(lastState);
                                                    ViewUtil.showMessage(e.getMessage());
                                                }

                                                @Override
                                                public void onNext(BaseResp resp) {

                                                    if(resp.isSuccess()){
                                                        mAdapter.remove(data);
                                                    }
                                                }
                                            });
                                }).onNegative((d1,w1)->{
                            ApiFactory.getCloudClinicApi().receive(data.clincRecord.id)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<BaseResp>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // showRefreshing(false);
                                            status.setChecked(lastState);
                                            ViewUtil.showMessage(e.getMessage());
                                        }

                                        @Override
                                        public void onNext(BaseResp resp) {

                                            if(resp.isSuccess()){
                                                loadTodaysWork();
                                            }
                                        }
                                    });
                        }).onNeutral((d2,w2)->{
                            d2.dismiss();
                        }).build().show();
                    }
                    else {

                        if (data.itemType == 1) {

                            if (data.clincRecord.status.equals("502-0003")) {

                                new MaterialDialog.Builder(getContext())
                                        .positiveText("开始视频")
                                        .negativeText("取消")
                                        .onPositive((dialog, which) -> {

                                            Confrence confrence=new Confrence();
                                            confrence.hWAccountName=AppContext.getUser().getHwUserName();
                                            confrence.cloudClincId=data.clincRecord.id;
                                            ApiFactory.getSmcApi().createCloudVideoCon(confrence)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Observer<BaseResp>() {
                                                        @Override
                                                        public void onCompleted() {

                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            ViewUtil.showMessage(e.getMessage());

                                                        }

                                                        @Override
                                                        public void onNext(BaseResp resp) {
                                                            if (resp.isSuccess()&& resp.getData() != null) {

                                                            }else{
                                                                ViewUtil.showMessage(resp.getMessage());
                                                            }

                                                        }
                                                    });
                                        })
                                        .onNegative((dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .show();
                            } else if (data.clincRecord.status.equals("502-0004")) {
                               // mCall.setText("是否结束");

                                new MaterialDialog.Builder(getContext())

                                        .positiveText("再次呼叫")
                                        .negativeText("去写小结")
                                        .neutralText("取消")
                                        .onPositive((d,w)->{
                                            ApiFactory.getLoginApi().getHWAccountByUserId(data.doctor.userId)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Observer<BaseResp<HWAccount>>() {
                                                        @Override
                                                        public void onCompleted() {

                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            ViewUtil.showMessage(e.getMessage());

                                                        }

                                                        @Override
                                                        public void onNext(BaseResp<HWAccount> resp) {
                                                            if (resp.isSuccess()&& resp.getData() != null) {
                                                                String name=resp.getData().getHwUserName();
                                                                // CreateConfActivity.launch(CloudClincActivity.this, names);
                                                                CallActivity.launchForCall(CloudClincActivity.this, name);
                                                                duty=data;
                                                                //  CallService.getInstance().
                                                            }

                                                        }
                                                    });
                                        }).onNegative((d1,w1)->{
                                            CloudClincSubmitFragment.launch(CloudClincActivity.this,data.clincRecord.id);
                                            //TODO:去写小结
                                }).onNeutral((d2,w2)->{
                                    d2.dismiss();
                                }).build().show();
                            }
                        } else {

                            new MaterialDialog.Builder(getContext())
                                    .content("专家正在忙，马上轮到你了，还需要继续等待吗？")
                                    .positiveText("不等了")
                                    .negativeText("继续等待")
                                    .onPositive((dialog, which) -> {
                                        ApiFactory.getCloudClinicApi().delete(data.clincRecord.id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<BaseResp>() {
                                                    @Override
                                                    public void onCompleted() {

                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        // showRefreshing(false);
                                                        status.setChecked(lastState);
                                                        ViewUtil.showMessage(e.getMessage());
                                                    }

                                                    @Override
                                                    public void onNext(BaseResp resp) {

                                                        if(resp.isSuccess()){
                                                            mAdapter.remove(data);
                                                        }
                                                    }
                                                });
                                    })
                                    .onNegative((dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();


                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发

            loadTodaysWork();

            this.start();
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }


    public class StickyHeaderAdapter implements StickyHeaderDecoration.IStickyHeaderAdapter<StickyHeaderAdapter.HeaderHolder> {

        private LayoutInflater mInflater;

        public StickyHeaderAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public long getHeaderId(int position) {

            try {

                //0今日待办-我发起的1今日待办-我接受的2已呼叫3预约门诊


                if(mAdapter.getItem(position).clincRecord.clincType.equals("501-0002")) {
                    return 3;
                }else {
                    if (mAdapter.getItem(position).itemType == 0)
                        return 0;//预约门诊呢
                    else {
                        if (mAdapter.getItem(position).clincRecord.status.equals("502-0004"))
                            return 2;
                        else if (mAdapter.getItem(position).clincRecord.status.equals("502-0003")) {
                            return 1;
                        }else{
                            return -1;
                        }
                    }
                }
            }catch (Exception e) {
                Log.i("position",String.valueOf(position));
                return -1;
            }

        }

        @Override
        public StickyHeaderAdapter.HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.header_treatment, parent, false);
            return new StickyHeaderAdapter.HeaderHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(StickyHeaderAdapter.HeaderHolder viewholder, int position) {


            int id=(int)getHeaderId(position);

            switch (id){
                case 0:
                    viewholder.header.setText("今日待办-我发起的");
                    break;
                case 1:
                    viewholder.header.setText("今日待办-我接受的");
                    break;
                case 2:
                    viewholder.header.setText("已呼叫");
                    break;
                case 3:
                    viewholder.header.setText("预约门诊");
                break;
            }

            //viewholder.header.setText(getHeaderId(position)==0?"今日待办-我发起的":"今日待办-我接收的");
        }

        class HeaderHolder extends RecyclerView.ViewHolder {
            public TextView header;

            public HeaderHolder(View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.head);
            }
        }
    }

}
