package com.example.android.questtime;

import android.support.annotation.NonNull;

public class PeoplePoints implements Comparable<PeoplePoints> {

    String username;
    int points;

    public PeoplePoints(){

    }

    public PeoplePoints(String username, int points){
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public int compareTo(@NonNull PeoplePoints o) {
        int comparePoints = o.getPoints();
        return comparePoints - this.getPoints();
    }
}
