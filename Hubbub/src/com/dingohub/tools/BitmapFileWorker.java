package com.dingohub.tools;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageButton;

import com.dingohub.hub_database.HubDatabase;

public class BitmapFileWorker extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageButton> imageButtonReference;
    private int data = 0;
    private String filepath;
    private int rWidth;
    private int rHeight;
    
    public BitmapFileWorker(ImageButton imageButton, String path, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the imageButton can be garbage collected
        imageButtonReference = new WeakReference<ImageButton>(imageButton);
        filepath = path;
    	rWidth = reqWidth;
    	rHeight = reqHeight;
    }
    
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return HubDatabase.decodeSampledBitmapFile(filepath, rWidth, rHeight);
    }

    // Once complete, see if imageButton is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageButtonReference != null && bitmap != null) {
            final ImageButton imageButton = imageButtonReference.get();
            if (imageButton != null) {
                imageButton.setImageBitmap(bitmap);
            }
        }
    }
}
