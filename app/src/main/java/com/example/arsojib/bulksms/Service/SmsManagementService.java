package com.example.arsojib.bulksms.Service;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.arsojib.bulksms.Model.Contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmsManagementService extends Service {

    BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    ArrayList<Contact> arrayList;

    String phone;
    String message;

    @Override
    public void onCreate() {
        super.onCreate();
        final Intent intent = new Intent();
        intent.setAction("SMS_MANAGEMENT");

        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        intent.putExtra("status", 1);
                        intent.putExtra("number", "");
                        sendBroadcast(intent);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }

            }
        };
        deliveredStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        intent.putExtra("status", 2);
                        intent.putExtra("number", "");
                        sendBroadcast(intent);
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        arrayList = (ArrayList<Contact>) intent.getSerializableExtra("contact_list");
        message = intent.getStringExtra("message");
        sendMySMS();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMySMS() {
        StringBuilder builder = new StringBuilder();
        String delim = "";
        for (int i = 0; i < arrayList.size(); i++) {
            builder.append(delim).append(arrayList.get(i).getNumber());
            delim = ";";
        }
        phone = builder.toString();
        SmsManager sms = SmsManager.getDefault();
        // if message length is too long messages are divided
        List<String> messages = sms.divideMessage(message);
        for (String msg : messages) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
            sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
        }
    }

}
