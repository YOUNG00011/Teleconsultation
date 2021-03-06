package com.wxsoft.telereciver.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.DoctorInfo;
import com.wxsoft.telereciver.entity.User;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.AppUtil;
import com.wxsoft.telereciver.util.FileUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.HashMap;

import butterknife.BindView;
import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.android.utils.Logger;
import cn.jiguang.share.wechat.Wechat;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CallingCardFragment extends BaseFragment {
    private static final String TAG = "CallingCardFragment";
    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, CallingCardFragment.class, null);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String toastMsg = (String) msg.obj;
            Log.i("??????",toastMsg);
            Toast.makeText(getActivity(),toastMsg,Toast.LENGTH_SHORT).show();

        }
    };
    @BindView(R.id.iv_qrcode)
    ImageView mQRCodeView;

    @BindView(R.id.iv_avatar)
    RoundedImageView avatar;

    @BindView(R.id.tv_name)
    TextView name;
    @BindView(R.id.tv_department)
    TextView department;
    @BindView(R.id.tv_hospital)
    TextView hospital;

    @BindView(R.id.btn_share_clinic)
    Button share;

    private Bitmap mQRCodeBitmap;
    private String mQRCodeUrl = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_calling_card;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();


        User user=AppContext.getUser();

        avatar.setOval(true);
        Glide.with(this).load(user.getUserImgUrl())
                .into(avatar);


        name.setText(user.getName());
        department.setText(user.getDepartmentName());
        hospital.setText(user.getHospitalName());

        loadQRCode(user.getDoctId());
//        Glide.with(this).load(mQRCodeUrl)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        mQRCodeView.setImageDrawable(resource);
//                        mQRCodeBitmap = ((BitmapDrawable) resource).getBitmap();
//                    }
//                });


        share.setOnClickListener(v -> {

            ApiFactory.getCommApi().queryDoctor(user.getDoctId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResp<DoctorInfo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(BaseResp<DoctorInfo> stringBaseResp) {

                            if(stringBaseResp.isSuccess()){
                                if(JShareInterface.isSupportAuthorize(Wechat.Name)) {



                                    ShareParams  params=new ShareParams();
                                    params.setTitle(user.getName());
                                    params.setText(user.getHospitalName());
                                    //params.setShareType(Platform.SHARE_TEXT);
                                    params.setShareType(Platform.SHARE_WEBPAGE);
                                    params.setUrl(stringBaseResp.getData().shareUrl);

                                    JShareInterface.share(Wechat.Name, params, mShareListener);
                                }
                            }

                        }
                    });


        });
        mQRCodeView.setOnLongClickListener(v -> {
            new MaterialDialog.Builder(_mActivity)
                .items(R.array.sava_pic)
                .itemsCallback((dialog, view2, which, text) -> {
                    if (which == 0) {
                        new RxPermissions(_mActivity)
                                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(granted -> {
                                    if (granted) {
                                        String fileName = FileUtil.getFileName(mQRCodeUrl);
                                        if (AppUtil.saveImageToGallery(_mActivity, fileName, mQRCodeBitmap)) {
                                            ViewUtil.showMessage("????????????");
                                        } else {
                                            ViewUtil.showMessage("????????????");
                                        }
                                    } else {
                                        ViewUtil.showMessage("???????????????");
                                    }
                                });
                    }
                })
                .show();
            return false;
        });
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("??????");
    }


    private PlatActionListener mShareListener = new PlatActionListener() {
        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> data) {
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "????????????";
                handler.sendMessage(message);
            }
        }

        @Override
        public void onError(Platform platform, int action, int errorCode, Throwable error) {
            Logger.e(TAG, "error:" + errorCode + ",msg:" + error);
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "????????????:" + error.getMessage() + "---" + errorCode;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onCancel(Platform platform, int action) {
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.obj = "????????????";
                handler.sendMessage(message);
            }
        }
    };
    private void loadQRCode(String docId){
        ApiFactory.getUserApi().getQRCode(docId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<String> resp) {
                        mQRCodeUrl=resp.getData();
                        Glide.with(CallingCardFragment.this).load(mQRCodeUrl)
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        mQRCodeView.setImageDrawable(resource);
                                        mQRCodeBitmap = ((BitmapDrawable) resource).getBitmap();
                                    }
                                });
                    }
                });
    }
}
