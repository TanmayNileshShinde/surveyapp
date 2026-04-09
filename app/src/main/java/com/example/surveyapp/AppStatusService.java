package com.example.surveyapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppStatusService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Trigger the premium toast instead of the default one
        showPremiumToast("Background Process Started");

        return START_STICKY; // Keeps the service running if the system kills it
    }

    private void showPremiumToast(String message) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Uses the same premium XML we built for the ModeReceiver
            View layout = inflater.inflate(R.layout.toast_custom, null);

            TextView text = layout.findViewById(R.id.toastText);
            ImageView icon = layout.findViewById(R.id.toastIcon);

            if (text != null) text.setText(message);
            if (icon != null) icon.setImageResource(android.R.drawable.ic_dialog_info);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}