package com.example.android09ld;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppSettings";
    private static final String BASE_URL_KEY = "base_url";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String baseUrl = sharedPreferences.getString(BASE_URL_KEY, null);

        if (baseUrl == null || baseUrl.isEmpty()) {
            askForBaseUrl();
        } else {
            proceedWithApp();
        }
    }
    private void askForBaseUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Base URL");

        final EditText input = new EditText(this);
        input.setHint("https://your-api.com");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String enteredUrl = input.getText().toString().trim();
            if (!enteredUrl.isEmpty() && enteredUrl.startsWith("http")) {
                sharedPreferences.edit().putString(BASE_URL_KEY, enteredUrl).apply();
                proceedWithApp();
            } else {
                Toast.makeText(this, "Invalid URL. Please enter a valid one.", Toast.LENGTH_SHORT).show();
                askForBaseUrl();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void proceedWithApp() {
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Using API: " + sharedPreferences.getString(BASE_URL_KEY, ""), Toast.LENGTH_LONG).show();
    }
}
