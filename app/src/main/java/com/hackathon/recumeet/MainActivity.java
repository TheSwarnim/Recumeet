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
import com.google.firebase.database.DatabaseReference;
import com.hackathon.recumeet.Fragments.ChatFragment;
import com.hackathon.recumeet.Fragments.FeedFragment;
import com.hackathon.recumeet.Fragments.MeetFragment;
import com.hackathon.recumeet.Fragments.ProfileFragment;
import com.hackathon.recumeet.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    private Toast backToast;
    BottomNavigationView bottomNavigationView;
    Fragment sFragment = null;
    FeedFragment feedFragment;
    ChatFragment chatFragment;
    MeetFragment meetFragment;
    SearchFragment searchFragment;
    ProfileFragment profileFragment;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feedFragment = new FeedFragment();
        chatFragment = new ChatFragment();
        meetFragment = new MeetFragment();
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, new FeedFragment()).commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_feed:
                            sFragment = feedFragment;
                            break;

                        case R.id.nav_chat:
                            sFragment = chatFragment;
                            break;

                        case R.id.nav_meet:
                            sFragment = meetFragment;
                            break;

                        case R.id.nav_search:
                            sFragment = searchFragment;
                            break;

                        case R.id.nav_settings:
                            sFragment = profileFragment;
                            break;

                    }

                    if (sFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_container, sFragment).commit();
                    }

                    return true;
                }
            };

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