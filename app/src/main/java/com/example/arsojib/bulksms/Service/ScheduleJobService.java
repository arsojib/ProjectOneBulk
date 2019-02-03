package com.example.arsojib.bulksms.Service;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Message;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleJobService extends Job {

    public static final String TAG = "job_demo_tag";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        ArrayList<Message> schedules = databaseHelper.getAllSchedule();

        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < schedules.size(); i++) {
            if (currentTime >= schedules.get(i).getTime()) {

            }
        }

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(ScheduleJobService.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }
}