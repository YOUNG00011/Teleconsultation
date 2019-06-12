package com.wxsoft.teleconsultation.util.keyboard.interfaces;

import android.view.ViewGroup;

import com.wxsoft.teleconsultation.util.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
