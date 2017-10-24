package com.indexyear.jd.dispatch.services;

import android.util.Log;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class FirebaseMessagingJobService extends JobService{
    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
