package com.wxsoft.telereciver.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.event.ImageEvent;

import java.util.ArrayList;

import cn.jpush.im.android.eventbus.EventBus;

public class SimpleAppsGridView extends RelativeLayout {

    protected View view;

    public SimpleAppsGridView(Context context) {
        this(context, null);
    }

    public SimpleAppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_apps, this);
        init();
    }

    protected void init() {
        GridView gv_apps = (GridView) view.findViewById(R.id.gv_apps);
        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppBean(R.drawable.icon_photo, "图片"));
        mAppBeanList.add(new AppBean(R.drawable.icon_camera, "拍摄"));
        mAppBeanList.add(new AppBean(R.drawable.icon_camera, "视频会诊"));
        mAppBeanList.add(new AppBean(0, null));
        mAppBeanList.add(new AppBean(0, null));
        mAppBeanList.add(new AppBean(0, null));
        AppsAdapter adapter = new AppsAdapter(getContext(), mAppBeanList);
        gv_apps.setAdapter(adapter);
    }

    public static class AppsAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context mContext;
        private ArrayList<AppBean> mDdata = new ArrayList<AppBean>();

        public AppsAdapter(Context context, ArrayList<AppBean> data) {
            this.mContext = context;
            this.inflater = LayoutInflater.from(context);
            if (data != null) {
                this.mDdata = data;
            }
        }

        @Override
        public int getCount() {
            return mDdata.size();
        }

        @Override
        public Object getItem(int position) {
            return mDdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_app, null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final AppBean appBean = mDdata.get(position);
            if (appBean != null && appBean.getIcon() != 0) {
                viewHolder.iv_icon.setBackgroundResource(appBean.getIcon());
                viewHolder.tv_name.setText(appBean.getFuncName());
                convertView.setOnClickListener(v -> {
                    if (appBean.getFuncName().equals("图片")) {
                        EventBus.getDefault().post(new ImageEvent(ImageEvent.IMAGE_MESSAGE));
                    } else if (appBean.getFuncName().equals("拍摄")) {
                        EventBus.getDefault().post(new ImageEvent(ImageEvent.TAKE_PHOTO_MESSAGE));
                    } else if (appBean.getFuncName().equals("视频会诊")) {
                        EventBus.getDefault().post(new ImageEvent(ImageEvent.VIDEO_CLINIC_MESSAGE));
                    }
                });
            }
            return convertView;
        }

        class ViewHolder {
            public ImageView iv_icon;
            public TextView tv_name;
        }
    }

    public static class AppBean {

        private int id;
        private int icon;
        private String funcName;

        public int getIcon() {
            return icon;
        }

        public String getFuncName() {
            return funcName;
        }

        public int getId() {
            return id;
        }

        public AppBean(int icon, String funcName){
            this.icon = icon;
            this.funcName = funcName;
        }
    }
}
