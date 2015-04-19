package com.dingohub.Views.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

/**
 * Created by ereio on 3/30/15.
 */
public class ProfileRecycleAdapter extends RecyclerView.Adapter<ProfileViewHolder> {

    String LOCATION = "Default Location";

    // Titles and Icons for Drawer display
    String mNavTitles[] = {"Today","Bubs","Hubs","Groups","Friends", "Settings"};

    int mIcons[] = {R.drawable.ic_public_black_24dp, R.drawable.ic_event_note_black_24dp,
            R.drawable.ic_layers_black_24dp, R.drawable.ic_group_work_black_24dp,
            R.drawable.ic_group_black_24dp, R.drawable.ic_settings_black_24dp};

    private String name;
    private byte[] profilePicture;
    private String email;
    private String location;

    private Context context;

    public ProfileRecycleAdapter(HubUser user, Context context) {
        name = user.firstname + "  " + user.lastname;
        email = user.email;

        if(user.location == null)
            this.location = LOCATION;

        profilePicture = user.picture;
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

        } else {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        if (holder.Holderid == ProfileViewHolder.TYPE_ITEM) {
            holder.menuItem.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);

        } else if (holder.Holderid == ProfileViewHolder.TYPE_HEADER) {
            // decodes the picture in a separate thread
            if(profilePicture != null){
                BitmapWorker worker = new BitmapWorker(holder.navProfile, profilePicture, 250, 250);
                worker.execute(0);
            }
            holder.navName.setText(name);
            holder.navEmail.setText(email);
            holder.navLocation.setText(location);

        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }

    @Override
    public int getItemViewType(int position) {
        // If it's not a user view being passed in
        // create the navigation drawer
            if (position == 0)
                return ProfileViewHolder.TYPE_HEADER;
            else
                return ProfileViewHolder.TYPE_ITEM;
    }
}