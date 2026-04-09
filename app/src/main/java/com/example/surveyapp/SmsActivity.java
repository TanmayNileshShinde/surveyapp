package com.example.surveyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SmsActivity extends AppCompatActivity {

    private EditText etPhone, etMsg;
    private TextView tvReceived;
    private Button btnSend;

    private final ActivityResultLauncher<String[]> smsPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean sendOk = result.get(Manifest.permission.SEND_SMS);
                Boolean receiveOk = result.get(Manifest.permission.RECEIVE_SMS);

                if (Boolean.TRUE.equals(sendOk) && Boolean.TRUE.equals(receiveOk)) {
                    Toast.makeText(this, "SMS permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SMS permissions denied. Enable in Settings.", Toast.LENGTH_LONG).show();
                }
            });

    private final BroadcastReceiver uiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("from");
            String msg = intent.getStringExtra("msg");
            tvReceived.setText("From: " + from + "\nMsg: " + msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        etPhone = findViewById(R.id.etPhone);
        etMsg = findViewById(R.id.etMsg);
        tvReceived = findViewById(R.id.tvReceived);
        btnSend = findViewById(R.id.btnSend);

        // --- PRE-FILL LOGIC ADDED HERE ---
        // Check if a message was passed to this activity from SurveyCompletedActivity
        String prefillMsg = getIntent().getStringExtra("prefill_msg");
        if (prefillMsg != null) {
            etMsg.setText(prefillMsg);
        }
        // ---------------------------------

        askSmsPermissions();
        String last = getSharedPreferences("sms_store", MODE_PRIVATE).getString("last_sms", null);
        if (last != null) tvReceived.setText(last);

        btnSend.setOnClickListener(v -> {
            if (!isValidInput()) return;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSmsNow();
            } else {
                askSmsPermissions();
            }
        });
    }

    private void askSmsPermissions() {
        boolean sendGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean receiveGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;

        if (!sendGranted || !receiveGranted) {
            smsPermLauncher.launch(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS});
        }
    }

    private boolean isValidInput() {
        String phone = etPhone.getText().toString().trim();
        String msg = etMsg.getText().toString().trim();

        if (phone.isEmpty() || phone.length() < 10) {
            etPhone.setError("Enter valid number");
            return false;
        }
        if (msg.isEmpty()) {
            etMsg.setError("Message cannot be empty");
            return false;
        }
        return true;
    }

    private void sendSmsNow() {
        String phone = etPhone.getText().toString().trim();
        String msg = etMsg.getText().toString().trim();

        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, msg, null, null);
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
            etMsg.setText("");
        } catch (Exception e) {
            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onStart() {
        super.onStart();
        // Updated package name
        IntentFilter filter = new IntentFilter("com.example.surveyapp.SMS_UI_UPDATE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(uiReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(uiReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(uiReceiver);
        } catch (Exception ignored) {}
    }
}