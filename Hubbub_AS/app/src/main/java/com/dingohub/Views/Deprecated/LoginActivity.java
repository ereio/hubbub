package com.dingohub.Views.Deprecated;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.SharedPrefKeys;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Activities.CreateUserActivity;
import com.dingohub.Views.Activities.DevActivities.MatUserMainDisplay;
import com.dingohub.hubbub.R;
import com.parse.PushService;

public class LoginActivity extends BaseGoogleActivity {

	// bundle attribute
	public final static String LOGOUT_DEFAULT = "LogoutDefault";

	ImageView AppLogo;
	Button SignUp;
	Button Login;
	EditText username;
	EditText password;
	
	SharedPreferences settings;
	String savedUsername;
	String savedPassword;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		PushService.setDefaultPushCallback(this,LoginActivity.class);
		// Initializes all static UI elements
		init_ui();

        // initalizes background footage for login page
        init_bg_video();

		// Initializes all button interactions and event handlers
		init_buttons();
		
		// Checks if past user has set AutoLogin
		auto_login_check();
		
		// Init key enter checks
		key_check();
	}

	private void key_check() {
			username.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//Toast.makeText(getApplicationContext(), Integer.toString(keyCode), Toast.LENGTH_SHORT);
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
					password.requestFocus();
					return true;
				}
					return false;
			}});
		
			password.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(password.getWindowToken(),0);
					Login.requestFocus();
					return true;
				}
					return false;
			}});
		
	}

    private void init_bg_video(){

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
	
	// Checks Login Information from Database
	private void authenticate_login(String username, String password){
		
		if(username.length() != 0 && password.length() != 0){
			int result = HubDatabase.AuthoLogin(username, password);
			
			if( HubDatabase.FLAG_QUERY_SUCCESSFUL == result ){
				SharedPreferences.Editor editSettings = settings.edit();
				editSettings.putString(SharedPrefKeys.USER_KEY, username);
				editSettings.putString(SharedPrefKeys.PASS_KEY, password);
                editSettings.putBoolean(SharedPrefKeys.AUTO_LOG, true);
				editSettings.apply();
				Intent intent = new Intent(getApplicationContext(), MatUserMainDisplay.class);
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
	
	// Checks bundle for login information from SignUp
	// - Checks sharedPrefs for past auto login set
	private void auto_login_check(){
		settings = getSharedPreferences(SharedPrefKeys.LOGIN_SETTINGS,0);
		boolean autoLogin = settings.getBoolean(SharedPrefKeys.AUTO_LOG, false);
		savedUsername = settings.getString(SharedPrefKeys.USER_KEY, null);
		savedPassword = settings.getString(SharedPrefKeys.PASS_KEY, null);
		
		if(savedUsername != null && savedPassword != null){
			username.setText(savedUsername, TextView.BufferType.EDITABLE);
			password.setText(savedPassword, TextView.BufferType.EDITABLE);
			
			if(autoLogin){
				authenticate_login(savedUsername, savedPassword);
			}			
		}
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
}