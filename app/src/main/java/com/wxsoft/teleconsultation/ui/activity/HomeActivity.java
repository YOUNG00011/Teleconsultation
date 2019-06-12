package com.wxsoft.teleconsultation.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.event.MessageEvent;
import com.wxsoft.teleconsultation.service.ListenCallService;
import com.wxsoft.teleconsultation.ui.activity.clinic.ClinicDetailActivity;
import com.wxsoft.teleconsultation.ui.base.SupportBaseActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.HomePageDoctorFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.HomePageTranslationerFragment;
import com.wxsoft.teleconsultation.ui.fragment.message.MessageFragment;
import com.wxsoft.teleconsultation.ui.fragment.user.UserFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DoubleClickExitUtil;
import com.wxsoft.teleconsultation.util.FileUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import me.yokeyword.fragmentation.SupportFragment;

import com.tbruyelle.rxpermissions.RxPermissions;

public class HomeActivity extends SupportBaseActivity {

    public static void launch(Activity from) {
        from.startActivity(new Intent(from, HomeActivity.class));
    }

    public static void reStart(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static final int FIRST =  0;
    public static final int SECOND = 1;
    public static final int THIRD =  2;

    private static final int[] TAB_ICON_RES = {
           // R.drawable.ic_home,
            R.drawable.selector_home,
            R.drawable.selector_message,
            R.drawable.selector_user
    };

    @BindView(R.id.bottom_bar)
    BottomNavigationView mBottomBar;

    //private String[] mTabTexts;

    //private BottomBarTab mMessageTab;

    private SupportFragment[] mFragments = new SupportFragment[3];
    private DoubleClickExitUtil mDoubleClick = new DoubleClickExitUtil(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        ListenCallService.start(this);
        setupFragments(savedInstanceState);
        setupBottomBar();
        Bundle bundle = getIntent().getBundleExtra(AppConstant.EXTRA_BUNDLE);
        if (bundle != null) {
            String clinicId = bundle.getString("clinicId");
            if (!TextUtils.isEmpty(clinicId)) {
                ClinicDetailActivity.launch(this, clinicId);
            } else {
                String systemId = bundle.getString("systemId");
                if (!TextUtils.isEmpty(systemId)) {
                    SystemMessageActivity.launch(this);
                }
            }
        }

        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (!granted) {
                        ViewUtil.showMessage("授权已取消");
                    }
                });



    }



    @Override
    protected void onResume() {
        super.onResume();
        int allUnReadMsgCount = JMessageClient.getAllUnReadMsgCount();
       // mMessageTab.setBadgeViewCount(allUnReadMsgCount);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListenCallService.stop(this);
    }

    private void setupFragments(Bundle savedInstanceState) {
        boolean isTranslationer = AppContext.getUser().isTranslationer();
        if (null == savedInstanceState) {
            if (isTranslationer) {
                mFragments[FIRST] = HomePageTranslationerFragment.newInstance();
            } else {
                mFragments[FIRST] = HomePageDoctorFragment.newInstance();
            }
            mFragments[SECOND] = MessageFragment.newInstance();
            mFragments[THIRD] = UserFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            if (isTranslationer) {
                mFragments[FIRST] = findFragment(HomePageTranslationerFragment.class);
            } else {
                mFragments[FIRST] = findFragment(HomePageDoctorFragment.class);
            }
            mFragments[SECOND] = findFragment(MessageFragment.class);
            mFragments[THIRD] = findFragment(UserFragment.class);
        }
    }

    private void setupBottomBar() {

        mBottomBar.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.home_1:
                    showHideFragment(mFragments[0]);
                    return true;
                case R.id.home_2:
                    showHideFragment(mFragments[1]);
                    return true;
                case R.id.home_3:
                    showHideFragment(mFragments[2]);
                    return true;

                default:return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return mDoubleClick.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        int unReadMsgCount = messageEvent.getUnReadMsgCount();
        //mMessageTab.setBadgeViewCount(unReadMsgCount);
    }
}
