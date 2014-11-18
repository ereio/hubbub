package com.dingohub.hub_database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class HubDatabase {
	public static final String TAG = "HubDatabase";
	
	public static final int FLAG_NULL_QUERY = 1;
	public static final int FLAG_QUERY_FAILED = -1;
	public static final int FLAG_QUERY_SUCCESSFUL = 0;
	
	private static final String parse_app_id = "zSa7P3L6UeletXOg3ivSGyCkfLOy7NVQxPq4HshT";
	private static final String parse_client_key = "56gYe0e3MNH0sWPLKen0XD9kmf0BrtvbyyxyQuuN";
	
	public static final String USERS_TABLE = "_User";
	public static final String EVENTS_TABLE = "HubEvent";

	public static int status = FLAG_NULL_QUERY;
	
	
	// ADD'S FOLLOWER TO USER
	public static int AddFollower(String id, String id2) {
		// TODO Auto-generated method stub
		return 0;
	}

	//INITIALIZE DATABASE
	public static boolean init_db(Context c){
		try{
			Parse.initialize(c, parse_app_id, parse_client_key);
			
		} catch(Exception e){
			Log.e(TAG, e.toString());
			
			return false;
		}
		return true;
	}
	
	// CREATES USER IN DATABASE
	public static int CreateUser(HubUser newUser, String pass, String confirmPass, String dob){
		status = FLAG_NULL_QUERY;
		
		ParseUser user = new ParseUser();
		user.setUsername(newUser.username);
		user.setPassword(pass);
		user.setEmail(newUser.email);
		user.put("details", newUser.details);
		user.put("full_name", newUser.firstname + "_" + newUser.lastname);
		user.put("date_of_birth", dob);
		user.put("friends", new JSONArray());
		
		try {
			user.signUp();
			
			status = FLAG_QUERY_SUCCESSFUL;
			
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		} 
		
		return status;
	}
	
	// CREATES A BUB(EVENT) IN DATABASE
	public static int CreateBub(Bub bub){
		status = FLAG_NULL_QUERY;
		
			ParseObject event = new ParseObject(EVENTS_TABLE);
			
			event.put("title", bub.title);
			event.put("location", bub.location);
			//event.put("picture", bub.picture);
			event.put("details", bub.details);
			event.put("permission_level", bub.permissions);
			event.put("start_dt", bub.start_date + "T" + bub.start_time);
			event.put("end_dt", bub.end_date + "T" + bub.end_time);
			event.put("yes_counter", 0);
			event.put("maybe_counter", 0);
			event.put("no_counter", 0);
			event.put("followers", new JSONArray());
			event.put("tags", bub.tags);
			
			event.saveInBackground(new SaveCallback(){

				@Override
				public void done(ParseException e) {
					if(e == null){
						status = FLAG_QUERY_SUCCESSFUL;
					}
					else{
						status = FLAG_QUERY_FAILED;
						Log.e(TAG,e.toString());
					}
				}
				
			});
			
			return status;
		}
	
	//VALIDATES LOGIN INFORMATION
	public static int AuthoLogin(String username, String password){
		status = FLAG_NULL_QUERY;
		
		try {
			ParseUser.logIn(username, password);
			
			status = FLAG_QUERY_SUCCESSFUL;
			
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}
		
		return status;
	}
	
	public static HubUser getCurrentUser(){
		ParseUser cur_user = ParseUser.getCurrentUser();
		
		if(cur_user != null)
			return GetUserFromDBObj(cur_user);
		else
			return null;
	}
	
	//FIND EVENTS BY DATE - CAN FIND ANY INCLUDING CURRENT DATE BUB DISPLAY
	public static ArrayList<Bub> FindEventByDate(String date){
		
		ArrayList<Bub> events = new ArrayList<Bub>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		query.whereContains("start_date", date);
		
		try{
			List<ParseObject> obj_list = query.find();
			for(ParseObject obj: obj_list){
				events.add(GetBubFromDBObj(obj));
			}
			status = FLAG_QUERY_SUCCESSFUL;
			
		} catch (ParseException e){
			status = FLAG_QUERY_FAILED;
			events = null;
			Log.e(TAG,e.toString());
		}
		return events;
	}

	
	// Converts ParseObject to HubUser
	@SuppressWarnings("unused")
	private static HubUser GetUserFromDBObj(ParseObject db_usr){
		HubUser usr = new HubUser();

		usr.id = db_usr.getObjectId();
		usr.username = db_usr.getString("username");
		usr.email = db_usr.getString("email");
		usr.details = db_usr.getString("details");
		String[] name = db_usr.getString("full_name").split("_");
		usr.firstname = name[0];
		usr.lastname = name[1];
		usr.friend_ids = db_usr.getJSONArray("friends");
		
		//usr.picture = db_usr.getBytes("picture");
		
		return usr;
	}
	

	// Converts ParseObject to Bub
	private static Bub GetBubFromDBObj(ParseObject db_event){
		Bub event = new Bub();
		
		event.id = db_event.getObjectId();
		event.title = db_event.getString("title");
		event.location = db_event.getString("location");
		
		//event.picture = db_event.getBytes("picture");
		
		event.details = db_event.getString("details");
		event.permissions = db_event.getString("permissions");
		
		String[] start_dt = db_event.getString("start_dt").split("T");
		event.start_date = start_dt[0];
		event.start_time = start_dt[1];
		
		String[] end_dt = db_event.getString("end_dt").split("T");
		event.end_date = end_dt[0];
		event.end_time = end_dt[1];
		
		event.yes_counter = db_event.getInt("yes_counter");
		event.maybe_counter = db_event.getInt("maybe_counter");
		event.no_counter = db_event.getInt("no_counter");
		
		event.follower_ids = db_event.getJSONArray("followers");
		
		return event;
	}
			
}
