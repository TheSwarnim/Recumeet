package com.hackathon.recumeet.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.PostAdapter;
import com.hackathon.recumeet.Feed.AddPostActivity;
import com.hackathon.recumeet.Models.Post;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    View view;
    private ImageView add;
    private ImageView user_pic;
    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private EditText search;

    private TextView isFollow;

    private FirebaseUser fUser;

    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);
        Init();

        add.setOnClickListener(v -> startActivity(new Intent(getContext(), AddPostActivity.class)));

        search.setOnClickListener(v -> {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragement_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                assert user != null;
                Picasso.get().load(user.getProfileUri()).networkPolicy(NetworkPolicy.OFFLINE).into(user_pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(user.getProfileUri()).into(user_pic);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        recyclerViewPosts.setHasFixedSize(true);

        linearLayoutManager.supportsPredictiveItemAnimations();
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        createPostList();
        return view;
    }

    private void createPostList() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Please Wait");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");
        ref.keepSynced(true);

        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following");
        ref1.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    final Post post = snapshot1.getValue(Post.class);
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            assert post != null;
                            if (snapshot.child(post.getPublisher()).exists() || post.getPublisher().equals(fUser.getUid())) {
                                postList.add(post);
                                isFollow.setVisibility(View.GONE);
                            }

                            postAdapter = new PostAdapter(getContext(), postList);
                            recyclerViewPosts.setAdapter(postAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void Init() {
        add = view.findViewById(R.id.add_image);
        user_pic = view.findViewById(R.id.profile_pic);
        search = view.findViewById(R.id.search_bar2);
        isFollow = view.findViewById(R.id.isfollow);
        postList = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerViewPosts = view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext());
    }
}