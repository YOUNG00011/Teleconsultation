package com.wxsoft.teleconsultation.ui.fragment.homepage.live;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.HWAccount;
import com.wxsoft.teleconsultation.entity.HWDeviceAccount;
import com.wxsoft.teleconsultation.entity.live.Live;
import com.wxsoft.teleconsultation.event.PatientOrTagChangedEvent;
import com.wxsoft.teleconsultation.event.UpdateLiveEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LiveDetailFragment extends BaseFragment {

    public static final String EXTRA_DISEASECOUNSELING_ID = "EXTRA_DISEASECOUNSELING_ID";
    private RequestManager mGlide;
    private RequestOptions mOptions;
    private Live live;
    public static void launch(Activity from,Live live) {
        FragmentArgs args = new FragmentArgs();
        args.add(EXTRA_DISEASECOUNSELING_ID, live);
        FragmentContainerActivity.launch(from, LiveDetailFragment.class,args);
    }

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.ll_root)
    LinearLayout mRootView;


    @BindView(R.id.iv_avatar)
    ImageView mLiveAvatar;

    @BindView(R.id.tv_name)
    TextView mNameView;

    @BindView(R.id.tv_time)
    TextView mTimeView;

    @BindView(R.id.tv_cost)
    TextView mCostView;

    @BindView(R.id.tv_doc)
    TextView mDocView;

    @BindView(R.id.tv_doc_detail)
    TextView mDocDetail;

    @BindView(R.id.tv_live_content)
    TextView mLiveContent;
    @BindView(R.id.bt1)
    TextView bt1;
    @BindView(R.id.bt2)
    TextView bt2;
    @BindView(R.id.bt3)
    TextView bt3;

    @OnClick(R.id.bt1)
    void onclick1(){
        if(live.status.equals("701-0001")){
            ApiFactory.getLiveApi().publish(live.id)
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
                                live.status="701-0002";
                                EventBus.getDefault().post(new UpdateLiveEvent());
                                setupViews();
                            } else {
                                ViewUtil.showMessage(resp.getMessage());
                            }
                        }
                    });
        }else if(live.status.equals("701-0002")){
            //调用接口部分
            ApiFactory.getLiveApi().start(live.id)
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
                                live.status="701-0003";
                                EventBus.getDefault().post(new UpdateLiveEvent());
                                setupViews();
                            } else {
                                ViewUtil.showMessage(resp.getMessage());
                            }
                        }
                    });
        }else if(live.status.equals("701-0003")){

        }
    }

    @OnClick(R.id.bt2)
    void onclick2(){
        if(live.status.equals("701-0001")){

            new MaterialDialog.Builder(_mActivity).title(R.string.delete_live_title)
                    .content(R.string.delete_live_content)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ApiFactory.getLiveApi().delete(live.id)
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
                                            EventBus.getDefault().post(new UpdateLiveEvent());
                                            _mActivity.finish();
                                        } else {
                                            ViewUtil.showMessage(resp.getMessage());
                                        }
                                    }
                                });
                    }).show();
        } else if(live.status.equals("701-0002") || live.status.equals("701-0003")){
            new MaterialDialog.Builder(_mActivity).title(R.string.stop_live_title)
                    .content(R.string.stop_live_content)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive((dialog, which) -> {
                        ApiFactory.getLiveApi().delete(live.id)
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
                                            EventBus.getDefault().post(new UpdateLiveEvent());
                                            _mActivity.finish();
                                        } else {
                                            ViewUtil.showMessage(resp.getMessage());
                                        }
                                    }
                                });
                    }).show();
        }
    }


    @OnClick(R.id.bt3)
    void onclick3(){
      if(live.status.equals("701-0002") || live.status.equals("701-0003")){
          new MaterialDialog.Builder(_mActivity).title(R.string.delete_live_title)
                  .content(R.string.delete_live_content)
                  .positiveText(R.string.ok)
                  .negativeText(R.string.cancel)
                  .onPositive((dialog, which) -> {
                      ApiFactory.getLiveApi().delete(live.id)
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
                                          EventBus.getDefault().post(new UpdateLiveEvent());
                                          _mActivity.finish();
                                      } else {
                                          ViewUtil.showMessage(resp.getMessage());
                                      }
                                  }
                              });
                  }).show();
        }
    }

    private List<HWAccount> mHWAccounts;
    private HWDeviceAccount mHWDeviceAccount;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_detail;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        live=(Live)getArguments().getSerializable(EXTRA_DISEASECOUNSELING_ID);
        setupToolbar();
        mGlide = Glide.with(this);


        mOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(false);
      //  loadData();
        setupViews();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {
        if (object instanceof PatientOrTagChangedEvent) {
           // loadData();
        }
    }

//

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.live_detail_title);
    }


//    private void loadData() {
//        showRefreshing(true);
//        ApiFactory.getDiseaseCounselingApi().createVideoCon(diseaseCounselingId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseResp<DiseaseCounseling>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        showRefreshing(false);
//                        ViewUtil.showMessage(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(BaseResp<DiseaseCounseling> resp) {
//                        showRefreshing(false);
//                        if (resp.isSuccess()) {
//                            live = resp.getData();
//                            prepare();
//                            if (live.doctor.getId().equals(AppContext.getUser().getDoctId())) {
//                                isMyClinic = false;
//                            } else {
//                                isMyClinic = true;
//                            }
//                            setupViews();
//                        } else {
//                            ViewUtil.showMessage(resp.getMessage());
//                        }
//                    }
//                });
//    }

    private void showRefreshing(final boolean refresh) {
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(refresh);
        });
    }

    private void setupViews() {
        if (mRootView.getVisibility() == View.GONE) {
            mRootView.setVisibility(View.VISIBLE);
        }
        mGlide.load(live.url).into(mLiveAvatar);
        mTimeView.setText(live.liveDate.replace("T"," "));
        mNameView.setText(live.liveTitle);
        mDocView.setText(live.doctorInfo.getName());
        mDocDetail.setText(live.doctorInfo.getHospitalName()+live.doctorInfo.getDepartmentName());
        mCostView.setText(String.valueOf(live.price)+"元");
        mLiveContent.setText(live.description);

        if(live.status.equals("701-0001")){
            if(bt1.getVisibility()==View.GONE)
                bt1.setVisibility(View.VISIBLE);
            if(bt2.getVisibility()==View.GONE)
                bt2.setVisibility(View.VISIBLE);
            if(bt3.getVisibility()==View.VISIBLE)
                bt3.setVisibility(View.GONE);

            bt1.setText(R.string.live_detail_text3);
            bt2.setText(R.string.live_detail_text4);
        } else if(live.status.equals("701-0002")){
            if(bt1.getVisibility()==View.GONE)
                bt1.setVisibility(View.VISIBLE);
            if(bt2.getVisibility()==View.GONE)
                bt2.setVisibility(View.VISIBLE);
            if(bt3.getVisibility()==View.GONE)
                bt3.setVisibility(View.VISIBLE);

            bt1.setText(R.string.live_detail_text5);
            bt2.setText(R.string.live_detail_text6);
            bt3.setText(R.string.live_detail_text7);
        }else if(live.status.equals("701-0003")){
            if(bt1.getVisibility()==View.GONE)
                bt1.setVisibility(View.VISIBLE);
            if(bt2.getVisibility()==View.GONE)
                bt2.setVisibility(View.VISIBLE);
            if(bt3.getVisibility()==View.GONE)
                bt3.setVisibility(View.VISIBLE);

            bt1.setText(R.string.live_detail_text5);
            bt2.setText(R.string.live_detail_text6);
            bt3.setText(R.string.live_detail_text7);
        }else if(live.status.equals("701-0004")){
            if(bt1.getVisibility()==View.VISIBLE)
                bt1.setVisibility(View.GONE);
            if(bt2.getVisibility()==View.VISIBLE)
                bt2.setVisibility(View.GONE);
            if(bt3.getVisibility()==View.VISIBLE)
                bt3.setVisibility(View.GONE);
        }else if(live.status.equals("701-0005")){

            if(bt1.getVisibility()==View.VISIBLE)
                bt1.setVisibility(View.GONE);
            if(bt2.getVisibility()==View.VISIBLE)
                bt2.setVisibility(View.GONE);
            if(bt3.getVisibility()==View.VISIBLE)
                bt3.setVisibility(View.GONE);
        }

    }
}
