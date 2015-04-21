package com.dingohub.Views.Activities.DevActivities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.dingohub.Views.Adapters.UserRecycleAdapter;
import com.dingohub.hubbub.R;
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatViewBubActivity extends BaseGoogleActivity{
	public static final String EVENT_KEY = "asduiuneukluhasd";
	public static final String TAG = "ViewEventActivity";

	TextView event_name;
    TextView event_details;
    TextView event_tags;
    TextView event_following;

	TextView event_location;
	TextView event_start_time;
	TextView event_end_time;
	TextView event_tappin;

	TextView event_numfollowers;
	
	TextView event_num_yes;
	TextView event_num_maybe;
	TextView event_num_no;
	TextView event_yes;
	TextView event_no;
	TextView event_maybe;
	
	ImageView event_picture;
	
	Button bFollow;
    Button bInvite;
	Button bEdit;

	Bub event;
	HubUser curUser;

	ArrayList<String> tagList;
    ArrayList<HubUser> followers = new ArrayList<>();

    RecyclerView userRecyclerView;
    RecyclerView.Adapter userAdapter;
    RecyclerView.LayoutManager userLayoutManager;

    DrawerLayout mDrawerLayout;

	ScheduledExecutorService ses;

    private boolean followingStatus = false;

	@Override
	public void onStop(){
		super.onStop();
		while(ses != null && !ses.isShutdown())
			ses.shutdown();
		finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_activity_view_bub);

        // Sets required values for the hub
        init_bub();

		// Init ui
		init_ui();
		
		// Set ui to event values
		set_ui();

        check_ping_in();

	}

    private void check_ping_in(){
        long sysBootTime = System.currentTimeMillis();

        if(Hubbub.DEBUG) {
            long testing = sysBootTime - pingInTimeInMillis();

            Log.i("SYSTEM TIME", DateFormat.getDateTimeInstance().toString());
            Log.i("DIFFERENCE", "" + testing);
            Log.i("SYSTEM TIME LONG", "" + sysBootTime);
            Log.i("TAPP TIME LONG", "" + pingInTimeInMillis());
        }

        if(pingInTimeInMillis() < sysBootTime){
            event_tappin.setText("Ping-In Happening Now");
            ses = Executors.newSingleThreadScheduledExecutor();
            event_yes.setVisibility(View.VISIBLE);
            event_no.setVisibility(View.VISIBLE);
            event_maybe.setVisibility(View.VISIBLE);
            PingInUpdater(event.id);
        }
        else{
            event_yes.setVisibility(View.INVISIBLE);
            event_no.setVisibility(View.INVISIBLE);
            event_maybe.setVisibility(View.INVISIBLE);
        }

    }
    private void init_bub(){
        Bundle bundle = getIntent().getExtras();
        curUser = HubDatabase.GetCurrentUser();

        // Get Event to View from event id passed
        if(bundle != null){
            String eventID = bundle.getString(EVENT_KEY);
            event = HubDatabase.GetBubsById(eventID);
        }
        else{
            event = new Bub();
            Toast.makeText(getApplicationContext(), "ERROR! Event not found!", Toast.LENGTH_SHORT).show();
        }
    }

	public void PingInUpdater(String id){
		final String EID = id;
		ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				final Bub counterEvent = HubDatabase.GetBubsById(EID);
				
				event_num_yes.post(new Runnable(){
					@Override
					public void run() {
						event_num_yes.setText("Going: " + Integer.toString(counterEvent.yes_counter));
					}
				});
				event_num_no.post(new Runnable(){
					@Override
					public void run() {
						event_num_no.setText("Not Attending: " + Integer.toString(counterEvent.no_counter));
					}
				});
				event_num_maybe.post(new Runnable(){
					@Override
					public void run() {
						event_num_maybe.setText("Maybe " + Integer.toString(counterEvent.maybe_counter));
					}
				});
			}
			
		}, 0, 5, TimeUnit.SECONDS);
	}
	
	public void init_ui(){

        SetDrawerAsBackButton(true, 0);
        reset_elevation();

        event_name = (TextView) findViewById(R.id.bubview_textview_name);
        event_location = (TextView) findViewById(R.id.bubview_textview_location);
        event_details = (TextView) findViewById(R.id.bubview_textview_details);

        event_start_time = (TextView) findViewById(R.id.bubview_start_info);
        event_end_time = (TextView) findViewById(R.id.bubview_end_info);

        event_tappin = (TextView) findViewById(R.id.bubview_ping_in_time);
        event_tags = (TextView) findViewById(R.id.bubview_textview_tags);

        event_following = (TextView) findViewById(R.id.bubview_textview_followers);

        event_yes = (TextView) findViewById(R.id.bubview_yes);
        event_no = (TextView) findViewById(R.id.bubview_no);
        event_maybe = (TextView) findViewById(R.id.bubview_maybe);

        bFollow = (Button) findViewById(R.id.bubview_button_follow);
        bInvite = (Button) findViewById(R.id.bubview_button_invite);

        event_picture = (ImageView) findViewById(R.id.bubview_image);
        userRecyclerView = (RecyclerView) findViewById(R.id.bubview_gridrecycleview_following);

	}

    @TargetApi(21)
    private void reset_elevation(){
        toolbar.setElevation(2f);
        setSupportActionBar(toolbar);
    }
	
	public void set_ui(){

        event_name.setText(event.title);
        event_location.setText(event.location);
        event_start_time.setText(event.start_time + " on " + event.start_date);
        event_end_time.setText("Ends at " + event.end_time + " on " + event.end_date);
		
		if(event.pingIn_time != 1)
			event_tappin.setText(event.pingIn_time + " hours before " + event.start_time);
		else
			event_tappin.setText(event.pingIn_time + " hour before " + event.start_time);

        event_tags.setText(getTags());

		if(!event.details.equals(""))
			event_details.setText(event.details);

        event_following.setText("Following: " + event.follower_ids.length());


        event_yes.setText("Going: " + Integer.toString(event.yes_counter));
        event_no.setText("Not Attending: " + Integer.toString(event.maybe_counter));
        event_maybe.setText("Maybe " + Integer.toString(event.no_counter));
		
		if(event.picture != null){
			BitmapWorker worker = new BitmapWorker(event_picture, event.picture, 250, 250);
			worker.execute(0);
		}

        // Sets recycler for the Follower User Circles
            // Finding View
        userRecyclerView.setHasFixedSize(true);

            // Setting layout Manager
        userLayoutManager = new GridLayoutManager(this, 5);
        userRecyclerView.setLayoutManager(userLayoutManager);

            // Setting adapter
        userAdapter = new UserRecycleAdapter(followers, getApplicationContext());
        userRecyclerView.setAdapter(userAdapter);

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

        bInvite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MatViewAllUsersActivity.class);
                intent.putExtra(MatViewAllUsersActivity.INVITE_KEY, event.id);
                startActivity(intent);
            }
        });
	}


    private void unfollow_hub(){
        ParsePush.unsubscribeInBackground(event.id);
        deleteFollower();

        // TODO - Similarly a remove for hub's in users accounts
        HubDatabase.RemoveFollower(event);
        HubDatabase.RemoveFollowedBub(event.id, HubDatabase.GetCurrentUser().id);
        bFollow.setText("Unfollow Bub");
        bFollow.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
        followingStatus = false;
        bInvite.setEnabled(false);
    }

    private void follow_hub(){
        ParsePush.subscribeInBackground(event.id);

        // TODO - Need database call to add a hub id to a user's account
        HubDatabase.AddFollower(event.id, HubDatabase.GetCurrentUser().id);
        HubDatabase.AddFollowedBub(event.id, HubDatabase.GetCurrentUser().id);
        bFollow.setText("Follow Hub");
        bFollow.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
        followingStatus = true;
        bInvite.setEnabled(true);
    }


    private boolean searchFollowing(){
		String curId = curUser.id;
		
		for(int i = 0; i < event.follower_ids.length(); ++i){
			try {
				String iUser = event.follower_ids.getString(i);
				
				if(curId.equals(iUser))
					return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public String getTags(){
		String print = new String("");
		try {
			
		for(int i = 0; i < event.tags.length(); i++){
			if(i > 0){
				print = new String(print + ", "+ event.tags.getString(i));
			} else{
				print = new String(event.tags.getString(i));
			}
		}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return print;
	}
	private void deleteFollower(){
		String curId = curUser.id;
		JSONArray newJson = new JSONArray();
		
		for(int i = 0; i < event.follower_ids.length(); ++i){
			try {
				String iUser = event.follower_ids.getString(i);
				if(!curId.equals(iUser)){
					newJson.put(event.follower_ids.get(i));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		event.follower_ids = newJson;
	}
	
	////////////////////////////////////////////////////////////////
	// Get Ping in system time - MODDED
	///////////////////////////////////////////////////////////////
	private long pingInTimeInMillis(){
		String[] stime = event.start_time.split("[: ]+");
		String[] sdate = event.start_date.split("/");
		
		int tappInVal = event.pingIn_time;
		
		int[] tt = new int[5];
		//year
		tt[0] = Integer.parseInt(sdate[2].trim());
		//month (Jan = 0)
		tt[1] = Integer.parseInt(sdate[1].trim())-1;
		//day
		tt[2] = Integer.parseInt(sdate[0].trim());
		
		//hour
		if(stime[2].trim().equals("PM"))
			tt[3] = Integer.parseInt(stime[0].trim()) + 12;
		else
			tt[3] = Integer.parseInt(stime[0].trim());
		
		//minutes
		tt[4] = Integer.parseInt(stime[1].trim());
		
		Calendar cal = Calendar.getInstance();

		cal.set(tt[0],tt[1],tt[2],tt[3],tt[4],0);
		cal.add(Calendar.HOUR_OF_DAY, - tappInVal);
		
		Log.i("SomethingElse",DateFormat.getDateTimeInstance().format(cal.getTime()));
		Log.i("SomethingElse","Tapp in: "+tappInVal+" hrs before");
		long result = cal.getTimeInMillis();
		return result;
	}
}