package com.example.counter;

import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import java.io.Serializable;
public class ItemViewModel extends ViewModel implements Serializable {
    public SharedPreferences sharedPreferences;
    public CounterNotification counterNotification;
}
