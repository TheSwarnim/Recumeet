package com.hackathon.recumeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hackathon.recumeet.UserAuth.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        ImageView bg = findViewById(R.id.img);
        ImageView logo = findViewById(R.id.logo);
        ImageView name = findViewById(R.id.text_logo);
        logo.animate().translationY(2800f).setDuration(1300).setStartDelay(1000);
        bg.animate().translationY(-2800f).setDuration(1300).setStartDelay(1000);
        name.animate().translationY(2800f).setDuration(1300).setStartDelay(1000);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fUser == null){
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
                finish();
            }
        },2300);
    }
}