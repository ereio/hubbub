package com.dingohub.Views.Fragments.MainUserFragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.DevActivities.MatCreateEventsActivity;
import com.dingohub.Views.Activities.DevActivities.MatUserProfileActivity;
import com.dingohub.Views.Activities.ViewEventActivity;
import com.dingohub.Views.Adapters.FriendRecycleAdapter;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.Views.Adapters.ProfileRecycleAdapter;
import com.dingohub.Views.Adapters.ProfileViewHolder;
import com.dingohub.hubbub.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class MatUserProfileFragment extends Fragment{
    public static int FRIENDS_SHOWN = 20;

    HubUser user;
    ArrayList<HubUser> friends = new ArrayList<>();

    FriendRecycleAdapter adapter;
    RecyclerView friendRecycleView;

    ImageView iProfilePic;
    TextView tUsername;
    TextView tFullName;
    TextView tAbout;


    protected GestureDetector mGestureDetector;

    public MatUserProfileFragment() {
        friends = HubDatabase.GetFriends(HubDatabase.getCurrentUser().id);
        user = HubDatabase.getCurrentUser();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mGestureDetector = new GestureDetector(getActivity() ,new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                // TODO - Implement a callback bool for highlighting
                // If a user as touched down on an object, it should be sent back with a
                // highlighting bool for layout objects to render a "pressed view"
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // this is to confirm a user wanted to actually select the object they tapped
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.material_fragment_user_profile, container,
                false);

        // initalizes the recyclers and adapters for Listeners
        init_ui(rootView);

        // initalizes the recyclers and adapters for Listeners
        init_adapters(rootView);

        return rootView;
    }

    private void init_ui(View rootView){
        iProfilePic = (ImageView) rootView.findViewById(R.id.image_profile_picture);
        tUsername = (TextView) rootView.findViewById(R.id.text_username);
        tFullName = (TextView) rootView.findViewById(R.id.text_fullname);
        tAbout = (TextView) rootView.findViewById(R.id.cardview_text_about);

        if(user.picture != null){
            BitmapWorker worker = new BitmapWorker(iProfilePic, user.picture, 250, 250);
            worker.execute(0);
        }
        tUsername.setText(user.username);
        tFullName.setText(user.firstname + " " + user.lastname);
        tAbout.setText(user.details);

    }

    // TODO - Implement the onPause call to save the hub data stored

    private void init_adapters(View rootView){

        // Setting the recycle view for the friends grid view
        friendRecycleView = (RecyclerView) rootView.findViewById(R.id.grid_recycle_view);
        friendRecycleView.setHasFixedSize(true);
        friendRecycleView.setItemAnimator(new DefaultItemAnimator());
        friendRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), FRIENDS_SHOWN));
        friendRecycleView.addOnItemTouchListener(new FriendRecycleViewListener());

        adapter = new FriendRecycleAdapter(friends, getActivity());

        friendRecycleView.setAdapter(adapter);
    }


    private class FriendRecycleViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getActivity().getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // Find the friend idea of the person clicked
                String followerId = friends.get(rv.getChildPosition(child)).id;

                // creates a bundle with the friend id in the new fragment
                Intent intent = new Intent(getActivity(), MatUserProfileActivity.class);
                intent.putExtra(Hubbub.USER_VIEW_KEY, followerId);
                startActivity(intent);

                //

            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }
}
