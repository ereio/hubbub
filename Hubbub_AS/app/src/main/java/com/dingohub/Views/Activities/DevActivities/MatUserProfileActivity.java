package com.dingohub.Views.Activities.DevActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.Views.Adapters.UserRecycleAdapter;
import com.dingohub.Views.Deprecated.ViewEventActivity;
import com.dingohub.hubbub.R;
import com.google.android.gms.wallet.fragment.BuyButtonAppearance;

import java.util.ArrayList;

/**
 * Created by ereio on 4/18/15.
 */
public class MatUserProfileActivity extends BaseGoogleActivity{
    public static int FRIENDS_SHOWN = 5;

    public static final String USERVIEW_ID = "USER_KEY";

    HubUser user;
    ArrayList<HubUser> friends = new ArrayList<>();
    ArrayList<Bub> userBubs = new ArrayList<>();

    RecyclerView friendRecycleView;
    UserRecycleAdapter adapter;

    RecyclerView bubRecyclerView;

    ImageView iProfilePic;
    TextView tUsername;
    TextView tFullName;
    TextView tAbout;

    HubUser viewedUser = new HubUser();

    public MatUserProfileActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_activity_user_profile);

        // init user info
        init_user_info();

        // initalizes the recyclers and adapters for Listeners
        init_ui();

        // initalizes the recyclers and adapters for Listeners
        init_adapters();
    }

    private void init_user_info(){
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            viewedUser.id = extras.getString(USERVIEW_ID, "");
        }

        if(!viewedUser.id.equals("")) {
            user = HubDatabase.GetUserById(viewedUser.id);
            friends = HubDatabase.GetFriends(viewedUser.id);
            userBubs = HubDatabase.GetBubsByFollower(viewedUser.id);
        }

    }
    private void init_ui(){
        iProfilePic = (ImageView) findViewById(R.id.image_profile_picture);
        tUsername = (TextView) findViewById(R.id.text_username);
        tFullName = (TextView) findViewById(R.id.text_fullname);
        tAbout = (TextView) findViewById(R.id.cardview_text_about);

        if(user.picture != null){
            BitmapWorker worker = new BitmapWorker(iProfilePic, user.picture, 250, 250);
            worker.execute(0);
        }
        tUsername.setText(user.username);
        tFullName.setText(user.firstname + " " + user.lastname);
        tAbout.setText(user.details);

    }

    // TODO - Implement the onPause call to save the hub data stored

    private void init_adapters(){

        // Setting the recycle view for the friends grid view
        friendRecycleView = (RecyclerView) findViewById(R.id.grid_recycle_view);
        friendRecycleView.setHasFixedSize(true);
        friendRecycleView.setItemAnimator(new DefaultItemAnimator());
        friendRecycleView.setLayoutManager(new GridLayoutManager(this, FRIENDS_SHOWN));
        friendRecycleView.addOnItemTouchListener(new UserRecyclerViewListener());

        adapter = new UserRecycleAdapter(friends, this);
        friendRecycleView.setAdapter(adapter);

        // Sets the recycler for the Events associated with a hub
        // Finding View
        bubRecyclerView = (RecyclerView) findViewById(R.id.hubview_gridrecycleview_eventlist);
        bubRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bubRecyclerView.addOnItemTouchListener(new BubRecyclerViewListener());
        //Setting adapter to load designated data
        HubbubRecycleAdapter bubAdapter = new HubbubRecycleAdapter(getApplicationContext(), userBubs, HubbubRecycleAdapter.NORMAL_BUB);
        bubRecyclerView.setAdapter(bubAdapter);
    }


    private class UserRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // Find the friend idea of the person clicked
                String followerId = friends.get(rv.getChildPosition(child)).id;

                // creates a bundle with the friend id in the new fragment
                Intent intent = new Intent(getApplicationContext(), MatUserProfileActivity.class);
                intent.putExtra(Hubbub.USER_VIEW_KEY, followerId);
                startActivity(intent);
                finish();
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }

    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                String eventId = userBubs.get(rv.getChildPosition(child)).id;
                Intent intent = new Intent(getApplicationContext(), MatViewBubActivity.class);
                intent.putExtra(MatViewBubActivity.EVENT_KEY, eventId);					// MAKE Bub parcelable
                startActivity(intent);
                finish();
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }
}
