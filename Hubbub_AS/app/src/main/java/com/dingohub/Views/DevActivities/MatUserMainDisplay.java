package com.dingohub.Views.DevActivities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dingohub.Views.BaseActivities.BaseGoogleActivity;
import com.dingohub.hubbub.R;

/**
 * Created by ereio on 3/30/15.
 */
public class MatUserMainDisplay extends BaseGoogleActivity {
        //First We Declare Titles And Icons For Our Navigation Drawer List View
        //This Icons And Titles Are holded in an Array as you can see

        String TITLES[] = {"Today","Bubs","Hubs","Groups","Friends", "Settings"};
        int ICONS[] = {R.drawable.ic_public_black_24dp, R.drawable.ic_event_note_black_24dp,
                       R.drawable.ic_layers_black_24dp, R.drawable.ic_group_work_black_24dp,
                       R.drawable.ic_group_black_24dp, R.drawable.ic_settings_black_24dp};

        //Similarly we Create a String Resource for the name and email in the header view
        //And we also create a int resource for profile picture in the header view

        String NAME = "Taylor Ereio";
        String EMAIL = "TaylorTEreio@gmail.com";
        int PROFILE = R.drawable.javarendereroutput;

        private Toolbar toolbar;                              // Declaring the Toolbar Object

        RecyclerView mRecyclerView;                           // Declaring RecyclerView
        RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
        RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
        DrawerLayout Drawer;                                  // Declaring DrawerLayout

        ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.material_activity_main);

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
            toolbar = (Toolbar) findViewById(R.id.material_toolbar);
            setSupportActionBar(toolbar);

            mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
            mRecyclerView.setHasFixedSize(true);
                                                                                         // Letting the system know that the list objects are of fixed size
            mAdapter = new ProfileRecycleAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,
                        getApplicationContext());       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
            // And passing the titles,icons,header view name, header view email,
            // and header view profile picture
            mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


            Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
            mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.app_name){

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                    // open I am not going to put anything here)
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    // Code here will execute once drawer is closed
                }



            }; // Drawer Toggle Object Made
            Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
}
