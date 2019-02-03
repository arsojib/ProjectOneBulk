package com.example.arsojib.bulksms.Model;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/29/2019.
 */

public class Message {

    private long id;
    private String message;
    private long time;
    private int count;

    public Message(long id, String message, long time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }

    public Message(long id, String message, long time, int count) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
