package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force Light Mode globally here too
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        // 2-second delay for the professional feel
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Check if user is already logged in (Remember Me Logic)
            SharedPreferences prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            Intent intent;
            if (isLoggedIn) {
                // Skip login, go to Dashboard
                intent = new Intent(SplashActivity.this, SurveyListActivity.class);
            } else {
                // Go to Login
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // Destroy splash so user can't "Go Back" to it
        }, 2000);
    }
}