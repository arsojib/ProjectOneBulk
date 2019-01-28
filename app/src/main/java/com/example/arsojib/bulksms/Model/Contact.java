package com.example.arsojib.bulksms.Model;

import java.io.Serializable;

public class Contact implements Serializable {

    private String name, number;
    private boolean check;

    public Contact(String name, String number, boolean check) {
        this.name = name;
        this.number = number;
        this.check = check;
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
