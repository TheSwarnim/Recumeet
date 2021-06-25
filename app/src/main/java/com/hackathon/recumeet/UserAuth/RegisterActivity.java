package com.hackathon.recumeet.UserAuth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.recumeet.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailEdit, passEdit, confPassEdit;
    CardView dirLoginTv;
    TextView regBtn;
    String email, password, confPass;
    private static ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        Init();

        regBtn.setOnClickListener(v -> {
            showSimpleProgressDialog(RegisterActivity.this, "Loading", "Registering User", false);
            email = emailEdit.getText().toString();
            password = passEdit.getText().toString();
            confPass = confPassEdit.getText().toString();
            if (email.length() == 0 || !isEmailValid(email)) {
                Toast.makeText(RegisterActivity.this, "Invalid Email Address, Please enter valid email", Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();
            } else if (password.length() == 0) {
                Toast.makeText(RegisterActivity.this, "Invalid Password, Please enter valid password", Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();
            } else if (!password.equals(confPass)) {
                Toast.makeText(RegisterActivity.this, "Passwords Do not matches, Enter valid password", Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, CreateUserActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                    removeSimpleProgressDialog();
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


    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        emailEdit = findViewById(R.id.email_reg);
        passEdit = findViewById(R.id.pass_reg);
        confPassEdit = findViewById(R.id.pass_reg_conf);
        regBtn = findViewById(R.id.btn_reg);
        dirLoginTv = findViewById(R.id.direct_login);
    }
}