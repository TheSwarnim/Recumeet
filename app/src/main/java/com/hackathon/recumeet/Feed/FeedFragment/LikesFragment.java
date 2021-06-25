package com.hackathon.recumeet.Feed.FeedFragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Adapter.LikeAdapter;
import com.hackathon.recumeet.Models.Like;
import com.hackathon.recumeet.R;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    View view;
    private final String postId;
    private List<Like> likeList;

    private RecyclerView recyclerView;
    private LikeAdapter likeAdapter;

    public LikesFragment(String postId) {
        this.postId = postId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_likes, container, false);
        likeList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.likeRecyler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        createLikedList();
        return view;
    }

    private void createLikedList() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading Likes");
        pd.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("likes").child(postId);

        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Like like = snapshot1.getValue(Like.class);
                    likeList.add(like);
                }

                likeAdapter = new LikeAdapter(getContext(), likeList);
                recyclerView.setAdapter(likeAdapter);
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }
}