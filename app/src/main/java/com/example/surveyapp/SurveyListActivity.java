package com.example.surveyapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SurveyListActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ModeReceiver modeReceiver = new ModeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

        // Start your custom App Status Service
        startService(new Intent(this, AppStatusService.class));

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Register Broadcast Receiver for Airplane & Silent Modes
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(modeReceiver, filter);

        // Setup the bottom navigation clicks
        setupNavigation();

        // Fix: Load the Home screen by default when the app first opens
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId(); // Get the ID

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_leaderboard) {
                selectedFragment = new LeaderboardFragment(); // THE NEW ROUTE
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    // I kept your flashlight toggle safe just in case you are calling it from somewhere else!
    private void toggleFlashlight() {
        try {
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            if (camManager != null) {
                String cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);
                Toast.makeText(this, R.string.flashlight_on, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Flashlight Error", Toast.LENGTH_SHORT).show();
        }
    }
    public void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custom, findViewById(R.id.toastRoot));

        TextView text = layout.findViewById(R.id.toastText);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(modeReceiver); // Prevents memory leaks
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}