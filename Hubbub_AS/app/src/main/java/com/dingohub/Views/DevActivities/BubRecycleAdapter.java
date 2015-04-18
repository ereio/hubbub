package com.dingohub.Views.DevActivities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

import java.util.ArrayList;

/**
 * Created by ereio on 4/17/15.
 */
public class BubRecycleAdapter extends RecyclerView.Adapter<BubViewHolder> {
    private final Context context;
    ArrayList<Bub> events;

    public BubRecycleAdapter( Context context, ArrayList<Bub> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public BubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == BubViewHolder.BUB_EVENT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_cardview_bubs, parent, false); //Inflating the layout

            BubViewHolder vhBub = new BubViewHolder(v, viewType, context); //Creating ViewHolder and passing the object of type view
            return vhBub; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == BubViewHolder.FOLLOWED_BUB_EVENT) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_cardview_followed_bubs, parent, false);

            BubViewHolder vhFollowedBub = new BubViewHolder(v, viewType, context);

            return vhFollowedBub;

        }
        return null;

    }

    @Override
    public void onBindViewHolder(BubViewHolder holder, int position) {

        if(holder.Holderid == BubViewHolder.FOLLOWED_BUB_EVENT){
            holder.fBubTitle.setText(events.get(position - 1).title);
            holder.fBubLocation.setText(events.get(position - 1).title);
            holder.fBubStartDate.setText(events.get(position - 1).title);
            holder.fBubEndTime.setText(events.get(position - 1).title);
            holder.fBubNumFollowing.setText(events.get(position - 1).title);
            holder.fBubStartTime.setText(events.get(position - 1).title);
            if(events.get(position - 1).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.fbubPicture, events.get(position - 1).picture, 250, 250);
                worker.execute(0);
            }
        } else if (holder.Holderid == BubViewHolder.BUB_EVENT){
            holder.bubTitle.setText(events.get(position - 1).title);
            holder.bubLocation.setText(events.get(position - 1).title);
            holder.bubTime.setText(events.get(position - 1).title);
            if(events.get(position - 1).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.bubPicture, events.get(position - 1).picture, 250, 250);
                worker.execute(0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size() + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ProfileViewHolder.TYPE_HEADER;

        return ProfileViewHolder.TYPE_ITEM;
    }
}