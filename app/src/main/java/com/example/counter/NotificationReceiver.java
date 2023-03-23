package com.example.counter;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationReceiver extends Worker {
    private static final String CHANNEL_ID = "0";
    private static final int NOTIFICATION_ID = 1;

    public NotificationReceiver(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        var context = getApplicationContext();

        var activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        var tasks = activityManager.getAppTasks();

        // The reason why we use get(0) is because getAppTasks() returns a list of ActivityManager.AppTask objects
        // in descending order of time, which means that the first object in the list represents the app that was used most recently.
        // Since we are interested in checking if our app is in the foreground, we only need to look
        // at the most recent usage statistics object, which is why we use get(0) to get the first object in the list.
        if (!tasks.isEmpty() && tasks.get(0).getTaskInfo().topActivity.getPackageName().equals(context.getPackageName())) {
            // App is in the foreground
            ContextCompat.getMainExecutor(context).execute(() -> {
                Toast.makeText(context, "Timeout!", Toast.LENGTH_SHORT).show();
            });
        } else {
            // App is minimized
            var builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Time to update the counter!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            var channel = new NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(NOTIFICATION_ID, notification);
        }

        return Result.success();
    }
}

