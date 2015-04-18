package com.dingohub.Model.DataAccess;

import org.json.JSONArray;


public class Bub {
	
	public String id;
	public String title;
	public String location;
	public String geolocation;
	public byte[] picture;
	public String details;
	public String permissions;
	public String start_date;
	public String start_time;
	public String end_date;
	public String end_time;
	public int yes_counter;
	public int maybe_counter;
	public String picture_title;
	public int no_counter;
	public JSONArray follower_ids;
	public JSONArray tags;
	public int pingIn_time;
	
	public Bub(){
		id = new String();
		title = new String();
		geolocation = new String();
		location = new String();
		details = new String();
		permissions = new String();
		start_date = new String();
		start_time = new String();
		end_date = new String();
		picture_title=new String();
		end_time = new String();
		picture = new byte[] {};
        follower_ids = new JSONArray();
        tags = new JSONArray();
		yes_counter = 0;
		no_counter = 0;
		maybe_counter = 0;
		pingIn_time = 0;
		 
	}
	public boolean AddFollower(HubUser usr){
		int status = HubDatabase.AddFollower(id, usr.id);
		
		if(status == HubDatabase.FLAG_QUERY_SUCCESSFUL){
			follower_ids.put(usr.id);
			return true;
		}
		else
			return false;
	}
}
