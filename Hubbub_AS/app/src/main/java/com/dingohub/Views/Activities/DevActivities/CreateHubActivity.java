package com.dingohub.Views.Activities.DevActivities;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dingohub.Hubbub;
import com.dingohub.Model.DataAccess.Hub;
import com.dingohub.Model.DataAccess.HubDatabase;
import com.dingohub.Model.Utilities.BitmapWorker;
import com.dingohub.Views.Activities.BaseActivities.BaseGoogleActivity;
import com.dingohub.hubbub.R;
import com.parse.ParsePush;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;

public class CreateHubActivity extends BaseGoogleActivity {
	private static int RESULT_LOAD_IMAGE = 1;

	public static final String CHANNEL_KEY = "CHANNEL_KEY";
    public static final String HUBS_CHANNELS = "HUBS_CHANNELS";
    public static final String PUSH_TYPE = "PUSH_TYPE";

	private EditText hub_name;
	private ImageButton event_picture;
	private EditText hub_location;
	TextView header;

	//Details of the event
	private EditText hub_details;
	private byte[] picture_bytes;

	// store the date and time chosen
	//by the user
	boolean pictureSelected = false;
	private Button create_button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_hub);

        init_ui();

        init_onClickListeners();

	}

    private void init_ui(){

        SetDrawerAsBackButton(false, 50);

        event_picture = (ImageButton) findViewById(R.id.user_background_image);
        create_button = (Button ) findViewById(R.id.hub_create_button);
        hub_name = (EditText) findViewById(R.id.hub_name);
        hub_location = (EditText) findViewById(R.id.hub_location);
        hub_details = (EditText) findViewById(R.id.hub_details);
        header = (TextView) findViewById(R.id.background_instructions);
    }

    private void init_onClickListeners(){

        event_picture.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }});


        create_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    CreateHub(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //when the create Event button is clicked
    public void CreateHub ( View v) throws JSONException {

        String hub_id = HubDatabase.CreateHub(createHubFromData());
        HubDatabase.AddFollowerToHub(hub_id, HubDatabase.GetCurrentUser().id);


        //subscribes user to the bub created
        ParsePush.subscribeInBackground(hub_id);

        if(Hubbub.DEBUG) {
            Toast.makeText(this, "Hub Creation Succeeded", Toast.LENGTH_SHORT).show();
        }
            finish();
    }


    //fix the ping in time
    private Hub createHubFromData(){
        Hub newHub = new Hub();

        newHub.name = hub_name.getText().toString();
        newHub.location = hub_location.getText().toString();
        newHub.geolocation = HubDatabase.locality;
        if(newHub.geolocation == null)
            newHub.geolocation = HubDatabase.GetCurrentUser().location;

        newHub.details = hub_details.getText().toString().trim();


        // Converts and compresses for the server side data storage
        if(pictureSelected){
            newHub.picture_title = newHub.name + "_picture";
            Bitmap temp = ((BitmapDrawable) event_picture.getDrawable()).getBitmap();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            newHub.picture = baos.toByteArray();
        }

        return newHub;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        GetLocalPicture(requestCode, resultCode, data);
	}

    private void GetLocalPicture(int requestCode, int resultCode, Intent data){
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // Decodes the file to a bitmap
            // Runs async task to shrink the photo and set the imagebutton to the picture
            // server side preperation doesn't begin until the user hits the Create button
            BitmapWorker worker = new BitmapWorker(event_picture, picturePath, 400, 300);
            worker.execute(0);
            pictureSelected = true;
            header.setVisibility(View.INVISIBLE);
        }
    }
	
	
		
		
	
}
