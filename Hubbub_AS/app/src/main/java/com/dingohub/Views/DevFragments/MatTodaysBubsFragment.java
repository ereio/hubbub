package com.dingohub.Views.DevFragments;

import java.util.ArrayList;
import java.util.Calendar;

import com.dingohub.Views.Activities.ViewEventActivity;
import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Views.DevActivities.BubRecycleAdapter;
import com.dingohub.hubbub.R;
import com.dingohub.Model.Utilities.EventListAdapter;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

public class MatTodaysBubsFragment extends Fragment{
	private static final int MONTH_OFFSET = 1;
	ArrayList<Bub> todays_events = null;
    BubRecycleAdapter adapter;
    FloatingActionButton fab;

	public MatTodaysBubsFragment() {}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.material_fragment_todays_bubs, container,
				false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        findTodaysEvents();
        adapter = new BubRecycleAdapter(getActivity(), todays_events);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
		return rootView;
	}
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
        todays_events = HubDatabase.FindEventByDate(date);
    }
}
