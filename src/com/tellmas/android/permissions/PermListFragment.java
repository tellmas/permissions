package com.tellmas.android.permissions;

// TODO convert this to use the AsyncTask and an ExpandableListView, and you know, actually get a list of permissions

//import com.tellmas.android.permissions.AppListFragment.GetThePermsAsyncTask;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PermListFragment extends ListFragment {

    //private ArrayList<PermissionInfo> thePermList;

    private Activity parentActivity;
    //private AppListFragmentListener parentActivityListener;


    /**
     * TODO
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate()");
        super.onCreate(savedInstanceState);

        this.parentActivity = this.getActivity();

        if (savedInstanceState != null) {
            //this.thePermList = savedInstanceState.getParcelableArrayList(GlobalDefines.BUNDLE_KEY_FOR_PERM_LIST);
            //this.displayTheResults(this.thePermList);
        } else {

            this.getTheDataAndDisplayIt();
        }
    }


    /**
     * TODO
     */
    public void getTheDataAndDisplayIt() {
        //GetThePermsAsyncTask getThePerms = new GetThePermsAsyncTask();
        //getThePerms.execute(this.parentActivity);
        TextView text = (TextView) this.parentActivity.findViewById(R.id.perms_list);
        text.setText("This will be the Permissions list.");
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
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreateView()");
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_permlist_layout, container, false);
    }


    /**
     *TODO
     *
     * @param activity the containing Activity
     * @throws ClassCastException
     */
    @Override
    public void onAttach(Activity activity) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onAttach()");
        super.onAttach(activity);

        try {
            //this.parentActivityListener = (PermListFragmentListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.getClass().getName() + " did not implement AppListFragmentListener");
        }
    }
}
