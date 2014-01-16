package com.tellmas.android.permissions;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores information about a permission:
 *    - a Permission object with the info about this permission
 *    - a List<ApplicationInfo> of the apps which request this permission
 * @implements Parcelable
 */
public class PermissionInfo implements Parcelable {

    private final Permission permission;
    private List<ApplicationInfo> apps;

    /**
     * standard constructor
     * @param appName the name (aka 'label') of the app
     * @param iconResourceId the app's icon's resource id
     * @param packageName the app's package name
     * @param permissions a list of the requested permissions
     */
    public PermissionInfo(Permission permission, List<ApplicationInfo> apps) {
        this.permission = permission;
        this.apps = apps;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable.Creator<T>
     */
    public PermissionInfo(Parcel parcel) {
        this.permission = parcel.readParcelable(Permission.class.getClassLoader());
        parcel.readTypedList(this.apps, ApplicationInfo.CREATOR);
    }


    /**
     * @return the permission's name
     */
    public Permission getPermission() {
        return this.permission;
    }
    /**
     * @return a list of the apps which request this permission
     */
    public List<ApplicationInfo> getApps() {
        return this.apps;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.permission, flags);
        parcel.writeTypedList(this.apps);
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     * @see http://stackoverflow.com/questions/4076946/parcelable-where-when-is-describecontents-used/4914799#4914799
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable.Creator<T>
     */
    public static final Parcelable.Creator<PermissionInfo> CREATOR = new Parcelable.Creator<PermissionInfo>() {
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public PermissionInfo createFromParcel(Parcel in) {
            return new PermissionInfo(in);
        }
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#newArray(int)
         */
        @Override
        public PermissionInfo[] newArray(int size) {
            return new PermissionInfo[size];
        }
    };
}
