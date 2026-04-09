package com.example.surveyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // --- 1. EXISTING BUTTON LOGIC + NEW SURVEYS ---
        view.findViewById(R.id.btn1).setOnClickListener(v -> openSurvey("Student Survey"));
        view.findViewById(R.id.btn2).setOnClickListener(v -> openSurvey("College Survey"));
        view.findViewById(R.id.btn3).setOnClickListener(v -> openSurvey("App Feedback Survey"));

        // GAME & SPORT SURVEYS
        view.findViewById(R.id.btn4).setOnClickListener(v -> openSurvey("Valorant Survey"));
        view.findViewById(R.id.btn5).setOnClickListener(v -> openSurvey("F1 Racing Survey"));
        view.findViewById(R.id.btn6).setOnClickListener(v -> openSurvey("Minecraft Hypixel Survey"));

        // TECH & LIFESTYLE SURVEYS
        view.findViewById(R.id.btn7).setOnClickListener(v -> openSurvey("Developer Survey"));
        view.findViewById(R.id.btn8).setOnClickListener(v -> openSurvey("Smartphone Usage Survey"));
        view.findViewById(R.id.btn9).setOnClickListener(v -> openSurvey("Gamer Setup Survey"));

        // --- 2. WIFI TOGGLE LOGIC ---
        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Button btnWifiToggle = view.findViewById(R.id.btnWifiToggle);

        btnWifiToggle.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivity(panelIntent);
            } else {
                if (wifiManager != null) {
                    wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
                }
            }
        });

        // --- 3. PREMIUM CARD LOGIC ---
        view.findViewById(R.id.cardCalculator).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CalculatorActivity.class)));

        view.findViewById(R.id.cardWebsite).setOnClickListener(v -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://nexus-fun.vercel.app/"));
            startActivity(webIntent);
        });

        view.findViewById(R.id.cardContacts).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyContactsActivity.class)));



        view.findViewById(R.id.cardTorch).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), TorchActivity.class)));

        return view;
    }

    // Helper method to open the survey
    void openSurvey(String title) {
        Intent i = new Intent(requireContext(), SurveyActivity.class);
        i.putExtra("title", title);
        startActivity(i);
    }
}