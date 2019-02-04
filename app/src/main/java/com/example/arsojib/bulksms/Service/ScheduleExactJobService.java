package com.example.arsojib.bulksms.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleExactJobService extends Job {

    public static final String TAG = "schedule_exact_job_service";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        long id = params.getExtras().getLong("id", 0);
        String message = params.getExtras().getString("message", "");
        if (id != 0 && !message.equals("")) {
            ArrayList<Contact> contacts = new ArrayList<>(databaseHelper.getAllScheduleNumberUsingScheduleID(id));
            Intent intent = new Intent(getContext(), SmsManagementService.class);
            intent.putExtra("contact_list", contacts);
            intent.putExtra("message", message);
            intent.putExtra("sim", 5);
            intent.putExtra("old", false);
            getContext().startService(intent);
            databaseHelper.deleteSingleSchedule(id);
        }
//        progressNotification(id, message);

        return Result.SUCCESS;
    }

    public static void scheduleExactJob(Message message) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("id", message.getId());
        extras.putString("message", message.getMessage());
        long time = message.getTime() - System.currentTimeMillis();
        new JobRequest.Builder(ScheduleExactJobService.TAG)
                .setExact(time)
                .setExtras(extras)
                .build()
                .schedule();
    }

    public static void scheduleExpiredJob(Message message) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("id", message.getId());
        extras.putString("message", message.getMessage());
        long time = message.getTime() - System.currentTimeMillis();
        new JobRequest.Builder(ScheduleExactJobService.TAG)
                .setExact(time + TimeUnit.MINUTES.toMillis(20))
                .setExtras(extras)
                .build()
                .schedule();
    }

    private void progressNotification(long id, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
        mBuilder.setContentTitle("Bulk SMS " + id)
                .setContentText(message)
                .setSmallIcon(R.drawable.message_white);

        mBuilder.setProgress(100, 0, false);
        notificationManager.notify(11, mBuilder.build());
    }

}