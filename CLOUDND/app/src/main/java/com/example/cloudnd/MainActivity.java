package com.example.cloudnd;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppSettings";
    private static final String BASE_URL_KEY = "base_url";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set layout first

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String baseUrl = sharedPreferences.getString(BASE_URL_KEY, null);

        askForBaseUrl();
    }

    private void askForBaseUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Base URL");

        final EditText input = new EditText(this);
        input.setHint("https://api.com");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String enteredUrl = input.getText().toString().trim();
            if (!enteredUrl.isEmpty() && enteredUrl.startsWith("http")) {
                sharedPreferences.edit().putString(BASE_URL_KEY, enteredUrl).apply();
                loadMapsFragment();
            } else {
                Toast.makeText(this, "Invalid URL. Please enter a valid one.", Toast.LENGTH_SHORT).show();
                askForBaseUrl();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void loadMapsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment mapsFragment = new MapsFragment();
        transaction.replace(R.id.fragmentContainer, mapsFragment);
        transaction.commit();
    }
}