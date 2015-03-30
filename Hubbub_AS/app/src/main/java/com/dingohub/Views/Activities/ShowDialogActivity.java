package com.dingohub.Views.Activities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



import com.dingohub.hubbub.R;
import com.dingohub.Domain.DataAccess.Bub;
import com.dingohub.Domain.DataAccess.HubDatabase;
import com.dingohub.Domain.Utilities.MyParseReceiver;

public class ShowDialogActivity extends Activity {

	
	private Button yes_button;
	private Button no_button;
	private Button maybe_button;
	private TextView event_title;
	private TextView event_location;
	private TextView event_time;
	private TextView event_start_date;
	//private ImageView event_picture;
	private TextView num_yes;
	private TextView num_no;
	private TextView num_maybe;
	
	Bub event;
	private Handler mHandler;
	ScheduledExecutorService ses;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_show_dialog_1);
	    
	    Bundle bundle = getIntent().getExtras();
	    final String event_id = bundle.getString(MyParseReceiver.DATA_KEY);
	    
	    event = HubDatabase.GetEventById(event_id);
	    
	    // Sets the ui edittext and such variables
	    init_ui();
	    
	    // Sets the event variables themselves
	    set_ui();
	    
		yes_button = (Button) findViewById(R.id.buttons_yes);
	    no_button = (Button) findViewById(R.id.button_no);
	    maybe_button = (Button) findViewById(R.id.buttons_maybe);
	    
	    yes_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				HubDatabase.RSVP(HubDatabase.RESPONSE_YES, event_id);
				while(!ses.isShutdown())
					ses.shutdown();
				finish();
			}
		});
	    no_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				HubDatabase.RSVP(HubDatabase.RESPONSE_NO, event_id);
				while(!ses.isShutdown())
					ses.shutdown();
				finish();
			}
		});
	    maybe_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				HubDatabase.RSVP(HubDatabase.RESPONSE_MAYBE, event_id);
				while(!ses.isShutdown())
					ses.shutdown();
				finish();
			}
		});
	    
	   check_tappin(event_id);
	}

	public void init_ui(){
		    event_title = (TextView) findViewById(R.id.dialog_event_title);
		    event_location = (TextView) findViewById(R.id.dialog_bub_location);
		    event_time = (TextView) findViewById(R.id.dialog_bub_time);
		    event_start_date = (TextView) findViewById(R.id.dialog_bub_date);
		  //  event_picture = (ImageView) findViewById(R.id.dialog_event_picture);
		    
		    num_yes = (TextView) findViewById(R.id.dialog_text_num_yes);
		    num_no = (TextView) findViewById(R.id.dialog_text_num_no);
		    num_maybe = (TextView) findViewById(R.id.dialog_text_num_maybe);
    
	}
	
	public void set_ui(){
		
	    event_title.setText(event.title);
	    event_location.setText(event.location);
	    event_time.setText(event.start_time);
	    event_start_date.setText(event.start_date);
	    num_yes.setText(Integer.toString(event.yes_counter));
		num_no.setText(Integer.toString(event.no_counter));
		num_maybe.setText(Integer.toString(event.maybe_counter));
		
	}
	
	public void check_tappin(String id){
		final String EID = id;
		ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				final Bub counterEvent = HubDatabase.GetEventById(EID);
				
				num_yes.post(new Runnable(){
					@Override
					public void run() {
						num_yes.setText(Integer.toString(counterEvent.yes_counter)); 
					}
				});
				num_no.post(new Runnable(){
					@Override
					public void run() {
						num_no.setText(Integer.toString(counterEvent.no_counter)); 
					}
				});
				num_maybe.post(new Runnable(){
					@Override
					public void run() {
						num_maybe.setText(Integer.toString(counterEvent.maybe_counter));
					}
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	
	}
	
}
