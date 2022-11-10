package com.example.proj1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mypackage.R;

public class Login_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }//If user is already authenticated or not? If so, close and carry on.

        Button btnRegister = findViewById(R.id.Login_Button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });

        TextView textViewSwitchToRegister = findViewById(R.id.tvSwitchToRegister);
        textViewSwitchToRegister.setOnClickListener(view -> switchToRegister());
    }//End of OnCreate

    private void switchToRegister() {
        Intent intent = new Intent(this, Registration_Activity.class);
        startActivity(intent);
        finish();
    }

    private void authenticateUser() {
        EditText etLoginEmail = findViewById(R.id.LoginEmail_Blank);
        EditText etLoginPassword = findViewById(R.id.LoginPassword_Blank);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

//Add string for correct email and password to bypass login string just for testing
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // Correct email and correct password then show main activity
                            showMainActivity();
                        } else {
                            Toast.makeText(Login_Activity.this, "Please check that you have entered the correct Email and Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}