package com.dingohub.Views.DevFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.DataAccess.HubUser;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.hubbub.R;

public class MatUserProfileFragment extends Fragment{
	ImageView iProfilePic;
	
	TextView tUsername;
	TextView tFullName;
	TextView tEmail;
	TextView tAbout;
	
	HubUser user;
	
	public MatUserProfileFragment() {
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
        View rootView = inflater.inflate(R.layout.material_fragment_todays_bubs, container,
                false);
        return rootView;
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
