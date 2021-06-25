package com.hackathon.recumeet.Feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.hackathon.recumeet.Feed.FeedFragment.CommentFragment;
import com.hackathon.recumeet.Feed.FeedFragment.LikesFragment;
import com.hackathon.recumeet.R;

import java.util.ArrayList;
import java.util.List;

public class LikeCommentActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CommentFragment commentFragment;
    private LikesFragment likesFragment;

    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_comment);

        Init();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdaptor viewPagerAdapter = new ViewPagerAdaptor(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(commentFragment, "Comment");
        viewPagerAdapter.addFragment(likesFragment, "Likes");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void Init() {
        postId = getIntent().getStringExtra("postId");
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        commentFragment = new CommentFragment(postId);
        likesFragment = new LikesFragment(postId);
    }

    private static class ViewPagerAdaptor extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitle = new ArrayList<>();

        public ViewPagerAdaptor(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String Title){
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