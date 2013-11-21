package com.tellmas.android.permissions;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores information about an app.
 *    * the name (aka 'label') of the app
 *    * the app's icon's resource id
 *    * the app's package name
 *    * a List<Permission> of the requested permissions
 * @implements Parcelable
 */
public class ApplicationInfo implements Parcelable {

    private final int iconId;
    private final String name;
    private final String packageName;
    private final List<Permission> permissions;

    /**
     * standard constructor
     * @param appName the name (aka 'label') of the app
     * @param iconResourceId the app's icon's resource id
     * @param packageName the app's package name
     * @param permissions a list of the requested permissions
     */
    public ApplicationInfo(String appName, int iconResourceId, String packageName, List<Permission> permissions) {
        this.name = appName;
        this.iconId = iconResourceId;
        this.packageName = packageName;
        this.permissions = permissions;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable.Creator<T>
     */
    public ApplicationInfo(Parcel parcel) {
        this.name = parcel.readString();
        this.packageName = parcel.readString();
        this.iconId = parcel.readInt();
        this.permissions = new ArrayList<Permission>();
        parcel.readTypedList(this.permissions, Permission.CREATOR);
    }


    /**
     * @return the app's icon's resource id
     */
    public int getIconResourceId() {
        return this.iconId;
    }
    /**
     * @return the app's name (aka 'label')
     */
    public String getName() {
        return this.name;
    }
    /**
     * @return the app's package name
     */
    public String getPackageName() {
        return this.packageName;
    }
    /**
     * @return a list of the requested permissions
     */
    public List<Permission> getPermissions() {
        return this.permissions;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name);
        parcel.writeString(this.packageName);
        parcel.writeInt(this.iconId);
        parcel.writeTypedList(this.permissions);
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
    public static final Parcelable.Creator<ApplicationInfo> CREATOR = new Parcelable.Creator<ApplicationInfo>() {
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public ApplicationInfo createFromParcel(Parcel in) {
            return new ApplicationInfo(in);
        }
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#newArray(int)
         */
        @Override
        public ApplicationInfo[] newArray(int size) {
            return new ApplicationInfo[size];
        }
    };
}
