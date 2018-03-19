package com.example.android.questtime;


/**
 * This class represents one user, and is used for constructing a new user when registering to an application.
 */
public class User {

    public String username;
    public String email;

    public User(){

    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}