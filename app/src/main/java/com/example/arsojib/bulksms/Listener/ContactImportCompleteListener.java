package com.example.arsojib.bulksms.Listener;

import com.example.arsojib.bulksms.Model.Contact;

import java.util.ArrayList;

public interface ContactImportCompleteListener {

    void onImportComplete(ArrayList<Contact> arrayList);
    void onImportCompleteCount(int count);

}
