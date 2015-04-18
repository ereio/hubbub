package com.dingohub.Model.DataAccess;

import org.json.JSONArray;

/**
 * Created by Allouch on 4/17/2015.
 */
public class Hub {

    public String id;
    public  String name;
    public String location;
    public JSONArray tags;
    public JSONArray follower_ids;
    public byte[] picture;
    public JSONArray bubs;


    public Hub(){
        id = null;
        name = null;
        location = null;
        picture = new byte[] {};


    }
}
