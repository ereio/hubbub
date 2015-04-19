package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingohub.hubbub.R;

/**
 * Created by ereio on 4/15/15.
 */
public class HubbubViewHolder extends RecyclerView.ViewHolder {
    int Holderid;

    public static final int BUB_EVENT = 0;  // Declaring Variable to Understand which View is being worked on
    public static final int FOLLOWED_BUB_EVENT = 1;
    public static final int HUB_ENVIRONMENT = 2;
    public static final int EMPTY_HUBS = 3;
    public static final int EMPTY_BUBS = 4;

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
    ImageView hubPicture;
    TextView hubName;
    TextView hubLocation;
    TextView hubAbout;
    TextView hubNumFollowing;
    TextView hubNumEvents;

    // Views for an error or empty statement
    ImageView emptyPicture;
    TextView  emptyStatement;
    RelativeLayout layout;
    Context context;

    public HubbubViewHolder(View itemView, int ViewType, Context context) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
        super(itemView);
        this.context = context;
        // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
        itemView.setClickable(true);
        if (ViewType == BUB_EVENT) {
            bubPicture = (ImageView) itemView.findViewById(R.id.cardview_fbub_picture);
            bubTime = (TextView) itemView.findViewById(R.id.cardview_bub_time);
            bubLocation = (TextView) itemView.findViewById(R.id.cardview_bub_location);
            bubTitle = (TextView) itemView.findViewById(R.id.cardview_bub_title);
            Holderid = BUB_EVENT;

        } else if (ViewType == FOLLOWED_BUB_EVENT) {
            fbubPicture = (ImageView) itemView.findViewById(R.id.cardview_fbub_picture);
            fBubTitle = (TextView) itemView.findViewById(R.id.cardview_fbub_title);
            fBubLocation = (TextView) itemView.findViewById(R.id.cardview_fbub_location);
            fBubStartDate = (TextView) itemView.findViewById(R.id.cardview_fbub_start_date);
            fBubStartTime = (TextView) itemView.findViewById(R.id.cardview_fbub_start_time);
            fBubNumFollowing = (TextView) itemView.findViewById(R.id.cardview_fbub_num_following);
            fBubEndTime = (TextView) itemView.findViewById(R.id.cardview_fbub_end_time);
            Holderid = FOLLOWED_BUB_EVENT;

        } else if (ViewType == HUB_ENVIRONMENT) {
            hubPicture = (ImageView) itemView.findViewById(R.id.cardview_hub_picture);
            hubName = (TextView) itemView.findViewById(R.id.cardview_hub_title);
            hubLocation = (TextView) itemView.findViewById(R.id.cardview_hub_location);
            hubAbout = (TextView) itemView.findViewById(R.id.carview_hub_about);
            hubNumFollowing = (TextView) itemView.findViewById(R.id.cardview_fbub_num_following);
            hubNumEvents = (TextView) itemView.findViewById(R.id.cardview_hub_num_events);
            Holderid = HUB_ENVIRONMENT;

        } else if (ViewType == EMPTY_HUBS) {
            emptyStatement = (TextView) itemView.findViewById(R.id.text_empty_header);
            emptyPicture = (ImageView) itemView.findViewById(R.id.picture_empty_header);
            layout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_empty_view);
            Holderid = EMPTY_HUBS;

        } else if (ViewType == EMPTY_BUBS) {
            emptyStatement = (TextView) itemView.findViewById(R.id.text_empty_header);
            emptyPicture = (ImageView) itemView.findViewById(R.id.picture_empty_header);
            layout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_empty_view);
            Holderid = EMPTY_BUBS;
        }
    }
}