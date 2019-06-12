package com.wxsoft.teleconsultation.util;

import android.os.Handler;

public class ThreadUtil {

    static Handler mHandler = new Handler();

    public static void runInThread(Runnable task) {
        new Thread(task).start();
    }

    public static void runInUiThread(Runnable task) {
        mHandler.post(task);
    }

}
