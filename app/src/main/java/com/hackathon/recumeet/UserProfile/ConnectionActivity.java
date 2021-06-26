package com.hackathon.recumeet.UserProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hackathon.recumeet.R;
import com.hackathon.recumeet.UserProfile.Fragments.FollowersFragment;
import com.hackathon.recumeet.UserProfile.Fragments.FollowingFragment;

import java.util.ArrayList;
import java.util.List;

public class ConnectionActivity extends AppCompatActivity {

    private String Uid;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FollowersFragment followersFragment;
    private FollowingFragment followingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Init();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdaptor viewPagerAdpater = new ViewPagerAdaptor(getSupportFragmentManager(), 0);
        viewPagerAdpater.addFragment(followersFragment, "Followers");
        viewPagerAdpater.addFragment(followingFragment, "Following");
        viewPager.setAdapter(viewPagerAdpater);
    }

    private void Init() {
        Uid = getIntent().getStringExtra("Uid");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        followersFragment = new FollowersFragment(Uid);
        followingFragment = new FollowingFragment(Uid);
    }

    private static class ViewPagerAdaptor extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitle = new ArrayList<>();

        public ViewPagerAdaptor(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String Title) {
            fragments.add(fragment);
            fragmentsTitle.add(Title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }
}