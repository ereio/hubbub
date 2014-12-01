package com.dingohub.fragments_user;

import java.util.ArrayList;
import java.util.Calendar;

import com.dingohub.activities_user.ViewEventActivity;
import com.dingohub.hub_database.Bub;
import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hubbub.R;
import com.dingohub.hubbub.UserMainDisplay;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


// note : make this fragment refresh ! without having to reopen the activity.
public class TodaysBubsFragment extends ListFragment{
	ArrayList<Bub> todays_events;
	
	public TodaysBubsFragment() {
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        month++;
       
        int year = c.get(Calendar.YEAR);
        String date = "" + day +"/"+ month+"/" + year;
		todays_events = HubDatabase.FindEventByDate(date);
		
		BubListAdapter adapter = new BubListAdapter(getActivity(), todays_events);
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_todays_bubs, container,
				false);
		return rootView;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		// Intent intent = new Intent(getActivity(), ViewEventFragment.class);
		// Will need to replace the layout's fragment or start a new activity
		// .... haven't decided.
		Intent intent = new Intent(getActivity().getApplicationContext(),ViewEventActivity.class);
		startActivity(intent);
	}
	
	
	
	// Adapter to handle all event list objects. Should be seperated from this class.
	public class BubListAdapter extends ArrayAdapter<Bub> {
		private final Context context;
		private final ArrayList<Bub> mBub;
		
		public BubListAdapter(Context context, ArrayList<Bub> events) {
			super(context, R.layout.listitem_bub, events);
			this.context = context;
			mBub = events;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.listitem_bub, parent ,false);
			
			TextView name = (TextView) row.findViewById(R.id.listitem_bub_title);
			TextView time = (TextView) row.findViewById(R.id.listitem_bub_time);
			TextView location = (TextView) row.findViewById(R.id.listitem_bub_location);
			//ImageView picture = (ImageView) row.findViewById(R.id.imageView1);
			
			// Gets the event needed from array in class
			Bub event = mBub.get(position);
			
			name.setText(event.title);
			time.setText("At " + event.start_date + " on " + event.start_time);
			location.setText(event.details);
			//picture.setImageBitmap(bm);
			return row;
		}
	}
}
