package com.example.arsojib.bulksms.Service;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Message;

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
        ArrayList<Contact> contacts = new ArrayList<>();
        if (id != 0 && !message.equals("")) {
            contacts.addAll(databaseHelper.getAllScheduleNumberUsingScheduleID(id));
            Intent intent = new Intent(getContext(), SmsManagementService.class);
            intent.putExtra("contact_list", contacts);
            intent.putExtra("message", message);
            intent.putExtra("sim", "0");
            intent.putExtra("old", false);
            getContext().startService(intent);
            databaseHelper.deleteSingleSchedule(id);
        }

        return Result.SUCCESS;
    }

    public static void scheduleExactJob(Message message) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putLong("id", message.getId());
        extras.putString("message", message.getMessage());
        long time = System.currentTimeMillis() - message.getTime();
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
        long time = System.currentTimeMillis() - message.getTime();
        new JobRequest.Builder(ScheduleExactJobService.TAG)
                .setExact(time + TimeUnit.MINUTES.toMillis(20))
                .setExtras(extras)
                .build()
                .schedule();
    }

}