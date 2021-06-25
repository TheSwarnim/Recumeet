package com.hackathon.recumeet.UserAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.hackathon.recumeet.MainActivity;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class CreateUserActivity extends AppCompatActivity {

//    private EditText first_name,;
//    private final String default_pic = "https://firebasestorage.googleapis.com/v0/b/clik-e24f0.appspot.com/o/default%2Fuser.png?alt=media&token=6843c07c-755d-4a9e-b34c-45acb35e760f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

//        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        display_name = findViewById(R.id.display_name);
//        final ImageView profile_pic = findViewById(R.id.profile_pic);
//
//        Button register = findViewById(R.id.register);
//
//        Picasso.get().load(default_pic).networkPolicy(NetworkPolicy.OFFLINE).into(profile_pic, new Callback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Picasso.get().load(default_pic).into(profile_pic);
//            }
//        });
//
//        register.setOnClickListener(v -> {
//            final ProgressDialog pd = new ProgressDialog(CreateUserActivity.this);
//            pd.setMessage("Logging In");
//            pd.show();
//            final String display_name_text = display_name.getText().toString().trim();
//
//            if (display_name_text.isEmpty()) {
//                display_name.setError("Empty");
//                display_name.requestFocus();
//            } else {
//
//                HashMap<String, Object> map = new HashMap<>();
//                assert fUser != null;
//                map.put("uId", fUser.getUid());
//                map.put("Name", display_name_text);
//                map.put("Bio", "");
//                map.put("ProfileUri", default_pic);
//                FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(CreateUserActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(CreateUserActivity.this, MainActivity.class));
//                        } else {
//                            Toast.makeText(CreateUserActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                        pd.dismiss();
//                        finish();
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
    }
}