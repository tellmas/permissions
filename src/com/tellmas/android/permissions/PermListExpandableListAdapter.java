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

/**
 * Adapts the data in the List of permissions to the permlist layout fragment.
 * @see android.widget.BaseExpandableListAdapter
 */
public class PermListExpandableListAdapter extends BaseExpandableListAdapter {

    private final Activity activity;
    public LayoutInflater inflater;
    private final List<com.tellmas.android.permissions.PermissionInfo> permList;
    private final int iconDpi;

    /**
     * Constructor for this Adapter
     * @param activity the permissions list fragment
     * @param list the List of data of the permissions and corresponding apps
     */
    public PermListExpandableListAdapter(Activity activity, List<com.tellmas.android.permissions.PermissionInfo> list) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.permList = list;
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        this.iconDpi = activityManager.getLauncherLargeIconDensity();
    }


    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public Object getChild(int permInfoPosition, int childPosition) {

        ApplicationInfo anAppInfo = null;

        List<ApplicationInfo> appsWithThisPermission = this.permList.get(permInfoPosition).getApps();
        if (appsWithThisPermission != null) {
            anAppInfo = appsWithThisPermission.get(childPosition);
        }
        return anAppInfo;
    }


    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int permInfoPosition, int childPosition) {

        return childPosition;
    }


    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getChildView(int permInfoPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ApplicationInfo anAppInfo = (ApplicationInfo) this.getChild(permInfoPosition, childPosition);
        // Shouldn't encounter a ClassCastException since an ApplicationInfo is
        //   defined in the appropriate class (or better well should be).

        // === App template ===
        if (convertView == null) {
          convertView = inflater.inflate(R.layout.permlist_app_layout, null);
        }
        // --- App label ---
        TextView labelText = (TextView) convertView.findViewById(R.id.app_label);
        // --- App package ---
        TextView packageText = (TextView) convertView.findViewById(R.id.app_package);
        // --- App icon ---
        ImageView iconImage = (ImageView) convertView.findViewById(R.id.app_icon);

        // if no apps requested this permission...
        if (anAppInfo == null) {
            // ...(shouldn't get here, 'cause why would this permission show up if no apps use it?).
            labelText.setText(this.activity.getResources().getString(R.string.no_apps));
            packageText.setVisibility(TextView.GONE);
            iconImage.setVisibility(TextView.GONE);
        } else {
            // --- set Label --
            labelText.setText(anAppInfo.getName());
            // --- set Package ---
            packageText.setText(anAppInfo.getPackageName());
            // === set Icon ===
            Resources resources;
            Drawable iconData = null;
            try {
                resources = this.activity.getPackageManager().getResourcesForApplication(anAppInfo.getPackageName());
                iconData = resources.getDrawableForDensity(anAppInfo.getIconResourceId(), this.iconDpi);
            } catch (NameNotFoundException nnfe) {
                // just let the image data be null
            }
            iconImage.setImageDrawable(iconData);
        }

        return convertView;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int permInfoPosition) {
        // will always be 1 or greater
        return this.permList.get(permInfoPosition).getApps().size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public Object getGroup(int permInfoPosition) {
        return this.permList.get(permInfoPosition);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return this.permList.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int permInfoPosition) {
        return permInfoPosition;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int permInfoPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.permlist_perm_layout, null);
        }

        Permission aPermission = ((com.tellmas.android.permissions.PermissionInfo) getGroup(permInfoPosition)).getPermission();

        // === Permission name ===
        TextView permissionText = (TextView) convertView.findViewById(R.id.perm_name);
        permissionText.setText(aPermission.getName());
        // === Permission description ===
        TextView descriptionText = (TextView) convertView.findViewById(R.id.perm_description);
        descriptionText.setText(aPermission.getDescription());

        return convertView;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int permInfoPosition, int childPosition) {
        return false;
    }

}
