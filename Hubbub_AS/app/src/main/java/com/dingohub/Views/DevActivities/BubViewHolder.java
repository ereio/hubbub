package com.dingohub.Views.DevActivities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hubbub.R;

/**
 * Created by ereio on 4/15/15.
 */
public class BubViewHolder extends RecyclerView.ViewHolder {
    int Holderid;

    public static final int BUB_EVENT = 0;  // Declaring Variable to Understand which View is being worked on
    public static final int FOLLOWED_BUB_EVENT = 1;
    public static final int HUB_EVENT = 2;

    // Views for Followed Bub cards
    ImageView fbubPicture;
    TextView fBubTitle;
    TextView fBubLocation;
    TextView fBubStartDate;
    TextView fBubEndTime;
    TextView fBubNumFollowing;
    TextView fBubStartTime;

    // Views for Bub Cards
    ImageView bubPicture;
    TextView bubLocation;
    TextView bubTime;
    TextView bubTitle;


    // Views for Hub Cards

    Context context;

    public BubViewHolder(View itemView, int ViewType, Context context) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
        super(itemView);
        this.context = context;
        // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
        itemView.setClickable(true);
        if (ViewType == BUB_EVENT) {
            bubPicture = (ImageView) itemView.findViewById(R.id.cardview_fbub_picture);
            bubTime = (TextView) itemView.findViewById(R.id.cardview_bub_time);
            bubLocation = (TextView) itemView.findViewById(R.id.cardview_bub_location);
            bubTitle = (TextView) itemView.findViewById(R.id.cardview_bub_title);
            Holderid = HUB_EVENT;     // setting holder id as 1 as the object being populated are of type item row

        } else if(ViewType == FOLLOWED_BUB_EVENT){
            fbubPicture = (ImageView) itemView.findViewById(R.id.cardview_fbub_picture);
            fBubTitle = (TextView) itemView.findViewById(R.id.cardview_fbub_title);
            fBubLocation = (TextView) itemView.findViewById(R.id.cardview_fbub_location);
            fBubStartDate = (TextView) itemView.findViewById(R.id.cardview_fbub_start_date);
            fBubStartTime = (TextView) itemView.findViewById(R.id.cardview_fbub_start_time);
            fBubNumFollowing = (TextView) itemView.findViewById(R.id.cardview_fbub_num_following);
            fBubEndTime = (TextView) itemView.findViewById(R.id.cardview_fbub_end_time);
        } else {
            // Enter elements for Hubs
            Holderid = FOLLOWED_BUB_EVENT;                                                // Setting holder id = 0 as the object being populated are of type header view
        }
    }
}