package com.xman.musicdemo;

import android.app.Application;

/**
 * Created by nieyunlong on 17/6/29.
 */

public class Appctx extends Application {
    public static Appctx instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Appctx getInstance() {
        return instance;
    }
}
