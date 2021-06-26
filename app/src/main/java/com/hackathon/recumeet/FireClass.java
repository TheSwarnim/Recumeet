package com.hackathon.recumeet;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FireClass {

    public String getUid(){
        return FirebaseAuth.getInstance().getUid();
    }

    public String getName(){
        final String[] name = new String[1];

        FirebaseDatabase.getInstance().getReference().child("users").child(getUid())
                .get().addOnSuccessListener(
                dataSnapshot -> name[0] = dataSnapshot.child("FName").getValue().toString() + " "
                        + dataSnapshot.child("LName").getValue().toString()
        ).addOnFailureListener(e -> {
            Log.i("Database Error", e.toString());
            name[0] = "";
        });

        return name[0];
    }
}
