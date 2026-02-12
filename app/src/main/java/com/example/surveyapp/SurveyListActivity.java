package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class SurveyListActivity extends AppCompatActivity {

    SharedPreferences prefs;
    GridView surveyGridView;
    // Items for the GridView (Requirement 11)
    String[] tools = {"Calculator", "Official Website"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

        prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);

        // --- 1. EXISTING BUTTON LOGIC ---
        findViewById(R.id.btn1).setOnClickListener(v -> openSurvey("Student Survey"));
        findViewById(R.id.btn2).setOnClickListener(v -> openSurvey("College Survey"));
        findViewById(R.id.btn3).setOnClickListener(v -> openSurvey("App Feedback Survey"));

        // --- 2. NEW GRIDVIEW LOGIC (Req 11 & 12 & 19) ---
        surveyGridView = findViewById(R.id.surveyGridView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tools);
        surveyGridView.setAdapter(adapter);

        surveyGridView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                // Clicked "Calculator" -> Open Calculator Activity (Req 12)
                startActivity(new Intent(this, CalculatorActivity.class));
            } else if (position == 1) {
                // Clicked "Official Website" -> Implicit Intent (Req 19)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nexus-fun.vercel.app/"));
                startActivity(intent);
            }
        });

        // --- 3. LOGOUT LOGIC ---
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // --- 4. START SERVICE (Req 20) ---
        startService(new Intent(this, AppStatusService.class));
    }

    void openSurvey(String title) {
        Intent i = new Intent(this, SurveyActivity.class);
        i.putExtra("title", title);
        startActivity(i);
    }
}