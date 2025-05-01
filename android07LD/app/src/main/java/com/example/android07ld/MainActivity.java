package com.example.android07ld;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;



public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "battery_channel";
    private boolean isTrackingEnabled = false;
    private BatteryLevelReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryReceiver = new BatteryLevelReceiver();
        SwitchCompat trackingSwitch = findViewById(R.id.switchCompat);

        isTrackingEnabled = getIntent().getBooleanExtra("tracking_enabled", false);
        trackingSwitch.setChecked(isTrackingEnabled);


        trackingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isTrackingEnabled = isChecked;
            if (isTrackingEnabled) {
                registerBatteryLevelReceiver();
            } else {
                unregisterBatteryLevelReceiver();
            }
        });

        createNotificationChannel();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (isTrackingEnabled) {
            registerBatteryLevelReceiver();
        }

    }

    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }


    private void unregisterBatteryLevelReceiver() {
        try {
            unregisterReceiver(batteryReceiver);
        } catch (IllegalArgumentException e) {
            Log.e("MainActivity", "not registered", e);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void createNotificationChannel() {
        CharSequence name = "Battery notifications";
        String description = "low battery level";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (isTrackingEnabled) {
//            unregisterBatteryLevelReceiver();
//        }
//    }

    public static class BatteryLevelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level * 100 / (float) scale;

            if (batteryPct <= 50) {
                showLowBatteryNotification(context, (int) batteryPct);
            }
        }

        private void showLowBatteryNotification(Context context, int batteryPct) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("tracking_enabled", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Senka akumuliatorius")
                    .setContentText("Įkrovimo lygis pasiekė: " + batteryPct + "%")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        isTrackingEnabled = intent.getBooleanExtra("tracking_enabled", isTrackingEnabled);
        Switch trackingSwitch = findViewById(R.id.switchCompat);
        trackingSwitch.setChecked(isTrackingEnabled);
    }
}
