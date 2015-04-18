package com.dingohub.Views.DevActivities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

/**
 * Created by ereio on 3/30/15.
 */
public class ProfileRecycleAdapter extends RecyclerView.Adapter<ProfileViewHolder> {

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private byte[] profilePicture;
    private String email;
    private String location;
    private Context context;

    ProfileRecycleAdapter(String Titles[], int Icons[], String Name, String Email, byte[] picture, String location, Context context) {
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        this.location = location;
        profilePicture = picture;
        this.context = context;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ProfileViewHolder.TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent, false); //Inflating the layout

            ProfileViewHolder vhItem = new ProfileViewHolder(v, viewType, context); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == ProfileViewHolder.TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header, parent, false);

            ProfileViewHolder vhHeader = new ProfileViewHolder(v, viewType, context);

            return vhHeader;

        }
        return null;

    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        if (holder.Holderid == ProfileViewHolder.TYPE_ITEM) {
            holder.menuItem.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {
            // decodes the picture in a separate thread
            if(profilePicture != null){
                BitmapWorker worker = new BitmapWorker(holder.profile, profilePicture, 250, 250);
                worker.execute(0);
            }
            holder.Name.setText(name);
            holder.email.setText(email);
            holder.location.setText(location);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ProfileViewHolder.TYPE_HEADER;

        return ProfileViewHolder.TYPE_ITEM;
    }
}