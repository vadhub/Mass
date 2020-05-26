package com.example.testbutton;

public class audioItem {
    private String text_Audio;
    private  long date;
    private String id_user;
    private String uriAudio;
    private int duration;

    public audioItem (){

    }
    public audioItem(String text_Audio, long date, String id_user, String uriAudio){
        this.id_user = id_user;
        this.date = date;
        this.text_Audio = text_Audio;
        this.uriAudio = uriAudio;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUriAudio() {
        return uriAudio;
    }

    public void setUriAudio(String uriAudio) {
        this.uriAudio = uriAudio;
    }


    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getText_Audio() {
        return text_Audio;
    }

    public void setText_Audio(String text_Audio) {
        this.text_Audio = text_Audio;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
