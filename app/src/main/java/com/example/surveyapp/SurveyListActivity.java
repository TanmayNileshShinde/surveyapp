package com.example.surveyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SurveyListActivity extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);

        prefs = getSharedPreferences("SurveyPrefs", MODE_PRIVATE);

        findViewById(R.id.btn1).setOnClickListener(v -> openSurvey("Student Survey"));
        findViewById(R.id.btn2).setOnClickListener(v -> openSurvey("College Survey"));
        findViewById(R.id.btn3).setOnClickListener(v -> openSurvey("App Feedback Survey"));

       Button logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    void openSurvey(String title) {
        Intent i = new Intent(this, SurveyActivity.class);
        i.putExtra("title", title);
        startActivity(i);
    }
}
