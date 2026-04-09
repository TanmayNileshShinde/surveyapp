package com.example.surveyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Firebase imports for the bonus points
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class SurveyCompletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_completed);

        TextView tvCompletedTitle = findViewById(R.id.tvCompletedTitle);
        Button btnShareSms = findViewById(R.id.btnShareSms);
        Button btnShareEmail = findViewById(R.id.btnShareEmail);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        // Get the survey name passed from SurveyActivity
        String surveyTitle = getIntent().getStringExtra("title");
        if (surveyTitle == null) surveyTitle = "Survey";

        tvCompletedTitle.setText(surveyTitle + " Completed!");

        // The pre-filled message we want to send
        String shareMessage = "Hey! I just completed the " + surveyTitle + " on SurveyApp and earned points. You should try it too!";

        btnShareSms.setOnClickListener(v -> {
            awardBonusPoints(); // Give the 5 points!
            Toast.makeText(this, "+5 Bonus Points for sharing!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SurveyCompletedActivity.this, SmsActivity.class);
            intent.putExtra("prefill_msg", shareMessage);
            startActivity(intent);
        });

        btnShareEmail.setOnClickListener(v -> {
            awardBonusPoints(); // Give the 5 points!
            Toast.makeText(this, "+5 Bonus Points for sharing!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SurveyCompletedActivity.this, EmailActivity.class);
            intent.putExtra("prefill_sub", "Join me on SurveyApp!");
            intent.putExtra("prefill_msg", shareMessage);
            startActivity(intent);
        });

        btnBackHome.setOnClickListener(v -> {
            // Clear the stack so pressing 'back' on home doesn't bring them back here
            Intent intent = new Intent(SurveyCompletedActivity.this, SurveyListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Method to securely add 5 points to the user's Firebase account
    private void awardBonusPoints() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            // Make sure to use your correct database URL
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://surveyapp-f1065-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users").child(userId);

            userRef.child("points").setValue(ServerValue.increment(5));
        }
    }
}