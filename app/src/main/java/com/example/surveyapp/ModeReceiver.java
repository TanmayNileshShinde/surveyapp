package com.example.surveyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
            String status = isAirplaneModeOn ? "Airplane Mode: ON" : "Airplane Mode: OFF";
            showCustomToast(context, status, android.R.drawable.ic_dialog_info);
        }
        else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        showCustomToast(context, "Phone is SILENT", android.R.drawable.ic_lock_silent_mode);
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        // FIXED: Changed icon to a more compatible system resource
                        showCustomToast(context, "Phone is on VIBRATE", android.R.drawable.ic_menu_call);
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        showCustomToast(context, "Phone is LOUD", android.R.drawable.button_onoff_indicator_on);
                        break;
                }
            }
        }
    }

    private void showCustomToast(Context context, String message, int iconRes) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return;

        // Inflate the premium layout we built
        View layout = inflater.inflate(R.layout.toast_custom, null);

        TextView text = layout.findViewById(R.id.toastText);
        ImageView icon = layout.findViewById(R.id.toastIcon);

        if (text != null) text.setText(message);
        if (icon != null) icon.setImageResource(iconRes);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}