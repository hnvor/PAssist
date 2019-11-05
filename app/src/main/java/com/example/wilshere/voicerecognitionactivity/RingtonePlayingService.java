package com.example.wilshere.voicerecognitionactivity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Created by Wilshere on 10.05.2017.
 */

public class RingtonePlayingService extends Service {
    MediaPlayer media_song;
    boolean isRunning;
    private TextToSpeech t1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent,int flags, int startId){
        String name = intent.getStringExtra("extra");
        String text = intent.getStringExtra("text");
        text = text.replaceAll("напомни","");
        final String finalText = text;
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale locale = new Locale("ru");
                    int result = t1.setLanguage(locale);
                    t1.speak("Напоминаю, тебе нужно: " + finalText, TextToSpeech.QUEUE_FLUSH, null);
                }
                else {
                    Locale locale = new Locale("ru");
                    int result = t1.setLanguage(locale);
                    t1.speak("Напоминаю, тебе нужно: " + finalText, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        //Intent intent2 = new Intent(this, PrefActivity.class);
       //intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent2);
      /*  if(name.equals("alarm on")){
        startId = 1;
        }
        else if (name.equals("alarm off"))
        {
            startId = 0;
        }
        else {
            startId = 0;
        }
        if (!this.isRunning && startId == 1){
            media_song = MediaPlayer.create(this, R.raw.bud);
            media_song.start();
            this.isRunning = true;
            startId = 0;
        }
        else if (this.isRunning && startId == 0){
            media_song.stop();
            media_song.reset();
            this.isRunning = false;
            startId = 0;
        }
        else if (!this.isRunning && startId == 0){
            this.isRunning = false;
            startId = 0;
        }
        else if (this.isRunning && startId == 1){
            this.isRunning = true;
            startId = 1;
        } else {
        }*/
        return START_NOT_STICKY;
    }


    public void onDestroy(){
        super.onDestroy();
        this.isRunning = false;
    }
}
