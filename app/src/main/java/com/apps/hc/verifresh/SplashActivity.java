package com.apps.hc.verifresh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent j=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(j);
        finish();

//        prefs = getSharedPreferences("com.hc.apps.verifresh", MODE_PRIVATE);
//        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser()!=null){
//            if (prefs.getBoolean("firstrun", true)) {
//                Intent i=new Intent(this,IntroActivity.class);
//                startActivity(i);
//            }else {
//                Intent i=new Intent(SplashActivity.this,MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//        }
//        else {
//            Intent i=new Intent(SplashActivity.this,LoginActivity.class);
//            startActivity(i);
//            finish();
//        }
    }
}
