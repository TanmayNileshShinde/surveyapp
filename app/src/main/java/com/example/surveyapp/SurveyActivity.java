package com.example.surveyapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;

public class SurveyActivity extends AppCompatActivity {

    // UI Elements
    RadioGroup radioGroup;
    RadioButton r1, r2, r3;
    CheckBox cb1, cb2, cb3;
    ProgressBar progressBar;
    ImageButton btnReset1, btnReset2, btnReset3, btnReset4;
    Spinner subjectSpinner;
    TextView q1, q2, q3;

    // ViewFlipper and Navigation Buttons
    ViewFlipper surveyFlipper;
    Button btnNext1, btnNext2, btnNext3, btnSubmit;

    String surveyTitle;

    // Timer and Picker Elements
    TextView txtDate, txtTime, txtTimer;
    Button btnDate, btnTime;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        initViews();
        setupPickers();

        surveyTitle = getIntent().getStringExtra("title");
        if (surveyTitle == null) surveyTitle = "Survey";

        // Dynamically load the questions
        setSurveyQuestions(surveyTitle);

        startTimer();
        setupStepNavigation();
    }

    private void initViews() {
        surveyFlipper = findViewById(R.id.surveyFlipper);
        btnNext1 = findViewById(R.id.btnNext1);
        btnNext2 = findViewById(R.id.btnNext2);
        btnNext3 = findViewById(R.id.btnNext3);
        btnSubmit = findViewById(R.id.btnSubmit);

        q1 = findViewById(R.id.q1);
        q2 = findViewById(R.id.q2);
        q3 = findViewById(R.id.q3);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        radioGroup = findViewById(R.id.radioGroup);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);

        btnReset1 = findViewById(R.id.btnReset1);
        btnReset2 = findViewById(R.id.btnReset2);
        btnReset3 = findViewById(R.id.btnReset3);
        btnReset4 = findViewById(R.id.btnReset4);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        progressBar = findViewById(R.id.progressBar);

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtTimer = findViewById(R.id.txtTimer);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);

        btnReset1.setOnClickListener(v -> resetSurvey());
        btnReset2.setOnClickListener(v -> resetSurvey());
        btnReset3.setOnClickListener(v -> resetSurvey());
        btnReset4.setOnClickListener(v -> resetSurvey());
    }

    private void setupStepNavigation() {
        // From Schedule to Q1
        btnNext1.setOnClickListener(v -> {
            surveyFlipper.showNext();
        });

        // From Q1 to Q2 (Validates Radio Buttons)
        btnNext2.setOnClickListener(v -> {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                showCustomToast("Please select an option for Question 1", false);
                return;
            }
            surveyFlipper.showNext();
        });

        // From Q2 to Q3 (Validates Checkboxes)
        btnNext3.setOnClickListener(v -> {
            if (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked()) {
                showCustomToast("Please select at least one choice for Question 2", false);
                return;
            }
            surveyFlipper.showNext();
        });

        // Submit Button (Validates Spinner)
        btnSubmit.setOnClickListener(v -> submitSurvey());
    }

    private void setupPickers() {
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    txtDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) ->
                    txtTime.setText(h + ":" + String.format("%02d", m)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                awardPoints();
                showCustomToast("Time over. Survey auto-submitted", false);

                Intent intent = new Intent(SurveyActivity.this, SurveyCompletedActivity.class);
                intent.putExtra("title", surveyTitle);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void submitSurvey() {
        // We only check Question 3 here, Q2 was checked on btnNext3
        if (subjectSpinner.getSelectedItemPosition() == 0) {
            showCustomToast("Please answer Question 3", false);
            return;
        }

        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            awardPoints();

            Intent intent = new Intent(SurveyActivity.this, SurveyCompletedActivity.class);
            intent.putExtra("title", surveyTitle);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void awardPoints() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance("https://surveyapp-f1065-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(userId);
            userRef.child("points").setValue(ServerValue.increment(10));
        }
    }

    private void setSurveyQuestions(String survey) {
        String[] spinnerItems;

        if (survey.equals("Student Survey")) {
            q1.setText("How do you rate your studies?");
            r1.setText("Excellent"); r2.setText("Good"); r3.setText("Average");
            q2.setText("Which facilities do you use?");
            cb1.setText("Library"); cb2.setText("Labs"); cb3.setText("Sports");
            q3.setText("What is your major field?");
            spinnerItems = new String[]{"Select Major", "Computer Science", "Engineering", "Arts", "Business"};

        } else if (survey.equals("College Survey")) {
            q1.setText("How is college infrastructure?");
            r1.setText("Very Good"); r2.setText("Good"); r3.setText("Poor");
            q2.setText("What needs improvement?");
            cb1.setText("Classrooms"); cb2.setText("Canteen"); cb3.setText("Parking");
            q3.setText("Overall college rating (1-5)?");
            spinnerItems = new String[]{"Select Rating", "5 - Excellent", "4 - Good", "3 - Average", "1 - Poor"};

        } else if (survey.equals("Valorant Survey")) {
            q1.setText("What is your preferred role?");
            r1.setText("Duelist"); r2.setText("Initiator"); r3.setText("Controller/Sentinel");
            q2.setText("Which maps are your favorite?");
            cb1.setText("Ascent"); cb2.setText("Bind"); cb3.setText("Split");
            q3.setText("What is your current Rank?");
            spinnerItems = new String[]{"Select Rank", "Iron/Bronze/Silver", "Gold/Platinum", "Diamond/Ascendant", "Immortal/Radiant"};

        } else if (survey.equals("F1 Racing Survey")) {
            q1.setText("Who is your favorite team?");
            r1.setText("Red Bull"); r2.setText("Ferrari"); r3.setText("McLaren");
            q2.setText("Which tracks do you love?");
            cb1.setText("Monaco"); cb2.setText("Silverstone"); cb3.setText("Spa");
            q3.setText("Who is your favorite driver?");
            spinnerItems = new String[]{"Select Driver", "Max Verstappen", "Charles Leclerc", "Lando Norris", "Lewis Hamilton"};

        } else if (survey.equals("Minecraft Hypixel Survey")) {
            q1.setText("What is your favorite Mini-game?");
            r1.setText("Skyblock"); r2.setText("Bedwars"); r3.setText("Murder Mystery");
            q2.setText("What do you enjoy doing most?");
            cb1.setText("PvP/Fighting"); cb2.setText("Building"); cb3.setText("Grinding Economy");
            q3.setText("How long have you been playing?");
            spinnerItems = new String[]{"Select Time", "Less than 1 Year", "1-3 Years", "3+ Years"};

        } else if (survey.equals("Developer Survey")) {
            q1.setText("What platform do you build for?");
            r1.setText("Android/Mobile"); r2.setText("Web (React/JS)"); r3.setText("Desktop/Other");
            q2.setText("Which languages do you use?");
            cb1.setText("Java"); cb2.setText("JavaScript"); cb3.setText("Python");
            q3.setText("How much experience do you have?");
            spinnerItems = new String[]{"Select Experience", "Beginner", "1-2 Years", "3-5 Years", "5+ Years"};

        } else if (survey.equals("Smartphone Usage Survey")) {
            q1.setText("What is your primary OS?");
            r1.setText("Android"); r2.setText("iOS"); r3.setText("Other");
            q2.setText("What apps do you use most?");
            cb1.setText("Social Media"); cb2.setText("Games"); cb3.setText("Productivity");
            q3.setText("How often do you upgrade your phone?");
            spinnerItems = new String[]{"Select Frequency", "Every Year", "Every 2 Years", "3+ Years"};

        } else if (survey.equals("Gamer Setup Survey")) {
            q1.setText("What is your primary platform?");
            r1.setText("PC"); r2.setText("Console"); r3.setText("Mobile");
            q2.setText("What gear matters most to you?");
            cb1.setText("High-End Mouse"); cb2.setText("Mechanical Keyboard"); cb3.setText("Good Headset");
            q3.setText("What is your monitor refresh rate?");
            spinnerItems = new String[]{"Select Rate", "60Hz", "120Hz/144Hz", "240Hz+"};

        } else {
            // Default: App Feedback Survey
            q1.setText("How is this app?");
            r1.setText("Easy"); r2.setText("Average"); r3.setText("Hard");
            q2.setText("What do you like?");
            cb1.setText("UI Design"); cb2.setText("Speed"); cb3.setText("Features");
            q3.setText("How often do you use it?");
            spinnerItems = new String[]{"Select Frequency", "Daily", "Weekly", "Rarely"};
        }

        // Attach the dropdown list to the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }

    private void showCustomToast(String msg, boolean success) {
        View layout = LayoutInflater.from(this).inflate(R.layout.toast_custom, findViewById(R.id.toastRoot));
        TextView text = layout.findViewById(R.id.toastText);
        ImageView icon = layout.findViewById(R.id.toastIcon);
        text.setText(msg);
        icon.setImageResource(success ? R.drawable.like : R.drawable.dislike);
        layout.setBackgroundResource(success ? R.drawable.toast_success_bg : R.drawable.toast_fail_bg);
        Toast toast = new Toast(this);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void resetSurvey() {
        txtDate.setText("No date selected");
        txtTime.setText("No time selected");
        radioGroup.clearCheck();
        cb1.setChecked(false); cb2.setChecked(false); cb3.setChecked(false);
        subjectSpinner.setSelection(0);
        showCustomToast("All choices cleared", true);
    }

    @Override
    public void onBackPressed() {
        if (surveyFlipper.getDisplayedChild() > 0) {
            surveyFlipper.showPrevious();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}