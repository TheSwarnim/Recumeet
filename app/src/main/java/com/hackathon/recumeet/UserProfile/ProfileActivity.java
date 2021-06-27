package com.hackathon.recumeet.UserProfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private String publisherId;

    private ImageView profile_pic;
    private TextView fullName, bio, noOfPosts, noOFFollowers, noOfFollowing, username, reusmeText, dob_text;
    private LinearLayout post, followers, following, resume;

    private Button follow, viewPosts;
    private FirebaseUser firebaseUser;

    private static ProgressDialog mProgressDialog;
    private String pdf_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Init();
        loadActivity();

        viewPosts.setOnClickListener(v -> openPosts());
        post.setOnClickListener(v -> openPosts());

        followers.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ConnectionActivity.class);
            intent.putExtra("Uid", publisherId);
            startActivity(intent);
        });

        following.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ConnectionActivity.class);
            intent.putExtra("Uid", publisherId);
            startActivity(intent);
        });

        follow.setOnClickListener(v -> {
            if (follow.getText().toString().equals("Follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(publisherId).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId)
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(publisherId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId)
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpload();
            }
        });
    }

    private void openUpload() {
        if (reusmeText.getText().equals("Click here to see resume")) {
            viewResume();
        } else {
            Toast.makeText(ProfileActivity.this, "Resume is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewResume() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url));
        startActivity(browserIntent);
    }

    private void loadActivity() {
        showSimpleProgressDialog(ProfileActivity.this, "Loading", "Loading profile", false);
        getUserData();
        setnoOFFollowers(noOFFollowers);
        setnoOFFollowing(noOfFollowing);
        setNoOfPosts(noOfPosts);
        getResume(reusmeText);
        setFollowStatus(follow);
        setDOB(dob_text);
        removeSimpleProgressDialog();
    }

    private void setDOB(TextView dob_text) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("DOB");
        ref.get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else {
                dob_text.setText(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString());
            }
        });
    }

    private void getResume(TextView reusmeText) {
        DatabaseReference ref9 = FirebaseDatabase.getInstance().getReference().child("users").child(publisherId).child("resumeLink");
        ref9.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                reusmeText.setText("Resume is not available this time");
                Toast.makeText(ProfileActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
            } else {
                if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString().equals("NULL")) {
                    reusmeText.setText("Resume is not Uploaded this time, Upload Resume");
                } else {
                    pdf_url = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString();
                    reusmeText.setText("Click here to see resume");
                }
            }
        });
    }

    private void openPosts() {
        Intent intent = new Intent(ProfileActivity.this, ProfilePosts.class);
        intent.putExtra("publisherId", publisherId);
        startActivity(intent);
    }

    private void Init() {
        publisherId = getIntent().getStringExtra("publisherId");

        profile_pic = findViewById(R.id.profile_pic);
        username = findViewById(R.id.full_name);
        fullName = findViewById(R.id.user_name);
        bio = findViewById(R.id.bio);

        noOfPosts = findViewById(R.id.no_posts);
        noOFFollowers = findViewById(R.id.no_followers);
        noOfFollowing = findViewById(R.id.no_following);
        reusmeText = findViewById(R.id.resume_text);

        follow = findViewById(R.id.follow);
        viewPosts = findViewById(R.id.view_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post = findViewById(R.id.post);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        resume = findViewById(R.id.resume);
        dob_text = findViewById(R.id.dob_text);
    }

    private void setnoOFFollowing(final TextView noOfFollowing) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("following");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOfFollowing.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setnoOFFollowers(final TextView noOFFollowers) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("followers");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noOFFollowers.setText(snapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setNoOfPosts(final TextView noOfPosts) {
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference().child("posts");
        ref4.keepSynced(true);

        ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int posts = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(publisherId)) {
                        posts++;
                    }
                }

                noOfPosts.setText(posts + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFollowStatus(final Button follow) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(publisherId).child("followers");
        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    follow.setText("Following");
                } else {
                    follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(publisherId);
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
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

}