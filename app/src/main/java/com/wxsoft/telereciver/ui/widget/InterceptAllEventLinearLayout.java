package com.wxsoft.telereciver.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptAllEventLinearLayout extends LinearLayout {

    public InterceptAllEventLinearLayout(Context context) {
        super(context);
    }

    public InterceptAllEventLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
