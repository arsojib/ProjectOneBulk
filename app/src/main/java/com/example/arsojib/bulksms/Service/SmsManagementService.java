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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
    String message, currentNumber = "";
    long messageId;
    boolean isOld;
    int ID, sim, sendCount = 0, smsCount = 0, messageSize = 0, messageSentComplete = 0;

    final Handler handler = new Handler();
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
                int status = 0;
                boolean notifiable = false;
                handler.removeCallbacksAndMessages(null);

                Log.d("Multipart_sms", arg1.toString());
                String number = "";
                try {
                    number = arrayList.get(smsCount - 1).getNumber();
                } catch (IndexOutOfBoundsException ignored) {
                }

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        if (number.equals(currentNumber)) {
                            messageSentComplete++;
                            notifiable = true;
                        }
                        Log.d("SMS_TEST", number + " " + messageSentComplete);
                        status = 1;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        if (number.equals(currentNumber)) {
                            messageSentComplete++;
                            notifiable = true;
                        }
                        status = 0;
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        if (number.equals(currentNumber)) {
                            messageSentComplete++;
                            notifiable = true;
                        }
                        status = 0;
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        if (number.equals(currentNumber)) {
                            messageSentComplete++;
                            notifiable = true;
                        }
                        status = 0;
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        if (number.equals(currentNumber)) {
                            messageSentComplete++;
                            notifiable = true;
                        }
                        status = 0;
                        break;
                    default:
                        break;
                }

                if (messageSentComplete >= messageSize) {
                    try {
                        messageSentComplete = 0;
                        String phone;
                        phone = arrayList.get(smsCount).getNumber();
                        send(phone);
                    } catch (IndexOutOfBoundsException ignored) {
                    } catch (NullPointerException ignored) {
                    }
                } else {
                    if (messageSentComplete == 1) {
                        if (notifiable) {
                            if (status == 1) {
                                intent.putExtra("status", 1);
                                intent.putExtra("number", number);
                                sendBroadcast(intent);
                                long time = System.currentTimeMillis();
                                databaseHelper.setMessageUpdate(messageId, number, 1, time);
                            } else {
                                intent.putExtra("status", 0);
                                intent.putExtra("number", "0");
                                sendBroadcast(intent);
                            }
                            notificationUpdate();
                        }
                    }
                }

                sentDelay();

            }
        };
        deliveredStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        String number = ((SmsManagementService) arg0).currentNumber;
//                        intent.putExtra("status", 2);
//                        intent.putExtra("number", number);
//                        sendBroadcast(intent);
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

    private void sentDelay() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    messageSentComplete = 0;
                    Log.d("SMS_TEST", "Handler");
                    String phone;
                    phone = arrayList.get(smsCount).getNumber();
                    send(phone);
                } catch (IndexOutOfBoundsException ignored) {
                } catch (NullPointerException ignored) {
                }
            }
        }, 30 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendCount = 0;
        smsCount = 0;
        messageSize = 0;
        messageSentComplete = 0;
        currentNumber = "";
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
        setSmsManager();
        progressNotification();
        try {
            messageSize = sms.divideMessage(message).size();
            String phone;
            phone = arrayList.get(0).getNumber();
            send(phone);
        } catch (IndexOutOfBoundsException ignored) {
        } catch (NullPointerException ignored) {
        }
//        sendMySMS();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    public void sendMySMS() {
//        Log.d(TAG, "send count " + arrayList.size());
//        for (int i = 0; i < arrayList.size(); i++) {
//            String phone;
//            phone = arrayList.get(i).getNumber();
//            send(phone);
//        }
//
//    }

    SmsManager sms;

    private void setSmsManager() {
        if (sim == Util.defaultID) {
            sms = SmsManager.getDefault();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                sms = SmsManager.getSmsManagerForSubscriptionId(sim);
            } else {
                sms = SmsManager.getDefault();
            }
        }
    }

    private void send(String phone) {
        Log.d(TAG, "service start");
        smsCount++;
        currentNumber = phone;
        // if message length is too long messages are divided
        ArrayList<String> messages = sms.divideMessage(message);
        ArrayList<PendingIntent> sentIntents = new ArrayList<>();
        ArrayList<PendingIntent> deliveredIntents = new ArrayList<>();

        if (messages.size() > 1) {
            for (String msg : messages) {
                sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0));
                deliveredIntents.add(PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0));
            }
            sms.sendMultipartTextMessage(phone, null, messages, sentIntents, deliveredIntents);
        } else {
            for (String msg : messages) {
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);
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
            float result = (float) sendCount / (float) arrayList.size();
            mBuilder.setProgress(100, (int) (result * 100), true);
        }
        notificationManager.notify(ID, mBuilder.build());
    }

}
