package com.hackathon.recumeet.UserAuth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class CreateUserActivity extends AppCompatActivity {

    private EditText first_name, last_name, user_name;
    Button register;
    ImageView profile_pic;
    FirebaseUser fUser;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private final String default_pic = "https://firebasestorage.googleapis.com/v0/b/clik-e24f0.appspot.com/o/default%2Fuser.png?alt=media&token=6843c07c-755d-4a9e-b34c-45acb35e760f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        Init();

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());

        Picasso.get().load(default_pic).networkPolicy(NetworkPolicy.OFFLINE).into(profile_pic, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(default_pic).into(profile_pic);
            }
        });

        register.setOnClickListener(v -> {
            final ProgressDialog pd = new ProgressDialog(CreateUserActivity.this);
            pd.setMessage("Logging In");
            pd.show();

            String fName = first_name.getText().toString();
            String lName = last_name.getText().toString();
            String userName = user_name.getText().toString();
            String DOB = getTodaysDate();

            HashMap<String, Object> map = new HashMap<>();
            assert fUser != null;
            map.put("uId", fUser.getUid());
            map.put("FName", fName);
            map.put("LName", lName);
            map.put("UName", userName);
            map.put("DOB", DOB);
            map.put("Bio", "");
            map.put("ProfileUri", default_pic);
            FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateUserActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateUserActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(CreateUserActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                    finish();
                }
            });

        });
    }

    private void Init() {
        profile_pic = findViewById(R.id.profile_pic);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        user_name = findViewById(R.id.user_name);
        register = findViewById(R.id.registerBtn);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
    }
}