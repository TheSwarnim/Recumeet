package com.hackathon.recumeet;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hackathon.recumeet.fragments.ChannelFragment;
import com.hackathon.recumeet.fragments.FeedFragment;
import com.hackathon.recumeet.fragments.MeetFragment;
import com.hackathon.recumeet.fragments.ProfileFragment;
import com.hackathon.recumeet.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    private Toast backToast;
    BottomNavigationView bottomNavigationView;
    Fragment sFragment = null;
    FeedFragment feedFragment;
    ChannelFragment chatFragment;
    MeetFragment meetFragment;
    SearchFragment searchFragment;
    ProfileFragment profileFragment;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new FeedFragment()).commit();
    }


    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.nav_feed) {
                        sFragment = feedFragment;
                    } else if (item.getItemId() == R.id.nav_chat) {
                        sFragment = chatFragment;
                    } else if (item.getItemId() == R.id.nav_meet) {
                        sFragment = meetFragment;
                    } else if (item.getItemId() == R.id.nav_search) {
                        sFragment = searchFragment;
                    } else if (item.getItemId() == R.id.nav_settings) {
                        sFragment = profileFragment;
                    }

                    if (sFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, sFragment).commit();
                    }

                    return true;
                }
            };

    private void Init() {
        feedFragment = new FeedFragment();
        chatFragment = new ChannelFragment();
        meetFragment = new MeetFragment();
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}