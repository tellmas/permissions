package com.tellmas.android.permissions;

import java.util.Comparator;

/**
 * Compares ApplicationInfo objects by ApplicationInfo.name
 *    using the String class's compareTo() method.
 */
public class ApplicationInfoComparator implements Comparator<ApplicationInfo> {

    /**
     * Compares ApplicationInfo objects by ApplicationInfo.name
     *
     * @param appInfo1 an ApplicationInfo object to compare
     * @param appInfo2 another ApplicationInfo object to compare to the first one
     * @return 0 if the strings are equal, a negative integer if the first string is before the second string, or a positive integer if the first string is after the second string
     */
    @Override
    public int compare(ApplicationInfo appInfo1, ApplicationInfo appInfo2) {
        return appInfo1.getName().compareTo(appInfo2.getName());
    }

}
