package com.dingohub.hub_database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.util.Log;










import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class HubDatabase {
	public static final String TAG = "HubDatabase";
	
	public static final int FLAG_NULL_QUERY = 1;
	public static final int FLAG_QUERY_FAILED = -1;
	public static final int FLAG_QUERY_SUCCESSFUL = 0;
	
	public static final int RESPONSE_YES = 1;
	public static final int RESPONSE_MAYBE = 0;
	public static final int RESPONSE_NO = -1;
	
	private static final String parse_app_id = "zSa7P3L6UeletXOg3ivSGyCkfLOy7NVQxPq4HshT";
	private static final String parse_client_key = "56gYe0e3MNH0sWPLKen0XD9kmf0BrtvbyyxyQuuN";
	
	public static final String USERS_TABLE = "_User";
	public static final String EVENTS_TABLE = "HubEvent";

	public static int status = FLAG_NULL_QUERY;
	
	public static final String EVENT = "HubEvent";
	public static final String _USERS = "_User";
	
	
	// ADD'S FOLLOWER TO USER
	public static int AddFollower(String event_id, String follower_id){
		//temp_id = follower_id;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENT);
		
		try {
			ParseObject object = query.get(event_id);
			
			JSONArray follower_array = object.getJSONArray("followers");
			follower_array.put(follower_id);
			object.put("followers", follower_array);
			object.increment("num_following");
			object.save();
			
			status = FLAG_QUERY_SUCCESSFUL;
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}
		
		return status;
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
	public static String CreateBub(Bub bub){
		status = FLAG_NULL_QUERY;
		
			ParseObject event = new ParseObject(EVENTS_TABLE);
			String id = ParseUser.getCurrentUser().getObjectId();
			event.put("title", bub.title);
			event.put("location", bub.location);
			event.put("event_creator_id", id);
			//gave picture the title change later to picture title
			//ParseFile e_picture = new ParseFile(bub.title,bub.picture);
			//event.put("picture", e_picture);
			event.put("details", bub.details);
			event.put("permission_level", bub.permissions);
			event.put("start_dt", bub.start_date + "T" + bub.start_time);
			event.put("end_dt", bub.end_date + "T" + bub.end_time);
			event.put("start_date", bub.start_date);
			event.put("end_date", bub.end_date);
			event.put("yes_counter", 0);
			event.put("maybe_counter", 0);
			event.put("no_counter", 0);
			event.put("followers", new JSONArray());
			event.put("tags", bub.tags);
			
		

				try{
					event.save();
					
					status = FLAG_QUERY_SUCCESSFUL;
					
				} catch(ParseException e){
					status = FLAG_QUERY_FAILED;
					Log.e(TAG,e.toString());
				}
			
			return event.getObjectId();
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
	
	
	//////////////////////////////////////////////
	// Get a user using an objectId
	// Returns:
	//   -populated TappUser if successful
	//   -null if failed
	//////////////////////////////////////////////
	public static HubUser GetUserById(String id){
		HubUser usr = new HubUser();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(_USERS);
		
		try {
			ParseObject db_usr = query.get(id);
		
			usr = GetUserFromDBObj(db_usr);
			
			status = FLAG_QUERY_SUCCESSFUL;
		
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			usr = null;
			Log.e(TAG,e.toString());
		}
		
		return usr;
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
	
/////////////////////////////////////////////////////////////
// Update event details
// Pass null for detail to remain unchanged
////////////////////////////////////////////////////////////
	public static int UpdateEvent(Bub event, String pic_name){
	ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENT);

	try {
		ParseObject object = query.get(event.id);
		if(event.title != null){
			object.put("title",event.title);
		}
		if(event.location != null){
			object.put("location",event.location);
		}

		if(event.start_date != null || event.start_time != null){
			if(event.start_date == null){
				String sd = object.getString("start_dt").split("T")[0];
				object.put("start_dt",sd+"T"+event.start_time);
			}
			else if(event.start_time == null){
				String st = object.getString("start_dt").split("T")[1];
				object.put("start_dt",event.start_date+"T"+st);
			}
			else{
				object.put("start_dt",event.start_date+"T"+event.start_time);
			}
		}
	if(event.end_date != null || event.end_time != null){
		if(event.end_date == null){
			String ed = object.getString("end_dt").split("T")[0];
			object.put("end_dt",ed+"T"+event.end_time);
		}
		else if(event.end_time == null){
			String et = object.getString("end_dt").split("T")[1];
			object.put("end_dt",event.end_date+"T"+et);
		}
		else{
			object.put("end_dt",event.end_date+"T"+event.end_time);
		}
	}

	if(event.follower_ids != null)
		object.put("followers", event.follower_ids);

	if(pic_name != null){
	ParseFile picture = new ParseFile(pic_name, event.picture);
	object.put("picture", picture);
	}

	if(event.details != null){
		object.put("details",event.details);
	}
	if(event.permissions != null){
		object.put("permisions",event.permissions);
	}
	if(event.tags != null){
		object.put("tags",event.tags);
	}
	object.save();

	status = FLAG_QUERY_SUCCESSFUL;
	} catch (ParseException e) {
		status = FLAG_QUERY_FAILED;
		Log.e(TAG,e.toString());
	}

	return status;
}
	
	
	////////////////////////////////////////////////
	// Get an event using an objectId
	// Returns:
	//   -populated TappEvent if successful
	//   -null if error occurred
	////////////////////////////////////////////////
	public static Bub GetEventById(String id){
		Bub event = new Bub();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE );
		
		try {
			ParseObject db_event = query.get(id);
			
			event = GetBubFromDBObj(db_event);
			
			status = FLAG_QUERY_SUCCESSFUL;
			
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			event = null;
			Log.e(TAG,e.toString());
		}
		
		return event;
	}
	
	
	
	
///////////////////////////////////////////////////////////////////
// Increment appropriate counter in event.
// Returns:
//   FLAG_QUERY_SUCCESSFUL if friends added successfully
//   FLAG_QUERY_FAILED if insertion failed
///////////////////////////////////////////////////////////////////
	public static int RSVP(int resp, String event_id){
		//rsvp_response = resp;

		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		try {
			ParseObject object = query.get(event_id);

			if(resp == RESPONSE_YES){
				object.increment("yes_counter");
			}
			if(resp == RESPONSE_MAYBE){
				object.increment("maybe_counter");
			}
			if(resp == RESPONSE_NO){
				object.increment("no_counter");
			}
			object.save();

				status = FLAG_QUERY_SUCCESSFUL;

		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}

		return status;
	}
			
}
