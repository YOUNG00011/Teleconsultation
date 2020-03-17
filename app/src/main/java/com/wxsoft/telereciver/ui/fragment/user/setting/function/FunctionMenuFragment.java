package com.wxsoft.telereciver.ui.fragment.user.setting.function;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;

public class FunctionMenuFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, FunctionMenuFragment.class, null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

    }

    private class FunctionViewHolder extends BaseViewHolder<Item> {

        public FunctionViewHolder(ViewGroup parent, int res) {
            super(parent, res);
        }
    }


    private class Item {

        // 咨询
        public static final int ACTION_CONSULT = 1;
        // 预约
        public static final int ACTION_ORDER = 2;
        // 转诊
        public static final int ACTION_REFERRAL = 3;
        // 会诊
        public static final int ACTION_clinic = 4;
        // 患者管理
        public static final int ACTION_patient_manager = 5;
        // 视频教学
        public static final int ACTION_video_teaching = 6;
        // 医技检查
        public static final int ACTION_technics_check = 7;
        // 电子处方
        public static final int ACTION = 8;

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
