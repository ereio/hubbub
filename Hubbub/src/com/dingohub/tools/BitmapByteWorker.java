package com.dingohub.tools;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.dingohub.hub_database.HubDatabase;

public class BitmapByteWorker extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> ImageViewReference;
    private int data = 0;
    private byte[] filepath;
    private int rWidth;
    private int rHeight;
    
    public BitmapByteWorker(ImageView ImageView, byte[] path, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        ImageViewReference = new WeakReference<ImageView>(ImageView);
        filepath = path;
    	rWidth = reqWidth;
    	rHeight = reqHeight;
    }
    
    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return HubDatabase.decodeSampledBitmapBytes(filepath, rWidth, rHeight);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (ImageViewReference != null && bitmap != null) {
            final ImageView ImageView = ImageViewReference.get();
            if (ImageView != null) {
                ImageView.setImageBitmap(bitmap);
            }
        }
    }
}
