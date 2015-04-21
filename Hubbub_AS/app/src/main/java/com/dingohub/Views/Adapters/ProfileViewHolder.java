package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hubbub.R;

/**
 * Created by ereio on 4/15/15.
 */
public class ProfileViewHolder extends RecyclerView.ViewHolder {
    int Holderid;

    public static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on

    public static final int TYPE_ITEM = 1;

    public static final int TYPE_USER_VIEW = 2;

    ImageView imageView;
    TextView menuItem;

    ImageView navProfile;
    TextView navLocation;
    TextView navName;
    TextView navEmail;

    TextView friendUsername;
    ImageView friendPicture;

    Context context;

    public ProfileViewHolder(View itemView, int ViewType, Context context) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
        super(itemView);
        this.context = context;
        // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
        itemView.setClickable(true);
        if (ViewType == TYPE_ITEM) {
            menuItem = (TextView) itemView.findViewById(R.id.rowText);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
            Holderid = TYPE_ITEM;                                               // setting holder id as 1 as the object being populated are of type item row
        } else if(ViewType == TYPE_HEADER) {
            navName = (TextView) itemView.findViewById(R.id.text_user_name);         // Creating Text View object from header.xml for name
            navEmail = (TextView) itemView.findViewById(R.id.text_user_email);
            navProfile = (ImageView) itemView.findViewById(R.id.imagebutton_account_picture);
            navLocation = (TextView) itemView.findViewById(R.id.text_user_location);
            Holderid = TYPE_HEADER;                                                // Setting holder id = 0 as the object being populated are of type header view
        }
    }
}