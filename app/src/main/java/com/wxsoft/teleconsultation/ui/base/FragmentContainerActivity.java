package com.wxsoft.teleconsultation.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.wxsoft.teleconsultation.R;

import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by liping on 2018/3/19.
 */
public class FragmentContainerActivity extends SupportBaseActivity {

    public static final String FRAGMENT_TAG = "FRAGMENT_CONTAINER";

    /**
     * 启动一个界面
     *
     * @param activity
     * @param clazz
     * @param args
     */
    public static void launch(Activity activity, Class<? extends Fragment> clazz, FragmentArgs args) {
        Intent intent = new Intent(activity, FragmentContainerActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        activity.startActivity(intent);
    }

    public static void launchForResult(Fragment fragment, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
        if (fragment.getActivity() == null)
            return;
        Activity activity = fragment.getActivity();

        Intent intent = new Intent(activity, FragmentContainerActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void launchForResult(Activity from, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
        Intent intent = new Intent(from, FragmentContainerActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        from.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fragment_container;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        String className = getIntent().getStringExtra("className");
        if (TextUtils.isEmpty(className)) {
            finish();
            return;
        }

        FragmentArgs values = (FragmentArgs) getIntent().getSerializableExtra("args");
        Fragment fragment = null;
        try {
            Class clazz = Class.forName(className);
            fragment = (Fragment) clazz.newInstance();
            // 设置参数给Fragment
            if (values != null) {
                try {
                    Method method = clazz.getMethod("setArguments", new Class[] { Bundle.class });
                    method.invoke(fragment, FragmentArgs.transToBundle(values));
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container, fragment, FRAGMENT_TAG).commit();
        }

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        if (null != toolbar) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
