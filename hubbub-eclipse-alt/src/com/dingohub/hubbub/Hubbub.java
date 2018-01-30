package com.dingohub.hubbub;

import android.app.Application;

public class Hubbub extends Application {

	public void onCreate(){
		Parse.initialize(this, "zSa7P3L6UeletXOg3ivSGyCkfLOy7NVQxPq4HshT", "56gYe0e3MNH0sWPLKen0XD9kmf0BrtvbyyxyQuuN");
	}
}
