package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

import java.util.ArrayList;

/**
 * Created by ereio on 4/18/15.
 */
public class FriendRecycleAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private ArrayList<HubUser> friends = new ArrayList<>();

    private byte[] friendPicture;
    private String friendUsername;

    private Context context;

    public FriendRecycleAdapter(ArrayList<HubUser> friends, Context context) {
        this.friends = friends;
        this.context = context;

    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_friends_list, parent, false);

        FriendViewHolder vhFriendList = new FriendViewHolder(v, viewType, context);

        return vhFriendList;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.friendUsername.setText(friends.get(position).username);
        if(friends.get(position).picture != null){
            BitmapWorker worker = new BitmapWorker(holder.friendPicture, friends.get(position).picture, 250, 250);
            worker.execute(0);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size(); // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        return FriendViewHolder.TYPE_FRIEND;
    }
}