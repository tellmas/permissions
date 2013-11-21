package com.tellmas.android.permissions;

import android.app.Application;

/**
 * Global constants for this app.
 */
public final class GlobalDefines extends Application {

    /**
     * the "tag" for android.util.Log
     */
    public static final String LOG_TAG = "PERMSIES";
    /**
     * the prefix for an Android system defined permission
     */
    public static final String ANDROID_PERMISSION_PREFIX = "android.permission.";

    private static GlobalDefines singleton;
    public static GlobalDefines getInstance() {
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
