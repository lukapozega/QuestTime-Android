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
    private List<String> userIds;
    private int numberOfUsers;
    private String key;
    private String privateKey;
    private int zastavica;

    public Room(String roomName, String difficulty, String type, List<String> categories, String privateKey) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.type = type;
        this.categories = categories;
        this.privateKey = privateKey;
    }

    public Room(String roomName, String difficulty, List<String> categories, String key, String privateKey, String type, int zastavica) {
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.categories = categories;
        this.key = key;
        this.privateKey = privateKey;
        this.type = type;
        this.zastavica = zastavica;
    }

    public Room(String roomName, String difficulty, List<String> categories, String key, List<String> userIds){
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.categories = categories;
        this.key = key;
        this.userIds = userIds;
    }

    public Room(String roomName, String difficulty, List<String> categories, String key, String type, int zastavica){
        this.roomName = roomName;
        this.difficulty = difficulty;
        this.categories = categories;
        this.key = key;
        this.type = type;
        this.zastavica = zastavica;

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

    public String getKey() {
        return key;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public int getZastavica() {
        return zastavica;
    }

    public void setZastavica(int zastavica) {
        this.zastavica = zastavica;
    }


}
