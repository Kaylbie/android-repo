package com.example.kontrolinis2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String MD5_URL = "http://md5.jsontest.com/?text=example_text1";
    private static final String PREFS_NAME = "MD5Prefs";
    private static final String MD5_KEY = "md5";
    private static final String CHANNEL_ID = "MD5_CHANGE_CHANNEL";

    private TextView md5TextView;
    private Button updateButton;

    private String savedMd5;

    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        md5TextView = findViewById(R.id.textView);
        updateButton = findViewById(R.id.button);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        savedMd5 = preferences.getString(MD5_KEY, null);
        //savedMd5="asd";
        //updateButton.setVisibility(View.GONE);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMd5(md5TextView.getText().toString());
                updateButton.setVisibility(View.GONE);
            }
        });

        mainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
        fetchMd5InThread();
    }

    private void fetchMd5InThread() {
        new Thread(() -> {
            try {
                URL url = new URL(MD5_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                JSONObject jsonObject = new JSONObject(response.toString());
                handleJsonResponse(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleJsonResponse(JSONObject jsonObject) {
        mainHandler.post(() -> {
            try {
                String currentMd5 = jsonObject.getString("md5");

                md5TextView.setText(currentMd5);

                if (savedMd5 == null || !savedMd5.equals(currentMd5)) {
                    updateButton.setVisibility(View.VISIBLE);
                    sendNotification("MD5 pasikeitė");
                } else {
                    updateButton.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveMd5(String newMd5) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MD5_KEY, newMd5);
        editor.apply();
        savedMd5 = newMd5;
    }

    private void sendNotification(String title) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText("Pasikeite MD")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
    private void createNotificationChannel() {

        CharSequence name = "MD5 pakeitimas";
        String description = "Pranešimas apie MD5 reikšmės pasikeitimą";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}