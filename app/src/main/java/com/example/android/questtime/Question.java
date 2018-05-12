package com.example.android.questtime;

import android.support.annotation.NonNull;

/**
 * Created by Luka on 07/05/2018.
 */

public class Question implements Comparable<Question>{

    private String text;
    private double timestamp;
    private int points;
    private String id;
    private String category;
    private String correct;
    private String wrong[];

    public Question(String text, double timestamp, int points, String id, String category) {
        this.text = text;
        this.timestamp = timestamp;
        this.points = points;
        this.id = id;
        this.category = category;
    }

    public Question(String text, int points, String id, String correct, String wrong[]) {
        this.text = text;
        this.points = points;
        this.id = id;
        this.correct=correct;
        this.wrong=wrong;
    }

    public String getText() {
        return text;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getPoints() {
        return points;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getCorrect () {
        return correct;
    }

    public String[] getWrong() {
        return wrong;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < id.length(); i++) {
            hash = hash*31 + id.charAt(i);
        }
        return hash;
    }

    @Override
    public int compareTo(@NonNull Question question) {
        double compareDate = question.getTimestamp();
        double d = compareDate - this.getTimestamp();
        return (int) d;
    }
}
