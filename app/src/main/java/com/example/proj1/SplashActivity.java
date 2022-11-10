package com.example.proj1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.mypackage.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    SharedPreferences onBoarding = getSharedPreferences("onBoardingScreen",MODE_PRIVATE);
                    boolean isFirstTime = onBoarding.getBoolean("firstTime",true);
                    if(isFirstTime){
                        SharedPreferences.Editor editor = onBoarding.edit();
                        editor.putBoolean("firstTime",false);
                        editor.commit();

                        Intent intent = new Intent(SplashActivity.this,OnBoarding.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                }
            }
        };
        thread.start();
    }
}