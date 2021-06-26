package com.hackathon.recumeet.UserProfile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EditProfile2Activity extends AppCompatActivity {

    private ImageView profile_pic, close, done;
    private DatabaseReference ref;
    private FirebaseUser fUser;
    private EditText first_name, last_name, user_name, bio;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;

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
                first_name.setText(user.getFName());
                last_name.setText(user.getLName());
                user_name.setText(user.getUName());
                bio.setText(user.getBio());
                dateButton.setText(user.getDOB());
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

            String fname_text = first_name.getText().toString().trim();
            String lname_text = last_name.getText().toString().trim();
            String uname_text = user_name.getText().toString().trim();
            String bio_text = bio.getText().toString().trim();
            String DOB = getTodaysDate();

            final HashMap<String, Object> map = new HashMap<>();
            map.put("FName", fname_text);
            map.put("LName", lname_text);
            map.put("UName", uname_text);
            map.put("Bio", bio_text);
            map.put("DOB", DOB);

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
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        user_name = findViewById(R.id.user_name);
        bio = findViewById(R.id.bio);
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}