package com.hackathon.recumeet.UserAuth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.recumeet.R;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailEdit, passEdit, confPassEdit;
    TextView regBtn, dirLoginTv;
    ImageView back;
    String email, password, confPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // start
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ProgressBar progressBar = findViewById(R.id.spin_kit);
        progressBar.setVisibility(View.GONE);
        Init();
        back.setOnClickListener(view -> onBackPressed());
        regBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            email = emailEdit.getText().toString();
            password = passEdit.getText().toString();
            confPass = confPassEdit.getText().toString();
            if (email.length() == 0 || !isEmailValid(email)) {
                Toast.makeText(RegisterActivity.this, "Invalid Email Address, Please enter valid email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (password.length() == 0) {
                Toast.makeText(RegisterActivity.this, "Invalid Password, Please enter valid password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (!password.equals(confPass)) {
                Toast.makeText(RegisterActivity.this, "Passwords Do not matches, Enter valid password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, CreateUserActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed : " + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                    }
                    //end
                    progressBar.setVisibility(View.GONE);
                });
            }
        });

        dirLoginTv.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public static boolean isEmailValid(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        emailEdit = findViewById(R.id.email_reg);
        passEdit = findViewById(R.id.pass_reg);
        confPassEdit = findViewById(R.id.pass_reg_conf);
        regBtn = findViewById(R.id.btn_reg);
        dirLoginTv = findViewById(R.id.direct_login);
        back = findViewById(R.id.login_close);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
        ad.setTitle("Exit");
        ad.setMessage("Are You Sure To Exit App");
        ad.setCancelable(true);

        ad.setPositiveButton(android.R.string.yes, (dialog, which) -> super.onBackPressed()).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog alert11 = ad.create();
        alert11.show();
    }
}