package com.tellmas.android.permissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

/**
 * Fragment for the content View.
 * For installed apps with a launcher, obtains the data and displays the app's:
 *   - icon, label and package name
 *   - requested permissions along with the indicated description (if existing/available)
 */
public class AppListFragment extends ListFragment {

    // all the data to display
    private ArrayList<ApplicationInfo> theAppList;

    private Activity parentActivity;
    private AppListFragmentListener parentActivityListener;

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
            this.theAppList = savedInstanceState.getParcelableArrayList(GlobalDefines.BUNDLE_KEY_FOR_APP_LIST);
            this.displayTheResults(this.theAppList);
        } else {
            this.getTheDataAndDisplayIt();
        }
    }


    /***
     * Executes the AsyncTask inner-class.
     */
    public void getTheDataAndDisplayIt() {
        GetTheAppsAsyncTask getTheApps = new GetTheAppsAsyncTask();
        getTheApps.execute(this.parentActivity);
    }


    /**
     * Returns this fragment's layout.
     *
     * @see android.app.Fragment#onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreateView()");
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_applist_layout, container, false);
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
            throw new ClassCastException(activity.getClass().getName() + " did not implement AppListFragmentListener");
        }
    }


    /*
     * Uses an expanded BaseExpandableListAdapter to display the apps and their permissions.
     * @param theList a List<ApplicationInfo> of the apps
     * (non-Javadoc)
     */
    private void displayTheResults(List<ApplicationInfo> theList) {

        int numberOfApps = 0;
        try {
            numberOfApps = theList.size();
            // === Display all the apps ===
            AppListExpandableListAdapter appListAdapter = new AppListExpandableListAdapter(this.parentActivity, theList);
            ExpandableListView appListView = (ExpandableListView) this.parentActivity.findViewById(R.id.apps_list);
            appListView.setAdapter(appListAdapter);
        // if 'theList' was null...
        } catch (NullPointerException npe) {
            Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": the list of apps was null");
        } finally {
            this.parentActivityListener.setFinished(numberOfApps, GlobalDefines.LIST_TYPE_APPS);
        }
    }


    /* Uses an expanded BaseExpandableListAdapter to display the apps and their permissions.
     * @param theList an ArrayList<ApplicationInfo> of the apps
     * (non-Javadoc)
     */
    private void finalizeTheResults(ArrayList<ApplicationInfo> theList) {
        this.theAppList = theList;
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
    public interface AppListFragmentListener {
        public void updateProgress(int soFar, int total);
        public void setFinished(int numOfApps, int itemType);
    }


    // -------------------------------------------------------------------------
    /* AsyncTask which obtains info on all the apps on the device (which have a launcher)
     * @param contexts a single element array containing the outer class's instance (i.e. 'this')
     * @return a List<ApplicationInfo> containing all the apps on the device (which have a launcher)
     * (non-Javadoc)
     */
    private class GetTheAppsAsyncTask extends AsyncTask<Activity, Integer, ArrayList<ApplicationInfo>> {

        @Override
        protected ArrayList<ApplicationInfo> doInBackground(Activity... contexts) {

            ArrayList<ApplicationInfo> applicationInfoList = null;
            final Activity context = contexts[0];
            final PackageManager pm = context.getPackageManager();
            Integer[] progress = {Integer.valueOf(0), Integer.valueOf(0)};

            // === List of apps which have a Launcher ===
            final Intent intentFilter = new Intent(Intent.ACTION_MAIN, null);
            intentFilter.addCategory(Intent.CATEGORY_LAUNCHER);
            final List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intentFilter, 0);
            progress[1] = Integer.valueOf(resolveInfoList.size());

            // === List of the packages of said apps ===
            List<String> packages = new ArrayList<String>(resolveInfoList.size());
            Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": starting iteration through 'resolveInfoList'");
            for (ResolveInfo resolveInfo : resolveInfoList) {
                ActivityInfo activity = resolveInfo.activityInfo;
                if (activity != null) {
                    packages.add(activity.packageName);
                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": activity: " + activity.packageName);
                }
            }

            int numOfPackages = packages.size();
            applicationInfoList = new ArrayList<ApplicationInfo>(numOfPackages);
            for (int i=0; i < numOfPackages; i++) {
                if (this.isCancelled()) {
                    return applicationInfoList;
                }

                PackageInfo packageInfo = null;

                try {
                    packageInfo = pm.getPackageInfo(packages.get(i), PackageManager.GET_PERMISSIONS);
                // if the package wasn't found on the system...
                } catch (NameNotFoundException nnfe) {
                    Log.e(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packages.get(i) + " wasn't found on the system.");
                    // ...skip it.
                    continue;
                    /* This really shouldn't happen though, because the same
                     * package manager was used to get the list of activities. */
                }

                // --- app's Label ---
                String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + appName);

                // --- app's Package ---
                String packageName = packageInfo.packageName;
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": " + packageName);

                // --- icon's Resource Id ---
                int iconResourceId = packageInfo.applicationInfo.icon;
                if (iconResourceId == 0) {
                    iconResourceId = packageInfo.applicationInfo.logo;
                }
                Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": icon resource id: " + Integer.toString(iconResourceId));

                // --- Requested Permissions ---
                String[] requestedPerms = packageInfo.requestedPermissions;
                List<Permission> permissionsList = null;
                if (requestedPerms != null) {
                    permissionsList = new ArrayList<Permission>();

                    Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": requested permissions array length: " + Integer.toString(requestedPerms.length));
                    for (String permission : requestedPerms) {

                        PermissionInfo permInfo = null;
                        try {
                            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": permission: " + permission);
                            permInfo = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
                        // if the package manager did not find the permission...
                        } catch (NameNotFoundException nnfe) {
                            Log.w(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": Permission not found: " + permission);
                            // ...create a bare-bones PermissionInfo object to use instead.
                            permInfo = new PermissionInfo();
                            permInfo.labelRes = 0;
                            permInfo.descriptionRes = 0;
                        }

                        String name = null;
                        try {
                            name = context.getResources().getString(permInfo.labelRes);
                            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": permission: " + name);
                        // if the Resource did not find the human readable name...
                        } catch (NotFoundException nfe) {
                            name = permInfo.name;
                            CharSequence nameCharSeq1 = permInfo.loadLabel(pm);
                            CharSequence nameCharSeq2 = permInfo.nonLocalizedLabel;
                            // if we already have a name...
                            if (name != null) {
                                // ...just use it.
                            } else if (nameCharSeq1 != null) {
                                name = nameCharSeq1.toString();
                            } else if (nameCharSeq2 != null) {
                                name = nameCharSeq2.toString();
                            } else {
                                name = permission;
                            }
                        }

                        String description = null;
                        try {
                            description = context.getResources().getString(permInfo.descriptionRes);
                            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": permission description string resource: " + Integer.toString(permInfo.descriptionRes));
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
                            // ...else if the permission is a 'android.permission.' permission...
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

                        permissionsList.add(new Permission(permission, name, description));

                        if (this.isCancelled()) {
                            return applicationInfoList;
                        }
                        progress[0] = Integer.valueOf(progress[0].intValue() + 1);
                        publishProgress(progress);

                    } // end for()
                }

                // --- Declared Permissions ---
                PermissionInfo[] declaredPerms = packageInfo.permissions;
                if (declaredPerms != null) {
                    if (permissionsList == null) {
                        permissionsList = new ArrayList<Permission>();
                    }
                }

                // === set up the app to be listed ===
                ApplicationInfo appInfo = new ApplicationInfo(appName, iconResourceId, packageName, permissionsList);
                applicationInfoList.add(appInfo);
            }

            // --- Sort the list of apps ---
            Collections.sort(applicationInfoList, new ApplicationInfoComparator());

            return applicationInfoList;
        } // end doInBackground


        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ArrayList<ApplicationInfo> theList) {
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
