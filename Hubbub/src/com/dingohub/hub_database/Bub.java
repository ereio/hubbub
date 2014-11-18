package com.dingohub.hub_database;

import org.json.JSONArray;


public class Bub {
	
	public String id;
	public String title;
	public String location;
	public byte[] picture;
	public String details;
	public String permissions;
	public String start_date;
	public String start_time;
	public String end_date;
	public String end_time;
	public int yes_counter;
	public int maybe_counter;
	public int no_counter;
	public JSONArray follower_ids;
	public JSONArray tags;
	
	public Bub(){
		id = null;
		title = null;
		location = null;
		details = null;
		permissions = null;
		start_date = null;
		start_time = null;
		end_date = null;
		end_time = null;
		picture = new byte[] {};
		yes_counter = 0;
		no_counter = 0;
		maybe_counter = 0;
		 
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
