package com.dingohub.Model.Utilities;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dingohub.Model.DataAccess.HubDatabase;

public class BitmapWorker extends AsyncTask<Object, Void, Bitmap> {
    private WeakReference<ImageButton> imageButtonReference = null;
    private WeakReference<ImageView> imageViewReference = null;

    private Object picture;
    private int rWidth;
    private int rHeight;
    
    public BitmapWorker(ImageButton imageButton, Object picture, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the imageButton can be garbage collected
        imageButtonReference = new WeakReference<ImageButton>(imageButton);
        this.picture = picture;
    	rWidth = reqWidth;
    	rHeight = reqHeight;
    }
    
    public BitmapWorker(ImageView imageButton, Object picture, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the imageButton can be garbage collected
    	imageViewReference = new WeakReference<ImageView>(imageButton);
        this.picture = picture;
    	rWidth = reqWidth;
    	rHeight = reqHeight;
    }
    
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Object... params) {
        try {
			return HubDatabase.decodePicture(picture, rWidth, rHeight);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    // Once complete, see if imageButton is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageButtonReference != null && bitmap != null) {
            ImageButton imageButton = imageButtonReference.get();
            if (imageButton != null)
                imageButton.setImageBitmap(bitmap);
                
        } else if (imageViewReference != null && bitmap != null){
        	ImageView imageview = imageViewReference.get();
            if (imageview != null)
            	imageview.setImageBitmap(bitmap);
        } else {
        	
        }
    }
}
