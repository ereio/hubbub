package com.dingohub.Model.Utilities;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONArray;

/**
 * Created by ereio on 5/17/15.
 */
public class ValidationManager {


    static public boolean CheckTags(Activity activity , String tags) {
        boolean check = true;

        String[] tagList = tags.split(",", 5);

        // check if more than five tags, only five tags allowed!
        if (tagList.length > 5) {
            Toast.makeText(activity,
                    "Events cannot have more than 5 tags, sorry :/", Toast.LENGTH_SHORT).show();
            check = false;
        }

        // check if tag is too long
        if (tagList[0].length() > 20) {
            Toast.makeText(activity,
                    "Tags cannot be more than 20 characters long :(", Toast.LENGTH_SHORT).show();
            check = false;
        }

        // check if an extra comma or invalid characters where inserted
        for (String cTag : tagList) {
            //if(cTag.contains(DBFunct.INVALID_CHARS)){
            Toast.makeText(activity,
                    "Tags cannot contain non-alphabetic characters, except a hashtag", Toast.LENGTH_SHORT).show();
            check = false;
            //}
        }

        if (check)
            return true;
        else
            return false;

    }


   static public JSONArray ConvertTags(String tags) {

        // splits entries into an array based on deliminating commas
        String[] tagStrings = tags.split("#");

        // omits any user inputed hashtags from the database log

        JSONArray tagJSON = new JSONArray();
        for (int i = 0; i < tagStrings.length; i++) {
            if (!tagStrings[i].equals(" ") &&
                    !tagStrings[i].toLowerCase().replaceAll(" ", "").equals(" ")
                    && !tagStrings[i].equals("")) {
                tagStrings[i] = tagStrings[i].toLowerCase().replaceAll(" ", "");
                tagJSON.put(tagStrings[i]);
            }
        }


        // returns the array for creation
        return tagJSON;
    }
}
