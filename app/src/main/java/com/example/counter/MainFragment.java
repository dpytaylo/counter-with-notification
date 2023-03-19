package com.example.counter;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class MainFragment extends Fragment {
    private SharedPreferences.Editor editor;

    private int counter = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemViewModel itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        var sharedPreferences = itemViewModel.sharedPreferences;
        counter = sharedPreferences.getInt("counter", 0);
        editor = sharedPreferences.edit();

        setupUi(view);
    }

    private void setupUi(View view) {
        String valueFormat = getString(R.string.value_format);

        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(String.format(valueFormat, counter));

        Button add = view.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                counter += 1;
                textView.setText(String.format(valueFormat, counter));

                editor.putInt("counter", counter);
                editor.apply();
            }
        });

        Button clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                counter = 0;
                textView.setText(String.format(valueFormat, counter));

                editor.putInt("counter", counter);
                editor.apply();
            }
        });

        Button settings = view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainFragment.this)
                    .navigate(R.id.action_mainFragment_to_settingsFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editor = null;
    }
}