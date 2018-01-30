package com.dingohub.Views.Activities.DevActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.Views.Adapters.UserRecycleAdapter;
import com.dingohub.hubbub.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ereio on 4/18/15.
 */
public class UserProfileActivity extends BaseGoogleActivity{
    public static int FRIENDS_SHOWN = 5;

    HubUser user;
    ArrayList<HubUser> friends = new ArrayList<>();
    ArrayList<HubUser> viewerFriends = new ArrayList<>();
    ArrayList<Bub> userBubs = new ArrayList<>();

    RecyclerView friendRecycleView;
    UserRecycleAdapter adapter;

    RecyclerView bubRecyclerView;

    ImageView iProfilePic;
    ImageView iBackgroundPic;
    TextView tUsername;
    TextView tFullName;
    TextView tAbout;
    Button toggleFollow;

    HubUser viewedUser = new HubUser();

    private Toolbar toolbar;
    boolean followStatus = false;

    public UserProfileActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // init user info
        init_user_info();

        // initalizes the recyclers and adapters for Listeners
        init_ui();

        // initalizes the recyclers and adapters for Listeners
        init_adapters();
    }

    private void init_user_info(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            viewedUser.id = extras.getString(Hubbub.USER_VIEW_KEY, "");
        }

        if(!viewedUser.id.equals("")) {
            user = HubDatabase.GetUserById(viewedUser.id);
            friends = HubDatabase.GetFriends(viewedUser.id);
            viewerFriends = HubDatabase.GetFriends(HubDatabase.GetCurrentUser().id);
            userBubs = HubDatabase.GetBubsByFollower(viewedUser.id);

            for(int i = 0; i < friends.size(); i++)
                if(viewedUser.id.equals(friends.get(i).id))
                    followStatus = true;
        }

    }

    private void init_ui(){

        SetDrawerAsBackButton(false, 100);

        iProfilePic = (ImageView) findViewById(R.id.image_profile_picture);
        tUsername = (TextView) findViewById(R.id.text_username);
        tFullName = (TextView) findViewById(R.id.text_fullname);
        tAbout = (TextView) findViewById(R.id.cardview_text_about);
        toggleFollow = (Button) findViewById(R.id.button_toggle_friend);
        iBackgroundPic = (ImageView) findViewById(R.id.image_profile_background_picture);

        if(user.backgroundPicture != null){
            BitmapWorker worker = new BitmapWorker(iBackgroundPic, user.backgroundPicture, 600, 250);
            worker.execute(0);
        } else {
            Random rand = new Random();
            int index = rand.nextInt(Hubbub.polygons.length);
            iBackgroundPic.setImageDrawable(getApplicationContext().getResources().getDrawable(Hubbub.polygons[index]));
        }
        if(user.picture != null){
            BitmapWorker worker = new BitmapWorker(iProfilePic, user.picture, 250, 250);
            worker.execute(0);
        }

        tUsername.setText(user.username);
        tFullName.setText(user.firstname + " " + user.lastname);
        tAbout.setText(user.details);

        Random random = new Random();
        int color_index = random.nextInt(6);
        tAbout.setBackgroundColor(getResources().getColor(Hubbub.color_array[color_index]));

        toggleFollow.setText(followStatus ? "Unfollow" : "Follow");

        toggleFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!followStatus) {
                    HubDatabase.AddFriend(HubDatabase.GetCurrentUser().id, viewedUser.id);
                    toggleFollow.setText("Unfollow");
                    followStatus = true;
                } else {
                    HubDatabase.RemoveFriend(HubDatabase.GetCurrentUser().id, viewedUser.id);
                    toggleFollow.setText("Follow");
                    followStatus = false;
                }
            }
        });


    }

    // TODO - Implement the onPause call to save the hub data stored

    private void init_adapters(){

        // Setting the recycle view for the friends grid view
        friendRecycleView = (RecyclerView) findViewById(R.id.grid_recycle_view);
        friendRecycleView.setHasFixedSize(true);
        friendRecycleView.setItemAnimator(new DefaultItemAnimator());
        friendRecycleView.setLayoutManager(new GridLayoutManager(this, FRIENDS_SHOWN));
        friendRecycleView.addOnItemTouchListener(new UserRecyclerViewListener());

        adapter = new UserRecycleAdapter(friends, this);
        friendRecycleView.setAdapter(adapter);

        // Sets the recycler for the Events associated with a hub
        // Finding View
        bubRecyclerView = (RecyclerView) findViewById(R.id.userbub_recycler_view);
        bubRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bubRecyclerView.addOnItemTouchListener(new BubRecyclerViewListener());
        //Setting adapter to load designated data
        HubbubRecycleAdapter bubAdapter = new HubbubRecycleAdapter(getApplicationContext(), userBubs, HubbubRecycleAdapter.NORMAL_BUB);
        bubRecyclerView.setAdapter(bubAdapter);
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
                if(friends.size() != 0) {
                    String followerId = friends.get(rv.getChildPosition(child)).id;
                    // checks to make sure you're not recursively calling the followers
                    if(followerId.equals(viewedUser.id))
                        return false;
                    // creates a bundle with the friend id in the new fragment
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    intent.putExtra(Hubbub.USER_VIEW_KEY, followerId);
                    startActivity(intent);
                    finish();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // Checks to make sure a bub is there to even click
                if(userBubs.size() != 0) {
                    String eventId = userBubs.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getApplicationContext(), ViewBubActivity.class);
                    intent.putExtra(ViewBubActivity.EVENT_KEY, eventId);                    // MAKE Bub parcelable
                    startActivity(intent);
                    finish();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }


}
