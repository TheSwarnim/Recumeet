package com.hackathon.recumeet.Feed.FeedFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.CommentAdapter;
import com.hackathon.recumeet.Models.Comment;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentFragment extends Fragment {

    View view;
    private final String postId;

    private ImageView profile_pic;
    private EditText comment;
    private TextView post;

    private FirebaseUser fUser;
    private DatabaseReference ref;

    private List<Comment> mComments;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;

    public CommentFragment(String postId) {
        this.postId = postId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        
        Init();

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
        ref1.keepSynced(true);

        ref1.addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        post.setOnClickListener(v -> {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Adding Comment");
            pd.show();
            String comment_text = comment.getText().toString().trim();

            if(comment_text.isEmpty()){
                comment.setError("Empty");
                comment.requestFocus();
                pd.dismiss();
            }
            else{
                ref = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);

                String commentId = ref.push().getKey();

                HashMap<String , Object> map = new HashMap<>();
                map.put("publisher", fUser.getUid());
                map.put("comment", comment_text);
                map.put("commentId", commentId);

                assert commentId != null;
                ref.child(commentId).setValue(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Comment Added Successfully", Toast.LENGTH_SHORT).show();
                        comment.getText().clear();
                    }
                    else{
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                });
            }
        });


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        loadComments();

        return view;
    }

    private void loadComments() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading Comments");
        pd.show();

        FirebaseDatabase.getInstance().getReference().child("comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mComments.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Comment comment = snapshot1.getValue(Comment.class);
                    mComments.add(comment);
                }

                commentAdapter = new CommentAdapter(getContext(), mComments, postId);
                recyclerView.setAdapter(commentAdapter);
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
        profile_pic = view.findViewById(R.id.profile_pic);
        comment = view.findViewById(R.id.comment_text);
        post = view.findViewById(R.id.post);
        recyclerView = view.findViewById(R.id.comment_view);
        mComments = new ArrayList<>();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}