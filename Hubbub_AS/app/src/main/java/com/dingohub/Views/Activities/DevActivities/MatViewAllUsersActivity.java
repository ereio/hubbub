package com.dingohub.Views.Activities.DevActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Adapters.UserRecycleAdapter;
import com.dingohub.hubbub.R;

import java.util.ArrayList;

/**
 * Created by ereio on 4/18/15.
 */
public class MatViewAllUsersActivity extends BaseGoogleActivity{
    public static int FRIENDS_SHOWN = 5;

    public static String INVITE_KEY = "INVITE_KEY";

    private Toolbar toolbar;

    RecyclerView userRecycleView;
    UserRecycleAdapter adapter;

    Bub invitedBub = new Bub();
    ArrayList<HubUser> followedUsers = new ArrayList<>();


    public MatViewAllUsersActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_activity_view_all_followers);

        // init all nessesary bundle extras for invites
        init_invite_details();

        // initalizes the recyclers and adapters for Listeners
        init_ui();

        // initalizes the recyclers and adapters for Listeners
        init_adapters();
    }

    @Override
    public void onResume(){
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init_invite_details(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            invitedBub.id = extras.getString(INVITE_KEY, "");
        }

        followedUsers = HubDatabase.GetFriends(HubDatabase.GetCurrentUser().id);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }
    private void init_ui(){
        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Testing the touch", Toast.LENGTH_LONG).show();
            }
        });
        setSupportActionBar(toolbar);

    }

    // TODO - Implement the onPause call to save the hub data stored

    private void init_adapters(){
        // Setting the recycle view for the friends grid view
        userRecycleView = (RecyclerView) findViewById(R.id.view_all_followers_recycler_viewer);
        userRecycleView.setHasFixedSize(true);
        userRecycleView.setItemAnimator(new DefaultItemAnimator());
        userRecycleView.setLayoutManager(new GridLayoutManager(this, FRIENDS_SHOWN));
        userRecycleView.addOnItemTouchListener(new UserRecyclerViewListener());

        adapter = new UserRecycleAdapter(followedUsers, this);
        userRecycleView.setAdapter(adapter);
    }


    private class UserRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // Find the friend idea of the person clicked
                if(followedUsers.size() != 0) {
                    String followerId = followedUsers.get(rv.getChildPosition(child)).id;

                    // TODO - INVITE PEOPLE TO BUBS
                    // Send a push notification here
                    invite_friend(followerId);

                    // creates a bundle with the friend id in the new fragment
                    Intent intent = new Intent(getApplicationContext(), MatViewBubActivity.class);
                    intent.putExtra(MatViewBubActivity.EVENT_KEY, invitedBub.id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }

    private void invite_friend(String invitedFollower){

    }
}
