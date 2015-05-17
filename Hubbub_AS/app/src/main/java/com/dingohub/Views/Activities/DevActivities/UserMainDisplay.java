package com.dingohub.Views.Activities.DevActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.DataAccess.SharedPrefKeys;
import com.dingohub.Views.Adapters.ProfileRecycleAdapter;
import com.dingohub.Views.Adapters.UserPanePagerAdapter;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.hubbub.R;

/**
 * Created by ereio on 3/30/15.
 */
public class UserMainDisplay extends BaseGoogleActivity {
        private static String TAG = "MatUserMainDisplay";

        // TODO - Merge BaseGoogleActivity composed with drawer
        // Drawer should be accessible on nearly all main user screens
        // Most activities won't have it, such as the CreateEvent
        // Should be available on the SearchEvent Activity however and thus
        // requires composition

        private Toolbar toolbar;
        private int TODAY_PANE = 1;

        // Current User Data
        HubUser user;
        SharedPreferences settings;

        RecyclerView profileRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
        DrawerLayout Drawer;                                  // Declaring DrawerLayout

        ActionBarDrawerToggle mDrawerToggle;

        // TODO - Should be using a standard fragment (LEGACY)
        /**
         * The {@link android.support.v4.view.PagerAdapter} that will provide
         * fragments for each of the sections. We use a {@link android.support.v13.app.FragmentPagerAdapter}
         * derivative, which will keep every loaded fragment in memory. If this
         * becomes too memory intensive, it may be best to switch to a
         * {@link android.support.v13.app.FragmentStatePagerAdapter}.
         */
        UserPanePagerAdapter mSectionsPagerAdapter;

        /**
         * The {@link android.support.v4.view.ViewPager} that will host the section contents.
         */
        ViewPager mViewPager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_main_display);

            // initalizes user details from database
            init_user();

            // initalize ui with user details
            init_ui();

            // initlaizes ui touch events on drawer objects
            init_touch_events();

            // TODO - Should be using a standard fragment (LEGACY)
            // Using this for now to render current views in material env
            init_pager_tabs();
        }

        @Override
        protected void onPause(){
            super.onPause();
        }

        private void init_user(){

            // init user object
            user = HubDatabase.GetCurrentUser();

            // Checks if activity reached without user data
            if(user == null){
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            // TODO - Create a worker to getLocality much like BitmapWorker
            user.location = getLocality();
            if(user.location == null) {
                settings = getSharedPreferences(SharedPrefKeys.LOGIN_SETTINGS, 0);
                user.location = settings.getString(SharedPrefKeys.LOC_KEY, null);
            }

        }

        private void init_ui(){
            toolbar = (Toolbar) findViewById(R.id.material_toolbar);
            toolbar.getBackground().setAlpha(255);
            setSupportActionBar(toolbar);

            profileRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
            profileRecyclerView.setHasFixedSize(true);

            mAdapter = new ProfileRecycleAdapter(user, getApplicationContext());

            profileRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
            profileRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


            Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
            mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar, R.string.openDrawer, R.string.app_name){

                @Override
                public void onDrawerOpened(View drawerView) { super.onDrawerOpened(drawerView); }

                @Override
                public void onDrawerClosed(View drawerView) { super.onDrawerClosed(drawerView); }

            }; // Drawer Toggle Object Made
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
        }

        private void init_touch_events() {
            // adds a touch listener for events within the profile drawer
            profileRecyclerView.addOnItemTouchListener( new ProfileRecyclerViewListener());

        }

        // TODO - Should be using a standard fragment (LEGACY)
        private void init_pager_tabs() {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new UserPanePagerAdapter(getFragmentManager(), user.username);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // Sets toolbar title
                    toolbar.setTitle(mSectionsPagerAdapter.getPageTitle(position));

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

           // Sets the Pages default
           mViewPager.setCurrentItem(TODAY_PANE);
        }

        private class ProfileRecyclerViewListener implements RecyclerView.OnItemTouchListener{
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());

                if(child!=null && mGestureDetector.onTouchEvent(e)){

                    if(Hubbub.DEBUG)
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " +
                            profileRecyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    Drawer.closeDrawers();

                    // TODO - Sets the pager, should instead just be a rendered fragment (LEGACY)
                    mViewPager.setCurrentItem(profileRecyclerView.getChildPosition(child));

                    // TODO - Instantiate a fragment on call instead of reference a pager
                    // Pagers were good for tabs but it's outdated layout design
                    // since we're using a main drawer for navigation we should instead
                    // use a fragment view to navigate through the user windows
                    switch(profileRecyclerView.getChildPosition(child)){
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        }

        @Override
        public void onConnected(Bundle arg0) {
            super.onConnected(arg0);

            // TODO - ERASE THIS, QUICK FIX FOR LOCATION DISPLAY
            // Location should be rendered in separate thread
            // TODO - Create seperate thread for updating events
            // Loc data is already fetched in an asynctask, but the update to the ui thread isn't
            TextView loc = (TextView) findViewById(R.id.text_user_location);
            loc.setText(getLocality());
            HubDatabase.SetLocation(getLocality());
            Log.i(Hubbub.TAG + TAG, "Locality found = " + getLocality());

        }
}
