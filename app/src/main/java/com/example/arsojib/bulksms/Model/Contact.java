package com.example.arsojib.bulksms.Model;

import java.io.Serializable;

public class Contact implements Serializable {

    private String name, number;
    private boolean check;
    private int id, status;
    private long messageId, time;

    public Contact(String name, String number, boolean check) {
        this.name = name;
        this.number = number;
        this.check = check;
    }

    public Contact(int id, long messageId, String number, int status, long time) {
        this.id = id;
        this.messageId = messageId;
        this.number = number;
        this.status = status;
        this.time = time;
    }

    public Contact(int id, long messageId, String number, int status, long time, boolean check) {
        this.id = id;
        this.messageId = messageId;
        this.number = number;
        this.status = status;
        this.time = time;
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
