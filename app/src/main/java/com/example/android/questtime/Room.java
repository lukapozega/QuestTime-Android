package com.example.android.questtime;

import java.util.List;

/**
 * Created by Luka on 30/03/2018.
 */

public class Room {

    private String roomName;
    private String difficulty;
    private String type;
    private List<String> categories;
    private int numberOfUsers;

    public Room(String roomName, String difficulty, String type, List<String> categories) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.type = type;
        this.categories = categories;
    }

    public Room(String roomName, String difficulty, List<String> categories, int numberOfUsers) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.categories = categories;
        this.numberOfUsers = numberOfUsers;
    }

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

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}