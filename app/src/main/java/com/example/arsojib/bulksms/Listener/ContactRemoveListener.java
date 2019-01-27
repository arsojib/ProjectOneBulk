package com.example.arsojib.bulksms.Listener;

import com.example.arsojib.bulksms.Model.Contact;

public interface ContactRemoveListener {

    void onContactRemove(String number, int position);
    void onContactUnCheck(Contact contact, int position);

}
