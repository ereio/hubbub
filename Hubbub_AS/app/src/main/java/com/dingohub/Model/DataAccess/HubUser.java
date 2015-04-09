package com.dingohub.Model.DataAccess;

import org.json.JSONArray;

public class HubUser {
	
	public String id;
	public String username;
	public String email;
	public String firstname;
	public String lastname;
	public String details;
	public String location;
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
