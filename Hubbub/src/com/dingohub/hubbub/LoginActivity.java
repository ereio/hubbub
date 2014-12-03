package com.dingohub.hubbub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.dingohub.activities_user.SearchEventsActivity;
import com.dingohub.hub_database.HubDatabase;
import com.parse.ParseObject;
import com.parse.PushService;

public class LoginActivity extends Activity {
	public static final String USER_KEY = "UserKey";
	public static final String PASS_KEY = "PassKey";
	public final static String AUTO_LOG = "AutoLogin";
	public final static String LOGOUT_DEFAULT = "LogoutDefault";
	public static final String LOGIN_SETTINGS = "LoginSettings";

	
	
	ImageView AppLogo;
	Button SignUp;
	Button Login;
	EditText username;
	EditText password;
	
	SharedPreferences settings;
	String savedUsername;
	String savedPassword;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		PushService.setDefaultPushCallback(this,LoginActivity.class);
		// Initializes all static UI elements
		init_ui();
		
		// Initializes all button interactions and event handlers
		init_buttons();
		
		// Checks if past user has set AutoLogin
		auto_login_check();
	}

	private void init_ui(){
		AppLogo = (ImageView) findViewById(R.id.image_hubbub_logo);
		SignUp = (Button) findViewById(R.id.button_sign_up);
		Login = (Button) findViewById(R.id.button_login);
		username = (EditText) findViewById(R.id.edittext_username);
		password = (EditText) findViewById(R.id.edittext_password);
	}
	
	private void init_buttons(){
		
		Login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				authenticate_login(username.getText().toString().trim(), 
								   password.getText().toString().trim());
			}
		});
		
		SignUp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (getApplicationContext(), CreateUserActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		return super.onPrepareOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Checks bundle for login information from SignUp
	// - Checks sharedPrefs for past auto login set
	private void auto_login_check(){
		settings = getSharedPreferences(LOGIN_SETTINGS,0);
		Bundle bundle = getIntent().getExtras();
		boolean unforce_login = false;
		
		// BUNDLE CHECK
		if(bundle != null){
			unforce_login = bundle.getBoolean(LOGOUT_DEFAULT, false);
			
			savedUsername = bundle.getString(USER_KEY);
			savedPassword = bundle.getString(PASS_KEY);
			
			username.setText(savedUsername, TextView.BufferType.EDITABLE);
			password.setText(savedPassword, TextView.BufferType.EDITABLE);
			
		} // AUTO LOGIN CHECK
		else if(settings.contains(USER_KEY) && settings.contains(PASS_KEY) && !unforce_login){
			savedUsername = settings.getString(USER_KEY, null);
			savedPassword = settings.getString(PASS_KEY, null);
			
			boolean autoLogin = settings.getBoolean(AUTO_LOG, false);
			
			username.setText(savedUsername, TextView.BufferType.EDITABLE);
			password.setText(savedPassword, TextView.BufferType.EDITABLE);
			
			if(autoLogin){
				authenticate_login(savedUsername, savedPassword);
			}
		}
	}
	
	// Checks Login Information from Database
	private void authenticate_login(String username, String password){
		
		if(username.length() != 0 && password.length() != 0){
			int result = HubDatabase.AuthoLogin(username, password);
			
			if( HubDatabase.FLAG_QUERY_SUCCESSFUL == result ){
				Bundle bundle = new Bundle();
				bundle.putString(UserMainDisplay.USER_KEY, username);
				bundle.putString(UserMainDisplay.PASS_KEY, password);
				Intent intent = new Intent(getApplicationContext(), UserMainDisplay.class);
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
			else{
				Toast.makeText(getApplicationContext(), "Login Infomation is not valid",
				Toast.LENGTH_LONG).show();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "Missing Username or Password",
			Toast.LENGTH_LONG).show();
		}
	}
	
	// DEBUG ACTIVITES
	private void DEBUG_ToEventSearch(){
		Intent intent = new Intent(getApplicationContext(),SearchEventsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	// DEBUG PARSE
	private void DEBUG_PARSE(){
		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();
	}
}
