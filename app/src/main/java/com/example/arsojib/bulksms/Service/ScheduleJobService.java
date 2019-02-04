package com.example.arsojib.bulksms.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.min;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleJobService extends Job {

    public static final String TAG = "schedule_job_service";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
//        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
//        ArrayList<Message> schedules = databaseHelper.getAllSchedule();
//        long currentTime = System.currentTimeMillis();
//        for (int i = 0; i < schedules.size(); i++) {
//            if (currentTime >= schedules.get(i).getTime()) {
//                ScheduleExactJobService.scheduleExpiredJob(schedules.get(i));
//            }
//        }
        progressNotification();
        return Result.SUCCESS;
    }

    public static void schedulePeriodicJob() {
        JobManager.instance().cancelAllForTag(ScheduleJobService.TAG);
        new JobRequest.Builder(ScheduleJobService.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .build()
                .schedule();
    }

    private void progressNotification() {
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
        mBuilder.setContentTitle("Bulk SMS")
                .setContentText("Send in periodic time...")
                .setSmallIcon(R.drawable.message_white);

        mBuilder.setProgress(100, 0, false);
        notificationManager.notify(11, mBuilder.build());
    }

}