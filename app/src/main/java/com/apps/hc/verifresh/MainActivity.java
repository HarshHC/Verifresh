package com.apps.hc.verifresh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences prefs = null;
    private DatabaseReference BlockRef;
    private boolean block_access=false;

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true)) {
            Intent i=new Intent(this,IntroActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        BlockRef=mDatabase.child("block");
        prefs = getSharedPreferences("com.hc.apps.verifresh", MODE_PRIVATE);
        block_access=prefs.getBoolean("block",false);

        if (prefs.getBoolean("firstrun", true)) {
            Intent i=new Intent(this,IntroActivity.class);
            startActivity(i);
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String alert = dataSnapshot.getValue(String.class);
                if(alert.equals("yes")){
                    prefs.edit().putBoolean("block",true).apply();
                    block_access=true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPopup();
                        }
                    }, 1000);
                    showPopup();
                }else{
                    prefs.edit().putBoolean("block",false).apply();
                    block_access=false;
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("MAINACTIVITY//", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        if(block_access){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPopup();
                }
            }, 3000);
        }
        BlockRef.addValueEventListener(postListener);

        //Animations
        animate(findViewById(R.id.cam_btn));

        LinearLayout top=findViewById(R.id.top_bar);
        top.setVisibility(View.VISIBLE);
        top.setAlpha(0.0f);
        top.animate()
                .translationY(top.getHeight())
                .alpha(1.0f)
                .setDuration(2000);

    }

    public void openCam(View view) {
        if(!block_access){
            Intent i=new Intent(this,CameraActivity.class);
            startActivity(i);
        }else {
            mAuth.signOut();
            finish();
        }
    }

    public void openIntructions(View view) {
        if(!block_access){
            Intent i=new Intent(this,IntroActivity.class);
            startActivity(i);
        }else {
            mAuth.signOut();
            finish();
        }
    }

    public void showPopup(){
        LayoutInflater layoutInflater=(LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.pop_custom_layout,null);

        Button closePopupBtn=customView.findViewById(R.id.ok_btn);

        //instantiate popup window
        PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, 900);
        popupWindow.setElevation(30);
        //display the popup window
        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER,0,0);
        //popupWindow.setAnimationStyle(R.anim.appear);
        closePopupBtn.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAuth.signOut();
                finish();
            }
        });
    }

    public void contribute(View view) {

        if(!block_access){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","harshchandrahc5@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "VeriFresh Contribution by "+mAuth.getCurrentUser().getDisplayName());
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hey "+mAuth.getCurrentUser().getDisplayName()+",\n\nThanks for showing interest in contributing. As of now we need a lot of data to improve the detection accuracy and also to add new items for detection. For this it would be great if you could take a small 30 second video of a fruit/veg holding it in your hand and then slowly moving around to get different backrounds while doing this keep rotating the fruit really slowly with your hand and also try slowly bringing the fruit/veg closer to the camera and then further in both dimly and brighly lit areas.\n\n After using the app if you saw that the app was giving incorrect results for a particular fruit/veg then it would be awesome if you could take the video as described above of that fruit/veg and send it.\n\nOtherwise if you notice that there is a particular fruit/veg you have which the app does not detect then a video of that would also be really helpful. If possible try taking videos of a healthy/fresh as well as unhealthy/not fresh variant of the same fruit. \n\nMore the data you provide , beter the detection will get.\n\nThank You so much!!!\n~Harsh Chandra\n\nDELETE ALL THE ABOVE TEXT AND SEND YOUR CONTRIBUTIONS INSTEAD\n\n\n\n");
            startActivity(Intent.createChooser(emailIntent, "Select email/gmail app for contribution instructions..."));
        }else {
            mAuth.signOut();
            finish();
        }

    }

    public void animate (View view) {
       Animation mAnimation = new ScaleAnimation(0.7f,1.0f,0.7f,1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setInterpolator(new AccelerateInterpolator());
        mAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.setAnimation(mAnimation);
    }

    public void ingredients(View view) {
        if(!block_access){
            Intent i=new Intent(this,IngredientsActivity.class);
            startActivity(i);
        }else {
            mAuth.signOut();
            finish();
        }
    }
}
