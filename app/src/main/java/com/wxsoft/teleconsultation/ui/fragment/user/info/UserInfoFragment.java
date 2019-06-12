package com.wxsoft.teleconsultation.ui.fragment.user.info;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.DoctorInfo;
import com.wxsoft.teleconsultation.entity.Education;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.requestbody.UpdateUserInfoBody;
import com.wxsoft.teleconsultation.event.ModifyUserAvatarEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.activity.LoginActivity;
import com.wxsoft.teleconsultation.ui.activity.SelectPhotoCategoryActivity;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.CallingCardFragment;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.FileUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import cn.jiguang.share.wechat.Wechat;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
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

import static com.wxsoft.teleconsultation.AppContext.user;

public class UserInfoFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, UserInfoFragment.class, null);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Item> mAdapter;
    private User mUser;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mUser = AppContext.getUser();
        setupRecyclerView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                if (data != null) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    commitAvatar(localMedia.getPath());
                }
            } else if (requestCode == SelectEducationFragment.REQUEST_EDUCATION) {
                if (data != null) {
                    Education education = (Education) data.getSerializableExtra(SelectEducationFragment.KEY_SELECTED_EDUCATION);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_HIGH_DEGREE) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(education.getEducationName());
                            mAdapter.update(item, i);
                            mUser.setEducation(education);
                            AppContext.setUser(mUser);
                            break;
                        }
                    }
                }
            } else if (requestCode == ModifyEmailFragment.REQUEST_EMAIL) {
                if (data != null) {
                    String email = data.getStringExtra(ModifyEmailFragment.KEY_EMAIL);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_EMAIL) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(email);
                            mAdapter.update(item, i);
                            mUser.setEmail(email);
                            AppContext.setUser(mUser);
                            break;
                        }
                    }
                }
            } else if (requestCode == InputFragment.REQUEST_DYNAMIC) {
                if (data != null) {
                    String dynamic = data.getStringExtra(InputFragment.KEY_CONTENT);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_DYNAMIC) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(dynamic);
                            mAdapter.update(item, i);
                            mUser.setDynamic(dynamic);
                            AppContext.setUser(mUser);
                            break;
                        }
                    }
                }
            } else if (requestCode == InputFragment.REQUEST_INTRODUCTION) {
                if (data != null) {
                    String introduce = data.getStringExtra(InputFragment.KEY_CONTENT);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_INTRODUCTION) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(introduce);
                            mAdapter.update(item, i);
                            mUser.setIntroduce(introduce);
                            AppContext.setUser(mUser);
                            break;
                        }
                    }
                }
            } else if (requestCode == InputFragment.REQUEST_GOODAT) {
                if (data != null) {
                    String goodAt = data.getStringExtra(InputFragment.KEY_CONTENT);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_ADEPT) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(goodAt);
                            mAdapter.update(item, i);
                            mUser.setGoodAt(goodAt);
                            AppContext.setUser(mUser);
                            break;
                        }
                    }
                }
            } else if (requestCode == InputFragment.REQUEST_ACHIEVEMENT) {
                if (data != null) {
                    String achievement = data.getStringExtra(InputFragment.KEY_CONTENT);
                    for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                        if (mAdapter.getItem(i).getAction() == Item.ACTION_ACHIEVEMENT) {
                            Item item = mAdapter.getItem(i);
                            item.setContent(achievement);
                            mAdapter.update(item, i);
                            mUser.setAchievement(achievement);
                            AppContext.setUser(mUser);
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
        activity.getSupportActionBar().setTitle(R.string.information_title);
    }

    private void setupRecyclerView() {
        // 设置上边距10dp
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, DensityUtil.dip2px(_mActivity, 16), 0, 0);
        mRecyclerView.setLayoutParams(params);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<Item>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new UserInfoViewHolder(parent, Glide.with(UserInfoFragment.this));
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            int action = mAdapter.getItem(position).getAction();
            switch (action) {
                case Item.ACTION_AVATAR:
                    PictureSelector.create(this)
                            .openGallery(PictureMimeType.ofImage())
                            .selectionMode(PictureConfig.SINGLE)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
                case Item.ACTION_MY_QRCODE:

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
                                        if(stringBaseResp.getData().shareUrl!=null) {


                                            CallingCardFragment.launch(_mActivity);
                                        }else{
                                            Toast.makeText(getActivity(),"您的机构还没有开通咨询业务",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });

                    break;

                case Item.ACTION_EMAIL:
                    ModifyEmailFragment.launch(this, mAdapter.getItem(position).getContent());
                    break;

                case Item.ACTION_DYNAMIC:
                    InputFragment.launchForDynamic(this, mAdapter.getItem(position).getContent());
                    break;

                case Item.ACTION_HIGH_DEGREE:
                    SelectEducationFragment.launch(this, mUser.getEducation());
                    break;

                case Item.ACTION_WORK_AGE:
                    showDatePicker(position);
                    break;

                case Item.ACTION_INTRODUCTION:
                    InputFragment.launchForIntroduction(this, mAdapter.getItem(position).getContent());
                    break;

                case Item.ACTION_ADEPT:
                    InputFragment.launchForGoodAt(this, mAdapter.getItem(position).getContent());
                    break;

                case Item.ACTION_ACHIEVEMENT:
                    InputFragment.launchForAchievement(this, mAdapter.getItem(position).getContent());
                    break;
            }
        });
        mAdapter.addAll(getItems());
    }

    private int mCurrentYear;
    private int mCurrentMonth;
    private int mCurrentDay;
    private void showDatePicker(int position) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    mCurrentYear = year;
                    mCurrentMonth = monthOfYear;
                    mCurrentDay = dayOfMonth;
                    String monthString =  (++monthOfYear) < 10 ? "0" + monthOfYear : "" + monthOfYear;
                    String dayString = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                    String startDate = year + "-" + monthString + "-" + dayString;
                    String endDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    int remainYear = DateUtil.remainYear(startDate, endDate);
                    commitWorkYear(remainYear, position);
                },
                mCurrentYear == 0 ? now.get(Calendar.YEAR) : mCurrentYear,
                mCurrentMonth == 0 ? now.get(Calendar.MONTH) : mCurrentMonth,
                mCurrentDay == 0 ? now.get(Calendar.DAY_OF_MONTH) : mCurrentDay);
        dialog.setMaxDate(Calendar.getInstance());
        dialog.show(_mActivity.getFragmentManager(), "DatePickerDialog");
    }

    private void commitAvatar(String filePath) {
        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        File file = new File(filePath);
        String fileName = FileUtil.getFileName(file);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("UserId", AppContext.getUser().getId());
        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/*"), file));

        String url = AppConstant.BASE_URL + "api/Platform/UploadUserHeadImg?isMobile=true";
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())//传参数、文件或者混合，改一下就行请求体就行
                .build();

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
                _mActivity.runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    try {
                        String str = response.body().string();
                        JSONObject jsonObject = new JSONObject(str);
                        String url = jsonObject.getString("data");
                        new AsyncTask<String, String, Bitmap>(){
                            @Override
                            protected Bitmap doInBackground(String... strings) {
                                try {
                                    return Glide.with(UserInfoFragment.this)
                                            .asBitmap()
                                            .load(url)
                                            .submit()
                                            .get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    return null;
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                if (bitmap == null) {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage("下载图片失败");
                                    return;
                                }

                                String avatarPath = save(bitmap);
                                if (avatarPath != null) {
                                    JMessageClient.updateUserAvatar(new File(avatarPath), new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseMessage) {
                                            if (responseCode == 0) {
                                                for (int i = 0; i < mAdapter.getAllData().size(); i++) {
                                                    if (mAdapter.getItem(i).getAction() == Item.ACTION_AVATAR) {
                                                        Item item = mAdapter.getItem(i);
                                                        item.setDrawableUrl(url);
                                                        mAdapter.update(item, i);
                                                        mUser.setUserImgUrl(url);
                                                        AppContext.setUser(mUser);
                                                        EventBus.getDefault().post(new ModifyUserAvatarEvent());
                                                        break;
                                                    }
                                                }
                                            } else {
                                                ViewUtil.dismissProgressDialog();
                                                ViewUtil.showMessage(responseCode + ":" + responseMessage);
                                            }
                                        }
                                    });
                                } else {
                                    ViewUtil.dismissProgressDialog();
                                    ViewUtil.showMessage("下载用户头像失败");
                                }

                            }
                        }.execute(mUser.getUserImgUrl());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            }
        });
    }

    private String save(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }

        File dir = new File(AppContext.getTmpPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(AppContext.getUserAvatarPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void commitWorkYear(int workYear, int position) {
        ViewUtil.createProgressDialog(_mActivity, "提交中...");
        UpdateUserInfoBody body = UpdateUserInfoBody.updateUserYearWorkBody(mUser.getId(), String.valueOf(workYear));
        ApiFactory.getUserApi().updateArchives(body)
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
                            Item item = mAdapter.getItem(position);
                            item.setContent(workYear + "年");
                            mAdapter.update(item, position);
                            mUser.setYearWork(workYear);
                            AppContext.setUser(mUser);
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_avatar))
                .setDrawableUrl(mUser.getUserImgUrl())
                .setAction(Item.ACTION_AVATAR)
                .build());

        items.add(new Item.Builder()
                .setTitle("我的二维码")
                .setDrawableUrl(mUser.getQrcodeImgUrl())
                .setAction(Item.ACTION_MY_QRCODE)
                .build());

        String name = mUser.getName();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_name))
                .setHasGroup(true)
                .setGroup(getString(R.string.information_personal))
                .setContent(name == null ? "" : name)
                .setAction(Item.ACTION_NULL)
                .build());

        items.add(new Item.Builder()
                .setTitle(getString(R.string.gender))
                .setContent(mUser.getSex() == 0 ? "女" : "男")
                .setAction(Item.ACTION_NULL)
                .build());

        String phone = mUser.getPhone();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.phone))
                .setContent(phone == null ? "" : phone)
                .setAction(Item.ACTION_NULL)
                .build());

        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_id_card))
                .setContent(mUser.getIdCard())
                .setAction(Item.ACTION_NULL)
                .build());

        String email = mUser.getEmail();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_email))
                .setHasGroup(true)
                .setGroup("")
                .setContent(email == null ? "" : email)
                .setAction(Item.ACTION_EMAIL)
                .build());

        String dynamic = mUser.getDynamic();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.dynamic))
                .setContent(dynamic == null ? "" : dynamic)
                .setAction(Item.ACTION_DYNAMIC)
                .build());

        String educationName = "";
        if (mUser.getEducation() != null) {
            educationName = mUser.getEducation().getEducationName();
        }
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_education))
                .setContent(educationName == null ? "" : educationName)
                .setAction(Item.ACTION_HIGH_DEGREE)
                .build());

        int year = (int) mUser.getYearWork();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_working_years))
                .setContent(year + "年")
                .setAction(Item.ACTION_WORK_AGE)
                .build());

        String hospital = mUser.getHospitalName();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_hospital))
                .setHasGroup(true)
                .setGroup(getString(R.string.information_practice_information))
                .setContent(hospital == null ? "" : hospital)
                .setAction(Item.ACTION_NULL)
                .build());

        String department = mUser.getDepartmentName();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.information_department))
                .setContent(department == null ? "" : department)
                .setAction(Item.ACTION_NULL)
                .build());

        String jobTitle = mUser.getJobTitleName();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.job_title))
                .setContent(jobTitle == null ? "" : jobTitle)
                .setAction(Item.ACTION_NULL)
                .build());

        String duty = mUser.getDutyName();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.duty))
                .setContent(duty == null ? "" : duty)
                .setAction(Item.ACTION_NULL)
                .build());

        String introduction = mUser.getIntroduce();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.introduction))
                .setHasGroup(true)
                .setGroup("")
                .setContent(introduction == null ? "" : introduction)
                .setAction(Item.ACTION_INTRODUCTION)
                .build());

        String goodAt = mUser.getGoodAt();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.good_at))
                .setContent(goodAt == null ? "" : goodAt)
                .setAction(Item.ACTION_ADEPT)
                .build());

        String achievement = mUser.getAchievement();
        items.add(new Item.Builder()
                .setTitle(getString(R.string.achievement))
                .setContent(achievement == null ? "" : achievement)
                .setAction(Item.ACTION_ACHIEVEMENT)
                .build());
        return items;
    }

    private class UserInfoViewHolder extends BaseViewHolder<Item> {

        private TextView mGroupTitleView;
        private TextView mTitleView;
        private TextView mContentView;
        private ImageView mIconView;
        private ImageView mArrowRightView;

        private RequestManager mGlide;
        private RequestOptions options;

        public UserInfoViewHolder(ViewGroup parent, RequestManager requestManager) {
            super(parent, R.layout.item_user_info);
            mGroupTitleView = $(R.id.tv_group_title);
            mTitleView = $(R.id.tv_title);
            mContentView = $(R.id.tv_content);
            mIconView = $(R.id.iv_icon);
            mArrowRightView = $(R.id.iv_arrow_right);

            this.mGlide = requestManager;

            options = new RequestOptions();
            options.centerCrop()
                    .dontAnimate()
                    .error(AppContext.getUser().getSex() == 0 ? R.drawable.ic_doctor_women : R.drawable.ic_doctor_man)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        @Override
        public void setData(Item data) {
            super.setData(data);
            if (data.hasGroup) {
                mGroupTitleView.setVisibility(View.VISIBLE );
                mGroupTitleView.setText(TextUtils.isEmpty(data.getGroup()) ? "" : data.getGroup());
            } else {
                mGroupTitleView.setVisibility(View.GONE );
            }
            mTitleView.setText(data.getTitle());
            if (TextUtils.isEmpty(data.getDrawableUrl())) {
                if (data.getDrawableRes() == 0) {
                    mIconView.setVisibility(View.GONE);
                    mContentView.setVisibility(View.VISIBLE);
                    mContentView.setText(TextUtils.isEmpty(data.getContent()) ? "" : data.getContent());
                } else {
                    mIconView.setVisibility(View.VISIBLE);
                    mContentView.setVisibility(View.GONE);
                    mIconView.setImageResource(data.getDrawableRes());
                }
            } else {
                mIconView.setVisibility(View.VISIBLE);
                mContentView.setVisibility(View.GONE);

                mGlide.setDefaultRequestOptions(options)
                        .load(data.getDrawableUrl())
                        .thumbnail(0.5f)
                        .into(mIconView);
            }
            mArrowRightView.setVisibility(data.getAction() == Item.ACTION_NULL ? View.GONE : View.VISIBLE);
        }
    }

    private static class Item {

        public static final int ACTION_NULL                  = 0;
        // 头像
        public static final int ACTION_AVATAR                = 1;
        // 我的二维码
        public static final int ACTION_MY_QRCODE             = 2;
        // 电子邮箱
        public static final int ACTION_EMAIL                 = 3;
        // 个人动态
        public static final int ACTION_DYNAMIC               = 4;
        // 最高学历
        public static final int ACTION_HIGH_DEGREE           = 5;
        // 工作年份
        public static final int ACTION_WORK_AGE              = 6;
        // 个人介绍
        public static final int ACTION_INTRODUCTION          = 7;
        // 擅长领域
        public static final int ACTION_ADEPT                 = 8;
        // 成果荣誉
        public static final int ACTION_ACHIEVEMENT           = 9;

        private String group;
        private boolean hasGroup;
        private String title;
        private String content;
        private String drawableUrl;
        private int drawableRes;
        private int action;

        public String getGroup() {
            return group;
        }

        public boolean isHasGroup() {
            return hasGroup;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDrawableUrl() {
            return drawableUrl;
        }

        public void setDrawableUrl(String drawableUrl) {
            this.drawableUrl = drawableUrl;
        }

        public int getDrawableRes() {
            return drawableRes;
        }

        public int getAction() {
            return action;
        }

        private static class Builder {
            private Item item;

            public Builder() {
                this.item = new Item();
            }

            public Builder setGroup(String group) {
                item.group = group;
                return this;
            }

            public Builder setHasGroup(boolean hasGroup) {
                item.hasGroup = hasGroup;
                return this;
            }

            public Builder setTitle(String title) {
                item.title = title;
                return this;
            }

            public Builder setContent(String content) {
                item.content = content;
                return this;
            }

            public Builder setDrawableUrl(String drawableUrl) {
                item.drawableUrl = drawableUrl;
                return this;
            }

            public Builder setDrawableRes(int drawableRes) {
                item.drawableRes = drawableRes;
                return this;
            }

            public Builder setAction(int action) {
                item.action = action;
                return this;
            }

            public Item build() {
                return item;
            }
        }
    }
}
