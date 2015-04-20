package com.dingohub.Views.Deprecated;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.hubbub.R;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.parse.ParsePush;

public class ViewEventActivity extends Activity{
	public static final String EVENT_KEY = "asduiuneukluhasd";
	public static final String TAG = "ViewEventActivity";
	TextView event_name;
	TextView event_location;
	TextView event_start_time;
	TextView event_end_time;
	TextView event_start_date;
	TextView event_end_date;
	TextView event_num_following;
	TextView event_tappin;
	TextView event_details;
	TextView event_tags;
	TextView event_following;
	TextView event_numfollowers;
	
	TextView event_num_yes;
	TextView event_num_maybe;
	TextView event_num_no;
	TextView event_yes;
	TextView event_no;
	TextView event_maybe;
	
	ImageView event_picture;
	
	Button bFollow;
	Button bUnfollow;

	Button bEdit;

    Bub event;
	HubUser curUser;
	ArrayList<String> tagList;
	ScheduledExecutorService ses;
	
	@Override
	public void onStop(){
		super.onStop();
		while(!ses.isShutdown())
			ses.shutdown();
		finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_event);
	
		// Init ui
		init_ui();
		
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
		
		// Set ui to event values
		set_ui();
		
		if(searchFollowing()){
			bFollow.setVisibility(View.INVISIBLE);
			bUnfollow.setVisibility(View.VISIBLE);
		}
		else{
			bFollow.setVisibility(View.VISIBLE);
			bUnfollow.setVisibility(View.INVISIBLE);
		}
		
		long sysBootTime = System.currentTimeMillis();
		long testing = sysBootTime - pingInTimeInMillis();
		
		Log.i("SYSTEM TIME",DateFormat.getDateTimeInstance().toString());
		Log.i("DIFFERENCE", "" + testing);
		Log.i("SYSTEM TIME LONG", "" + sysBootTime);
		Log.i("TAPP TIME LONG", "" + pingInTimeInMillis());
		
		if(pingInTimeInMillis() < sysBootTime){
			event_tappin.setText("STARTED!!");
			event_num_yes.setVisibility(View.VISIBLE);
			event_num_no.setVisibility(View.VISIBLE);
			event_num_maybe.setVisibility(View.VISIBLE);
			event_yes.setVisibility(View.VISIBLE);
			event_no.setVisibility(View.VISIBLE);
			event_maybe.setVisibility(View.VISIBLE);
			check_pingin(event.id);
		}
		else{
			ses = Executors.newSingleThreadScheduledExecutor();
			event_num_yes.setVisibility(View.INVISIBLE);
			event_num_no.setVisibility(View.INVISIBLE);
			event_num_maybe.setVisibility(View.INVISIBLE);
			event_yes.setVisibility(View.INVISIBLE);
			event_no.setVisibility(View.INVISIBLE);
			event_maybe.setVisibility(View.INVISIBLE);
		}
		
		bFollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// NEED A REMOVE FOLLOWER BUTTON!
				ParsePush.subscribeInBackground(event.id);
				HubDatabase.AddFollower(event.id, HubDatabase.GetCurrentUser().id);
                HubDatabase.AddFollowedBub(event.id, HubDatabase.GetCurrentUser().id);
				v.setVisibility(View.INVISIBLE);
				bUnfollow.setVisibility(View.VISIBLE);
			}});
		
		bUnfollow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ParsePush.unsubscribeInBackground(event.id);
				deleteFollower();
				HubDatabase.RemoveFollower(event);
                HubDatabase.RemoveFollowedBub(event.id, HubDatabase.GetCurrentUser().id);
				v.setVisibility(View.INVISIBLE);
				bFollow.setVisibility(View.VISIBLE);
			}});
		
		
		
	}
	
	public void check_pingin(String id){
		final String EID = id;
		ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				final Bub counterEvent = HubDatabase.GetBubsById(EID);
				
				event_num_yes.post(new Runnable(){
					@Override
					public void run() {
						event_num_yes.setText(Integer.toString(counterEvent.yes_counter)); 
					}
				});
				event_num_no.post(new Runnable(){
					@Override
					public void run() {
						event_num_no.setText(Integer.toString(counterEvent.no_counter)); 
					}
				});
				event_num_maybe.post(new Runnable(){
					@Override
					public void run() {
						event_num_maybe.setText(Integer.toString(counterEvent.maybe_counter));
					}
				});
			}
			
		}, 0, 5, TimeUnit.SECONDS);
	}
	
	public void init_ui(){
        event_name = (TextView) findViewById(R.id.eventview_name);
        event_location = (TextView) findViewById(R.id.eventlist_location);
        event_start_time = (TextView) findViewById(R.id.eventlist_start_time);
        event_end_time = (TextView) findViewById(R.id.eventlist_end_time);
        event_start_date = (TextView) findViewById(R.id.eventlist_start_date);
        event_end_date = (TextView) findViewById(R.id.eventlist_end_date);
        event_tappin = (TextView) findViewById(R.id.eventview_tappin_time);
        event_tags = (TextView) findViewById(R.id.eventview_tags);
        event_following = (TextView) findViewById(R.id.eventview_following_list);
        event_details = (TextView) findViewById(R.id.cardview_text_about);
        event_numfollowers = (TextView) findViewById(R.id.eventlist_numfollowers);

        event_num_yes = (TextView) findViewById(R.id.eventview_num_yes);
        event_num_maybe = (TextView) findViewById(R.id.eventview_num_maybe);
        event_num_no = (TextView) findViewById(R.id.eventview_num_no);
        event_yes = (TextView) findViewById(R.id.eventview_yes);
        event_no = (TextView) findViewById(R.id.eventview_no);
        event_maybe = (TextView) findViewById(R.id.eventview_maybe);

        bFollow = (Button) findViewById(R.id.eventview_button_follow);
        bUnfollow = (Button) findViewById(R.id.eventview_button_unfollow);

        event_picture = (ImageView) findViewById(R.id.bubview_image);
	}
	
	public void set_ui(){
		if(event.title != null)
			event_name.setText(event.title);
		
		if(event.location != null)
			event_location.setText(event.location);
		
		if(event.start_time != null)
			event_start_time.setText(event.start_time);
		
		if(event.start_date != null)
			event_start_date.setText(event.start_date);
		
		if(event.end_time != null)
			event_end_time.setText(event.end_time);
		
		if(event.end_date != null)
			event_end_date.setText(event.end_date);
		
		if(event.pingIn_time != 1)
			event_tappin.setText(event.pingIn_time + " hours before " + event.start_time);
		else
			event_tappin.setText(event.pingIn_time + " hour before " + event.start_time);
		
		// Parse JSON!!!
		if(event.tags != null)
			event_tags.setText(getTags());
		
		if(event.details != null)
			event_details.setText(event.details);
		
		if(event.follower_ids != null)
			event_following.setText(getFollowerNames());
			
		
		if(event.follower_ids != null)
			event_numfollowers.setText("" + event.follower_ids.length());
		
		event_num_yes.setText(Integer.toString(event.yes_counter));
		event_num_maybe.setText(Integer.toString(event.maybe_counter));
		event_num_no.setText(Integer.toString(event.no_counter));
		
		if(event.picture != null){
			BitmapWorker worker = new BitmapWorker(event_picture, event.picture, 250, 250);
			worker.execute(0);
		}
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
	
	private String getFollowerNames(){
		String print = new String("");
		String curId = new String();
		try {
			
		for(int i = 0; i < event.follower_ids.length(); i++){
				curId = new String(event.follower_ids.getString(i));
			if(i > 0){
				print = new String(print + ", " + HubDatabase.GetUserById(curId).username);
			} else{
				print = new String(HubDatabase.GetUserById(curId).username);
			}
		}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return print;
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
		
		Log.i("CreateEvent",DateFormat.getDateTimeInstance().format(cal.getTime()));
		Log.i("CreateEvent","Tapp in: "+tappInVal+" hrs before");
		long result = cal.getTimeInMillis();
		return result;
	}
}