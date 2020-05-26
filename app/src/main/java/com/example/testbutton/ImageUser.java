package com.example.testbutton;

public class ImageUser {
    private String gmailUser;
    private String urlImage;

    private long date;

    public ImageUser() {
    }

    public ImageUser(String gmailUser, String urlImage, long date) {
        this.gmailUser = gmailUser;
        this.urlImage = urlImage;
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
