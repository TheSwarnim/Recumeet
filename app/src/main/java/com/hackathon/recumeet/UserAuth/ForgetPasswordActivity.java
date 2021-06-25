package com.hackathon.recumeet.UserAuth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.recumeet.R;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText emailEdit;
    Button sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        Init();

        sendMail.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            if (!isEmailValid(email) || email.length() == 0) {
                Toast.makeText(ForgetPasswordActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else {
                getResetEmail(email);
            }
        });
    }

    public static boolean isEmailValid(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void Init() {
        emailEdit = findViewById(R.id.email_reset);
        sendMail = findViewById(R.id.getEmail);
    }

    private void getResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgetPasswordActivity.this, "Reset Email Sent Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ForgetPasswordActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}