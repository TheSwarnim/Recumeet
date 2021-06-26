package com.hackathon.recumeet;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class FireClass {

    public String getUid(){
        return FirebaseAuth.getInstance().getUid();
    }

    public String getName(){
        final String[] name = new String[1];
        name[0] = "";

        FirebaseDatabase.getInstance().getReference().child("users").child(getUid())
                .get().addOnSuccessListener(
                new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        name[0] = dataSnapshot.child("Name").getValue().toString();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });

        return name[0];
    }
}
