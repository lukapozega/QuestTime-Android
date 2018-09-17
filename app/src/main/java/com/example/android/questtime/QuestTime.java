package com.example.android.questtime;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by fgrebenac on 3/7/18.
 */

public class QuestTime extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }

}
