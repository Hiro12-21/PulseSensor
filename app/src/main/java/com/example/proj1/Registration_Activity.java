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
import com.google.firebase.database.FirebaseDatabase;
import com.mypackage.R;

public class Registration_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }//If user is already authenticated or not? If so, close and carry on.

        Button btnRegister = findViewById(R.id.Register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        TextView textViewSwitchToLogin = findViewById(R.id.tvSwitchToLogin);
        textViewSwitchToLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switchToLogin();
            }
        });//Button to switch from registration page back to login page

    }//End of on Create

    private void switchToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        EditText etFirstName = findViewById(R.id.FirstName_Blank);
        EditText etLastName = findViewById(R.id.LastName_Blank);
        EditText etEmail = findViewById(R.id.Email_Blank);
        EditText etPassword = findViewById(R.id.Password_Blank);

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String id = firstName + lastName;

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) //Created account successfully
                        {
                            User userinfo = new User(firstName, lastName, email);
                            FirebaseDatabase.getInstance().getReference("User Information")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                            showMainActivity();
                                        }
                                    });
                        } else {
                            //If sign in fails, display a message to the user.
                            Toast.makeText(Registration_Activity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }//End of registerUser method

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}