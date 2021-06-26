package com.hackathon.recumeet.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.recumeet.Feed.AddPostActivity;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.hackathon.recumeet.UserAuth.LoginActivity;
import com.hackathon.recumeet.UserProfile.ConnectionActivity;
import com.hackathon.recumeet.UserProfile.EditProfile2Activity;
import com.hackathon.recumeet.UserProfile.ProfilePosts;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import io.getstream.chat.android.client.ChatClient;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    View view;
    private ImageView profile_pic, more;
    private TextView fullName, bio, noOfPosts, noOffollowers, noOffollowing, username;
    private Button editProfile, viewPosts;

    private LinearLayout post, followers, following, resume;

    private FirebaseUser firebaseUser;
    private DatabaseReference ref1;

    private AlertDialog.Builder ad;

    private ChatClient chatClient = ChatClient.instance();

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Init();

        ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid());

        viewPosts.setOnClickListener(v -> openPosts());
        post.setOnClickListener(v -> openPosts());

        more.setOnClickListener(v -> {
            ad = new AlertDialog.Builder(getContext());
            ad.setTitle("Logout");
            ad.setMessage("Are You Sure To Logout");
            ad.setCancelable(true);

            ad.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                // chat logout
                if (chatClient.getCurrentUser() != null) {
                    chatClient.disconnect();
                }
                //firebase logout
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
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
        
        resume.setOnClickListener(v -> UploadDoc());

        getUserData();
        setNoOfFollowers(noOffollowers);
        setNoOfFollowing(noOffollowing);
        getPostData(noOfPosts);

        return view;
    }

    private void UploadDoc() {
        selectPDF();
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode==RESULT_OK && data != null && data.getData() != null){
            uploadPDF(data.getData());
        }
    }

    private void uploadPDF(Uri data) {
        StorageReference reference = storageReference.child("resume" + System.currentTimeMillis() + ".pdf");

        reference.putFile(data).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri uri = uriTask.getResult();

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
            String uid = FirebaseAuth.getInstance().getUid();

            assert uid != null;
            assert uri != null;
            ref.child(uid).child("resumeLink").setValue(uri.toString()).addOnCompleteListener(task -> {
                Toast.makeText(getContext(), "Resume Added Successfully", Toast.LENGTH_SHORT).show();
            });

        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
        });
    }

    private void openPosts() {
        Intent intent = new Intent(getContext(), ProfilePosts.class);
        intent.putExtra("publisherId", firebaseUser.getUid());
        startActivity(intent);
    }


    private void getPostData(final TextView noOfPosts) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference().child("posts");
        ref5.keepSynced(true);

        ref5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(firebaseUser.getUid()) && post.getImageUri() != null) {
                        i++;
                    }
                }
                noOfPosts.setText(String.valueOf(i));
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
        viewPosts = view.findViewById(R.id.view_post);

        noOfPosts = view.findViewById(R.id.no_posts);
        noOffollowers = view.findViewById(R.id.no_followers);
        noOffollowing = view.findViewById(R.id.no_following);

        post = view.findViewById(R.id.post);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        resume = view.findViewById(R.id.resume);

        username = view.findViewById(R.id.user_name);
        more = view.findViewById(R.id.profile_menu);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("resume");

    }
}