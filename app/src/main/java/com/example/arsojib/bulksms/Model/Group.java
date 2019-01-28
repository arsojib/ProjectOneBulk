package com.example.arsojib.bulksms.Model;

/**
 * Created by AR Sajib on 1/28/2019.
 */

public class Group {

    private String id, title, count;
    private boolean check;

    public Group(String id, String title, String count, boolean check) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
