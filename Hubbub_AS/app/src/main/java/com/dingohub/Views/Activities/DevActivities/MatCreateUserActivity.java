package com.dingohub.Views.Activities.DevActivities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.DataAccess.SharedPrefKeys;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.Views.Deprecated.LoginActivity;
import com.dingohub.hubbub.R;

import java.io.ByteArrayOutputStream;

public class MatCreateUserActivity extends BaseGoogleActivity {

	EditText eUsername;
	EditText ePass;
	EditText eFirstName;
	EditText eLastName;
	EditText eAuthPass;
	EditText eEmail;
	EditText eDOB;
	EditText eDetail;

	Button bCreate;
    TextView bBackgroundPrompt;
	ImageView bProfilePic;
    ImageButton bBackgroundPic;

	String mUsername = null;
	String mFirstname = null;
	String mLastname = null;
	String mEmail = null;
	String mPass = null;
	String mAuthPass = null;
	String mDetails = null;
	String mDOB = null;
	byte[] mProfilePic = null;

	SharedPreferences prefs;
	boolean pictureSelected = false;

	HubUser newuser;
    private Toolbar toolbar;

    int CURRENT_DECODING = 0;
    final static int PROFILE_BACKGROUND = 1;
    final static int PROFILE_PICTURE = 2;

	private static int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.material_activity_signup);
		// Init Variables
		init_ui();

		// Init OnClicks
		init_onclick();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.clear();
		return true;
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

	private void init_ui(){
		eUsername = (EditText) findViewById(R.id.entry_username);
		eUsername.setTextColor(Color.DKGRAY);
		ePass = (EditText) findViewById(R.id.entry_pass);
		eAuthPass = (EditText) findViewById(R.id.entry_confirm_pass);
		eFirstName = (EditText) findViewById(R.id.entry_f_name);
		eLastName = (EditText) findViewById(R.id.entry_l_name);
		eEmail = (EditText) findViewById(R.id.entry_email);
		eDOB = (EditText) findViewById(R.id.entry_dob);
		eDetail = (EditText) findViewById(R.id.entry_about_me);

		bCreate = (Button) findViewById(R.id.button_create_user);
        bBackgroundPic = (ImageButton) findViewById(R.id.user_background_image);
        bProfilePic = (ImageView) findViewById(R.id.imagebutton_account_picture);
        bBackgroundPrompt = (TextView) findViewById(R.id.profile_background_prompt);
	}

	private void init_onclick(){
        SetDrawerAsBackButton(false, 0);

		bCreate.setOnClickListener(new OnClickListener(){
			int result = HubDatabase.FLAG_QUERY_FAILED;

			@Override
			public void onClick(View v) {
				if(check_fields()){
					create_hubbub_user();
					result = HubDatabase.CreateUser(newuser, mPass, mAuthPass, mDOB);

					if(result == HubDatabase.FLAG_NULL_QUERY){
						Toast.makeText(getApplicationContext(), "An unspecified error has occurred, please try again later",
						Toast.LENGTH_LONG).show();
					}
					else if(result == HubDatabase.FLAG_QUERY_FAILED) {
                        if (!mEmail.contains("@")) {
                            Toast.makeText(getApplicationContext(), "An invalid email address was entered. Try a different email",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Server could not create user :(",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
					else if(result == HubDatabase.FLAG_QUERY_SUCCESSFUL){
						Toast.makeText(getApplicationContext(), "Account creation success",
						Toast.LENGTH_SHORT).show();
						finalize_creation();
					}
					else{
						Toast.makeText(getApplicationContext(), "There is some weird Parse shit going on",
						Toast.LENGTH_SHORT).show();
					}
				}
			}});

		bProfilePic.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
                CURRENT_DECODING = PROFILE_PICTURE;
			}});

        bBackgroundPic.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                bBackgroundPrompt.setVisibility(View.INVISIBLE);
                CURRENT_DECODING = PROFILE_BACKGROUND;
            }});

	}
	private void finalize_creation(){
		SharedPreferences.Editor eSettings = getSharedPreferences(SharedPrefKeys.LOGIN_SETTINGS,0).edit();
		eSettings.putString(SharedPrefKeys.USER_KEY, mUsername);
		eSettings.putString(SharedPrefKeys.PASS_KEY, mPass);
		eSettings.commit();
		
		Intent login = new Intent(getApplicationContext(), MatLoginActivity.class);
		login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(login);
	}
	
	public boolean check_fields(){
		clearFieldError();
		boolean status = true;
		
		// MAKE A MAX CHAR CHECK OF 16
		if(eUsername.getText().toString().trim().length() < 4){
			eUsername.setHintTextColor(Color.RED);
			Toast.makeText(getApplicationContext(), "Username should be more than 4 Characters", 
			Toast.LENGTH_SHORT).show();
			test_entry();
			status = false;
		}
		
		if(ePass.getText().toString().trim().length() < 6){
			ePass.setTextColor(Color.RED);
			eAuthPass.setTextColor(Color.RED);
			ePass.setHintTextColor(Color.RED);
			eAuthPass.setHintTextColor(Color.RED);
			Toast.makeText(getApplicationContext(), "Passwords should be more than 6 Characters", 
			Toast.LENGTH_SHORT).show();
			status = false;
		}
		
		if(eUsername.getText().toString().contains(" \\'=-.,;!@#$%^&*){}|")){
			eUsername.setTextColor(Color.RED);
			Toast.makeText(getApplicationContext(), "Username cannot contain spaces or special Characters", 
			Toast.LENGTH_SHORT).show();
		}
		
		if(!ePass.getText().toString().equals(eAuthPass.getText().toString())){
			ePass.setTextColor(Color.RED);
			eAuthPass.setTextColor(Color.RED);
			Toast.makeText(getApplicationContext(), "Passwords do not match", 
			Toast.LENGTH_SHORT).show();
			status = false;
		}

		if(!test_entry()){
			Toast.makeText(getApplicationContext(), "One or more fields need to be filled in", 
			Toast.LENGTH_SHORT).show();
			status = false;
		}


		if(status)
			extract_fields();

        return status;
		
	}
	
	public void clearFieldError(){
        int color = getResources().getColor(R.color.ColorPrimary);

		eUsername.setTextColor(color);
		eFirstName.setTextColor(color);
		eLastName.setTextColor(color);
		ePass.setTextColor(color);
		eAuthPass.setTextColor(color);
		eEmail.setTextColor(color);
		eDOB.setTextColor(color);
		
		eUsername.setHintTextColor(Color.GRAY);
		eFirstName.setHintTextColor(Color.GRAY);
		eLastName.setHintTextColor(Color.GRAY);
		ePass.setHintTextColor(Color.GRAY);
		eAuthPass.setHintTextColor(Color.GRAY);
		eEmail.setHintTextColor(Color.GRAY);
		eDOB.setHintTextColor(color);
		
	}
	
	public boolean test_entry(){
		
		boolean status = true;

		if(!(eUsername.getText().toString().length() > 0)){
			eUsername.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(eFirstName.getText().toString().length() > 0)){
			eFirstName.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(eLastName.getText().toString().length() > 0)){
			eLastName.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(ePass.getText().toString().length() > 0)){
			ePass.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(eAuthPass.getText().toString().length() > 0)){
			eAuthPass.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(eEmail.getText().toString().length() > 0)){
			eEmail.setHintTextColor(Color.RED);
			status = false;
		}
		if(!(eDOB.getText().toString().length() > 0)){
			eDOB.setHintTextColor(Color.RED);
			status = false;
		}	

			return status;
	}
	
	public void extract_fields(){
			mUsername = eUsername.getText().toString().trim();
			mFirstname  = eFirstName.getText().toString().trim();
			mLastname = eLastName.getText().toString().trim();
			mPass = ePass.getText().toString().trim();
			mAuthPass = eAuthPass.getText().toString().trim();
			mEmail = eEmail.getText().toString().trim();
			mDOB = eDOB.getText().toString().trim();
			mDetails = eDetail.getText().toString().trim();
			
			// Converts and compresses for the server side data storage
			if(pictureSelected){
				Bitmap temp = ((BitmapDrawable)bProfilePic.getDrawable()).getBitmap();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				temp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
				mProfilePic = baos.toByteArray();
			}
    }
	
	private void create_hubbub_user(){
		newuser = new HubUser();
		newuser.username = mUsername;
		newuser.firstname = mFirstname;
		newuser.lastname = mLastname;
		newuser.email = mEmail;
        newuser.location = getLocality();
		newuser.details = mDetails;
		newuser.picture = mProfilePic;
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
	
			// Runs async task to shrink the photo and set the imagebutton to the picture
			// server side preperation doesn't begin until the user hits the Create button
            if(CURRENT_DECODING == PROFILE_BACKGROUND){
                BitmapWorker worker = new BitmapWorker(bBackgroundPic, picturePath, 800, 400);
                worker.execute(0);
            } else {
                BitmapWorker worker = new BitmapWorker(bProfilePic, picturePath, 250, 250);
                worker.execute(0);
            }

            CURRENT_DECODING = 0;
			pictureSelected = true;

		}
	}
	
}
