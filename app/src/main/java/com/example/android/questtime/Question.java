package com.example.android.questtime;

/**
 * Created by Luka on 07/05/2018.
 */

public class Question {

    private String text;
    private long timestamp;
    private int points;
    private String id;
    private String category;
    private String correct;
    private String wrong[];

    public Question(String text, long timestamp, int points, String id, String category) {
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

    public long getTimestamp() {
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
}
