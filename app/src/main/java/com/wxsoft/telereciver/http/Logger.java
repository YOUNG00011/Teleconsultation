package com.wxsoft.telereciver.http;

import android.util.Log;

public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        Log.i("http", message);
    }
}
