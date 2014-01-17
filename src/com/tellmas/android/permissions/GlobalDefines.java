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


    public static final String BUNDLE_KEY_FOR_APP_LIST = "appInfoList";
    public static final String BUNDLE_KEY_FOR_PERM_LIST = "permInfoList";

    /**
     * names of the defined Classes to be used as Fragments in the content View
     */
    public static final String[] FRAGMENT_CLASS_NAMES = {
        "AppListFragment",
        "PermListFragment"
    };
    // === names (and how to access the names) of the defined Classes to be used as Fragments in the content View ===
    // TODO use an enum or EnumMap for the indices
    public static final int FRAGMENT_CLASS_INDEX_FOR_APPLIST = 0;
    public static final int FRAGMENT_CLASS_INDEX_FOR_PERMLIST = 1;
    /**
     * the index of the desired starting Fragment
     */
    public static final int STARTING_FRAGMENT_INDEX = FRAGMENT_CLASS_INDEX_FOR_APPLIST;

    /**
     * indicates the index of the Fragment to restore upon creation in the savedInstanceState Bundle
     */
    public static final String BUNDLE_KEY_FRAGMENT_TO_RESTORE = "FragmentToRestore";


    /**
     * Defines for the type of list
     */
    public static final int LIST_TYPE_APPS = 1;
    public static final int LIST_TYPE_PERMS = 2;


    public static final int EXIT_STATUS_ERROR = 1;


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
