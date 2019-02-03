package com.example.arsojib.bulksms.Service;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleJobCreator implements JobCreator {

    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case ScheduleJobService.TAG:
                return new ScheduleJobService();
            default:
                return null;
        }
    }

}