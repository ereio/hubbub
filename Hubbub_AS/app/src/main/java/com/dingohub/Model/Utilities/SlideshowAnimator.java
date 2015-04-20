package com.dingohub.Model.Utilities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingohub.hubbub.R;

import java.util.Random;

/**
 * Created by ereio on 4/19/15.
 */
public class SlideshowAnimator implements Runnable {

    private static int IMAGES[] = {R.drawable.intro_image1, R.drawable.intro_image2, R.drawable.intro_image3,
                                   R.drawable.intro_image4, R.drawable.intro_image5, R.drawable.intro_image6};
    private static int COLOR[] = {R.color.ColorAccent, R.color.ColorPrimary, R.color.ColorPrimaryDark};
    private static int currentColor = 0;
    private static int currentPicture = 0;
    
    final int BG_WIDTH = 850;
    final int BG_HEIGHT = 1400;

    Random randomIndex = new Random();
    int cropWidth = 0;
    int cropHeight = 0;

    Bitmap scaledBitmap;
    Bitmap backgroundBitmap;
    final AnimationSet animationSet = new AnimationSet(true);
    final Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
    final Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);

    final Animation.AnimationListener animationListener = new Animation.AnimationListener(){
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            StartNewSlide();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private int imageIndex = 0;
    ImageView backgroundImage;
    Context context;
    Activity activity;

    public SlideshowAnimator(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }
    @Override
    public void run() {
        StartNewSlide();
    }


    private void StartNewSlide() {
        background_animation();
        logo_animation();
    }

    private void logo_animation(){

        int colorIndex = randomIndex.nextInt(3);
        int colorFrom = currentColor;
        int colorTo = currentColor = COLOR[colorIndex];

        // TODO - ANIMATION - move to looper thread only
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView appName = (TextView) activity.findViewById(R.id.textview_app_name);
                appName.setTextColor(context.getResources().getColor(currentColor));

            }
        });

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        // TODO - ANIMATION - can only be run on a looper thread
        /*
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

            }
        });


        colorAnimation.start();
        */
    }

    private void background_animation(){
        cropHeight = 0;
        cropWidth = 0;


        imageIndex %= IMAGES.length;
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(),
                IMAGES[imageIndex]);

        find_crop_dimentions();

        backgroundBitmap = backgroundBitmap.createBitmap(backgroundBitmap, 0, 0,
                backgroundBitmap.getWidth()-cropWidth, backgroundBitmap.getHeight()-cropHeight);

        scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, BG_WIDTH, BG_HEIGHT, true);

        imageIndex++;

        // Constructs the animations that need to run
        animation_construction();

        animationSet.setAnimationListener(animationListener);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                backgroundImage = (ImageView) activity.findViewById(R.id.login_background_image);
                backgroundImage.setImageBitmap(scaledBitmap);

                // TODO - ANIMATION - can only be run on a looper thread
                //backgroundImage.startAnimation(animationSet);
            }
        });
    }

    private void find_crop_dimentions(){
        if(backgroundBitmap.getWidth() > BG_WIDTH)
            cropWidth = backgroundBitmap.getWidth() - BG_WIDTH;
        if(backgroundBitmap.getHeight() > BG_HEIGHT)
            cropHeight = backgroundBitmap.getHeight() - BG_HEIGHT;
    }

    private void animation_construction(){
        animationSet.addAnimation(fadeInAnimation);
        animationSet.addAnimation(fadeOutAnimation);
        fadeInAnimation.setDuration(2000);
        fadeInAnimation.setStartOffset(0);
        fadeOutAnimation.setDuration(2000);
        fadeOutAnimation.setStartOffset(2000 + 1000);   // offset is meant to continue to display
    }
}
