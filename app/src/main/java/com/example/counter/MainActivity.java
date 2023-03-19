package com.example.counter;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private ItemViewModel itemViewModel;

    public static final String COUNTER_RES = "counter";
    public static final String NOTIFICATION_TIME_BOOLEAN_RES = "notification_time_boolean";
    public static final String NOTIFICATION_TIME_RES = "notification_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        var sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        var editor = sharedPreferences.edit();

        if (!sharedPreferences.contains(COUNTER_RES)) {
            editor.putInt(COUNTER_RES, 0);
        }
        if (!sharedPreferences.contains(NOTIFICATION_TIME_BOOLEAN_RES)) {
            editor.putBoolean(NOTIFICATION_TIME_BOOLEAN_RES, false);
        }
        if (!sharedPreferences.contains(NOTIFICATION_TIME_RES)) {
            editor.putString(NOTIFICATION_TIME_RES, "00:00");
        }

        editor.apply();

        var counterNotification = new CounterNotification(getApplicationContext());
        if (sharedPreferences.getBoolean(NOTIFICATION_TIME_BOOLEAN_RES, false)) {
            var time = sharedPreferences.getString(NOTIFICATION_TIME_RES, "00:00");
            if (isValidTime(time)) {
                int hours = Integer.parseInt(time.substring(0, 2));
                int minutes = Integer.parseInt(time.substring(3, 5));

                counterNotification.enableNotification(hours, minutes);
            } else {
                editor.putString(NOTIFICATION_TIME_RES, "00:00");
            }
        }

        itemViewModel.sharedPreferences = sharedPreferences;
        itemViewModel.counterNotification = counterNotification;
    }

    public static boolean isValidTime(String time) {
        // We are using this "not-very-smart" construction because
        // Android SimpleDateFormat can parse this "00:123" like a valid date

        if (time.length() != 5) {
            return false;
        }

        if (time.charAt(2) != ':') {
            return false;
        }

        var hours = Integer.parseInt(time.substring(0, 2));
        var minutes = Integer.parseInt(time.substring(3, 5));

        return hours <= 23 && minutes <= 59;
    }
}