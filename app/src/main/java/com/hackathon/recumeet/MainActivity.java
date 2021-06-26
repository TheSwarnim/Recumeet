package com.hackathon.recumeet;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hackathon.recumeet.fragments.ChannelFragment;
import com.hackathon.recumeet.fragments.FeedFragment;
import com.hackathon.recumeet.fragments.MeetFragment;
import com.hackathon.recumeet.fragments.ProfileFragment;
import com.hackathon.recumeet.fragments.SearchFragment;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;

    private Toast backToast;
    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(5);
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        integerDeque.push(R.id.nav_feed);
        loadFragment(new FeedFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (integerDeque.contains(id)) {
                if (id == R.id.nav_feed) {
                    if (integerDeque.size() != 1) {
                        if (flag) {
                            integerDeque.addFirst(R.id.nav_feed);
                            flag = false;
                        }
                    }
                }
                integerDeque.remove(id);
            }
            integerDeque.push(id);
            loadFragment(getFragment(item.getItemId()));
            return true;
        });
    }

    private Fragment getFragment(int itemId) {
        if (R.id.nav_feed == itemId) {
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            return new FeedFragment();
        } else if (R.id.nav_search == itemId) {
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            return new SearchFragment();
        } else if (R.id.nav_meet == itemId) {
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            return new MeetFragment();
        } else if (R.id.nav_chat == itemId) {
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            return new ChannelFragment();
        } else if (R.id.nav_settings == itemId) {
            bottomNavigationView.getMenu().getItem(4).setChecked(true);
            return new ProfileFragment();
        }

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        return new FeedFragment();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment, fragment.getClass().getSimpleName())
                .commit();
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