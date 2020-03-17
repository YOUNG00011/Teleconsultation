package com.wxsoft.telereciver.util.keyboard.interfaces;

import android.view.ViewGroup;

import com.wxsoft.telereciver.util.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
