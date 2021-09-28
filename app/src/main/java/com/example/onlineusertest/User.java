package com.example.onlineusertest;


public class User{
    public String name,onlineStatus;
    public User() {
// Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public User(String name,String onlineStatus) {
        this.name = name;
        this.onlineStatus=onlineStatus;
    }
}