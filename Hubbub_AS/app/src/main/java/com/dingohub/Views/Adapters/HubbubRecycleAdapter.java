package com.dingohub.Views.Adapters;

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
public class HubbubRecycleAdapter extends RecyclerView.Adapter<HubbubViewHolder> {
    public static final int FOLLOWED_BUB = 0;
    public static final int NORMAL_BUB = 1;
    public static final int NORMAL_HUB = 2;
    public static final int HUBBUB_SEARCH = 3;

    private int adapter_type = 0;
    private final Context context;
    ArrayList<Bub> events;

    public HubbubRecycleAdapter(Context context, ArrayList<Bub> events, int adapter_type) {
        this.events = events;
        this.context = context;
        this.adapter_type = adapter_type;
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

        }
        return null;

    }

    @Override
    public void onBindViewHolder(HubbubViewHolder holder, int position) {

        if(holder.Holderid == HubbubViewHolder.FOLLOWED_BUB_EVENT){
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
        } else if (holder.Holderid == HubbubViewHolder.BUB_EVENT){
            holder.bubTitle.setText(events.get(position).title);
            holder.bubLocation.setText(events.get(position).location);
            holder.bubTime.setText(events.get(position).start_time);
            if(events.get(position).picture != null){
                BitmapWorker worker = new BitmapWorker(holder.bubPicture, events.get(position).picture, 250, 250);
                worker.execute(0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(adapter_type == NORMAL_BUB)
            return HubbubViewHolder.BUB_EVENT;

        if(adapter_type == FOLLOWED_BUB)
            return HubbubViewHolder.FOLLOWED_BUB_EVENT;

        if(adapter_type == NORMAL_HUB)
            return HubbubViewHolder.HUB_ENVIRONMENT;

        if(adapter_type == HUBBUB_SEARCH){
            if(position == 0)
                return HubbubViewHolder.HUB_ENVIRONMENT;
            else
                return HubbubViewHolder.BUB_EVENT;
        }
        return 0;
    }


}