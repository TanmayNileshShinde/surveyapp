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

import java.util.Calendar;

public class SurveyActivity extends AppCompatActivity {

    // ===== EXISTING =====
    RadioGroup radioGroup;
    RadioButton r1, r2, r3;
    CheckBox cb1, cb2, cb3;
    ProgressBar progressBar;
    ImageButton btnReset;
    Button btnSubmit;
    Spinner subjectSpinner;
    TextView q1, q2, q3;

    // ===== NEW (ADDED) =====
    TextView txtDate, txtTime, txtTimer;
    Button btnDate, btnTime;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        initViews();

        // ===== NEW : Init added views =====
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtTimer = findViewById(R.id.txtTimer);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);

        // ===== Survey title logic (UNCHANGED) =====
        String survey = getIntent().getStringExtra("title");
        if (survey == null) survey = "Survey";

        setSurveyQuestions(survey);

        // ===== NEW : Date Picker =====
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, y, m, d) ->
                            txtDate.setText(d + "/" + (m + 1) + "/" + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // ===== NEW : Time Picker =====
        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this,
                    (view, h, m) ->
                            txtTime.setText(h + ":" + String.format("%02d", m)),
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    true
            ).show();
        });

        // ===== NEW : Countdown Timer (60 seconds) =====
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("Time Left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                showCustomToast("Time over. Survey auto-submitted", false);
                startActivity(new Intent(SurveyActivity.this, SurveyListActivity.class));
                finish();
            }
        }.start();

        // ===== EXISTING =====
        btnSubmit.setOnClickListener(v -> submitSurvey());
    }

    // ===== INIT (UNCHANGED) =====
    private void initViews() {
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

        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetSurvey());

        subjectSpinner = findViewById(R.id.subjectSpinner);
        progressBar = findViewById(R.id.progressBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        q3.setVisibility(View.GONE);
        subjectSpinner.setVisibility(View.GONE);
    }

    // ===== QUESTIONS (UNCHANGED) =====
    private void setSurveyQuestions(String survey) {

        if (survey.equals("Student Survey")) {

            q1.setText("How do you rate your studies?");
            r1.setText("Excellent");
            r2.setText("Good");
            r3.setText("Average");

            q2.setText("Which facilities do you use?");
            cb1.setText("Library");
            cb2.setText("Labs");
            cb3.setText("Sports");

            setupSpinner();

        } else if (survey.equals("College Survey")) {

            q1.setText("How is college infrastructure?");
            r1.setText("Very Good");
            r2.setText("Good");
            r3.setText("Poor");

            q2.setText("What needs improvement?");
            cb1.setText("Classrooms");
            cb2.setText("Canteen");
            cb3.setText("Parking");

        } else {

            q1.setText("How is this app?");
            r1.setText("Easy");
            r2.setText("Average");
            r3.setText("Hard");

            q2.setText("What do you like?");
            cb1.setText("UI");
            cb2.setText("Speed");
            cb3.setText("Simplicity");
        }
    }

    // ===== SPINNER (UNCHANGED) =====
    private void setupSpinner() {
        q3.setText("What is your favorite subject?");
        q3.setVisibility(View.VISIBLE);
        subjectSpinner.setVisibility(View.VISIBLE);

        String[] subjects = {
                "Select Subject",
                "Math",
                "Science",
                "History",
                "English"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subjects
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapter);
    }

    // ===== SUBMIT (UNCHANGED) =====
    private void submitSurvey() {

        if (radioGroup.getCheckedRadioButtonId() == -1 ||
                (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked())) {
            showCustomToast("Please answer all questions", false);
            return;
        }

        if (subjectSpinner.getVisibility() == View.VISIBLE &&
                subjectSpinner.getSelectedItemPosition() == 0) {
            showCustomToast("Please select a subject", false);
            return;
        }

        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {

            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);

            showCustomToast("Survey submitted successfully", true);
            startActivity(new Intent(this, SurveyListActivity.class));
            finish();

        }, 1500);
    }

    // ===== TOAST (UNCHANGED) =====
    private void showCustomToast(String msg, boolean success) {
        View layout = LayoutInflater.from(this)
                .inflate(R.layout.toast_custom, findViewById(R.id.toastRoot));

        ImageView icon = layout.findViewById(R.id.toastIcon);
        TextView text = layout.findViewById(R.id.toastText);

        text.setText(msg);
        icon.setImageResource(success ? R.drawable.like : R.drawable.dislike);
        layout.setBackgroundResource(success
                ? R.drawable.toast_success_bg
                : R.drawable.toast_fail_bg);

        Toast toast = new Toast(this);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    // ===== RESET (UNCHANGED) =====
    private void resetSurvey() {

        radioGroup.clearCheck();

        cb1.setChecked(false);
        cb2.setChecked(false);
        cb3.setChecked(false);

        if (subjectSpinner.getVisibility() == View.VISIBLE) {
            subjectSpinner.setSelection(0);
        }

        showCustomToast("Choices cleared", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
