package com.example.arsojib.bulksms.Service;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Message;

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
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        ArrayList<Message> schedules = databaseHelper.getAllSchedule();
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < schedules.size(); i++) {
            if (currentTime >= schedules.get(i).getTime()) {
                ScheduleExactJobService.scheduleExpiredJob(schedules.get(i));
            }
        }
        return Result.SUCCESS;
    }

    public static void schedulePeriodicJob() {
        JobManager.instance().cancelAllForTag(ScheduleJobService.TAG);
        new JobRequest.Builder(ScheduleJobService.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(4*60), TimeUnit.MINUTES.toMillis(30))
                .build()
                .schedule();
    }

}