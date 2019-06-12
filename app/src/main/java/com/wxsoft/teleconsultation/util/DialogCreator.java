package com.wxsoft.teleconsultation.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogCreator {

    public static Dialog mLoadingDialog;

    public static Dialog createResendDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdUtil.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(
                IdUtil.getLayout(context, "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        Button cancelBtn = (Button) view.findViewById(IdUtil.getViewID(context, "jmui_cancel_btn"));
        Button resendBtn = (Button) view.findViewById(IdUtil.getViewID(context, "jmui_commit_btn"));
        cancelBtn.setOnClickListener(listener);
        resendBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createDelConversationDialog(Context context,
                                                     View.OnClickListener listener, boolean isTop) {
        Dialog dialog = new Dialog(context, IdUtil.getStyle(context, "jmui_default_dialog_style"));
        View v = LayoutInflater.from(context).inflate(
                IdUtil.getLayout(context, "jmui_dialog_delete_conv"), null);
        dialog.setContentView(v);
        final LinearLayout deleteLl = (LinearLayout) v.findViewById(IdUtil
                .getViewID(context, "jmui_delete_conv_ll"));
        final LinearLayout top = (LinearLayout) v.findViewById(IdUtil
                .getViewID(context, "jmui_top_conv_ll"));
        TextView tv_top = (TextView) v.findViewById(IdUtil.getViewID(context, "tv_conv_top"));
        if (isTop) {
            tv_top.setText("消息置顶");
        } else {
            tv_top.setText("取消置顶");
        }

        deleteLl.setOnClickListener(listener);
        top.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
