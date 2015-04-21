package com.dingohub.Views.Activities.DevActivities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.Views.Adapters.UserRecycleAdapter;
import com.dingohub.hubbub.R;
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MatViewHubActivity extends BaseGoogleActivity{
	public static final String HUB_KEY = "HUB_KEY_TO_VIEW";
	public static final String TAG = "ViewEventActivity";

    TextView hub_name;
    TextView hub_location;
	TextView hub_num_following;
	TextView hub_about;
	TextView hub_num_events;

    ImageView hub_picture;

	Button bFollow;

    RecyclerView userRecyclerView;
    RecyclerView.Adapter userAdapter;
    RecyclerView.LayoutManager userLayoutManager;

    RecyclerView bubRecyclerView;
    RecyclerView.Adapter bubAdapter;
    RecyclerView.LayoutManager bubLayoutManager;


    ArrayList<HubUser> followers = new ArrayList<>();
    ArrayList<Bub> containedBubs = new ArrayList<>();

	Hub currentHub;
	HubUser curUser;
    boolean followingStatus = false;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_activity_view_hub);

        // Init the hub based on id passed by bundle
        // and returned results from database
        init_hub();

		// Init ui
		init_ui();
		
		// Set ui to event values
		set_ui();

        // Find if the current user if following the hub
	    followingStatus = searchFollowing();

		bFollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                // Determines whether the click is meant to follow
                // or unfollow based on current status
                if(followingStatus)
                    unfollow_hub();
                else
                    follow_hub();

			}});
		
	}


    private void init_hub(){
        Bundle bundle = getIntent().getExtras();
        curUser = HubDatabase.GetCurrentUser();

        // Get Event to View from event id passed
        if(bundle != null){
            String hubID = bundle.getString(HUB_KEY);
            currentHub = HubDatabase.GetHubFromId(hubID);
        } else {
            currentHub = new Hub();
            Log.e(Hubbub.TAG, "Hub Not found in database upon viewing call");
        }

        // Finding values for the users followed and the bubs within the hub

        //A function to query the users following the hub in the database
        followers = HubDatabase.GetFollowersFromHub(currentHub.id);
        // function to query the bubs in a hub
        containedBubs = HubDatabase.GetBubsFromHub(currentHub.id);



    }

	public void init_ui(){

        SetDrawerAsBackButton(false, 0);

        // Sets recycler for the Follower User Circles
            // Finding View
        userRecyclerView = (RecyclerView) findViewById(R.id.hubview_gridrecycleview_following);
        userRecyclerView.setHasFixedSize(true);

            // Setting layout Manager
        userLayoutManager = new GridLayoutManager(this, 3);
        userRecyclerView.setLayoutManager(userLayoutManager);

            // Setting adapter
        userAdapter = new UserRecycleAdapter(followers, getApplicationContext());
        userRecyclerView.setAdapter(userAdapter);

        // Sets the recycler for the Events associated with a hub
            // Finding View
        bubRecyclerView = (RecyclerView) findViewById(R.id.hubview_gridrecycleview_eventlist);

            // Setting layout Manager
        bubLayoutManager = new LinearLayoutManager(this);
        bubRecyclerView.setLayoutManager(bubLayoutManager);

             //Setting adapter to load designated data
        bubAdapter = new HubbubRecycleAdapter(getApplicationContext(), containedBubs, HubbubRecycleAdapter.NORMAL_BUB);
        bubRecyclerView.setAdapter(bubAdapter);

        // Sets standard assets
        hub_name = (TextView) findViewById(R.id.hubview_name);
        hub_location = (TextView) findViewById(R.id.bubview_textview_location);
        hub_about = (TextView) findViewById(R.id.bubview_textview_details);
        hub_num_events = (TextView) findViewById(R.id.hubview_label_eventlist);
        hub_num_following = (TextView) findViewById(R.id.hubview_label_following);
		bFollow = (Button) findViewById(R.id.hubview_button_follow);
        hub_picture = (ImageView) findViewById(R.id.bubview_image);

	}
	
	public void set_ui(){
        hub_name.setText(currentHub.name);
        hub_location.setText(currentHub.location);
        hub_about.setText(currentHub.details);
        hub_num_events.setText("Followers: " + Integer.toString(currentHub.follower_ids.length()));
        hub_num_following.setText("Bubs: " +  Integer.toString(currentHub.bubs.length()));

        if(currentHub.picture != null){
            BitmapWorker worker = new BitmapWorker(hub_picture, currentHub.picture, 250, 250);
            worker.execute(0);
        }


	}

    private void unfollow_hub(){
        ParsePush.unsubscribeInBackground(currentHub.id);
        deleteFollower();

        // TODO - Similarly a remove for hub's in users accounts
        HubDatabase.RemoveFollowerFromHub(currentHub.id, HubDatabase.GetCurrentUser().id);
        HubDatabase.RemoveFollowedHub(currentHub.id, HubDatabase.GetCurrentUser().id);
        bFollow.setText("Unfollow Hub");
    }

    private void follow_hub(){
        // TODO Auto-generated method stub
        // NEED A REMOVE FOLLOWER BUTTON!
        ParsePush.subscribeInBackground(currentHub.id);

        // TODO - Need database call to add a hub id to a user's account
        HubDatabase.AddFollowerToHub(currentHub.id, HubDatabase.GetCurrentUser().id);
        HubDatabase.AddFollowedHub(currentHub.id, HubDatabase.GetCurrentUser().id);
        bFollow.setText("Follow Hub");
    }

    // TODO - Not sure if correct method of finding user from hub
	private boolean searchFollowing(){
		String curId = curUser.id;
		
		for(int i = 0; i < currentHub.follower_ids.length(); ++i){
			try {
				String iUser = currentHub.follower_ids.getString(i);
				
				if(curId.equals(iUser))
					return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

    // TODO - Not sure if correct method of deleting user from hub
	private void deleteFollower(){
		String curId = curUser.id;
		JSONArray newJson = new JSONArray();
		
		for(int i = 0; i < currentHub.follower_ids.length(); ++i){
			try {
				String iUser = currentHub.follower_ids.getString(i);
				if(!curId.equals(iUser)){
					newJson.put(currentHub.follower_ids.get(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        currentHub.follower_ids = newJson;
	}
}