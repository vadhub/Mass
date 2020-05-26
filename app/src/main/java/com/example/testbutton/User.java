package com.example.testbutton;

public class User {
    private String nameUser;
    private String email;
    private  long date;
    private String avatar;

    public User() {
    }

    public User(String nameUser, String email, long date, String avatar) {
        this.avatar = avatar;
        this.date = date;
        this.nameUser = nameUser;
        this.email = email;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
