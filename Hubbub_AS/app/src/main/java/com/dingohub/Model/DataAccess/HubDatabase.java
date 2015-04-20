package com.dingohub.Model.DataAccess;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dingohub.Hubbub;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


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
    public static final String HUBS_TABLE = "Hubs";
	
	// Parse Bub Table Attributes
	public static final String TITLE = "title";
	public static final String LOCATION = "location";
	public static final String DETAILS = "details";
	public static final String GEOLOC = "geolocation";
	public static final String PICTURE = "picture";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String FOLLOWERS = "followers";
    public static final String FOLLOWED_BUBS = "followed_bubs";
	public static final String TAGS = "tags";
	public static final String PERMISSION = "permission_level";
	public static final String YES = "yes_counter";
	public static final String NO = "no_counter";
	public static final String MAYBE = "maybe_counter";
	public static final String CREATOR = "event_creator_id";
	public static final String NUM_FOL = "num_followers";
    public static final String NUM_FRIENDS = "num_friends";
	
	// Parse User Table Attributes
	public static final String FULL_NAME = "full_name";
    public static final String CURRENT_LOCATION = "location";
	public static final String DATE_OF_BIRTH = "date_of_birth";
	public static final String FRIENDS = "friends";
	public static final String PROFILE_PIC = "profile_picture";
	public static final String ABOUT = "about_me";
	
	public static String locality = null;


    //parse Hub Table Attributes
    public static final String NAME = "name";
	public static final String BUBS_COL = "Bubs";

    private static HubUser currentUser = new HubUser();

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
	
	public static void SetLocation(String local){
		locality = local;
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
        if(newUser.location == null)
            newUser.location = "Tallahassee_Florida";
        user.put(CURRENT_LOCATION, newUser.location);
		user.put(FRIENDS, new JSONArray());
		
		try {
			user.signUp();
			status = FLAG_QUERY_SUCCESSFUL;
			
			if(newUser.picture != null){
				ParseFile picture = new ParseFile(newUser.id + "_profile_picture", newUser.picture);
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
			if(bub.picture.length != 0){
				ParseFile e_picture = new ParseFile(bub.picture_title,bub.picture);
				event.put(PICTURE, e_picture);
			}
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
	public static HubUser GetCurrentUser(){

        // Saves calls to the database as the user will be cached
        // locally
        if(currentUser.id != null && !currentUser.id.equals(""))
            return currentUser;

        ParseUser cur_user = ParseUser.getCurrentUser();

		if(cur_user != null) {
            currentUser = GetUserFromDBObj(cur_user);
            return GetUserFromDBObj(cur_user);
        } else {
            return null;
        }
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
	
	
	
	////////////////////////////////////////////////////////////
	// Find Bub By Date
	// FIND EVENTS BY DATE - CAN FIND ANY INCLUDING CURRENT DATE BUB DISPLAY
	////////////////////////////////////////////////////////////
	public static ArrayList<Bub> GetBubsByDate(String date){
		
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
	public static ArrayList<Bub> GetBubsByTag(String tag){
		
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

    public static  ArrayList<Hub> GetHubsByTag(String tag)
    {
        ArrayList<Hub> hubs = new ArrayList<Hub>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);
        query.whereEqualTo(TAGS,tag);

        try{
            List<ParseObject> obj_list = query.find();
            for(ParseObject obj: obj_list){
                Log.d("ALI","FOUND HUB");
                hubs.add(GetHubFromDBObj(obj));
            }
            status = FLAG_QUERY_SUCCESSFUL;

        } catch (ParseException e){
            status = FLAG_QUERY_FAILED;
            hubs = null;
            Log.e(TAG,e.toString());
        }
        return hubs;

    }
	
	////////////////////////////////////////////////
	// Get an event using an objectId
	// Returns:
	//   -populated TappEvent if successful
	//   -null if error occurred
	////////////////////////////////////////////////
	public static Bub GetBubsById(String id){
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
	public static  ArrayList<Bub> GetBubsByFollower(String userID){
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
			bubs = new ArrayList<>();
			Log.e(Hubbub.TAG + " " + TAG,e.toString());
		}
		
		return bubs;
	}


    /**
     * Finds all of the Bubs which a particular user is following
     * @param userId the user to find Bubs for
     * @return ArrayList containing followed Bubs
     */
    public static ArrayList<Bub> GetFollowedBubs(String userId) {
        ArrayList<Bub> bubs = new ArrayList<Bub>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);
        query.whereContainedIn(TAGS, new ArrayList<String>());

        try {
            ParseObject object = query.get(userId);
            JSONArray followed_array = object.getJSONArray(FOLLOWED_BUBS);
            int len = followed_array.length();

            for (int i = 0; i < len; i++) {
                Bub tmp = GetBubFromId(followed_array.get(i).toString());

                if (tmp == null) throw new Exception("Specified event ID not found in event table.");

                bubs.add(tmp);
            }

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        } catch (Exception e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        }

        return bubs;
    }
	

	///// Used to calculate a nessesary size for Bitmap images
	// Efficiency in memory allocation and standardization
	public static int CalculateImageSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
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
	
	// Main function for decoding bitmp - Runs the calculations and then scales
	public static Bitmap DecodePicture(Object picture, int reqWidth, int reqHeight) throws Exception{
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    if(picture instanceof String){
		    // Calculate inSampleSize
	    	BitmapFactory.decodeFile((String)picture,options);
		    options.inSampleSize = CalculateImageSampleSize(options, reqWidth, reqHeight);
	    	// Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeFile((String)picture,options);
		    
	    } else if(picture instanceof byte[]){
	    	 // Calculate inSampleSize
            if(((byte[]) picture).length <= 1)
                return null;
	    	BitmapFactory.decodeByteArray((byte[])picture, 0, ((byte[])picture).length, options);
	    	options.inSampleSize = CalculateImageSampleSize(options, reqWidth, reqHeight);
	    	
	    	// Decode bitmap with inSampleSize set
	    	options.inJustDecodeBounds = false;
	    	return BitmapFactory.decodeByteArray((byte[])picture, 0, ((byte[])picture).length, options);
	    	
	    } else {
	    	throw new Exception("Invalid Picture Accessor");
	    }
		
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

    public static void AddBubToHub(String event_id, JSONArray tags) throws JSONException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);

        ArrayList<String> tag_list = new ArrayList<String>();

        for (int i = 1; i < tags.length(); ++i)
            tag_list.add(tags.get(i).toString());

        query.whereContainedIn(TAGS, tag_list);

        try {
            List<ParseObject> obj_list = query.find();

            for (ParseObject obj : obj_list) {
                JSONArray bubs_list = obj.getJSONArray(BUBS_COL);

                bubs_list.put(event_id);
                obj.put(BUBS_COL, bubs_list);
                obj.saveInBackground();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Bub> GetBubsFromHub(String hubid) {
        ArrayList<Bub> bubs = new ArrayList<Bub>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);

        try {
            ParseObject object = query.get(hubid);
            JSONArray bubs_array = object.getJSONArray(BUBS_COL);
            int len = bubs_array.length();

            for (int i = 0; i < len; i++) {
                Bub tmp = GetBubFromId(bubs_array.get(i).toString());

                if (tmp == null) throw new Exception("Specified event ID not found in event table.");

                bubs.add(tmp);
            }

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        } catch (Exception e) {
            status = FLAG_QUERY_FAILED;
            bubs = null;
            Log.e(TAG, e.toString());
        }

        return bubs;
    }

    /**
     * Adds a user to the array of followers for a given Hub
     * @param hub_id the ID of the Hub to add the follower to
     * @param user_id the ID of the user to add as a follower
     * @return indication of success or failure
     */
    public static int AddFollowerToHub(String hub_id, String user_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);

        try {
            ParseObject object = query.get(hub_id);

            JSONArray followers_array = object.getJSONArray(FOLLOWERS);
            followers_array.put(user_id);
            object.put(FOLLOWERS, followers_array);
            object.saveInBackground();

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        }

        return status;
    }

    /**
     * Removes a user as a follower of a Hub
     * @param hub_id the ID of the Hub to remove the user from
     * @param user_id the ID of the user to be removed from a Hub
     * @return indication of success or failure
     */
    public static int RemoveFollowerFromHub(String hub_id, String user_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);

        try {
            ParseObject object = query.get(hub_id);
            JSONArray followers_array = object.getJSONArray(FOLLOWERS);
            ArrayList<String> list = new ArrayList<String>();
            int len = followers_array.length();

            if (followers_array != null) {
                for (int i = 0; i < len; i++) {
                    list.add(followers_array.get(i).toString());
                }
            }

            list.remove(user_id);
            object.put(FOLLOWERS, new JSONArray(list));

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        }

        return status;
    }

    public static ArrayList<HubUser> GetFollowersFromHub(String hubid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);
        ArrayList<HubUser> followers = new ArrayList<HubUser>();

        try {
            ParseObject object = query.get(hubid);
            JSONArray followers_array = object.getJSONArray(FOLLOWERS);
            int len = followers_array.length();

            if (followers_array != null) {
                for (int i = 0; i < len; i++) {
                    followers.add(GetUserFromId(followers_array.get(i).toString()));
                }
            }

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;

            followers = null;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            followers = null;
            Log.e(TAG, e.toString());
        }

        return followers;
    }

    /**
     * When a user follows a Bub, it adds it to an array for that particular user for easier access
     * than parsing every single event.
     * @param event_id the ID of the Bub being added
     * @param follower_id the ID of the follower
     * @return indication of success or failure
     */
    public static int AddFollowedBub(String event_id, String follower_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);

        try {
            ParseObject object = query.get(follower_id);

            JSONArray followed_array = object.getJSONArray(FOLLOWED_BUBS);
            followed_array.put(event_id);
            object.put(FOLLOWED_BUBS, followed_array);
            object.saveInBackground();

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        }

        return status;
    }

    /**
     * When a user unfollows a Bub, it is removed from the array stored on a per-user basis.
     * @param event_id the ID of the Bub being removed
     * @param follower_id the ID of the follower
     * @return indication of success or failure
     */
    public static int RemoveFollowedBub(String event_id, String follower_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);

         try {
             ParseObject object = query.get(follower_id);
             JSONArray followed_array = object.getJSONArray(FOLLOWED_BUBS);
             ArrayList<String> list = new ArrayList<String>();
             int len = followed_array.length();

             if (followed_array != null) {
                 for (int i = 0; i < len; i++) {
                     list.add(followed_array.get(i).toString());
                 }
             }

             list.remove(event_id);
             object.put(FOLLOWED_BUBS, new JSONArray(list));

             status = FLAG_QUERY_SUCCESSFUL;
         } catch (ParseException e) {
             status = FLAG_QUERY_FAILED;
             Log.e(TAG, e.toString());
         } catch (JSONException e) {
             status = FLAG_QUERY_FAILED;
             Log.e(TAG, e.toString());
         }

        return status;
    }

    /**
     * When a user follows another user, he is added as a 'friend'.
     * This relationship is one-way, and frankly a bit creepy.
     * @param user_id the user adding a friend
     * @param friend_id the user being added
     * @return indication of success or failure
     */
    public static int AddFriend(String user_id, String friend_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);

        try {
            ParseObject object = query.get(user_id);
            JSONArray friend_array = object.getJSONArray(FRIENDS);
            friend_array.put(friend_id);
            object.put(FRIENDS, friend_array);
            object.increment(NUM_FRIENDS);
            object.saveInBackground();

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        }

        return status;
    }

    /**
     * If a user gets sick of stalking another user, they are able
     * to delete said user. Also applies when a user files a restraining order.
     * @param user_id the user unfollowing another
     * @param friend_id the user being unfollowed
     * @return indication of success or failure
     */
    public static int RemoveFriend(String user_id, String friend_id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);

        try {
            ParseObject object = query.get(user_id);
            JSONArray friend_array = object.getJSONArray(FRIENDS);
            ArrayList<String> list = new ArrayList<String>();
            int len = friend_array.length();

            if (friend_array != null) {
                for (int i = 0; i < len; i++) {
                    list.add(friend_array.get(i).toString());
                }
            }

            list.remove(friend_id);
            object.put(FRIENDS, new JSONArray(list));
            object.increment(NUM_FRIENDS, -1);

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            Log.e(TAG, e.toString());
        }

        return status;
    }

    /**
     * Get a list of all friends of a user, returned as an ArrayList of HubUsers
     * @param userId user to find friends of
     * @return an ArrayList of HubUsers
     */
    public static ArrayList<HubUser> GetFriends(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);
        ArrayList<HubUser> friends = new ArrayList<HubUser>();

        try {
            ParseObject object = query.get(userId);
            JSONArray friend_array = object.getJSONArray(FRIENDS);
            int len = friend_array.length();

            if (friend_array != null) {
                for (int i = 0; i < len; i++) {
                    friends.add(GetUserFromId(friend_array.get(i).toString()));
                }
            }

            status = FLAG_QUERY_SUCCESSFUL;
        } catch (ParseException e) {
            status = FLAG_QUERY_FAILED;

            friends = null;
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            status = FLAG_QUERY_FAILED;
            friends = null;
            Log.e(TAG, e.toString());
        }

        return friends;
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
			if(pic != null)
				usr.picture = pic.getData();
		} catch (ParseException e) {
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
			if(pic != null)
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
		event.maybe_counter = db_event.getInt(MAYBE);
		event.no_counter = db_event.getInt(NO);
		event.tags = db_event.getJSONArray(TAGS);
		
		event.follower_ids = db_event.getJSONArray(FOLLOWERS);
		
		return event;
	}

    private static Hub GetHubFromDBObj(ParseObject db_event) {
        Hub hub = new Hub();

        hub.id = db_event.getObjectId();
        hub.name = db_event.getString(NAME);
        hub.location = db_event.getString(LOCATION);

        try {
            ParseFile pic = db_event.getParseFile(PICTURE);
            if (pic != null)
                hub.picture = pic.getData();
        } catch (Exception e) {
            hub.picture = null;
            Log.e(TAG, e.toString());
        }


        hub.tags = db_event.getJSONArray(TAGS);

        hub.follower_ids = db_event.getJSONArray(FOLLOWERS);

        return hub;
    }

    /**
     * Create a HubUser object given an userID
     * @param userId ID of user to lookup
     * @return HubUser object representation
     */
    public static HubUser GetUserFromId(String userId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(USERS_TABLE);

        try {
            return GetUserFromDBObj(query.get(userId));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Create a Bub object given an eventID
     * @param eventId event to instantiate
     * @return instantiation of given eventID
     */
    public static Bub GetBubFromId(String eventId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(EVENTS_TABLE);

        try {
            return GetBubFromDBObj(query.get(eventId));
        } catch (ParseException e) {
            return null;
        }
    }

    public static Hub GetHubFromId(String hubid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(HUBS_TABLE);

        try {
            return GetHubFromDBObj(query.get(hubid));
        } catch (ParseException e) {
            return null;
        }
    }
	
}
