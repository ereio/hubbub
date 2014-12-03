package com.dingohub.tools;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hub_database.Bub;
import com.dingohub.hubbub.R;



public class EventListAdapter extends ArrayAdapter<Bub> {
	private final Context context;
	private final ArrayList<Bub> tEvents;
	
	public EventListAdapter(Context context, ArrayList<Bub> events) {
		super(context, R.layout.listitem_bub, events);
		this.context = context;
		tEvents = events;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isEnabled(int position){
		super.isEnabled(position);
		return true;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.listitem_bub, parent , false);
		
		TextView name = (TextView) row.findViewById(R.id.listitem_fbub_title);
		TextView time = (TextView) row.findViewById(R.id.listitem_bub_time);
		TextView location = (TextView) row.findViewById(R.id.listitem_fbub_location);
		ImageView picture = (ImageView) row.findViewById(R.id.listitem_fbub_picture);
		
		// Gets the event needed from array in class
		Bub event = tEvents.get(position);
		
		name.setText(event.title);
		time.setText("At " + event.start_time + " on " + event.start_date);
		location.setText(event.details);
		if(event.picture != null){
			BitmapByteWorker worker = new BitmapByteWorker(picture, event.picture, 250, 250);
			worker.execute(0);
		}
		
		return row;
	}
}