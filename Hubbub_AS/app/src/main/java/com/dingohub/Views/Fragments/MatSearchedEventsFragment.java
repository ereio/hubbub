package com.dingohub.Views.Fragments;

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
import com.dingohub.Views.Deprecated.ViewEventActivity;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.hubbub.R;

public class MatSearchedEventsFragment extends Fragment {
    public static final String TAG_KEY = "TAG_KEY";
    ArrayList<Bub> search_events;
    ArrayList<Hub> search_hubs;
    String tag = null;

    HubbubRecycleAdapter adapter;
    RecyclerView bubRecycleView;
    protected GestureDetector mGestureDetector;

    public MatSearchedEventsFragment() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            tag = bundle.getString(TAG_KEY);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // This is a defense against Events will Null tags (ONLY HAPPENS IN DEBUGGING)
        search_events = tag != null ? HubDatabase.GetBubsByTag(tag) : new ArrayList<Bub>();
        search_hubs = tag != null ? HubDatabase.GetHubsByTag(tag) : new ArrayList<Hub>();

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
        View rootView = inflater.inflate(R.layout.material_fragment_todays_bubs, container,
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

                if (rv.getChildPosition(child) == 0) {
                    String hubId = search_hubs.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                    intent.putExtra(ViewEventActivity.EVENT_KEY, hubId);
                    startActivity(intent);

                } else {
                    String eventId = search_events.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                    intent.putExtra(ViewEventActivity.EVENT_KEY, eventId);
                    startActivity(intent);
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }
    }
}
