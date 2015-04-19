package com.dingohub.Views.Deprecated;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.hubbub.R;
import com.dingohub.Model.Utilities.BitmapWorker;

public class UserProfileFragment extends Fragment{
	ImageView iProfilePic;
	
	TextView tUsername;
	TextView tFullName;
	TextView tEmail;
	TextView tAbout;
	
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
		return inflater.inflate(R.layout.fragment_user_profile, container,
				false);
	}
	
	private void ui_init(){
		iProfilePic = (ImageView) getView().findViewById(R.id.image_profile_picture);
		tUsername = (TextView) getView().findViewById(R.id.text_username);
		tFullName = (TextView) getView().findViewById(R.id.text_fullname);
		tAbout = (TextView) getView().findViewById(R.id.cardview_text_about);
	}
	
	private void set_ui(){
		tUsername.setText(user.username.toString());
		tFullName.setText(user.firstname.toString() + " " + user.lastname.toString());
		tAbout.setText(user.details.toString());
		
		if(user.picture != null){
			BitmapWorker worker = new BitmapWorker(iProfilePic, user.picture, 250, 250);
			worker.execute(0);
		}
	}
}
