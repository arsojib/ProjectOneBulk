package com.example.arsojib.bulksms.Service;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmsManagementService extends Service {

    BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;
    DatabaseHelper databaseHelper;
    NotificationManager notificationManager;
    NotificationCompat.Builder mBuilder;

    ArrayList<Contact> arrayList;
    String message;
    long messageId;
    int ID, sendCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        final Intent intent = new Intent();
        intent.setAction("SMS_MANAGEMENT");

        databaseHelper = new DatabaseHelper(this);
        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        notificationUpdate();
                        intent.putExtra("status", 1);
                        intent.putExtra("number", "");
                        sendBroadcast(intent);
                        long time = System.currentTimeMillis();
                        databaseHelper.setMessageUpdate(messageId, "", 1, time);
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
                        long time = System.currentTimeMillis();
                        databaseHelper.setMessageUpdate(messageId, "", 2, time);
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
        messageId = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        databaseHelper.addMessage(messageId, message, time, 0, arrayList);
        progressNotification();
        sendMySMS();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMySMS() {
        for (int i = 0; i < arrayList.size(); i++) {
            String phone;
            phone = arrayList.get(i).getNumber();
            send(phone);
        }

    }

    private void send(String phone) {
        SmsManager sms = SmsManager.getDefault();
        // if message length is too long messages are divided
        List<String> messages = sms.divideMessage(message);
        for (String msg : messages) {
            PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
            sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
        }
    }

    private void progressNotification() {
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("SMS Sending")
                .setContentText("Send in progress...")
                .setSmallIcon(R.drawable.message_white);

        mBuilder.setProgress(100, 0, false);
        notificationManager.notify(ID, mBuilder.build());
    }

    private void notificationUpdate() {
        sendCount++;
        if (sendCount >= arrayList.size()) {
            mBuilder.setContentText("Sms Send Complete")
                    // Removes the progress bar
                    .setProgress(0,0,false);
        } else {
            mBuilder.setProgress(100, (sendCount/arrayList.size()) * 100, false);
        }
        notificationManager.notify(ID, mBuilder.build());
    }

}
