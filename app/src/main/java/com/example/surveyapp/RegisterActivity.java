package com.example.surveyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    // Corrected variable declarations to match your layout
    EditText etName, etEmail, etPassword, etAge;
    RadioGroup rgGender;
    Spinner spinnerCity;
    Button btnRegister;

    // Firebase variables
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 1. Correctly mapped the variables to your XML IDs
        etName = findViewById(R.id.etName); // Make sure you added this ID to your XML!
        etEmail = findViewById(R.id.etRegUser);
        etPassword = findViewById(R.id.etRegPass);
        etAge = findViewById(R.id.etAge);
        rgGender = findViewById(R.id.rgGender);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnRegister = findViewById(R.id.btnRegister);

        // Setup city dropdown
        String[] cities = {"Select City", "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Jaipur", "Lucknow", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Register button click
        btnRegister.setOnClickListener(v -> {
            // Safely grab text, including the new Name field
            String name = etName != null ? etName.getText().toString().trim() : "";
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String age = etAge.getText().toString().trim();

            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            String gender = selectedGenderId != -1 ? ((RadioButton) findViewById(selectedGenderId)).getText().toString() : "";
            String city = spinnerCity.getSelectedItem().toString();

            // 2. Added 'name' to the validation check
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || gender.isEmpty() || city.equals("Select City")) {
                showCustomToast("Please fill all details", false);
                return;
            }

            if (password.length() < 6) {
                showCustomToast("Password must be at least 6 characters", false);
                return;
            }

            // Create user with Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // 3. Using your correct Asia-Southeast1 URL
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://surveyapp-f1065-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(userId);

                            // 4. Save EVERYTHING to the database
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name); // Now saving the actual name!
                            userMap.put("email", email);
                            userMap.put("age", age);
                            userMap.put("gender", gender);
                            userMap.put("city", city);
                            userMap.put("points", 0);
                            userMap.put("latitude", 0.0);
                            userMap.put("longitude", 0.0);

                            mDatabase.setValue(userMap).addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    showCustomToast("Registration Successful!", true);
                                    startActivity(new Intent(RegisterActivity.this, SurveyListActivity.class));
                                    finish();
                                } else {
                                    showCustomToast("DB Error: " + dbTask.getException().getMessage(), false);
                                }
                            });
                        } else {
                            showCustomToast("Auth Error: " + task.getException().getMessage(), false);
                        }
                    });
        });
    }

    // Your existing Custom toast function
    private void showCustomToast(String message, boolean success) {
        View layout = getLayoutInflater().inflate(R.layout.toast_custom, findViewById(R.id.toastRoot));
        ImageView icon = layout.findViewById(R.id.toastIcon);
        TextView text = layout.findViewById(R.id.toastText);

        text.setText(message);

        if (success) {
            icon.setImageResource(R.drawable.like); // success icon
            layout.setBackgroundResource(R.drawable.toast_success_bg);
        } else {
            icon.setImageResource(R.drawable.dislike); // fail icon
            layout.setBackgroundResource(R.drawable.toast_fail_bg);
        }

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}