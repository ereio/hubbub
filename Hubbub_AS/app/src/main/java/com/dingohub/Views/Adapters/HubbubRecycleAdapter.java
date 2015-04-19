package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ereio on 4/17/15.
 */
public class HubbubRecycleAdapter extends RecyclerView.Adapter<HubbubViewHolder> {
    public static final int FOLLOWED_BUB = 0;
    public static final int NORMAL_BUB = 1;
    public static final int NORMAL_HUB = 2;
    public static final int HUBBUB_SEARCH = 3;

    private int adapter_type = 0;
    private final Context context;
    ArrayList<Bub> events = new ArrayList<>();
    ArrayList<Hub> hubs = new ArrayList<>();
    Random randomColor = new Random();
    int color_array[] = {R.color.ColorAccent, R.color.ColorPrimary, R.color.ColorPrimaryDark, R.color.Khaki,
                         R.color.DarkTurquoise, R.color.DarkSeaGreen};

    public HubbubRecycleAdapter(Context context, ArrayList<Bub> events, int adapter_type){
        this.context = context;
        this.adapter_type = adapter_type;

        if(events != null)
            this.events = events;
    }

    public HubbubRecycleAdapter(Context context, ArrayList<Bub> events, ArrayList<Hub> hubs, int adapter_type) {

        this.context = context;
        this.adapter_type = adapter_type;

        if(hubs != null)
            this.hubs = hubs;

        if(events != null)
            this.events = events;
    }

    @Override
    public HubbubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == HubbubViewHolder.BUB_EVENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_cardview_bubs, parent, false); //Inflating the layout

            HubbubViewHolder vhBub = new HubbubViewHolder(v, viewType, context); //Creating ViewHolder and passing the object of type view
            return vhBub; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == HubbubViewHolder.FOLLOWED_BUB_EVENT) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_cardview_followed_bubs, parent, false);

            HubbubViewHolder vhFollowedBub = new HubbubViewHolder(v, viewType, context);

            return vhFollowedBub;

        } else if (viewType == HubbubViewHolder.HUB_ENVIRONMENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_cardview_followed_bubs, parent, false);

            HubbubViewHolder vhHubEnv = new HubbubViewHolder(v, viewType, context);

            return vhHubEnv;
        } else if (viewType == HubbubViewHolder.EMPTY_BUBS || viewType == HubbubViewHolder.EMPTY_HUBS){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_empty_header, parent, false);

            HubbubViewHolder vhHubEnv = new HubbubViewHolder(v, viewType, context);

            return vhHubEnv;
        }

        return null;

    }

    @Override
    public void onBindViewHolder(HubbubViewHolder holder, int position) {

        if(holder.Holderid == HubbubViewHolder.FOLLOWED_BUB_EVENT) {
            holder.fBubTitle.setText(events.get(position).title);
            holder.fBubLocation.setText(events.get(position).location);
            holder.fBubStartDate.setText(events.get(position).start_date);
            holder.fBubEndTime.setText(events.get(position).end_time);
            holder.fBubNumFollowing.setText(String.valueOf(events.get(position).follower_ids.length()));
            holder.fBubStartTime.setText(events.get(position).start_time);
            if(events.get(position).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.fbubPicture, events.get(position).picture, 250, 250);
                worker.execute(0);
            }
        } else if (holder.Holderid == HubbubViewHolder.BUB_EVENT) {
            holder.bubTitle.setText(events.get(position).title);
            holder.bubLocation.setText(events.get(position).location);
            holder.bubTime.setText(events.get(position).start_time);
            if(events.get(position).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.bubPicture, events.get(position).picture, 250, 250);
                worker.execute(0);
            }

        } else if (holder.Holderid == HubbubViewHolder.HUB_ENVIRONMENT) {
            holder.hubAbout.setText(hubs.get(position).name);
            holder.hubNumEvents.setText(hubs.get(position).name);
            holder.hubNumFollowing.setText(hubs.get(position).name);
            holder.hubLocation.setText(hubs.get(position).name);

            if(hubs.get(position).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.hubPicture, hubs.get(position).picture, 250, 250);
                worker.execute(0);
            }
        } else if (holder.Holderid == HubbubViewHolder.EMPTY_HUBS){
            int colorIndex = randomColor.nextInt(6);                             //sets random color index for
                                                                                 //randomized error messages
            holder.emptyStatement.setText("There doesn't seem to be any hubs here :(");
            holder.emptyPicture.setImageDrawable(context.getDrawable(R.drawable.intro_events));
            holder.layout.setBackgroundColor(context.getResources().getColor(color_array[colorIndex]));

        } else if(holder.Holderid == HubbubViewHolder.EMPTY_BUBS) {
            int colorIndex = randomColor.nextInt(6);

            // Sees whether the bubs being passed were of a followed bubs list or normal
            if(adapter_type == FOLLOWED_BUB){
                holder.emptyStatement.setText("You haven't followed any hubs yet! Look for\n" +
                        "bubs in todays events or search with the search glass above.");
            } else {
                holder.emptyStatement.setText("There doesn't seem to be any hubs here :(");
            }

            holder.layout.setBackgroundColor(context.getResources().getColor(color_array[colorIndex]));
            holder.emptyPicture.setImageDrawable(context.getDrawable(R.drawable.intro_events));


        }
    }

    @Override
    public int getItemCount() {

        if(adapter_type == NORMAL_BUB || adapter_type == FOLLOWED_BUB) {
            if(events.size() != 0)
                return events.size();                   // returns only if there are values
        } else if(adapter_type == NORMAL_HUB) {         // to list
            if(hubs.size() != 0)
                return hubs.size();
        } else if(adapter_type == HUBBUB_SEARCH) {
            return events.size() + 1;                   // to add the header hub
        }

        return 1;                                       // otherwise we have an empty list message
    }

    @Override
    public int getItemViewType(int position) {
        int testing = -1;

        if (adapter_type == NORMAL_BUB) {
            if (events.size() != 0){
                testing = HubbubViewHolder.BUB_EVENT;
                return HubbubViewHolder.BUB_EVENT;
            } else {
                return HubbubViewHolder.EMPTY_BUBS;
            }

        } else if (adapter_type == FOLLOWED_BUB){
            if (events.size() != 0)
                return HubbubViewHolder.FOLLOWED_BUB_EVENT;
            else
                return HubbubViewHolder.EMPTY_BUBS;

        } else if(adapter_type == NORMAL_HUB) {
            if(hubs.size() != 0)
                return HubbubViewHolder.HUB_ENVIRONMENT;
            else
                return HubbubViewHolder.EMPTY_HUBS;

        } else if(adapter_type == HUBBUB_SEARCH){
            // Checks to make sure there is something to inflate with
            // whether it be hubs or bubs
            if(position == 0 && hubs.size() != 0)
                return HubbubViewHolder.HUB_ENVIRONMENT;
             else if(position == 0)
                return HubbubViewHolder.EMPTY_HUBS;
             else if(events.size() != 0)
                return HubbubViewHolder.BUB_EVENT;
             else
                return HubbubViewHolder.EMPTY_BUBS;

        } else {
            return HubbubViewHolder.EMPTY_BUBS;
        }
    }


}