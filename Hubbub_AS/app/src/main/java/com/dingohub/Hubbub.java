package com.dingohub;

import android.app.Application;

import com.dingohub.Domain.DataAccess.HubDatabase;

public class Hubbub extends Application {
	
	@Override
	public void onCreate(){
			super.onCreate();
			HubDatabase.init_db(getApplicationContext());
	}
}
