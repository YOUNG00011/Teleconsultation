package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.MenuItem;
import com.wxsoft.teleconsultation.entity.Todo;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.live.LivePermission;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.event.UpdateDiseaseCounselingStatusEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.clinic.ClinicActivity;
import com.wxsoft.teleconsultation.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.teleconsultation.ui.activity.cloudclinc.CloudClincActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.diseasecounseling.DiseaseCounselingDetailFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.diseasecounseling.DiseaseCounselingManageFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.live.LiveListFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.patientmanager.PatientManagerFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.PrescriptionConListFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.PrescriptionListFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.PrescriptionManageFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.register.RegisterManageFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.TransferTreatmentDetailFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.TransferTreatmentManageFragment;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePageContentFragment extends BaseFragment {

    public static HomePageContentFragment newInstance() {
        return new HomePageContentFragment();
    }

    @BindView(R.id.bottom_sheet)
    BottomSheetLayout mBottomSheet;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mTaskRecyclerView;

    EasyRecyclerView recyclerViewService;

    private RecyclerArrayAdapter<Todo> mAdapter;

    RecyclerArrayAdapter<Item> adapter;
    private Banner mBanner;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_page_content;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mBottomSheet.setPeekOnDismiss(true);
        setupTaskRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        if (null != mBanner) {
            mBanner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    private void setupTaskRecyclerView() {
        adapter = new RecyclerArrayAdapter<Item>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ServiceViewHolder(parent);
            }
        };

        mTaskRecyclerView.setBackgroundResource(R.color.white);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mTaskRecyclerView.setRefreshingColorResources(R.color.colorPrimary);

//        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
//        mTaskRecyclerView.addItemDecoration(itemDecoration);
        mTaskRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new TaskViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {

            Todo todo=mAdapter.getItem(position);



            if(todo.bussinessType.equals("111-0001")){
                ClinicDetailActivity.launch(_mActivity, todo.bussinessId);
            }else if(todo.bussinessType.equals("111-0002")){
                DiseaseCounselingDetailFragment.launch(_mActivity,todo.bussinessId);
            }else if(todo.bussinessType.equals("111-0003")){
                DiseaseCounselingDetailFragment.launch(_mActivity,todo.bussinessId);
            }else if(todo.bussinessType.equals("111-0004")){
                TransferTreatmentDetailFragment.launch(_mActivity,todo.bussinessId,true);
            }
        });

        mTaskRecyclerView.setRefreshListener(() -> {
            loadData();
        });

        loadData();
    }

    private void loadData() {
        QueryRequestBody body = QueryRequestBody.getConsultationWaitHandleRequestBody(AppContext.getUser().getDoctId(), 20, 1);
        ApiFactory.getCommApi().getMenu(AppContext.getUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<MenuItem>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<List<MenuItem>> resp) {

                        adapter.clear();
                        adapter.addAll(getItems(resp.getData()));

                        if(adapter.getAllData().size()==0){
                            recyclerViewService.setVisibility(View.GONE);
                        }
                    }
                });

        ApiFactory.getCommApi().queryConsultationWaitHandle(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<QueryResponseData<Todo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdapter.getCount() == 0) {
                            ((TextView) ButterKnife.findById(mTaskRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
                            mTaskRecyclerView.showError();
                            mTaskRecyclerView.getErrorView().setOnClickListener(v -> {
                                mTaskRecyclerView.showProgress();
                                loadData();
                            });
                        } else {
                            showRefreshing(false);
                            ViewUtil.showMessage(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(BaseResp<QueryResponseData<Todo>> resp) {
                        showRefreshing(false);
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<QueryResponseData<Todo>> resp) {
        if (!resp.isSuccess()) {
            mTaskRecyclerView.getErrorView().setOnClickListener(v -> {
                mTaskRecyclerView.showProgress();
                loadData();
            });
            return;
        }

        List<Todo> datas = new ArrayList<>();
        List<Todo> clinics = resp.getData().getResultData();
        if (clinics == null || clinics.isEmpty()) {
            datas.add(new Todo());
        } else {
            datas.addAll(clinics);
        }

        mAdapter.clear();
        mAdapter.addAll(datas);

        List<Integer> bannerDatas = new ArrayList<>(4);
//        bannerDatas.add(R.drawable.ic_banner01);
//        bannerDatas.add(R.drawable.ic_banner02);
        bannerDatas.add(R.drawable.ic_home_banner);
        mAdapter.removeAllHeader();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_home, null);
            }

            @Override
            public void onBindView(View headerView) {
                mBanner = ButterKnife.findById(headerView, R.id.banner);
                //设置图片加载器
                mBanner.setImageLoader(new GlideImageLoader());
                mBanner.setOnBannerListener(position -> {

                });

                //设置图片集合
                mBanner.setImages(bannerDatas);
                //banner设置方法全部调用完毕时最后调用
                mBanner.start();


                recyclerViewService = ButterKnife.findById(headerView, R.id.recycler_view_service);

                GridLayoutManager gridLayoutManager=new GridLayoutManager(_mActivity, 4);
                recyclerViewService.setLayoutManager(gridLayoutManager);
                recyclerViewService.setAdapter(adapter);

                adapter.setOnItemClickListener(position -> {
                    int action = adapter.getItem(position).getAction();
                    if (action == Item.ACTION_CONSULT) {
                        DiseaseCounselingManageFragment.launch(_mActivity);
                    } else if (action == Item.ACTION_CLINIC) {
                        ClinicActivity.launch(_mActivity);
                    } else if (action == Item.ACTION_CLOUD_CLINIC) {
                        CloudClincActivity.launch(_mActivity);
                    } else if (action == Item.ACTION_PATIENT_MANAGER) {
                        PatientManagerFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_REGISTER){
                        RegisterManageFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_PRESCRIPTION){
                        PrescriptionManageFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_AUDITPRESCRIPTION){
                        PrescriptionListFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_PRESCRIPTION_CON){
                        PrescriptionConListFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_TRANSFER_TREATMENT){
                        TransferTreatmentManageFragment.launch(_mActivity);
                    }else if(action==Item.ACTION_TRANSFER_LIVE){


                        if(AppContext.getUser().hasLivePermission){
                            LiveListFragment.launch(_mActivity);
                        }else {
                            ApiFactory.getLiveApi().getLivePermission(AppContext.getUser().getDoctId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<BaseResp<LivePermission>>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            showRefreshing(false);
                                            ViewUtil.showMessage(e.getMessage());
                                        }

                                        @Override
                                        public void onNext(BaseResp<LivePermission> resp) {
                                            showRefreshing(false);
                                            processLivePermission(resp);
                                        }
                                    });
                        }

                    }
                });

                LinearLayout taskTitleLayout = ButterKnife.findById(headerView, R.id.ll_task_title);
                if (clinics == null || clinics.isEmpty()) {
                    return;//taskTitleLayout.setVisibility(View.GONE);
                } else {
                    taskTitleLayout.setVisibility(View.VISIBLE);
                    TextView taskTitleView =  ButterKnife.findById(headerView, R.id.tv_task_title);
                    taskTitleView.setText(String.format(getString(R.string.home_todo), clinics.size()));
                }
            }
        });
    }

    private void processLivePermission(BaseResp<LivePermission> permissionBaseResp){
        if(permissionBaseResp.isSuccess() ){
            LivePermission permission=permissionBaseResp.getData();

            if(permission==null){

                new MaterialDialog.Builder(getActivity())
                        .title("直播申请。")
                        .content("您没有直播权限，是否申请？")
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> {

                            //没有申请记录，是否申请
                            User user=AppContext.getUser();
                            LivePermission permission1=new LivePermission();
                            permission1.hospitalId =user.getHospitalId();
                            permission1.hospitalName =user.getHospitalName();
                            permission1.doctorId=user.getDoctId();
                            permission1.doctorName=user.getName();
                            permission1.createrName=user.getName();
                            permission1.createrId=user.getId();
                            permission1.description="Android手机端申请";
                            permission1.status="702-0001";
                            permission1.applyDate=DateUtil.currentTime();
                            ApiFactory.getLiveApi().saveLivePermission(permission1)
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
                                                ViewUtil.showMessage("申请成功，请等待审核");
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


            }else{
               if(permission.status==null||permission.status.equals("702-0001")){
                   ViewUtil.showMessage("你的直播申请正在被审核，请等待审核通过");
               }else if(permission.status.equals("702-0002")){

                   AppContext.getUser().hasLivePermission=true;

               }else if(permission.status.equals("702-0003")){
                   new MaterialDialog.Builder(getActivity())
                           .title("直播申请。")
                           .content("您之前的直播申请未通过，是否重新申请？")
                           .positiveText(R.string.ok)
                           .negativeText(R.string.cancel)
                           .onPositive((dialog, which) -> {

                               //没有申请记录，是否申请
                               User user=AppContext.getUser();
                               LivePermission permission1=new LivePermission();
                               permission1.hospitalId =user.getHospitalId();
                               permission1.hospitalName =user.getHospitalName();
                               permission1.doctorId=user.getDoctId();
                               permission1.doctorName=user.getName();
                               permission1.createrName=user.getName();
                               permission1.createrId=user.getId();
                               permission1.description="Android手机端申请";
                               permission1.applyDate=DateUtil.currentTime();
                               ApiFactory.getLiveApi().saveLivePermission(permission1)
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
                                                   ViewUtil.showMessage("申请成功，请等待审核");
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
            }
           // if(permission.)
        }else{
            ViewUtil.showMessage(permissionBaseResp.getMessage());
        }
    }

    private void showRefreshing(final boolean refresh) {
        mTaskRecyclerView.getSwipeToRefresh().post(() -> {
            mTaskRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private List<Item> getItems(List<MenuItem> menus) {
        List<Item> items = new ArrayList<>();

        for(MenuItem item :menus){
            if(item.moduleId.equals("113-0001")){
                Item item1 = new Item(R.drawable.ic_clinic,getString(R.string.home_menu_clinic), Item.ACTION_CLINIC);
                items.add(item1);
            }else if(item.moduleId.equals("113-0002")){
                Item item1 = new Item(R.drawable.ic_cloud_clinic,getString(R.string.home_menu_cloud_clinic), Item.ACTION_CLOUD_CLINIC);
                items.add(item1);
            }else if(item.moduleId.equals("113-0003")){
                Item item1 =new Item(R.drawable.ic_consult,getString(R.string.home_menu_advisory), Item.ACTION_CONSULT);
                items.add(item1);
            }else if(item.moduleId.equals("113-0004")){
                Item item1 = new Item(R.drawable.ic_register, getString(R.string.home_menu_register), Item.ACTION_REGISTER);
                items.add(item1);
            }else if(item.moduleId.equals("113-0005")){
                Item item1 = new Item(R.drawable.ic_treatment,getString(R.string.home_menu_trsnsfer_treatment), Item.ACTION_TRANSFER_TREATMENT);
                items.add(item1);
            }else if(item.moduleId.equals("113-0006")){
                Item item1 = new Item(R.drawable.ic_live,getString(R.string.home_menu_live), Item.ACTION_TRANSFER_LIVE);
                items.add(item1);
            }else if(item.moduleId.equals("113-0007")){
                Item item1 = new Item(R.drawable.ic_patient_manager, getString(R.string.home_menu_patient), Item.ACTION_PATIENT_MANAGER);
                items.add(item1);
            }
        }

        Item itemPre = new Item(R.drawable.ic_register, getString(R.string.home_menu_prescription), Item.ACTION_PRESCRIPTION);
        items.add(itemPre);

        Item itemPre1 = new Item(R.drawable.ic_register, getString(R.string.home_menu_audit_prescription), Item.ACTION_AUDITPRESCRIPTION);
        items.add(itemPre1);

        Item itemPre2 = new Item(R.drawable.ic_register,getString(R.string.home_menu_audit_prescription_con), Item.ACTION_PRESCRIPTION_CON);
        items.add(itemPre2);

        return items;
    }


    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);
        }
    }

    private class TaskViewHolder extends BaseViewHolder<Todo> {

        private RelativeLayout mRootView;
        private TextView mTimeView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mDescribeView;
        private RoundedImageView mType;
        private TextView mTypeName;

        public TaskViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_task);
            mRootView = $(R.id.rl_root);
            mType = $(R.id.riv_type);
            mTimeView = $(R.id.tv_time);
            mNameView = $(R.id.tv_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mDescribeView = $(R.id.tv_describe);
            mTypeName = $(R.id.tv_type_name);
        }

        @Override
        public void setData(Todo data) {
            super.setData(data);
            if (TextUtils.isEmpty(data.bussinessId)) {
                mRootView.setVisibility(View.GONE);
            } else {
                mRootView.setVisibility(View.VISIBLE);
                mNameView.setText(data.patientName);
                mGenderView.setText(data.patientSex.equals("1")?"男":"女");
                mAgeView.setText(String.valueOf(data.patientAge));
                mDescribeView.setText(data.bussinessText);

                String date = data.bussinessDate;
                String year = date.substring(0, 4);
                String monthAndDay = date.substring(5, 10);
               // mTimeView.setText("");
                mTypeName.setText(data.bussinessTypeName);
                if(data.bussinessType.equals("111-0001")){
                    mType.setImageResource(R.drawable.ic_clinic);
                }else if(data.bussinessType.equals("111-0002")){
                    mType.setImageResource(R.drawable.ic_treatment);
                }else if(data.bussinessType.equals("111-0003")){
                    mType.setImageResource(R.drawable.ic_treatment);
                }else if(data.bussinessType.equals("111-0004")){
                    mType.setImageResource(R.drawable.ic_treatment);
                }
                String time=data.bussinessDate.substring(0,19).replace("T"," ");
                mTimeView.setText(DateUtil.formatDisplayTime(time,null));
                //mMonthAndDayView.setText(monthAndDay);
            }
        }
    }

    private class ServiceViewHolder extends BaseViewHolder<Item> {

        private ImageView mIconView;
        private TextView mTitleView;

        public ServiceViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_home_service);
            mIconView = $(R.id.iv_icon);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);
            mTitleView.setText(data.getTitle());
            mIconView.setImageResource(data.getDrawableRes());
        }
    }

    private class Item {

        public static final int ACTION_CONSULT = 1;
        public static final int ACTION_CLINIC = 2;
        public static final int ACTION_CLOUD_CLINIC = 3;
        public static final int ACTION_PATIENT_MANAGER = 4;
        /**
         * 预约
         */
        public static final int ACTION_REGISTER = 5;
        public static final int ACTION_TRANSFER_TREATMENT = 6;
        public static final int ACTION_TRANSFER_LIVE = 7;
        public static final int ACTION_PRESCRIPTION = 8;
        public static final int ACTION_AUDITPRESCRIPTION = 9;
        public static final int ACTION_PRESCRIPTION_CON = 10;

        private int drawableRes;
        private String title;
        private int action;

        public Item(int drawableRes, String title, int action) {
            this.drawableRes = drawableRes;
            this.title = title;
            this.action = action;
        }

        public int getDrawableRes() {
            return drawableRes;
        }

        public String getTitle() {
            return title;
        }

        public int getAction() {
            return action;
        }
    }
}

