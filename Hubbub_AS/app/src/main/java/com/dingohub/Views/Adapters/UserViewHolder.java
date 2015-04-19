package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hubbub.R;

/**
 * Created by ereio on 4/18/15.
 */
public class UserViewHolder extends RecyclerView.ViewHolder{
    int Holderid;

    public static final int TYPE_FRIEND  = 0;  // Declaring Variable to Understand which View is being worked on

    TextView friendUsername;
    ImageView friendPicture;

    Context context;

    public UserViewHolder(View itemView, int ViewType, Context context) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
        super(itemView);
        this.context = context;
        // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
        itemView.setClickable(true);
                         // Setting holder id = 0 as the object being populated are of type header view
        if (ViewType == TYPE_FRIEND) {
            friendUsername = (TextView) itemView.findViewById(R.id.textview_friend_username);
            friendPicture = (ImageView) itemView.findViewById(R.id.picture_circle_friend);
            Holderid = TYPE_FRIEND;
        }
    }
}
