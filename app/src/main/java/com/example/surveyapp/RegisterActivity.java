package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegUser, etRegPass, etAge;
    RadioGroup rgGender;
    Spinner spinnerCity;
    Button btnRegister;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find views
        etRegUser = findViewById(R.id.etRegUser);
        etRegPass = findViewById(R.id.etRegPass);
        etAge = findViewById(R.id.etAge);
        rgGender = findViewById(R.id.rgGender);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnRegister = findViewById(R.id.btnRegister);

        prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);

        // Setup city dropdown
        String[] cities = {"Select City", "Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Jaipur", "Lucknow", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Register button click
        btnRegister.setOnClickListener(v -> {
            String username = etRegUser.getText().toString().trim();
            String password = etRegPass.getText().toString().trim();
            String age = etAge.getText().toString().trim();

            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            String gender = selectedGenderId != -1 ? ((RadioButton) findViewById(selectedGenderId)).getText().toString() : "";
            String city = spinnerCity.getSelectedItem().toString();

            // Validation
            if (username.isEmpty() || password.isEmpty() || age.isEmpty() || gender.isEmpty() || city.equals("Select City")) {
                showCustomToast("Please fill all details", false);
                return;
            }

            // Check if username already exists
            if (prefs.contains("user_" + username)) {
                showCustomToast("Username already exists", false);
                return;
            }

            // Save all user details
            prefs.edit()
                    .putString("user_" + username, password)
                    .putString("age_" + username, age)
                    .putString("gender_" + username, gender)
                    .putString("city_" + username, city)
                    .apply();

            showCustomToast("Registration successful", true);

            // Go back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // Custom toast function
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
