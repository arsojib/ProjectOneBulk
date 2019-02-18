package com.example.arsojib.bulksms.Service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

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
    boolean isOld;
    int ID, sim, sendCount = 0;

    String TAG = "SmsManagementService";

    @Override
    public void onCreate() {
        super.onCreate();
        final Intent intent = new Intent();
        intent.setAction("SMS_MANAGEMENT");
        Log.d(TAG, "service start");

        databaseHelper = new DatabaseHelper(this);
        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        notificationUpdate();
                        String number = ((SmsManagementService) arg0).arrayList.get(0).getNumber();
                        intent.putExtra("status", 1);
                        intent.putExtra("number", number);
                        sendBroadcast(intent);
                        long time = System.currentTimeMillis();
                        databaseHelper.setMessageUpdate(messageId, number, 1, time);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        notificationUpdate();
                        intent.putExtra("status", 0);
                        intent.putExtra("number", "0");
                        sendBroadcast(intent);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        notificationUpdate();
                        intent.putExtra("status", 0);
                        intent.putExtra("number", "0");
                        sendBroadcast(intent);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        notificationUpdate();
                        intent.putExtra("status", 0);
                        intent.putExtra("number", "0");
                        sendBroadcast(intent);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        notificationUpdate();
                        intent.putExtra("status", 0);
                        intent.putExtra("number", "0");
                        sendBroadcast(intent);
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
                        String number = ((SmsManagementService) arg0).arrayList.get(0).getNumber();
                        intent.putExtra("status", 2);
                        intent.putExtra("number", number);
                        sendBroadcast(intent);
                        long time = System.currentTimeMillis();
                        databaseHelper.setMessageUpdate(messageId, number, 2, time);
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
        sim = intent.getIntExtra("sim", Util.defaultID);
        isOld = intent.getBooleanExtra("old", false);
        messageId = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        if (!isOld) {
            databaseHelper.addMessage(messageId, message, time, 0, arrayList);
        } else {
            messageId = intent.getLongExtra("message_id", 0);
        }
        progressNotification();
        sendMySMS();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void sendMySMS() {
        Log.d(TAG, "send count " + arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            String phone;
            phone = arrayList.get(i).getNumber();
            send(phone);
        }

    }

    private void send(String phone) {
        Log.d(TAG, "service start");
        SmsManager sms;
        if (sim == Util.defaultID) {
            sms = SmsManager.getDefault();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                sms = SmsManager.getSmsManagerForSubscriptionId(sim);
            } else {
                sms = SmsManager.getDefault();
            }
        }
        // if message length is too long messages are divided
        List<String> messages = sms.divideMessage(message);
        for (String msg : messages) {
            try {
                Thread.sleep(1200);
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "send sent " + message);
    }

    private void progressNotification() {
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "bulk_sms";
        CharSequence name = "bulk_sms_channel";
        String Description = "bulk sms sending channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.setContentTitle("Bulk SMS")
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
                    .setProgress(0, 0, false);
        } else {
            mBuilder.setProgress(100, (sendCount / arrayList.size()) * 100, true);
        }
        notificationManager.notify(ID, mBuilder.build());
    }

}
