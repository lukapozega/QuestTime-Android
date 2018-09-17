package com.example.android.questtime.utils.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.example.android.questtime.R;

public class MediaUtils {

    public static void playButtonClick(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("com.example.android.questtime", context.MODE_PRIVATE);
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sound);
        if(preferences.getBoolean("Sound", true)) {
            mediaPlayer.start();
        }
    }

}
