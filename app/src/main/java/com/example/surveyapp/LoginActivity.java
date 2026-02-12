package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    Button btnLogin, btnRegisterLink;
    ToggleButton toggle;
    SharedPreferences prefs;

    // 5 hardcoded users
    String[] hardUsers = {"admin", "user1", "user2", "user3", "user4"};
    String[] hardPasswords = {"1234", "pass1", "pass2", "pass3", "pass4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        toggle = findViewById(R.id.toggleBtn);
        btnRegisterLink = findViewById(R.id.btnRegisterLink); // optional "Register" button

        prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);

        // AUTO LOGIN if remembered
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, SurveyListActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            boolean valid = false;

            // 1️⃣ Check hardcoded users
            for (int i = 0; i < hardUsers.length; i++) {
                if (user.equals(hardUsers[i]) && pass.equals(hardPasswords[i])) {
                    valid = true;
                    break;
                }
            }

            // 2️⃣ Check registered users in SharedPreferences
            if (!valid) {
                String storedPass = prefs.getString("user_" + user, null);
                if (storedPass != null && storedPass.equals(pass)) {
                    valid = true;
                }
            }

            if (valid) {
                if (toggle.isChecked()) {
                    prefs.edit().putBoolean("isLoggedIn", true).apply();
                }
                showCustomToast("Login Successful", true);
                startActivity(new Intent(this, SurveyListActivity.class));
                finish();
            } else {
                showCustomToast("Invalid Login", false);
            }
        });

        // Optional: go to registration screen
        btnRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

    // Custom toast function
    private void showCustomToast(String message, boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custom, findViewById(R.id.toastRoot));

        ImageView icon = layout.findViewById(R.id.toastIcon);
        TextView text = layout.findViewById(R.id.toastText);

        text.setText(message);

        if (success) {
            icon.setImageResource(R.drawable.like);
            layout.setBackgroundResource(R.drawable.toast_success_bg);
        } else {
            icon.setImageResource(R.drawable.dislike);
            layout.setBackgroundResource(R.drawable.toast_fail_bg);
        }

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
