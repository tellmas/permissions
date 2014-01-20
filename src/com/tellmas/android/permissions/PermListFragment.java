package com.tellmas.android.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.tellmas.android.permissions.AppListFragment.AppListFragmentListener;

public class PermListFragment extends ListFragment {

    private ArrayList<com.tellmas.android.permissions.PermissionInfo> thePermList;

    private Activity parentActivity;
    private AppListFragmentListener parentActivityListener;

    private ExpandableListView permListView;


    /**
     * @param savedInstanceState data to start with
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate()");
        super.onCreate(savedInstanceState);

        this.parentActivity = this.getActivity();

        if (savedInstanceState != null) {
            this.thePermList = savedInstanceState.getParcelableArrayList(GlobalDefines.BUNDLE_KEY_FOR_PERM_LIST);
        }
    }


    /**
     * @param outState where to store the data that will be restored later
     * @see android.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(GlobalDefines.BUNDLE_KEY_FOR_PERM_LIST, this.thePermList);
    }


    /**
     * Executes the AsyncTask inner-class.
     */
    public void getTheDataAndDisplayIt() {
        GetThePermsAsyncTask getThePerms = new GetThePermsAsyncTask();
        getThePerms.execute(this.parentActivity);
    }


    /**
     * Sets this fragment's layout and executes the task to get the permissions and the apps which use them.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState data with which to start
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreateView()");
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_permlist_layout, container, false);
    }


    /**
     * Sets up the connection for callbacks to the parent Activity.
     *
     * @param activity the containing Activity
     * @throws ClassCastException
     * @see android.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onAttach()");
        super.onAttach(activity);

        try {
            this.parentActivityListener = (AppListFragmentListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.getClass().getName() + " did not implement PermListFragmentListener");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        this.permListView = (ExpandableListView) this.getView().findViewById(R.id.perms_list);

        if (this.thePermList == null) {
            this.getTheDataAndDisplayIt();
        } else {
            this.displayTheResults(this.thePermList);
        }

        if (this.getRetainInstance() == true) {
            this.setRetainInstance(false);
        }
    }


    /* ******************* Unused lifecycle methods *********************** */
    @Override
    public void onStart() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onStart()");
        super.onStart();
    }
    @Override
    public void onResume() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onResume()");
        super.onResume();
    }
    @Override
    public void onPause() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onPause()");
        super.onPause();
    }
    @Override
    public void onStop() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onStop()");
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onDestroyView()");
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onDestroy()");
        super.onDestroy();
    }
    @Override
    public void onDetach() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onDetach()");
        super.onDetach();
    }
    /* ****************** END Unused lifecycle methods ******************** */


    /*
     * Uses an expanded BaseExpandableListAdapter to display the permissions and the apps that request them.
     * @param theList a List<PermissionInfo> of the permissions
     * (non-Javadoc)
     */
    private void displayTheResults(List<com.tellmas.android.permissions.PermissionInfo> theList) {

        int numberOfApps = 0;
        try {
            numberOfApps = this.thePermList.size();

            // === Display all the permissions ===
            PermListExpandableListAdapter permListAdapter = new PermListExpandableListAdapter(this.parentActivity, this.thePermList);
            this.permListView.setAdapter(permListAdapter);
        // if 'theList' was null...
        } catch (NullPointerException npe) {
            Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": the list of permissions was null");
        } finally {
            this.parentActivityListener.setFinished(numberOfApps, GlobalDefines.LIST_TYPE_PERMS);
        }
    }


    /* Uses an expanded BaseExpandableListAdapter to display the apps and their permissions.
     * @param theList an ArrayList<ApplicationInfo> of the apps
     * (non-Javadoc)
     */
    private void finalizeTheResults(ArrayList<com.tellmas.android.permissions.PermissionInfo> theList) {
        this.thePermList = theList;
        this.displayTheResults(theList);
    }


    /*
     * Calls the parent Activity's callback to update the progress.
     *
     * @param soFar how far along the app list we are
     * @param total total number of apps in the list
     * (non-Javadoc)
     */
    private void setProgress(int soFar, int total) {
        this.parentActivityListener.updateProgress(soFar, total);
    }


    // -------------------------------------------------------------------------
    /**
     * Interface of callbacks to the containing Activity (which the containing
     *   Activity must implement).
     */
    public interface PermListFragmentListener {
        public void updateProgress(int soFar, int total);
        public void setFinished(int numOfApps, int itemType);
    }


    // -------------------------------------------------------------------------
    /* AsyncTask which obtains info on all the permissions which are requested
     *   by the apps on the device (which have a launcher)
     * @param contexts a single element array containing the outer class's instance (i.e. 'this')
     * @return a List<PermissionInfo> containing all the permissions
     * (non-Javadoc)
     */
    private class GetThePermsAsyncTask extends AsyncTask<Activity, Integer, ArrayList<com.tellmas.android.permissions.PermissionInfo>> {

        @Override
        protected ArrayList<com.tellmas.android.permissions.PermissionInfo> doInBackground(Activity... contexts) {

            final Activity context = contexts[0];
            final PackageManager pm = context.getPackageManager();
            Integer[] progress = {Integer.valueOf(0), Integer.valueOf(0)};

            // === List of apps which have a Launcher ===
            final Intent intentFilter = new Intent(Intent.ACTION_MAIN, null);
            intentFilter.addCategory(Intent.CATEGORY_LAUNCHER);
            final List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intentFilter, 0);

            // The permissions with corresponding apps.
            Map<String,HashSet<String>> permissionsWithTheirApps = new HashMap<String,HashSet<String>>();
            // === Iterate through the found apps to get their requested permissions. ===
            int appsFoundNum = resolveInfoList.size();
            for (int i=0; i < appsFoundNum; i++) {

                if (this.isCancelled()) {
                    return null;
                }

                // --- Get the package info. ---
                PackageInfo packageInfo = null;
                String packageName = null;
                try {
                    // Get the package name of an app found earlier.
                    try {
                        packageName = resolveInfoList.get(i).activityInfo.packageName;
                    // if no ActivityInfo...
                    } catch (NullPointerException npe) {
                        Log.w(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": No ActivityInfo for this ResolveInfo: " + resolveInfoList.get(i).toString() + ". Excluded from listing.");
                        // ...skip it.
                        continue;
                    }
                    packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
                // if the package wasn't found on the system...
                } catch (NameNotFoundException nnfe) {
                    Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packageName + " wasn't found on the system.");
                    // ...skip it.
                    continue;
                    /* This really shouldn't happen though, because the same
                     * package manager was used to get the list of activities. */
                }
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packageName);

                // === get the requested permissions for this app ===
                String[] permsRequestedByThisApp = packageInfo.requestedPermissions;
                // if this app requests one or more permissions...
                if (permsRequestedByThisApp != null) {
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": requested permissions array length: " + Integer.toString(permsRequestedByThisApp.length));

                    // === Iterate through the permissions requested by the current app. ===
                    for (String perm : permsRequestedByThisApp) {

                    	Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": current permission: " + perm);

                        if (this.isCancelled()) {
                            return null;
                        }

                        // Get the set of apps that requested this permission.
                        HashSet<String> appsForThisPermission = (HashSet<String>) permissionsWithTheirApps.get(perm);
                        try {
                            // if the app was not already listed...
                            if (!appsForThisPermission.contains(packageName)) {
                                // ...add it.
                                appsForThisPermission.add(packageName);
                                permissionsWithTheirApps.put(perm, appsForThisPermission);
                                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": Adding " + packageName + " to " + perm);
                            }
                            // else... this else case shouldn't happen.
                            // We're iterating through the apps, so this is the first time we've encountered this app.

                        // if we haven't encountered this permission yet...
                        } catch (NullPointerException npe) {
                            appsForThisPermission = new HashSet<String>();
                            appsForThisPermission.add(packageName);
                            permissionsWithTheirApps.put(perm, appsForThisPermission);
                            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": First time encountering: " + perm + ". Adding it to the list.");
                            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": Adding " + packageName + " to " + perm);
                        }
                    }
                // ...else the app didn't request any permissions...
                } else {
                    // ...so the permissions won't be listed.
                	Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": No permissions for this app.");
                }

                // set total number of permissions for the progress indicator
                progress[1] = Integer.valueOf(permissionsWithTheirApps.size());

            } // end of iterating through the apps in the ResolveInfo list


            // the List to return
            ArrayList<com.tellmas.android.permissions.PermissionInfo> theListOfPermissions =
                    new ArrayList<com.tellmas.android.permissions.PermissionInfo>(permissionsWithTheirApps.size());
            Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": *** Construting the data which will be displayed. ***");

            // === Iterate through the permissions to construct the data List which will be returned. ===
            Iterator<String> permissionsIterator = permissionsWithTheirApps.keySet().iterator();
            while (permissionsIterator.hasNext()) {

                String permission = permissionsIterator.next();

                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": === Permission ===");
                PermissionInfo permInfo = null; // android.content.pm.PermissionInfo
                try {
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + permission);
                    permInfo = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
                // if the package manager did not find the permission...
                } catch (NameNotFoundException nnfe) {
                    Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": Permission not found: " + permission);
                    // ...create a bare-bones PermissionInfo object to use instead.
                    permInfo = new PermissionInfo();
                    permInfo.labelRes = 0;
                    permInfo.descriptionRes = 0;
                }

                // --- permission Name ---
                String permissionName = null;
                try {
                    permissionName = context.getResources().getString(permInfo.labelRes);
                // if the Resource did not find the human readable name...
                } catch (NotFoundException nfe) {
                    permissionName = permInfo.name;
                    CharSequence nameCharSeq1 = permInfo.loadLabel(pm);
                    CharSequence nameCharSeq2 = permInfo.nonLocalizedLabel;
                    // if we already have a name...
                    if (permissionName != null) {
                        // ...just use it.
                    } else if (nameCharSeq1 != null) {
                        permissionName = nameCharSeq1.toString();
                    } else if (nameCharSeq2 != null) {
                        permissionName = nameCharSeq2.toString();
                    } else {
                        permissionName = permission;
                    }
                }
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + permissionName);

                // --- permission Description ---
                String description = null;
                try {
                    description = context.getResources().getString(permInfo.descriptionRes);
                // if the Resource did not find the description...
                } catch (NotFoundException nfe) {
                    CharSequence descCharSeq1 = permInfo.loadDescription(pm);
                    CharSequence descCharSeq2 = permInfo.nonLocalizedDescription;
                    if (descCharSeq1 != null) {
                        description = descCharSeq1.toString();
                    } else if (descCharSeq2 != null) {
                        description = descCharSeq2.toString();
                    } else if (permInfo.descriptionRes == 0) {
                        description = context.getResources().getString(R.string.permission_defined_by_app);
                    // ...else if the permission is an 'android.permission.' permission...
                    } else if (permission.startsWith(GlobalDefines.ANDROID_PERMISSION_PREFIX, 0)) {
                        // ...but we didn't find the description earlier...
                        // ...log the permission.
                        Log.e(GlobalDefines.LOG_TAG,
                            this.getClass().getSimpleName() +
                            ": Error getting description for (the android.permission. permission): " +
                            permission
                        );
                        description = null;
                    } else {
                        description = context.getResources().getString(R.string.permission_defined_elsewhere);
                    }
                }
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + description);

                // the data for all the apps that use this permission
                List<ApplicationInfo> theAppsDataList = new LinkedList<ApplicationInfo>();
                // the set of app package names for this permission
                HashSet<String> theAppsPackages = permissionsWithTheirApps.get(permission);

                // === Iterate through each of the apps by package name. ===
                Iterator<String> appPackagesIterator = theAppsPackages.iterator();
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": --- Apps using this permission ---");
                while (appPackagesIterator.hasNext()) {

                    if (this.isCancelled()) {
                        return theListOfPermissions;
                    }

                    String packageName = appPackagesIterator.next();
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packageName);

                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = pm.getPackageInfo(packageName, 0);
                    // if the package wasn't found on the system...
                    } catch (NameNotFoundException nnfe) {
                        Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packageName + " wasn't found on the system.");
                        // ...skip it.
                        continue;
                        /* This really shouldn't happen though, because the same
                         * package manager was used to get the list of activities. */
                    }

                    // --- app's Label ---
                    String appLabel = packageInfo.applicationInfo.loadLabel(pm).toString();
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + appLabel);

                    // --- icon's Resource Id ---
                    int iconResourceId = packageInfo.applicationInfo.icon;
                    if (iconResourceId == 0) {
                        iconResourceId = packageInfo.applicationInfo.logo;
                    }
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": icon resource id: " + Integer.toString(iconResourceId));

                    // Add this app's data to the List for the permission.
                    theAppsDataList.add(
                        new ApplicationInfo(appLabel, iconResourceId, packageName, null)
                    );

                }

                // Sort the apps data list.
                Collections.sort(theAppsDataList, new ApplicationInfoComparator());

                // Add the permission (with its apps' data) to the data List.
                theListOfPermissions.add(
                    new com.tellmas.android.permissions.PermissionInfo(
                        new Permission(permission, permissionName, description),
                        theAppsDataList
                    )
                );


                progress[0] = Integer.valueOf(progress[0].intValue() + 1);
                publishProgress(progress);

            } // end of iterating through the permissions

            // --- Sort the List of permissions ---
            Collections.sort(theListOfPermissions, new PermissionInfoComparator());

            return theListOfPermissions;
        } // end doInBackground


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<com.tellmas.android.permissions.PermissionInfo> theList) {
            finalizeTheResults(theList);
        }


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgress(progress[0].intValue(), progress[1].intValue());
        }


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onCancelled()
         */
        @Override
        protected void onCancelled() {
            // display nothing
            finalizeTheResults(null);
        }

    } // end getTheAppsAsyncTask
}
