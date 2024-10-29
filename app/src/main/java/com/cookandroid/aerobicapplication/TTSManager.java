package com.cookandroid.aerobicapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.cookandroid.aerobicapplication.userdata.CommentData;

import java.util.Locale;
import java.util.Random;

public class TTSManager {
    private TextToSpeech tts;
    private CommentData exerciseTips;

    public TTSManager(Context context, CommentData tips) {
        this.exerciseTips = tips;
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREAN);
            }
        });
    }

    public void speakRandomTip() {
        Random random = new Random();
        int randomIndex = random.nextInt(exerciseTips.tips.length);
        String tip = exerciseTips.tips[randomIndex];
        tts.speak(tip, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

}
