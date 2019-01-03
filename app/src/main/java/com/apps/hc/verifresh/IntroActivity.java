package com.apps.hc.verifresh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("com.hc.apps.verifresh", MODE_PRIVATE);
        showSkipButton(false);

        addSlide(SampleSlide.newInstance(R.layout.slide));
        addSlide(AppIntroFragment.newInstance("Using VeriFresh","Just point the camera at the fruit/vegetable and get the result in seconds !",R.drawable.s1, Color.parseColor("#37474F")));
        addSlide(AppIntroFragment.newInstance("Best Practices","For accurate results take 1 fruit/veg at a time and rotate it to show all sides. Make the item the main subject in the camera and avoid blur.",R.drawable.s2, Color.parseColor("#5E35B1")));
        addSlide(AppIntroFragment.newInstance("Never take another unhealthy fruit back home!","Get specific health details for a variety of fruits and vegetebles",R.drawable.s3,Color.parseColor("#00897B")));
        addSlide(AppIntroFragment.newInstance("Warning","This version of the app is highly experimental and has a high chance of showing undesired results.",R.drawable.slide6,ContextCompat.getColor(this,R.color.colorRed)));

        setZoomAnimation();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        if(prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).apply();
            Intent i=new Intent(IntroActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
