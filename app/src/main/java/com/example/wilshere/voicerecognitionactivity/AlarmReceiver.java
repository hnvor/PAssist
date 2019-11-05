package com.example.wilshere.voicerecognitionactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    String not;
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("extra");
        String text = intent.getStringExtra("text");
        Intent service_intent = new Intent(context, RingtonePlayingService.class);
        service_intent.putExtra("extra", name);
        service_intent.putExtra("text", text);
        context.startService(service_intent);
    }

    public void Text(String text) {
        not = text;
    }

}