package com.dingohub.Views.Fragments.MainUserFragments;

import java.util.ArrayList;
import java.util.Calendar;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.Activities.DevActivities.ViewBubActivity;
import com.dingohub.Views.Adapters.HubbubRecycleAdapter;
import com.dingohub.Views.Activities.DevActivities.CreateEventsActivity;
import com.dingohub.hubbub.R;
import com.melnykov.fab.FloatingActionButton;

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

public class TodaysBubsFragment extends Fragment{
	private static final int MONTH_OFFSET = 1;
	ArrayList<Bub> todays_events = null;
    HubbubRecycleAdapter adapter;
    FloatingActionButton fab;

    RecyclerView bubRecycleView;
    protected GestureDetector mGestureDetector;

	public TodaysBubsFragment() {}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

        // TODO - change to get a hold on the activity created version of this
        // should be in the base google activity
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
		View rootView = inflater.inflate(R.layout.fragment_todays_bubs, container,
				false);
        bubRecycleView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        bubRecycleView.setHasFixedSize(true);
        bubRecycleView.setItemAnimator(new DefaultItemAnimator());
        bubRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bubRecycleView.addOnItemTouchListener(new BubRecyclerViewListener());

        findTodaysEvents();
        adapter = new HubbubRecycleAdapter(getActivity(), todays_events, HubbubRecycleAdapter.NORMAL_BUB);
        bubRecycleView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(bubRecycleView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CreateEventsActivity.class);
                startActivity(i);
            }
        });

		return rootView;
	}

    // TODO - Implement the onPause call to save the hub data stored
	/*
	 @Override
	 public void onResume(){
		 super.onResume();

		findTodaysEvents();
	     if(todays_events.size() != 0){
			adapter = new BubRecycleAdapter(getActivity(), todays_events);
			recyclerView.setAdapter(adapter);


		}
	 }
*/
    private void findTodaysEvents(){
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + MONTH_OFFSET;
        int year = c.get(Calendar.YEAR);

        String date = day + "/" + month + "/" + year;
        todays_events = HubDatabase.GetBubsByDate(date);
    }

    private class BubRecyclerViewListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if(child!=null && mGestureDetector.onTouchEvent(e)){

                if(Hubbub.DEBUG)
                    Toast.makeText(getActivity().getApplicationContext(), "The Item Clicked is: " +
                            rv.getChildPosition(child), Toast.LENGTH_SHORT).show();

                if(todays_events.size() != 0) {
                    String eventId = todays_events.get(rv.getChildPosition(child)).id;
                    Intent intent = new Intent(getActivity(), ViewBubActivity.class);
                    intent.putExtra(ViewBubActivity.EVENT_KEY, eventId);                    // MAKE Bub parcelable
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
