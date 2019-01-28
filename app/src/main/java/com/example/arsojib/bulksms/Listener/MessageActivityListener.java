package com.example.arsojib.bulksms.Listener;

public interface MessageActivityListener {

    void onMessageSentSuccessfully(String number);
    void onMessageFailure(String number);
    void onMessageDelivered(String number);
    void onMessageNotDelivered(String number);

}
