package com.tellmas.android.permissions;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores information about a permission requested by an app:
 *    * canonical name
 *    * a human readable name (if available)
 *    * a description (if available)
 *    @implements Parcelable
 */
public class Permission implements Parcelable {

    // canonical name of the permission
    private final String permission;
    // human readable name of the permission
    private final String name;
    // description of the permission
    private final String description;

    /**
     * standard constructor
     * @param permission the permission's canonical name
     * @param name the human readable name of the permission (null if not available)
     * @param description the human readable description of the permission (null if not available)
     */
    public Permission(String permission, String name, String description) {
        this.permission = permission;
        this.name = name;
        this.description = description;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable.Creator<T>
     */
    public Permission(Parcel parcel) {
        this.permission = parcel.readString();
        this.name = parcel.readString();
        this.description = parcel.readString();
    }

    /**
     * @return the fully qualified permission name
     */
    public String getPermission() {
        return this.permission;
    }
    /**
     * @return the human readable name of the permission (null if not set)
     */
    public String getName() {
        return this.name;
    }
    /**
     * @return the human readable description of the permission (null if not set)
     */
    public String getDescription() {
        return this.description;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.permission);
        parcel.writeString(this.name);
        parcel.writeString(this.description);
    }


    /*
     * (non-Javadoc)
     * @see android.os.Parcelable.Creator<T>
     */
    public static final Parcelable.Creator<Permission> CREATOR = new Parcelable.Creator<Permission>() {
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public Permission createFromParcel(Parcel in) {
            return new Permission(in);
        }
        /*
         * (non-Javadoc)
         * @see android.os.Parcelable.Creator#newArray(int)
         */
        @Override
        public Permission[] newArray(int size) {
            return new Permission[size];
        }
    };
}