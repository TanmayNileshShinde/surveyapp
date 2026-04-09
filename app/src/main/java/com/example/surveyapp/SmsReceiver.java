package com.example.surveyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        String format = bundle.getString("format");
        StringBuilder msg = new StringBuilder();
        String sender = "";

        for (Object pdu : pdus) {
            SmsMessage sms;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                sms = SmsMessage.createFromPdu((byte[]) pdu);
            }
            sender = sms.getDisplayOriginatingAddress();
            msg.append(sms.getMessageBody());
        }

        String finalMsg = msg.toString();
        Toast.makeText(context, "SMS from: " + sender, Toast.LENGTH_LONG).show();

        // Updated package name
        Intent ui = new Intent("com.example.surveyapp.SMS_UI_UPDATE");
        ui.setPackage(context.getPackageName());
        ui.putExtra("from", sender);
        ui.putExtra("msg", finalMsg);
        context.sendBroadcast(ui);
    }
}