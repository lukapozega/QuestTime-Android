package com.example.android.questtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

/**
 * Created by Luka on 09/07/2018.
 */

public class ClickSound {

    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    public ClickSound(Context context) {
        sharedPreferences = context.getSharedPreferences("com.example.android.questtime", context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(context, R.raw.sound);
    }

    public void start() {
        if (sharedPreferences.getBoolean("Sound", true)) {
            mediaPlayer.start();
        }
    }

}
