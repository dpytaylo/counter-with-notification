package com.example.counter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private CounterNotification counterNotification;
    private boolean notificationTimeBoolean;
    private String notificationTime = "00:00";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemViewModel itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        sharedPreferences = itemViewModel.sharedPreferences;
        counterNotification = itemViewModel.counterNotification;

        loadSettings();

        EditText editTime = view.findViewById(R.id.editTextTime);
        editTime.setText(notificationTime);

        SwitchCompat enableNotification = view.findViewById(R.id.enable_notification);
        enableNotification.setChecked(notificationTimeBoolean);

        enableNotification.setOnClickListener(view2 -> {
            if (enableNotification.isChecked()) {
                enableNotification();
            } else {
                disableNotification();
            }
        });

        Button editTimeButton = view.findViewById(R.id.edit_text_time_button);
        editTimeButton.setOnClickListener(view2 -> {
            if (!editTime.isEnabled()) {
                editTime.setEnabled(true);
                editTimeButton.setText(R.string.save);
            } else {
                var newTime = editTime.getText().toString().strip();

                if (!MainActivity.isValidTime(newTime)) {
                    Toast.makeText(
                        getContext(),
                        "Invalid time value! Please, use 'HH:mm' format (HH: 00-23, mm: 00-59)",
                        Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                enableNotification(notificationTimeBoolean, newTime);

                editTime.setEnabled(false);
                editTimeButton.setText(R.string.edit_text_time_button);
            }
        });

        Button returnButton = view.findViewById(R.id.return_button);
        returnButton.setOnClickListener(view2 -> NavHostFragment.findNavController(SettingsFragment.this)
            .navigate(R.id.action_settingsFragment_to_mainFragment));
    }

    private void loadSettings() {
        notificationTimeBoolean = sharedPreferences.getBoolean(MainActivity.NOTIFICATION_TIME_BOOLEAN_RES, false);
        notificationTime = sharedPreferences.getString(MainActivity.NOTIFICATION_TIME_RES, "00:00");
    }

    private void enableNotification() {
        notificationTimeBoolean = true;

        var editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.NOTIFICATION_TIME_BOOLEAN_RES, notificationTimeBoolean);
        editor.apply();

        var hours = Integer.parseInt(notificationTime.substring(0, 2));
        var minutes = Integer.parseInt(notificationTime.substring(3, 5));

        counterNotification.enableNotification(hours, minutes);
    }

    private void enableNotification(boolean notificationTimeBoolean, String notificationTime) {
        this.notificationTimeBoolean = notificationTimeBoolean;
        this.notificationTime = notificationTime;

        var editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.NOTIFICATION_TIME_BOOLEAN_RES, notificationTimeBoolean);
        editor.putString(MainActivity.NOTIFICATION_TIME_RES, notificationTime);
        editor.apply();

        if (!notificationTimeBoolean) {
            return;
        }

        var hours = Integer.parseInt(notificationTime.substring(0, 2));
        var minutes = Integer.parseInt(notificationTime.substring(3, 5));

        counterNotification.enableNotification(hours, minutes);
    }

    private void disableNotification() {
        notificationTimeBoolean = false;

        var editor = sharedPreferences.edit();
        editor.putBoolean("notification-time-boolean", false);
        editor.apply();

        counterNotification.disableNotification();
    }
}