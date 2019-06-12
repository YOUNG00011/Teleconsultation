package com.wxsoft.teleconsultation.http;

import android.util.Log;

public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        Log.i("http", message);
    }
}
