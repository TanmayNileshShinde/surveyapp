package com.example.surveyapp;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class TorchActivity extends AppCompatActivity {

    private boolean isFlashlightOn = false;
    private CameraManager cameraManager;
    private String cameraId;
    private MaterialButton btnToggleTorch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torch);

        btnToggleTorch = findViewById(R.id.btnToggleTorch);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            // cameraIdList[0] is typically the rear-facing camera with the flash
            if (cameraManager != null && cameraManager.getCameraIdList().length > 0) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Camera access issue", Toast.LENGTH_SHORT).show();
        }

        btnToggleTorch.setOnClickListener(v -> toggleFlashlight());
    }

    private void toggleFlashlight() {
        if (cameraId == null) {
            Toast.makeText(this, "No flashlight available on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Flip the boolean state
            isFlashlightOn = !isFlashlightOn;

            // Turn the torch on or off
            cameraManager.setTorchMode(cameraId, isFlashlightOn);

            // Update the button text to reflect the current state
            if (isFlashlightOn) {
                btnToggleTorch.setText("Turn Off Torch");
                btnToggleTorch.setBackgroundColor(android.graphics.Color.parseColor("#EF4444")); // Red when ON
            } else {
                btnToggleTorch.setText("Turn On Torch");
                btnToggleTorch.setBackgroundColor(android.graphics.Color.parseColor("#4F46E5")); // Indigo when OFF
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to toggle flashlight", Toast.LENGTH_SHORT).show();
        }
    }

    // Turns off the flashlight if the user leaves the app or presses back
    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashlightOn) {
            toggleFlashlight();
        }
    }
}