package com.hackathon.recumeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hackathon.recumeet.UserAuth.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fUser == null){
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        } else {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        }
        finish();
    }
}