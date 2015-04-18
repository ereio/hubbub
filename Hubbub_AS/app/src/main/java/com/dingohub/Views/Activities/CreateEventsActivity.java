package com.dingohub.Views.Activities;


import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;

import com.dingohub.Model.DataAccess.Bub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.Utilities.AlarmReceiver;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Fragments.*;
import com.dingohub.hubbub.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateEventsActivity extends BaseGoogleActivity {
	private String [] ping_time;
	
	
	private static int RESULT_LOAD_IMAGE = 1;
	String evalue;
	
	public static final String CHANNEL_KEY = "CHANNEL_KEY";

    private String UtcTime;
	private EditText start_time;
	private EditText end_time;
	private EditText start_date;
	private EditText end_date;
	private EditText eTags;
	private EditText event_name;
	private ImageButton event_picture;
	private EditText event_location;
	private String set_noon;
	TextView header;
	
	//Details of the event
	private EditText event_details;
	private byte[] picture_bytes;
	
	// store the date and time chosen
	//by the user
	private int time_hour;
	private int time_minute;
	private int date_year;
	private int date_month;
	private int date_day;
	
	
	boolean pictureSelected = false;
	RadioButton privacy;
	Spinner pingIn;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	
	private Button create_button;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alt_activity_create_bub);
		
		 ping_time = new String[]{"0 hours before", "1 hour before","2 hours before","3 hours before",
				"4 hours before","5 hours before","6 hours before","7 hours before","8 hours before"};
		 
		 pingIn = (Spinner) findViewById(R.id.pingIn_spinner);
		 
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item ,ping_time);
		 pingIn.setAdapter(adapter);
		 
		 	start_time = (EditText) findViewById(R.id.bub_start_time);
			end_time = (EditText) findViewById(R.id.bub_end_time);
			start_date = (EditText) findViewById(R.id.bub_start_date);
			end_date = (EditText) findViewById(R.id.bub_end_date);
			event_picture = (ImageButton) findViewById(R.id.bub_image_button);
			create_button = (Button ) findViewById(R.id.bub_create);
			header = (TextView) findViewById(R.id.bub_name_header);
			
			// these are listeners to when the editTexts are pressed
			start_time.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if(hasFocus)
			        {
			          OpenTimePicker(v);
			          evalue = "start_time";
			        }else{
			           //Hide your calender here
			        }
			    }
			});
			
			
			end_time.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if(hasFocus)
			        {
			          OpenTimePicker(v);
			          evalue = "end_time";
			        }else{
			           //Hide your calender here
			        }
			    }
			});
			
			
			
			start_date.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if(hasFocus)
			        {
			          OpenDatePicker(v);
			          evalue = "start_date";
			        }else{
			           //Hide your calender here
			        }
			    }
			});
			
			
			
			end_date.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if(hasFocus)
			        {
			          OpenDatePicker(v);
			          evalue = "end_date";
			        }else{
			           //Hide your calender here
			        }
			    }
			});
			
			
			event_picture.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
					header.setVisibility(View.INVISIBLE);
				}});
		
			
			create_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						CreateEvent(v);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			
		}
	
	//when the create Event button is clicked
		public void CreateEvent ( View v) throws JSONException
		{



            Date d = new Date(pingInActualTime());
            String format = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            SimpleDateFormat dateForm = new SimpleDateFormat (format);

            dateForm.setTimeZone(TimeZone.getTimeZone("UTC"));
            UtcTime = dateForm.format(d) ;

			event_name = (EditText) findViewById(R.id.bub_name);
			event_location  = (EditText) findViewById(R.id.bub_location);
			event_details = (EditText) findViewById(R.id.bub_details);
			eTags = (EditText) findViewById(R.id.bub_tag);


                String event_id = HubDatabase.CreateBub(createEventFromData());
                HubDatabase.AddFollower(event_id, HubDatabase.getCurrentUser().id);
                HubDatabase.AddBubToHub(event_id,convertTags());
                HubDatabase.AddFollowedBub(event_id, HubDatabase.getCurrentUser().id);
                HashMap<String, Object> params = new HashMap<String, Object>();

                params.put("pingIn", UtcTime);
                params.put(CHANNEL_KEY, event_id);
                ParseCloud.callFunctionInBackground("pushNotification", params, new FunctionCallback<String>() {

                    @Override
                    public void done(String object, ParseException e) {
                        // TODO Auto-generated method stub
                        if (e == null)
                            Toast.makeText(getApplicationContext(), "worked", Toast.LENGTH_SHORT).show();

                    }
                });

                ParsePush.subscribeInBackground(event_id);
                Toast.makeText(this,
                        "Event Creation Succeeded", Toast.LENGTH_SHORT).show();
                finish();
           // }
			}
		
	
	//This opens the time picker dialog,it is 
		//called when the button with id = set_time is called
		public void OpenTimePicker(View v)
		{
			DialogFragment fragment = new TimePickerFragment();
			fragment.show(getFragmentManager(),"timePicker");
			
			
		}
		
		
		//This opens the date picker dialog,it is 
		//called when the button with id = set_date is called
		public void OpenDatePicker(View v)
		{
		DialogFragment fragment = new DatePickerFragment();
		fragment.show(getFragmentManager(), "datepicker");
			
		}
	
		
		
		// this function sets the Date in the text View and sets the 
		// date variables to be uploaded to the database
		// It is called from the DatePickerFragment
		public void setDate(View v,int year ,int month,int day)
		{
			EditText showDate;
			
			if(evalue.equals("start_date"))
				showDate = (EditText)findViewById(R.id.bub_start_date);
			else
				showDate = (EditText)findViewById(R.id.bub_end_date);
			
			date_year = year;
			date_month = ++month;
			date_day = day;
			
			showDate.setText(""+day+"/"+month+"/"+year);
			
		}
		
		
		// this function sets the Time in the text View and sets the 
		// time variables to be uploaded to the database
		// it is called from the TimePickerFragment
		public void setTime(View v,int hour,int minute){
			
		EditText showTime;
		if(evalue.equals("start_time")) {
            showTime = (EditText) findViewById(R.id.bub_start_time);
            time_hour = hour;
            time_minute = minute;
        }
		else
			showTime = (EditText)findViewById(R.id.bub_end_time);
		

		
		
		if(hour >= 12)
		{
			if(minute >= 10)
				showTime.setText(""+(hour-12)+": "+minute+ " PM");
			else
				showTime.setText(""+(hour-12)+": 0"+minute+ " PM");
				
			set_noon = "PM";
		}
		else
		{
			if(minute >= 10)
				showTime.setText(""+(hour)+": "+minute + " AM");
			else
				showTime.setText(""+(hour)+": 0"+minute + " AM");
			
			set_noon = "AM";
		}
		}
		
		
	
		private boolean checkTags(){
			boolean check = true;
			String tagString = eTags.getText().toString();
			String[] tagList = tagString.split(",", 5);
			
			// check if more than five tags, only five tags allowed!
			if(tagList.length > 5){
				Toast.makeText(this, 
				"Events cannot have more than 5 tags, sorry :/", Toast.LENGTH_SHORT).show();
				check = false;
			}
			
			// check if tag is too long
			if(tagList[0].length() > 20){
				Toast.makeText(this, 
				"Tags cannot be more than 20 characters long :(", Toast.LENGTH_SHORT).show();
				check = false;
			}
			
			// check if an extra comma or invalid characters where inserted
			//for(String cTag : tagList){
				//if(cTag.contains(DBFunct.INVALID_CHARS)){
				//	Toast.makeText(this,
				//	"Tags cannot contain non-alphabetic characters, except a hashtag", Toast.LENGTH_SHORT).show();
				//	check = false;
				//}
			//}
			
			if(check)
				return true;
			else
				return false;

		}
	
		
	
	
		private JSONArray convertTags(){
			// splits entries into an array based on deliminating commas
			String[] tagStrings = eTags.getText().toString().split("#");
			
			// omits any user inputed hashtags from the database log

            JSONArray tagJSON = new JSONArray();
			for(int i = 0 ; i < tagStrings.length ; i++){
				tagStrings[i] = tagStrings[i].toLowerCase().replaceAll(" ","");
                tagJSON.put(tagStrings[i]);
			}

			

			// returns the array for creation
			return tagJSON;
		}

	//fix the ping in time
		private Bub createEventFromData(){
			Bub newEvent = new Bub();
			JSONArray JSONTags = convertTags();
			newEvent.title = event_name.getText().toString();
			newEvent.location = event_location.getText().toString();



			newEvent.details = event_details.getText().toString().trim();
			newEvent.permissions = "public";

			newEvent.start_time = start_time.getText().toString();
			newEvent.end_time = end_time.getText().toString();

			newEvent.start_date = start_date.getText().toString();
			newEvent.end_date = end_date.getText().toString();

			newEvent.tags = JSONTags;
			newEvent.pingIn_time = Integer.parseInt(pingIn.getSelectedItem().toString().split(" ")[0]);

			// Converts and compresses for the server side data storage
			if(pictureSelected){
				newEvent.picture_title = newEvent.title + " Picture";
				Bitmap temp = ((BitmapDrawable) event_picture.getDrawable()).getBitmap();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				temp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
				newEvent.picture = baos.toByteArray();
			}
			
			
			return newEvent;
		}



    private long pingInActualTime()
    {

        Calendar c = new GregorianCalendar(date_year,date_month-1,date_day,time_hour,time_minute);
        c.add(c.HOUR,-1*Integer.parseInt(pingIn.getSelectedItem().toString().split(" ")[0]) );

        return c.getTimeInMillis();


    }
////////////////////////////////////////////////////////////////
// Get tapp in system time
///////////////////////////////////////////////////////////////
		private long pingInTimeInMillis(){
			EditText showDate = (EditText)findViewById(R.id.bub_start_date);
			EditText showTime = (EditText)findViewById(R.id.bub_start_time);

			String[] stime = showTime.getText().toString().split("[: ]+");
			String[] sdate = showDate.getText().toString().split("/");

			int tappInVal = Integer.parseInt(pingIn.getSelectedItem().toString().split(" ")[0]);

			int[] tt = new int[5];
			//year
			tt[0] = Integer.parseInt(sdate[2].trim());
			//month (Jan = 0)
			tt[1] = Integer.parseInt(sdate[1].trim())-1;
			//day
			tt[2] = Integer.parseInt(sdate[0].trim());

			//hour
			if(stime[2].trim().equals("PM"))
				tt[3] = Integer.parseInt(stime[0].trim()) + 12;
			else
				tt[3] = Integer.parseInt(stime[0].trim());

			//minutes
			tt[4] = Integer.parseInt(stime[1].trim());

			Calendar cal = Calendar.getInstance();

			cal.set(tt[0],tt[1],tt[2],tt[3],tt[4],0);
			cal.add(Calendar.HOUR_OF_DAY, - tappInVal);

			Log.i("CreateEvent",DateFormat.getDateTimeInstance().format(cal.getTime()));
			Log.i("CreateEvent","Tapp in: "+tappInVal+" hrs before");

	return cal.getTimeInMillis();
}

	//////////////////////////////////////////
	// Convert system time to time since boot
	//////////////////////////////////////////
	public long sysTimeToTSB(long sysTimeOfEvent){
		long sysBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();

		return sysTimeOfEvent - sysBootTime;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			// Decodes the file to a bitmap
			// Runs async task to shrink the photo and set the imagebutton to the picture
			// server side preperation doesn't begin until the user hits the Create button
			BitmapWorker worker = new BitmapWorker(event_picture, picturePath, 200, 200);
			worker.execute(0);
			pictureSelected = true;
		}
	}
	
	
		
		
	
}
