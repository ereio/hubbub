package com.dingohub.Views.Fragments.MainUserFragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Activities.DevActivities.MatCreateEventsActivity;
import com.dingohub.Views.Activities.ViewEventActivity;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.hubbub.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class MatUserHubsFragment extends Fragment{
    ArrayList<Bub> followed_hubs = new ArrayList<>();
    HubbubRecycleAdapter adapter;
    FloatingActionButton fab;

    RecyclerView hubRecycleView;
    protected GestureDetector mGestureDetector;

    public MatUserHubsFragment() {}

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
        View rootView = inflater.inflate(R.layout.material_fragment_user_hubs, container,
                false);

        // initalizes and searches for user bubs
        init_user_bubs();

        // initalizes the recyclers and adapters for Listeners
        init_adapters(rootView);

        // initalizes floating action button
        init_fab(rootView);

        return rootView;
    }

    // TODO - Implement the onPause call to save the hub data stored

    private void init_adapters(View rootView){

        hubRecycleView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        hubRecycleView.setHasFixedSize(true);
        hubRecycleView.setItemAnimator(new DefaultItemAnimator());
        hubRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hubRecycleView.addOnItemTouchListener(new BubRecyclerViewListener());

        adapter = new HubbubRecycleAdapter(getActivity(), followed_hubs, HubbubRecycleAdapter.NORMAL_HUB);

        hubRecycleView.setAdapter(adapter);
    }
    private void init_user_bubs(){
        // TODO - create a database call FindHubsByFollower()
        //followed_hubs = HubDatabase.FindHubsByFollower(HubDatabase.GetCurrentUser().id);

    }

    private void init_fab(View rootView){
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(hubRecycleView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MatCreateEventsActivity.class);
                startActivity(i);
            }
        });
    }


    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getActivity().getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                String eventId = followed_hubs.get(rv.getChildPosition(child)).id;
                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                intent.putExtra(ViewEventActivity.EVENT_KEY, eventId);					// MAKE Bub parcelable
                startActivity(intent);
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }
}
