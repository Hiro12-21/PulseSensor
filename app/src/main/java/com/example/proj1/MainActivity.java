package com.example.proj1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mypackage.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    HomeFragment homeFragment = new HomeFragment();
    HeartFragment heartFragment = new HeartFragment();
    MapFragment mapFragment = new MapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Bind BottomNavigationView
        // Set which page as the main page and for the bottom icon, which of the value is checked
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.getMenu().findItem(R.id.nav_heart).setChecked(true);
        bottomNav.setOnItemSelectedListener(navListener);

        //Changed the NewActivity to be 'MainFragment' names container in which all other fragment will replace
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                heartFragment).commit();

        //Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        //SharedPreferences

        if (currentUser == null) {
            Intent intent = new Intent(this, Login_Activity.class);
            startActivity(intent);
            finish();
            return;
        }

    }//On Create Ends here

    BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                switch (item.getItemId()) {
                    case(R.id.nav_home):
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;

                    case(R.id.nav_heart):
                        //startActivity(new Intent(getApplicationContext(), BluetoothActivity.class));
                        //overridePendingTransition(0, 0);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, heartFragment).commit();
                        return true;

                    case(R.id.nav_map):
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                        return true;
                        }
                        return false;
                    };

}//end mainactivity



