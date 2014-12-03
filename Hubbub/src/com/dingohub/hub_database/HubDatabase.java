package com.dingohub.hub_database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	public static final String INVALID_CHARS = ",~%&*{ }\\< >?/|+\";";
	// Query Status
	public static final int FLAG_NULL_QUERY = 1;
	public static final int FLAG_QUERY_FAILED = -1;
	public static final int FLAG_QUERY_SUCCESSFUL = 0;
	public static int status = FLAG_NULL_QUERY;
	
	// Ping-In Status
	public static final int RESPONSE_YES = 1;
	public static final int RESPONSE_MAYBE = 0;
	public static final int RESPONSE_NO = -1;
	
	// Parse Client Information
	private static final String parse_app_id = "zSa7P3L6UeletXOg3ivSGyCkfLOy7NVQxPq4HshT";
	private static final String parse_client_key = "56gYe0e3MNH0sWPLKen0XD9kmf0BrtvbyyxyQuuN";

	// Parse Table Names
	public static final String EVENT = "HubEvent";
	public static final String _USERS = "_User";
	public static final String USERS_TABLE = "_User";
	public static final String EVENTS_TABLE = "HubEvent";
	
	// Parse Bub Table Attributes
	public static final String TITLE = "title";
	public static final String LOCATION = "location";
	public static final String DETAILS = "details";
	public static final String GEOLOC = "geolocation";
	public static final String PICTURE = "picture";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String FOLLOWERS = "followers";
	public static final String TAGS = "tags";
	public static final String PERMISSION = "permission_level";
	public static final String YES = "yes_counter";
	public static final String NO = "no_counter";
	public static final String MAYBE = "maybe_counter";
	public static final String CREATOR = "event_creator_id";
	public static final String NUM_FOL = "num_followers";
	
	// Parse User Table Attributes
	public static final String FULL_NAME = "full_name";
	public static final String DATE_OF_BIRTH = "date_of_birth";
	public static final String FRIENDS = "friends";
	public static final String PROFILE_PIC = "profile_picture";
	public static final String ABOUT = "about_me";
	
	
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
		user.put(ABOUT, newUser.details);
		user.put(FULL_NAME, newUser.firstname + "_" + newUser.lastname);
		user.put(DATE_OF_BIRTH, dob);
		user.put(FRIENDS, new JSONArray());
		
		try {
			user.signUp();
			status = FLAG_QUERY_SUCCESSFUL;
			
			if(newUser.picture != null){
				ParseFile picture = new ParseFile(newUser.username + " Profile Picture", newUser.picture);
				user.put(PROFILE_PIC, picture);
				user.saveInBackground();
			}
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
			event.put(TITLE, bub.title);
			event.put(LOCATION, bub.location);
			event.put(CREATOR, id);
			//gave picture the title change later to picture title
			//ParseFile e_picture = new ParseFile(bub.title,bub.picture);
			//event.put("picture", e_picture);
			event.put(DETAILS, bub.details);
			event.put(PERMISSION, bub.permissions);
			event.put(START_DATE, bub.start_date + "T" + bub.start_time);
			event.put(END_DATE, bub.end_date + "T" + bub.end_time);
			event.put(YES, 0);
			event.put(MAYBE, 0);
			event.put(NO, 0);
			event.put(FOLLOWERS, new JSONArray());
			event.put(TAGS, bub.tags);
			
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
	
	///////////////////////////////////////
	// Gets Current HubUser
	// No different than direct parse call other than Hubuser
	// Object return
	///////////////////////////////////////
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
	
	/**////////////////   BUBS + EVENT QUERYING METHODS   ////////////////**/
	
	
	////////////////////////////////////////////////////////////
	// Find Bub By Date
	// FIND EVENTS BY DATE - CAN FIND ANY INCLUDING CURRENT DATE BUB DISPLAY
	////////////////////////////////////////////////////////////
	public static ArrayList<Bub> FindEventByDate(String date){
		
		ArrayList<Bub> events = new ArrayList<Bub>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		query.whereContains(START_DATE, date);
		
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

	////////////////////////////////////////////////////////////
	// Find Bub By Tag
	// Searches all events through the tag array
	////////////////////////////////////////////////////////////
	public static ArrayList<Bub> FindEventByTag(String tag){
		
		ArrayList<Bub> events = new ArrayList<Bub>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		query.whereEqualTo(TAGS, tag);
		
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
	
	////////////////////////////////////////////////
	// Get an event using an objectId
	// Returns:
	//   -populated TappEvent if successful
	//   -null if error occurred
	////////////////////////////////////////////////
	public static Bub GetEventById(String id){
		Bub event = new Bub();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		
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

	////////////////////////////////////////////////
	// Get an event using a follower ID
	// Returns events which a sepecfic user has
	// followered in a bub array.
	////////////////////////////////////////////////
	public static  ArrayList<Bub> FindBubByFollower(String userID){
		ArrayList<Bub> bubs = new ArrayList<Bub>();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);
		query.whereEqualTo(FOLLOWERS, userID);
		try {
			List<ParseObject> obj_list = query.find();
			
			for(ParseObject obj: obj_list)
				bubs.add(GetBubFromDBObj(obj));
			
			status = FLAG_QUERY_SUCCESSFUL;
			
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			bubs = null;
			Log.e(TAG,e.toString());
		}
		
		return bubs;
	}
	
	///// Used to calculate a nessesary size for Bitmap images
	// Efficiency in memory allocation and standardization
	public static int calcImageSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth){
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize)  > reqWidth){
				inSampleSize += 2;
			}
		}
		
		return inSampleSize;
	}
	
	// Main function for decoding bitmap - Runs the calculations and then scales
	public static Bitmap decodeSampledBitmapFile(String filepath, int reqWidth, int reqHeight){
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filepath,options);

	    // Calculate inSampleSize
	    options.inSampleSize = calcImageSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filepath,options);
		
	}
	
	// Main function for decoding bitmap - Runs the calculations and then scales
	public static Bitmap decodeSampledBitmapBytes(byte[] pictureBytes, int reqWidth, int reqHeight){
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length, options);
	  
	    // Calculate inSampleSize
	    options.inSampleSize = calcImageSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length, options);
		
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
			object.put(TITLE,event.title);
		}
		if(event.location != null){
			object.put(LOCATION,event.location);
		}

		if(event.start_date != null || event.start_time != null){
			if(event.start_date == null){
				String sd = object.getString(START_DATE).split("T")[0];
				object.put(START_DATE,sd+"T"+event.start_time);
			}
			else if(event.start_time == null){
				String st = object.getString(START_DATE).split("T")[1];
				object.put(START_DATE,event.start_date+"T"+st);
			}
			else{
				object.put(START_DATE,event.start_date+"T"+event.start_time);
			}
		}
	if(event.end_date != null || event.end_time != null){
		if(event.end_date == null){
			String ed = object.getString(END_DATE).split("T")[0];
			object.put(END_DATE,ed+"T"+event.end_time);
		}
		else if(event.end_time == null){
			String et = object.getString(END_DATE).split("T")[1];
			object.put(END_DATE,event.end_date+"T"+et);
		}
		else{
			object.put(END_DATE,event.end_date+"T"+event.end_time);
		}
	}

	if(event.follower_ids != null)
		object.put(FOLLOWERS, event.follower_ids);

	if(pic_name != null && event.picture != null){
		ParseFile picture = new ParseFile(pic_name, event.picture);
		object.put(PICTURE, picture);
	}

	if(event.details != null){
		object.put(DETAILS,event.details);
	}
	if(event.permissions != null){
		object.put(PERMISSION,event.permissions);
	}
	if(event.tags != null){
		object.put(TAGS,event.tags);
	}
	object.save();

	status = FLAG_QUERY_SUCCESSFUL;
	} catch (ParseException e) {
		status = FLAG_QUERY_FAILED;
		Log.e(TAG,e.toString());
	}

	return status;
}
	/////////////////////////////////////////////////////////////
	// Unfollow Bub
	//Pass null for detail to remain unchanged
	////////////////////////////////////////////////////////////
	public static int RemoveFollower(Bub event){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENT);
		try {
			ParseObject object = query.get(event.id);
			object.put(FOLLOWERS, event.follower_ids);
			object.increment(NUM_FOL, -1);
			object.saveInBackground();
			status = FLAG_QUERY_SUCCESSFUL;
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}
		
		return status;
	}
	
	/////////////////////////////////////////////////////////////
	// Add Follower To Bub
	// Adds follower to bub using event_id an follower_id to add to array
	////////////////////////////////////////////////////////////
	public static int AddFollower(String event_id, String follower_id){
		//temp_id = follower_id;
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENT);
		
		try {
			ParseObject object = query.get(event_id);
			
			JSONArray follower_array = object.getJSONArray(FOLLOWERS);
			follower_array.put(follower_id);
			object.put(FOLLOWERS, follower_array);
			object.increment(NUM_FOL);
			object.saveInBackground();
			
			status = FLAG_QUERY_SUCCESSFUL;
		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}
		
		return status;
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
				object.increment(YES);
			}
			if(resp == RESPONSE_MAYBE){
				object.increment(MAYBE);
			}
			if(resp == RESPONSE_NO){
				object.increment(NO);
			}
			object.save();

				status = FLAG_QUERY_SUCCESSFUL;

		} catch (ParseException e) {
			status = FLAG_QUERY_FAILED;
			Log.e(TAG,e.toString());
		}

		return status;
	}

	// Converts ParseObject to HubUser
	private static HubUser GetUserFromDBObj(ParseObject db_usr){
		HubUser usr = new HubUser();

		usr.id = db_usr.getObjectId();
		usr.username = db_usr.getString("username");
		usr.email = db_usr.getString("email");
		usr.details = db_usr.getString(ABOUT);
		String[] name = db_usr.getString(FULL_NAME).split("_");
		usr.firstname = name[0];
		usr.lastname = name[1];
		usr.friend_ids = db_usr.getJSONArray(FRIENDS);
		try {
			ParseFile pic = db_usr.getParseFile(PROFILE_PIC);
			usr.picture = pic.getData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return usr;
	}
	

	// Converts ParseObject to Bub
	private static Bub GetBubFromDBObj(ParseObject db_event){
		Bub event = new Bub();
		
		event.id = db_event.getObjectId();
		event.title = db_event.getString(TITLE);
		event.location = db_event.getString(LOCATION);
		
		try{
			ParseFile pic = db_event.getParseFile(PICTURE);
			event.picture = pic.getData();
		} catch(Exception e){
			event.picture = null;
			Log.e(TAG, e.toString());
		}
		
		event.details = db_event.getString(DETAILS);
		event.permissions = db_event.getString(PERMISSION);
		
		String[] start_dt = db_event.getString(START_DATE).split("T");
		event.start_date = start_dt[0];
		event.start_time = start_dt[1];
		
		String[] end_dt = db_event.getString(END_DATE).split("T");
		event.end_date = end_dt[0];
		event.end_time = end_dt[1];
		
		event.yes_counter = db_event.getInt(YES);
		event.maybe_counter = db_event.getInt(NO);
		event.no_counter = db_event.getInt(MAYBE);
		event.tags = db_event.getJSONArray(TAGS);
		
		event.follower_ids = db_event.getJSONArray(FOLLOWERS);
		
		return event;
	}
	
}
