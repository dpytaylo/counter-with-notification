package com.example.counter;

import android.content.Context;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.Serializable;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CounterNotification implements Serializable {
    private static final String WORKER_TAG = "CounterNotificationTag";
    private final Context context;

    public CounterNotification(Context context) {
        this.context = context;
    }

    public void enableNotification(int hours, int minutes) {
        var now = new Date();
        var firstNotification = Calendar.getInstance();
        firstNotification.setTimeInMillis(System.currentTimeMillis());
        firstNotification.set(Calendar.HOUR_OF_DAY, hours);
        firstNotification.set(Calendar.MINUTE, minutes);
        firstNotification.set(Calendar.SECOND, 0);

        var initialDelay = Duration.between(now.toInstant(), firstNotification.toInstant());
        if (initialDelay.isNegative()) {
            initialDelay = initialDelay.plus(Duration.ofDays(1));
        }

        var workManager = WorkManager.getInstance(context);
        List<WorkInfo> info;
        try {
            info = workManager.getWorkInfosByTag(WORKER_TAG).get();

            for (var element : info) {
                workManager.cancelWorkById(element.getId());
            }
        } catch (Exception _e) {
            // Do nothing
        }

        var request = new PeriodicWorkRequest.Builder(NotificationReceiver.class, Duration.ofDays(1))
            .addTag(WORKER_TAG)
            .setInitialDelay(initialDelay)
            .build();

        workManager.enqueue(request);
    }

    public void disableNotification() {
        var workManager = WorkManager.getInstance(context);

        try {
            var info = workManager.getWorkInfosByTag(WORKER_TAG).get();
            for (var element : info) {
                workManager.cancelWorkById(element.getId());
            }
        } catch (Exception _e) {
            // Do nothing
        }
    }
}
