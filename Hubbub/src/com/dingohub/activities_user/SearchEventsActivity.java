package com.dingohub.activities_user;

import com.dingohub.fragments_user.SearchedEventsFragment;
import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hubbub.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SearchEventsActivity extends Activity{
	public static final String TAG_KEY = "TagKey";
	EditText eTags;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_events);
		eTags = (EditText) findViewById(R.id.edittext_search_tag);
		
		eTags.setOnEditorActionListener( new EditText.OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
				   event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					
					if(!event.isShiftPressed())
						searchTags(eTags.getText().toString());
						return true;
					}
				return false;
			}
		});
		
		eTags.setOnFocusChangeListener( new EditText.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					searchTags(eTags.getText().toString());
				}
			}
		});
	}
	
	private boolean checkTags(String tag){
		if(tag.contains(HubDatabase.INVALID_CHARS)){
			Toast.makeText(getApplicationContext(), "Tags cannot contain invalid characters",
			Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	private void searchTags(String tag){
		if(checkTags(tag)){
			FragmentManager manager = getFragmentManager();
			SearchedEventsFragment eventList = new SearchedEventsFragment(tag);
			manager.beginTransaction().replace(R.id.fragment_search_bubs, eventList).commit();
		}
	}
}
