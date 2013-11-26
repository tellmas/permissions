package com.tellmas.android.permissions;

import com.tellmas.android.permissions.AppListFragment;
import com.tellmas.android.permissions.PermListFragment;
import com.tellmas.android.permissions.AppListFragment.AppListFragmentListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * the Main Activity
 * Fetches the data for and displays the apps with a launcher installed on the
 *  device along with their requested permissions.
 *
 *  @extends Activity
 *  @implements AppListFragmentListener
 */
public class MainActivity extends Activity implements AppListFragmentListener {

    private ProgressBar progressBar = null;

    private FragmentManager fragmentManager;
    private final Fragment fragments[] = new Fragment[2];

    // --- Navigation Drawer ---
    private String[] slideoutMenuItems;
    private DrawerLayout drawerLayout;
    private ListView slideOutList;
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Sets the:
     *   - main layout
     *   - "hourglass"
     *   - navigation menu
     *   - initial content Fragment
     *
     * @param savedInstanceState data to start with
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.progressBar = (ProgressBar)findViewById(R.id.progress);
        this.progressBar.setProgress(0);
        this.progressBar.setVisibility(View.VISIBLE);

        // === Slide-Out Menu ===
        this.slideoutMenuItems = getResources().getStringArray(R.array.slideout_menu_items);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.slideOutList = (ListView) findViewById(R.id.slideout_drawer);
        // Set the adapter for the list view (of the slide-out menu)
        this.slideOutList.setAdapter(
            new ArrayAdapter<String>(
                this,
                R.layout.drawer_item,
                this.slideoutMenuItems
            )
        );
        // Set the list's click listener
        this.slideOutList.setOnItemClickListener(new DrawerItemClickListener());

        // --- Set the ActionBar app icon to toggle the nav drawer ---
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.getActionBar().setHomeButtonEnabled(true);

        final CharSequence activityTitle = this.getTitle();

        // Tie together the the proper interactions between the sliding drawer and the ActionBar app icon
        this.drawerToggle = new ActionBarDrawerToggle(
                this,
                this.drawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            // Called when a drawer has settled in a completely closed state.
            @Override
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely open state.
            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(activityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // set the just defined ActionBarDrawerToggle as the drawer listener
        this.drawerLayout.setDrawerListener(drawerToggle);

        // --- create the default starting Fragment ---
        this.fragments[GlobalDefines.STARTING_FRAGMENT_INDEX] = this.instantiateFragment(GlobalDefines.STARTING_FRAGMENT_INDEX);

        // === insert into the content frame the default starting Fragment ===
        this.fragmentManager = this.getFragmentManager();
        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, this.fragments[GlobalDefines.STARTING_FRAGMENT_INDEX]);
        fragmentTransaction.commit();
    }


    /**
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onPostCreate()");
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        this.drawerToggle.syncState();
    }


    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onOptionsItemSelected()");

        // if ActionBarDrawerToggle returns true...
        if (this.drawerToggle.onOptionsItemSelected(item)) {
            // ...then it has handled the app icon touch event.
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Saves all the app's data so don't have to go get it all again on a screen orientation change.
     *
     * @param outState the Bundle in which to store the data
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        // TODO: determine if we need to save anything
        //outState.putParcelableArrayList(GlobalDefines.BUNDLE_KEY_FOR_APP_LIST, this.theAppList);
    }


    /**
     * Updates the ProgressBar.
     * Also sets the max value for the ProgressBar if the @param 'soFar' is 1 or less
     *   (the idea being when this method is first called)
     * @param soFar how far along the app list we are
     * @param total total number of apps in the list
     */
    @Override
    public void updateProgress(int soFar, int total) {
        if (soFar <= 1) {
            this.progressBar.setMax(total);
        }
        this.progressBar.setProgress(soFar);
    }


    /**
     * Remove the "hourglass"/Progressbar and set the number of found apps.
     * Called when the active Fragment has finished loading its data.
     *
     *  @param numOfApps the number of apps to be displayed
     */
    @Override
    public void setFinished(int numOfApps) {

        this.progressBar.setVisibility(View.GONE);

        TextView numOfAppsInList = (TextView) this.findViewById(R.id.number_of_apps_num);
        numOfAppsInList.setText(Integer.toString(numOfApps));
    }


    /*
     * Creates a new instance of the requested Class.
     * @param fragmentClassNamesIndex index of the desired Class name in GlobalDefines.FRAGMENT_CLASS_NAMES
     * @return an instance of the requested Class (a sub-class of Fragment)
     * (non-Javadoc)
     */
    private Fragment instantiateFragment(int fragmentClassNamesIndex) {

        // --- Get the name of the class to instantiate. ---
        String fragmentClassName = null;
        try {
            fragmentClassName = GlobalDefines.FRAGMENT_CLASS_NAMES[fragmentClassNamesIndex];
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Log.e(GlobalDefines.LOG_TAG,
                    this.getClass().getSimpleName() +
                    ": instantiateFragment(): passed in index not valid: " +
                    Integer.toString(fragmentClassNamesIndex)
            );
            // TODO pop up an error message to the user instead of exiting?
            // (shouldn't reach this point in production though)
            System.exit(GlobalDefines.EXIT_STATUS_ERROR);
        }

        // --- Create a new instance  of the indicated class. ---
        Object theInstance = null;
        if (fragmentClassName.equals("AppListFragment")) {
            theInstance = new AppListFragment();
        } else if (fragmentClassName.equals("PermListFragment")) {
            theInstance = new PermListFragment();
        }

        // Reflection code which doesn't quite work yet. To be used in the future.
        /*
        try {
            Class<?> classObj = Class.forName(fragmentClassName);
            Constructor<?> constructor = classObj.getConstructor();
            theInstance = constructor.newInstance();
        } catch (Exception e) {
            Log.e(GlobalDefines.LOG_TAG,
                    this.getClass().getSimpleName() +
                    ": instantiateFragment(): Class not recognized: " +
                    fragmentClassName +
                    " - " + e.getClass().getSimpleName()
            );
            // TODO pop up an error message to the user instead of exiting?
            System.exit(GlobalDefines.EXIT_STATUS_ERROR);
        }
         */

        return (Fragment) theInstance;
    }


    /*
     * Replaces the Fragment in the content View. Handles instantiating a new Fragment object if necessary.
     * (non-Javadoc)
     */
    private void swapContent(int position, long id) {
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": swapContent()");
        Log.d(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": swapContent(): position: " + Integer.toString(position) + "  -  id: " + Long.toString(id));

        boolean keepLastTransaction = true;
        boolean replaceFragment = true;
        Fragment removeMe = null;
        Fragment currentlyDisplayedFragment = this.fragmentManager.findFragmentById(R.id.content);

        // if the user chose a Fragment which hasn't been displayed yet...
        if (this.fragments[position] == null) {

            // ...create a new instance so it can be displayed.
            this.fragments[position] = this.instantiateFragment(position);

        // else if user selected the same menu item as what's displayed...
        } else if (currentlyDisplayedFragment == this.fragments[position]) {

            // TODO need to use reflection here 'cause if there's more than a few classes...
            // TODO or maybe define an interface with getTheDataAndDisplayIt() required.


            // ...show the progress bar, and...
            this.progressBar.setProgress(0);
            this.progressBar.setVisibility(View.VISIBLE);


            // ...update the Fragment's data/display and keep it there.
            if (currentlyDisplayedFragment instanceof AppListFragment) {
                ((AppListFragment)currentlyDisplayedFragment).getTheDataAndDisplayIt();
            }
            else if (currentlyDisplayedFragment instanceof PermListFragment) {
                ((PermListFragment)currentlyDisplayedFragment).getTheDataAndDisplayIt();
            }

            replaceFragment = false;

        // else if user selected something different (AND it's been displayed before)...
        } else if (currentlyDisplayedFragment != this.fragments[position]) {
            // ...keep a reference to the old one so it can be deleted.
            removeMe = this.fragments[position];
            // Create a new instance to be displayed.
            this.fragments[position] = this.instantiateFragment(position);
        }

        // if need to do a transaction...
        if (replaceFragment) {

            // === the FragmentTransaction ===
            FragmentTransaction transaction = this.fragmentManager.beginTransaction();
            if (removeMe != null) {
                transaction.remove(removeMe);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.content, this.fragments[position]);
            if (keepLastTransaction && transaction.isAddToBackStackAllowed()) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }

        // hide the slide-out menu
        this.drawerLayout.closeDrawer(this.slideOutList);
    }


    // -------------------------------------------------------------------------
    /*
     * Click listener for each item in the Navigation drawer
     * @see android.widget.AdapterView.OnItemClickListener
     * (non-Javadoc)
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            swapContent(position, id);
        }
    }
}
