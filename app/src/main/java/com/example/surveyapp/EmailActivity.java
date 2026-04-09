package com.example.surveyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EmailActivity extends AppCompatActivity {

    private EditText etTo, etSub, etMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email); // Updated layout name

        etTo = findViewById(R.id.etTo);
        etSub = findViewById(R.id.etSub);
        etMsg = findViewById(R.id.etMsg);
        Button btnSend = findViewById(R.id.btnSendEmail);

        // --- PRE-FILL LOGIC ADDED HERE ---
        // Check if a subject or message was passed to this activity
        String prefillSub = getIntent().getStringExtra("prefill_sub");
        String prefillMsg = getIntent().getStringExtra("prefill_msg");

        if (prefillSub != null) etSub.setText(prefillSub);
        if (prefillMsg != null) etMsg.setText(prefillMsg);
        // ---------------------------------

        btnSend.setOnClickListener(v -> sendEmail());
    }

    private void sendEmail() {
        String to = etTo.getText().toString().trim().replace(" ", "");
        String sub = etSub.getText().toString().trim();
        String msg = etMsg.getText().toString().trim();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(Intent.EXTRA_SUBJECT, sub);
        intent.putExtra(Intent.EXTRA_TEXT, msg);

        // try Gmail first
        intent.setPackage("com.google.android.gm");

        try {
            startActivity(intent);
        } catch (Exception e) {
            // fallback to chooser
            startActivity(Intent.createChooser(intent, "Choose Email App"));
        }
    }
}