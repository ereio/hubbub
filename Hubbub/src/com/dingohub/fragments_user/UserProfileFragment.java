package com.dingohub.fragments_user;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hub_database.HubDatabase;
import com.dingohub.hub_database.HubUser;
import com.dingohub.hubbub.R;

public class UserProfileFragment extends Fragment{
	ImageView iProfilePic;
	
	TextView tUsername;
	TextView tFullName;
	TextView tEmail;
	TextView tDetail;
	
	HubUser user;
	
	public UserProfileFragment() {
	}

	@Override
	public void onStart(){
		super.onStart();
		ui_init();
		user = HubDatabase.getCurrentUser();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		set_ui();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_currentuser_profile, container,
				false);
	}
	
	private void ui_init(){
		iProfilePic = (ImageView) getView().findViewById(R.id.image_profile_picture);
		tUsername = (TextView) getView().findViewById(R.id.text_username);
		tFullName = (TextView) getView().findViewById(R.id.text_fullname);
		//tEmail = (TextView) getView().findViewById(R.id.text_email);
		//tDetail = (TextView) getView().findViewById(R.id.text_details);
	}
	
	private void set_ui(){
		tUsername.setText(user.username.toString());
		tFullName.setText(user.firstname.toString() + " " + user.lastname.toString());
		//tEmail.setText(user.email.toString());
		//tDetail.setText(user.details.toString());
	}
}
