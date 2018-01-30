package com.dingohub.Views.Fragments.MainUserFragments;

import java.util.ArrayList;

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
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Views.Activities.DevActivities.SearchEventsActivity;
import com.dingohub.Views.Activities.DevActivities.ViewBubActivity;
import com.dingohub.Views.Activities.DevActivities.ViewHubActivity;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.hubbub.R;

public class SearchedEventsFragment extends Fragment {
    ArrayList<Bub> search_events;
    ArrayList<Hub> search_hubs;
    String tag = null;

    HubbubRecycleAdapter adapter;
    RecyclerView bubRecycleView;
    protected GestureDetector mGestureDetector;

    public SearchedEventsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {
            tag = bundle.getString(SearchEventsActivity.TAG_KEY);
        }

        // This is a defense against Events will Null tags (ONLY HAPPENS IN DEBUGGING)
        search_events = tag != null ? HubDatabase.GetBubsByTag(tag) : new ArrayList<Bub>();
        search_hubs = tag != null ? HubDatabase.GetHubsByTag(tag) : new ArrayList<Hub>();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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
        View rootView = inflater.inflate(R.layout.fragment_todays_bubs, container,
                false);

        bubRecycleView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        bubRecycleView.setHasFixedSize(true);
        bubRecycleView.setItemAnimator(new DefaultItemAnimator());
        bubRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bubRecycleView.addOnItemTouchListener(new BubRecyclerViewListener());

        adapter = new HubbubRecycleAdapter(getActivity(), search_events, search_hubs, HubbubRecycleAdapter.HUBBUB_SEARCH);
        bubRecycleView.setAdapter(adapter);

        return rootView;

    }

    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && mGestureDetector.onTouchEvent(e)) {

                if (Hubbub.DEBUG)
                    Toast.makeText(getActivity().getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                // meant to catch the empty hub notice

                if (rv.getChildPosition(child) == 0) {
                    if(search_hubs.size() == 0)
                        return false;
                    String hubId = search_hubs.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getActivity(), ViewHubActivity.class);
                    intent.putExtra(ViewHubActivity.HUB_KEY, hubId);
                    startActivity(intent);
                } else {
                    if(search_events.size() == 0)
                        return false;
                    String eventId = search_events.get(rv.getChildPosition(child)-1).id;
                    Intent intent = new Intent(getActivity(), ViewBubActivity.class);
                    intent.putExtra(ViewBubActivity.EVENT_KEY, eventId);
                    startActivity(intent);
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
