package com.example.testbutton;

public class Page {
    private int type;
    private String author;
    private String text;
    private String imageUserOnAudio;
    private String uriAudio;

    public String getUriAudio() {
        return uriAudio;
    }

    public void setUriAudio(String uriAudio) {
        this.uriAudio = uriAudio;
    }

    public Page() {
    }

    public Page(int type, String text,String author) {
        this.author = author;
        this.type = type;
        this.text = text;
    }
    public Page(int type, String text,String author, String uriAudio) {
        this.author = author;
        this.type = type;
        this.text = text;
        this.uriAudio = uriAudio;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
