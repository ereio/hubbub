package com.dingohub.hub_database;

import org.json.JSONArray;

public class HubUser {
	
	public String id;
	public String username;
	public String email;
	public String firstname;
	public String lastname;
	public String details;
	public byte[] picture;
	public JSONArray friend_ids;
	
	public boolean SendFriendRequest(){
		return true;
	}
	
	public boolean AcceptFriendRequest(){
		return true;
	}
	
	public String[] FriendsList(){
		return null;
	}
}