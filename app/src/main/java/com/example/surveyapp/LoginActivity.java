package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private MaterialCheckBox cbRememberMe; // Added Checkbox variable
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        cbRememberMe = findViewById(R.id.cbRememberMe); // Linked the Checkbox

        // --- REMEMBER ME: Check Memory on Startup ---
        SharedPreferences prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean("rememberMe", false);

        if (isRemembered) {
            // Auto-fill the boxes if they checked it last time
            etEmail.setText(prefs.getString("savedEmail", ""));
            etPassword.setText(prefs.getString("savedPassword", ""));
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // --- REMEMBER ME: Save Memory on Success ---
                        SharedPreferences.Editor editor = getSharedPreferences("SurveyPrefs", MODE_PRIVATE).edit();

                        if (cbRememberMe.isChecked()) {
                            // Save the credentials
                            editor.putBoolean("rememberMe", true);
                            editor.putString("savedEmail", email);
                            editor.putString("savedPassword", password);
                        } else {
                            // Wipe the credentials if they unchecked it
                            editor.putBoolean("rememberMe", false);
                            editor.putString("savedEmail", "");
                            editor.putString("savedPassword", "");
                        }

                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Jump to the main dashboard
                        startActivity(new Intent(LoginActivity.this, SurveyListActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}