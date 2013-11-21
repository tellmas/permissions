package com.tellmas.android.permissions;

import java.util.List;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * (non-Javadoc)
 * @see android.widget.BaseExpandableListAdapter
 */
public class AppListExpandableListAdapter extends BaseExpandableListAdapter {

    private final Activity activity;
    public LayoutInflater inflater;
    private final List<ApplicationInfo> appList;
    private final int iconDpi;

    public AppListExpandableListAdapter(Activity activity, List<ApplicationInfo> list) {
        this.activity = activity;
        this.appList = list;
        this.inflater = activity.getLayoutInflater();
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        this.iconDpi = activityManager.getLauncherLargeIconDensity();
    }

    @Override
    public Object getChild(int appInfoPosition, int childPosition) {

        List<Permission> permissions = this.appList.get(appInfoPosition).getPermissions();
        if (permissions == null) {
            return null;
        }
        return permissions.get(childPosition);
    }

    @Override
    public long getChildId(int appInfoPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int appInfoPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Object permissionObj = getChild(appInfoPosition, childPosition);

        // === Permission template ===
        if (convertView == null) {
          convertView = inflater.inflate(R.layout.app_list_item_dropdown_layout, null);
        }
        // --- Permission ---
        TextView permissionView = (TextView)convertView.findViewById(R.id.permission_name);
        // --- Description ---
        TextView descriptionView = (TextView)convertView.findViewById(R.id.permission_description);
        if (permissionObj == null) {
            permissionView.setText(this.activity.getResources().getString(R.string.no_permissions));
            descriptionView.setVisibility(TextView.GONE);
        } else {
            Permission permission = (Permission)permissionObj;
            permissionView.setText(permission.getName());
            descriptionView.setText(permission.getDescription());
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int appInfoPosition) {

        List<Permission> permissions = this.appList.get(appInfoPosition).getPermissions();
        if (permissions == null) {
            // Indicate to leave space to state that there are no permissions.
            return 1;
        } else {
            return permissions.size();
        }
    }

    @Override
    public Object getGroup(int appInfoPosition) {
        return this.appList.get(appInfoPosition);
    }

    @Override
    public int getGroupCount() {
        return this.appList.size();
    }

    @Override
    public long getGroupId(int appInfoPosition) {
        return appInfoPosition;
    }

    @Override
    public View getGroupView(int appInfoPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_list_item_layout, null);
        }

        ApplicationInfo appInfo = (ApplicationInfo)getGroup(appInfoPosition);

        TextView labelText = (TextView) convertView.findViewById(R.id.app_label);
        labelText.setText(appInfo.getName());

        TextView packageText = (TextView) convertView.findViewById(R.id.app_package);
        packageText.setText(appInfo.getPackageName());

        ImageView icon = (ImageView) convertView.findViewById(R.id.app_icon);
        Resources resources;
        Drawable iconData = null;
        try {
            resources = this.activity.getPackageManager().getResourcesForApplication(appInfo.getPackageName());
            iconData = resources.getDrawableForDensity(appInfo.getIconResourceId(), this.iconDpi);
        } catch (NameNotFoundException nnfe) {
            // just let the image data be null
        }
        icon.setImageDrawable(iconData);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int appInfoPosition, int childPosition) {
        return false;
    }

}
