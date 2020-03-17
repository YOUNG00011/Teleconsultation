package com.wxsoft.telereciver.event;

/**
 * Created by ${chenyn} on 2017/4/9.
 */

public class ImageEvent {

    public static final int IMAGE_MESSAGE = 1;
    public static final int TAKE_PHOTO_MESSAGE = 2;
    public static final int VIDEO_CLINIC_MESSAGE = 3;

    private int mFlag;

    public ImageEvent(int flag) {
        mFlag = flag;
    }

    public int getFlag() {
        return mFlag;
    }
}
