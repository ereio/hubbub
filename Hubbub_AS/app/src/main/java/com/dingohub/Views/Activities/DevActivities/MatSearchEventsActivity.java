package com.dingohub.Views.Activities.DevActivities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Fragments.MatSearchedEventsFragment;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.hubbub.R;


public class MatSearchEventsActivity extends BaseGoogleActivity{
	public static final String TAG_KEY = "TagKey";
	EditText eTags;
	Toolbar toolbar;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_activity_search_events);

        // Sets the ui for the search activity
        init_ui();

        // Sets the liseners for when the user inputs tags
        set_search_listener();

        // init the search for tags with message
        searchTags(null);

	}

    private void init_ui() {
        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MatSearchEventsActivity.super.onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void set_search_listener(){
        eTags = (EditText) findViewById(R.id.edittext_search_tag);

        eTags.setOnEditorActionListener( new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                        if (!event.isShiftPressed() && checkTags(eTags.getText().toString()))
                            searchTags(eTags.getText().toString());
                        return true;
                    }
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

    private void searchTags(String tag){

            FragmentManager manager = getFragmentManager();

            // Creates bundle to pass arguments
            Bundle bundle = new Bundle();
            if(tag != null)
                bundle.putString(TAG_KEY, tag.toLowerCase().trim());
            else
                bundle.putString(TAG_KEY, tag);

            // Sets arguments for new eventList from searched tag
            MatSearchedEventsFragment eventList = new MatSearchedEventsFragment();
            eventList.setArguments(bundle);

            manager.beginTransaction().replace(R.id.fragment_search, eventList).commit();
    }
	
	private boolean checkTags(String tag){
		if(tag.contains(HubDatabase.INVALID_CHARS)){
			Toast.makeText(getApplicationContext(), "Tags cannot contain invalid characters",
			Toast.LENGTH_LONG).show();
			return false;
		}

		if(tag.equals("")){
			Toast.makeText(getApplicationContext(), "Have to type a tag first!",
			Toast.LENGTH_LONG).show();
            return false;
		}

        if(tag.contains(" ")){
            Toast.makeText(getApplicationContext(), "Cannot search multiple tags or multi-word tags",
                    Toast.LENGTH_LONG).show();
            return false;
        }
		return true;
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

}
