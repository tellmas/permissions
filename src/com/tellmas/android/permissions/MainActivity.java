package com.tellmas.android.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tellmas.android.permissions.AppListFragment.AppListFragmentListener;

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
    private int currentlyDisplayedFragmentIndex;

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
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fragmentManager = this.getFragmentManager();

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

        // === Set up the starting Fragment. ===
        // if a Fragment is indicated to be restored...
        if (savedInstanceState != null) {

            this.currentlyDisplayedFragmentIndex = savedInstanceState.getInt(GlobalDefines.BUNDLE_KEY_FRAGMENT_INDEX);
            this.fragments[this.currentlyDisplayedFragmentIndex] =
                    this.fragmentManager.findFragmentByTag(savedInstanceState.getString(GlobalDefines.BUNDLE_KEY_FRAGMENT_TAG));

            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, this.fragments[this.currentlyDisplayedFragmentIndex], new Time().toString());
            fragmentTransaction.commit();

            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate(): saved Fragment index: " + Integer.toString(savedInstanceState.getInt(GlobalDefines.BUNDLE_KEY_FRAGMENT_INDEX)));
            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate(): saved Fragment tag: " + savedInstanceState.getString(GlobalDefines.BUNDLE_KEY_FRAGMENT_TAG));
            Log.v(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onCreate(): Fragment has retain instance set: " + Boolean.toString(this.fragments[this.currentlyDisplayedFragmentIndex].getRetainInstance()));

        // ...else create the default starting Fragment...
        } else {
            this.fragments[GlobalDefines.STARTING_FRAGMENT_INDEX] = this.instantiateFragment(GlobalDefines.STARTING_FRAGMENT_INDEX);
            this.currentlyDisplayedFragmentIndex = GlobalDefines.STARTING_FRAGMENT_INDEX;

            // === insert into the content frame the default starting Fragment ===
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content, this.fragments[this.currentlyDisplayedFragmentIndex], new Time().toString());
            fragmentTransaction.commit();
        }
    }


    /**
     * Tells the current Fragment to turn off retaining its state since it only needs that if this Activity was recreated.
     *
     * @param savedInstanceState data used to restore the previous state
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);

        this.fragments[currentlyDisplayedFragmentIndex].setRetainInstance(false);
    }


    /**
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onPostCreate()");
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        this.drawerToggle.syncState();
    }


    /**
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onOptionsItemSelected()");

        // if ActionBarDrawerToggle returns true...
        if (this.drawerToggle.onOptionsItemSelected(item)) {
            // ...then it has handled the app icon touch event.
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Saves the current Fragment's tag and index in case currently undergoing a configuration change.
     *
     * @param outState the Bundle in which to store the data
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        // Save the tag and index of the current Fragment in case we are doing a configuration change.
        //    (and want to restore the Fragment easily)
        Fragment fragment = this.fragments[currentlyDisplayedFragmentIndex];
        outState.putString(GlobalDefines.BUNDLE_KEY_FRAGMENT_TAG, fragment.getTag());
        outState.putInt(GlobalDefines.BUNDLE_KEY_FRAGMENT_INDEX, this.currentlyDisplayedFragmentIndex);

        fragment.setRetainInstance(true);
    }

    /* ******************* Unused lifecycle methods *********************** */
    @Override
    protected void onStart() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onStart()");
        super.onStart();
    }
    @Override
    protected void onResume() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onResume()");
        super.onResume();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onWindowFocusChanged(): has focus: " + Boolean.toString(hasFocus));
        super.onWindowFocusChanged(hasFocus);
    }
    @Override
    protected void onPause() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onPause()");
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onStop()");
        super.onStop();
    }
    @Override
    protected void onRestart() {
        Log.i(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": onRestart()");
        super.onRestart();
    }
    /* ****************** END Unused lifecycle methods ******************** */


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
    public void setFinished(int numOfItems, int itemType) {

        // TODO: find a better way to handle the screen re-orientation
        try {
            this.progressBar.setVisibility(View.GONE);

            // TODO: convert this to be more automatic
            TextView numOfItemsLabel = (TextView) this.findViewById(R.id.number_of_apps_label);
            if (itemType == GlobalDefines.LIST_TYPE_APPS) {
                numOfItemsLabel.setText(getResources().getString(R.string.number_of_apps));
            } else if (itemType == GlobalDefines.LIST_TYPE_PERMS) {
                numOfItemsLabel.setText(getResources().getString(R.string.number_of_perms));
            }
            numOfItemsLabel.setVisibility(View.VISIBLE);

            TextView numOfItemsInList = (TextView) this.findViewById(R.id.number_of_items_num);
            numOfItemsInList.setText(Integer.toString(numOfItems));
            numOfItemsInList.setVisibility(View.VISIBLE);

        // if couldn't find the Views...
        } catch (NullPointerException npe) {
            Log.w(GlobalDefines.LOG_TAG, this.getClass().getSimpleName() + ": setFinished(): NPE when getting Views. Screen orientation probably changed.");
        }
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

        boolean keepLastTransaction = true;
        boolean replaceFragment = true;
        Fragment removeMe = null;
        Fragment currentlyDisplayedFragment = this.fragmentManager.findFragmentById(R.id.content);
        this.currentlyDisplayedFragmentIndex = position;

        // --- Show the Progress bar again. ---
        this.progressBar.setProgress(0);
        this.progressBar.setVisibility(View.VISIBLE);

        // --- Hide the number of items. ---
        TextView numOfItemsNum = (TextView) findViewById(R.id.number_of_items_num);
        numOfItemsNum.setVisibility(View.INVISIBLE);


        // if the user chose a Fragment which hasn't been displayed yet...
        if (this.fragments[position] == null) {

            // --- Hide the text label describing the type of items. ---
            TextView numOfItemsText = (TextView) findViewById(R.id.number_of_apps_label);
            numOfItemsText.setVisibility(View.INVISIBLE);

            // ...create a new instance so it can be displayed.
            this.fragments[position] = this.instantiateFragment(position);

        // else if user selected the same menu item as what's displayed...
        } else if (currentlyDisplayedFragment == this.fragments[position]) {

            // TODO need to use reflection here 'cause if there's more than a few classes...
            // TODO or maybe define an interface with getTheDataAndDisplayIt() required.

            // Update the Fragment's data/display and keep it there.
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

            // --- Hide the text label describing the type of items. ---
            TextView numOfItemsText = (TextView) findViewById(R.id.number_of_apps_label);
            numOfItemsText.setVisibility(View.INVISIBLE);
        }

        // if need to do a transaction...
        if (replaceFragment) {

            // === the FragmentTransaction ===
            FragmentTransaction transaction = this.fragmentManager.beginTransaction();
            if (removeMe != null) {
                transaction.remove(removeMe);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.content, this.fragments[position], new Time().toString());
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
