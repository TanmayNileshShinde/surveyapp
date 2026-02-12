package com.example.surveyapp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class AppStatusService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Background Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}