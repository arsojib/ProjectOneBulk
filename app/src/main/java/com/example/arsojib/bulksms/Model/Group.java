package com.example.arsojib.bulksms.Model;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/28/2019.
 */

public class Group {

    private String id, title, count;
    private boolean check;
    private ArrayList<Contact> arrayList;

    public Group(String id, String title, String count, boolean check, ArrayList<Contact> arrayList) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.check = check;
        this.arrayList = arrayList;
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

    public ArrayList<Contact> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Contact> arrayList) {
        this.arrayList = arrayList;
    }
}
