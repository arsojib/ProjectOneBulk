package com.example.arsojib.bulksms.Utils;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.evernote.android.job.JobManager;
import com.example.arsojib.bulksms.Service.ScheduleJobCreator;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        JobManager.create(this).addJobCreator(new ScheduleJobCreator());
    }
}
