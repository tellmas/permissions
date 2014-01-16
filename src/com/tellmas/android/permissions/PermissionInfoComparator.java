package com.tellmas.android.permissions;

import java.util.Comparator;

/**
 * Compares PermissionInfo objects by Permission.name
 *    using the String class's compareTo() method.
 */
public class PermissionInfoComparator implements Comparator<PermissionInfo> {

    /**
     *
     */
    public PermissionInfoComparator() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Compares PermissionInfo objects by PermissionInfo.name
     *
     * @param permInfo1 an PermissionInfo object to compare
     * @param permInfo2 another PermissionInfo object to compare to the first one
     * @return 0 if the PermissionInfo objects have the same name, a negative integer if the first name is alphabetically before the second, or a positive integer if the first name is alphabetically after the second name
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(PermissionInfo permInfo1, PermissionInfo permInfo2) {
        return permInfo1.getPermission().getName().compareTo(permInfo2.getPermission().getName());
    }

}
