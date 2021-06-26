package com.hackathon.recumeet.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.PhotoAdapter;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.hackathon.recumeet.UserAuth.LoginActivity;
import com.hackathon.recumeet.UserProfile.ConnectionActivity;
import com.hackathon.recumeet.UserProfile.EditProfile2Activity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;

public class ProfileFragment extends Fragment {

    View view;
    private ImageView profile_pic;
    private TextView fullName;
    private TextView bio;
    private Button editProfile;

    private TextView noOfPosts;
    private TextView noOffollowers;
    private TextView noOffollowing;

    private RelativeLayout followers;
    private RelativeLayout following;

    private List<String> followingList;
    private List<String> followerList;

    private TextView username;
    private ImageView more;

    private FirebaseUser firebaseUser;
    private DatabaseReference ref1;

    private List<Post> myPhotoList;
    private PhotoAdapter photoAdpatar;
    private RecyclerView recyclerView;

    private AlertDialog.Builder ad;

    private ChatClient chatClient = ChatClient.instance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Init();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        photoAdpatar = new PhotoAdapter(getContext(), myPhotoList, firebaseUser.getUid());
        recyclerView.setAdapter(photoAdpatar);

        ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid());

        more.setOnClickListener(v -> {
            ad = new AlertDialog.Builder(getContext());
            ad.setTitle("Logout");
            ad.setMessage("Are You Sure To Logout");
            ad.setCancelable(true);

            ad.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                // chat logout
                if(chatClient.getCurrentUser() != null){
                    chatClient.disconnect();
                }

                //firebase logout
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
            }).setNegativeButton(android.R.string.no, (dialog, which) -> {
                dialog.dismiss();
                Toast.makeText(getContext(), "Logging Out Process cancelled", Toast.LENGTH_SHORT).show();
            });

            AlertDialog alert11 = ad.create();
            alert11.show();
        });

        editProfile.setOnClickListener(v1 -> startActivity(new Intent(getContext(), EditProfile2Activity.class)));

        followers.setOnClickListener(v13 -> {
            Intent intent = new Intent(getContext(), ConnectionActivity.class);
            intent.putExtra("Uid", firebaseUser.getUid());
            startActivity(intent);
        });

        following.setOnClickListener(v12 -> {
            Intent intent = new Intent(getContext(), ConnectionActivity.class);
            intent.putExtra("Uid", firebaseUser.getUid());
            startActivity(intent);
        });

        getUserData();
        setNoOfFollowers(noOffollowers);
        setNoOfFollowing(noOffollowing);
        getPostData(noOfPosts);

        return view;
    }

    private void getPostData(final TextView noOfPosts) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference().child("posts");
        ref5.keepSynced(true);

        ref5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(firebaseUser.getUid()) && post.getImageUri() != null) {
                        myPhotoList.add(post);
                    }
                }
                noOfPosts.setText(String.valueOf(myPhotoList.size()));
                Collections.reverse(myPhotoList);
                photoAdpatar.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNoOfFollowing(final TextView noOffollowing) {
        final DatabaseReference ref3 = ref1.child("following");
        ref3.keepSynced(true);

        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOffollowing.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNoOfFollowers(final TextView noOffollowers) {
        final DatabaseReference ref2 = ref1.child("followers");
        ref2.keepSynced(true);

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOffollowers.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {
        assert firebaseUser != null;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                assert user != null;
                Picasso.get().load(user.getProfileUri()).networkPolicy(NetworkPolicy.OFFLINE).into(profile_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(user.getProfileUri()).into(profile_pic);
                    }
                });

                fullName.setText(user.getFName() + " " + user.getLName());
                username.setText(user.getUName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Init() {
        profile_pic = view.findViewById(R.id.profile_pic);
        fullName = view.findViewById(R.id.full_name);
        bio = view.findViewById(R.id.bio);
        editProfile = view.findViewById(R.id.editprofile);

        noOfPosts = view.findViewById(R.id.no_posts);
        noOffollowers = view.findViewById(R.id.no_followers);
        noOffollowing = view.findViewById(R.id.no_following);

        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);

        username = view.findViewById(R.id.user_name);
        more = view.findViewById(R.id.profile_menu);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        myPhotoList = new ArrayList<>();
        followingList = new ArrayList<>();
        followerList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
    }
}