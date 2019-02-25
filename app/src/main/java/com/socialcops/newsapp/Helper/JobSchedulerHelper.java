package com.socialcops.newsapp.Helper;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.socialcops.newsapp.Activity.MainActivity;
import com.socialcops.newsapp.Services.NotificationService;

import java.util.concurrent.TimeUnit;

public class JobSchedulerHelper {

    private Context context;
    private final String JOB_TAG = "news_notification_tag";
    private FirebaseJobDispatcher dispatcher;

    final int periodicity = (int)TimeUnit.HOURS.toSeconds(3); // Every 3 hour periodicity expressed as seconds
    final int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(4);

    public JobSchedulerHelper(Context context) {
        this.context = context;
    }

    public void createJobDispatcher() {
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NotificationService.class)
                // uniquely identifies the job
                .setTag(JOB_TAG)
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(periodicity, toleranceInterval))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run when the device is charging
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        dispatcher.mustSchedule(myJob);
    }

    public void cancelAllJobs() {
        dispatcher.cancel(JOB_TAG);
    }
}
