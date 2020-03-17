package com.wxsoft.telereciver.helper.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxsoft.telereciver.R;

import static android.text.TextUtils.isEmpty;

public class VaryViewHelperController {

    private IVaryViewHelper mHelper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.mHelper = helper;
    }

    public void showNetworkError(View.OnClickListener onClickListener) {
        View layout = mHelper.inflate(R.layout.comm_message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        textView.setText(mHelper.getContext().getResources().getString(R.string.comm_no_network_msg));

        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_exception);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        mHelper.showLayout(layout);
    }

    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = mHelper.inflate(R.layout.comm_message);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);

        if (!isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(mHelper.getContext().getResources().getString(R.string.comm_error_msg));
        }

        ImageView imageView = layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.ic_exception);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        mHelper.showLayout(layout);
    }

    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener) {
        View layout = mHelper.inflate(R.layout.comm_message);
        TextView textView = layout.findViewById(R.id.message_info);

        if (!isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        }
        else {
            textView.setText(mHelper.getContext().getResources().getString(R.string.comm_empty_msg));
        }

        ImageView imageView = layout.findViewById(R.id.message_icon);
        imageView.setImageResource(R.drawable.watermark);

        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }

        mHelper.showLayout(layout);
    }

    public void showLoading(String msg) {
        View layout = mHelper.inflate(R.layout.comm_loading);

        if (!isEmpty(msg)) {
            TextView textView = layout.findViewById(R.id.loading_msg);
            textView.setText(msg);
        }

        mHelper.showLayout(layout);
    }

    public void restore() {
        mHelper.restoreView();
    }
}
