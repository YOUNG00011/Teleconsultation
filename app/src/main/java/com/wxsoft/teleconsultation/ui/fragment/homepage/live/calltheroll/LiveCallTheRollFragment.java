package com.wxsoft.teleconsultation.ui.fragment.homepage.live.calltheroll;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wxsoft.teleconsultation.App;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.EMRTab;
import com.wxsoft.teleconsultation.entity.Photo;
import com.wxsoft.teleconsultation.entity.live.Live;
import com.wxsoft.teleconsultation.event.UpdateLiveEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.FileUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 会诊申请
 */
public class LiveCallTheRollFragment extends BaseFragment {


    private Photo photo;
    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, LiveCallTheRollFragment.class, null);
    }

    public static void launch(Activity from, Live clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_CLINIC, clinic);
        FragmentContainerActivity.launch(from, LiveCallTheRollFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_CLINIC = "FRAGMENT_ARGS_CLINIC";

    @BindView(R.id.live_title)
    ClearableEditText liveTitle;


    @BindView(R.id.live_content)
    TextView liveContent;

    @BindView(R.id.live_date)
    TextView liveDate;

    @BindView(R.id.live_price)
    ClearableEditText livePrice;

    @BindView(R.id.live_timestamp)
    ClearableEditText liveTimestamp;

    @BindView(R.id.live_notifyable)
    SwitchCompat notifyable;


    @BindView(R.id.live_bg)
    ImageView bg;



    @OnClick(R.id.l_live_date)
    void timeClick() {
        showDatePicker();
    }

    @OnClick(R.id.l_live_bg)
    void avatarClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live_call_the_roll;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        setupToolbar();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
           if (requestCode == PictureConfig.CHOOSE_REQUEST) {
               if (data != null) {
                   List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                   LocalMedia localMedia = selectList.get(0);
                   photo=new Photo(localMedia.getPath(),localMedia.getPictureType());
                   File file = new File(localMedia.getPath());
                   Glide.with(_mActivity).load(file).into(bg);
               }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_live_title);
    }

    private void setupView() {

    }

    private void processResponse(BaseResp<List<EMRTab>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        List<EMRTab> emrTabs = resp.getData();
        if (emrTabs == null || emrTabs.isEmpty()) {
            ViewUtil.showMessage("获取类型失败");
            return;
        }

        App.mEMRTabs.addAll(emrTabs);
    }

    private String mDate;
    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    String monthString = (++monthOfYear) < 10 ? "0" + monthOfYear : "" + monthOfYear;
                    String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    mDate = year + "-" + monthString + "-" + dayString;
                    showTimePicker();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        dialog.setMinDate(Calendar.getInstance());
        dialog.show(_mActivity.getFragmentManager(), "DatePickerDialog");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog dialog = TimePickerDialog.newInstance(
                (view, hourOfDay, minute, second) -> {
                    String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                    String minuteString = minute < 10 ? "0" + minute : "" + minute;
                    String secondString = second < 10 ? "0" + second : "" + second;
                    // 期望的时间
                    String mExpectTime = mDate + " " + hourString + ":" + minuteString + ":" + secondString;
                    // 获取当前时间
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowTime = sdf.format(date);
                    if (DateUtil.isDateBefore(nowTime, mExpectTime)) {
                        liveDate.setText(mExpectTime);
                    } else {
                        new MaterialDialog.Builder(_mActivity)
                                .content("期望时间需大于当前时间！")
                                .positiveText("我知道了")
                                .show();
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        dialog.show(_mActivity.getFragmentManager(), "TimePickerDialog");
    }

    private void commit() {

        String title = liveTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ViewUtil.showMessage("直播名称不能为空");
            return;
        }

        String content = liveContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ViewUtil.showMessage("直播描述不能为空");
            return;
        }

        String date = liveDate.getText().toString();
        if (TextUtils.isEmpty(date)) {
            ViewUtil.showMessage("直播时间不能为空");
            return;
        }

        String sprice = livePrice.getText().toString();
        if (TextUtils.isEmpty(sprice)) {
            ViewUtil.showMessage("直播价格不能为空");
            return;
        }

        String timestamp = liveTimestamp.getText().toString();
        if (TextUtils.isEmpty(timestamp)) {
            ViewUtil.showMessage("直播时长不能为空");
            return;
        }

        String applyDoctorId = AppContext.getUser().getDoctId();
        String applyDoctorName = AppContext.getUser().getName();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("doctorId", applyDoctorId)
                .addFormDataPart("doctorName", applyDoctorName)
                .addFormDataPart("LiveTitle", title)
                .addFormDataPart("description", content)
                .addFormDataPart("LiveDate", date)
                .addFormDataPart("Price", sprice)
                .addFormDataPart("Minutes", timestamp)
                .addFormDataPart("IsNeedBroadcast", notifyable.isChecked() ? "true" : "false")
                .addFormDataPart("status", "701-0001");

        if (photo != null) {
            File file = new File(photo.getLocalPath());
            String fileExtension = FileUtil.getFileExtension(file);
            String fileName = photo.getCategory() + "|" + System.currentTimeMillis() + fileExtension;
            builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/*"), file));

        }
        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        ApiFactory.getLiveApi().save(builder.build()).subscribeOn(Schedulers.io())
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
                            EventBus.getDefault().post(new UpdateLiveEvent());
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

}
