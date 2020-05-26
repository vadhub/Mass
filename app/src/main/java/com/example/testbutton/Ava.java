package com.example.testbutton;

public class Ava {
    private String gmailUser;
    private String urlImage;

    private long dateA;

    public Ava() {
    }

    public Ava(String gmailUser, String urlImage, long dateA) {
        this.gmailUser = gmailUser;
        this.urlImage = urlImage;
        this.dateA = dateA;
    }

    public long getDate() {
        return dateA;
    }

    public void setDate(long date) {
        this.dateA = dateA;
    }

    public String getGmailUser() {
        return gmailUser;
    }

    public void setGmailUser(String gmailUser) {
        this.gmailUser = gmailUser;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
