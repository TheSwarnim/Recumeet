package com.hackathon.recumeet.UserProfile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.recumeet.Fragments.ProfileFragment;
import com.hackathon.recumeet.Models.User;
import com.hackathon.recumeet.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class EditProfile2Activity extends AppCompatActivity {

    private ImageView profile_pic, close, done;
    private EditText name, Bio;
    private DatabaseReference ref;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile2);

        Init();

        assert fUser != null;
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getName());
                Bio.setText(user.getBio());
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

            }
        });

        close.setOnClickListener(v -> {
            Fragment fragment = new ProfileFragment();
            finish();
        });

        done.setOnClickListener(v -> {

            final ProgressDialog pd = new ProgressDialog(EditProfile2Activity.this);
            pd.setMessage("Updating Profile");
            pd.show();

            String name_text = name.getText().toString().trim();
            String bio_text = Bio.getText().toString().trim();

            final HashMap<String, Object> map = new HashMap<>();
            map.put("Name", name_text);
            map.put("Bio", bio_text);

            ref.updateChildren(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfile2Activity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfile2Activity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            });
        });

        profile_pic.setOnClickListener(v -> Toast.makeText(EditProfile2Activity.this, "Under Dev", Toast.LENGTH_SHORT).show());
    }


    private void Init() {
        profile_pic = findViewById(R.id.profile_pic);
        close = findViewById(R.id.close);
        done = findViewById(R.id.save);
        name = findViewById(R.id.name);
        Bio = findViewById(R.id.bio);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}