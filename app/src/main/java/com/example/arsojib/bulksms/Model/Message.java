package com.example.arsojib.bulksms.Model;

/**
 * Created by AR Sajib on 1/29/2019.
 */

public class Message {

    private long id;
    private String message;
    private long time;

    public Message(long id, String message, long time) {
        this.id = id;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
