package com.example.android.questtime;

/**
 * Created by Luka on 07/05/2018.
 */

public class Question {

    private String text;
    private long timestamp;
    private int points;

    public Question(String text, long timestamp, int points) {
        this.text = text;
        this.timestamp = timestamp;
        this.points = points;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getPoints() {
        return points;
    }
}
