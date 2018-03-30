package com.example.android.questtime;

import java.util.List;

/**
 * Created by Luka on 30/03/2018.
 */

public class Room {

    private String roomName;
    private String difficulty;
    private String type;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    private List<String> categories;


    public Room(String roomName, String difficulty, String type, List<String> categories) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.type = type;
        this.categories = categories;
    }
}
