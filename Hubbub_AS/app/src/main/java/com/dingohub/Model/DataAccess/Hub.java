package com.dingohub.Model.DataAccess;

import org.json.JSONArray;

/**
 * Created by Allouch on 4/17/2015.
 */
public class Hub {

    public String id;
    public String name;
    public String location;
    public String details;
    public JSONArray tags;
    public JSONArray follower_ids;
    public byte[] picture;
    public JSONArray bubs;


    public Hub(){
        id = new String();
        name = new String();
        location = new String();
        details = new String();
        picture = new byte[] {};
        follower_ids = new JSONArray();
        bubs = new JSONArray();
        tags = new JSONArray();
    }
}
