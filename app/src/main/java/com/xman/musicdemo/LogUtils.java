package com.xman.musicdemo;

import android.util.Log;

/**
 * Created by nieyunlong on 17/6/29.
 */

public class LogUtils {
    public static final boolean isDebug = true;

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(tag, message);
        }

    }
}
