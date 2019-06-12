package com.wxsoft.teleconsultation.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Patient;

public class CallPopWindow  extends PopupWindow implements View.OnClickListener {
    TextView patient, doctor,cancel_action;

    Patient p;
    Doctor d;
    public OnCallListener o;

    private View view;

    public void setCalls(Patient pcall,Doctor dcall){
        p=pcall;
        String p1=p.getPhone();
        if(p1==null || TextUtils.isEmpty(p1)) {
            patient.setVisibility(View.GONE);
        }
        patient.setText(p.getPhone());
        d=dcall;

        doctor.setText(d.getName());
    }
    //actionType:0
    public CallPopWindow(Context context) {
        super(context);
//
//        setWidth(GridLayout.LayoutParams.MATCH_PARENT);
//        setHeight(GridLayout.LayoutParams.WRAP_CONTENT);
//        setTouchable(true);
        setOutsideTouchable(true);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.pop_call_some_one, null);
        setContentView(view);

        patient = view.findViewById(R.id.patient);
        doctor = view.findViewById(R.id.doctor);
        cancel_action= view.findViewById(R.id.cancel_action);
        patient.setOnClickListener(this);
        doctor.setOnClickListener(this);
        cancel_action.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            default:
                o.back(((TextView)v).getText().toString());
                break;

            case R.id.cancel_action:
                break;
        }
        this.dismiss();
    }

    public interface OnCallListener {
        public void back(String phone);
    }

    public void setOnCallListener(OnCallListener o){
        this.o=o;
    }

}
