package com.example.android06ld;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private TextView timeDifferenceTextView;
    private TextView symbolCountTextView;
    private TextView resultTextView;
    private Handler handler;
    private Runnable symbolRunnable;
    private int symbolIndex;
    private String selectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timeDifferenceTextView = findViewById(R.id.timeDifferenceTextView);
        symbolCountTextView = findViewById(R.id.symbolCountTextView);
        resultTextView = findViewById(R.id.resultTextView);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        registerForContextMenu(timeDifferenceTextView);
        registerForContextMenu(symbolCountTextView);
        handler = new Handler();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.timeDifferenceTextView || v.getId() == R.id.symbolCountTextView) {
            menu.add(0, v.getId(), 0, "Simbolių skaičius šiame tekste");
            menu.add(0, v.getId(), 1, "Simbolių vardijimas po vieną");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Simbolių skaičius šiame tekste")) {
            showCharacterCountDialog((TextView )findViewById(item.getItemId()));
        } else if (item.getTitle().equals("Simbolių vardijimas po vieną")) {
            startSymbolDisplay((TextView) findViewById(item.getItemId()));
        }
        return true;
    }
    private void showCharacterCountDialog(TextView textView) {
        String text = textView.getText().toString();
        countAllSymbols(text);
    }
    private void startSymbolDisplay(TextView textView) {
        selectedText = textView.getText().toString();
        symbolIndex = 0;

        symbolRunnable = new Runnable() {
            @Override
            public void run() {
                if (symbolIndex < selectedText.length()) {
                    resultTextView.setText(String.valueOf(selectedText.charAt(symbolIndex) + "                    fj    riu         r              ru       ru  "));
                    symbolIndex++;
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(symbolRunnable);

    }
    private void countAllSymbols(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            count++;
        }
        String result = "Tekste yra " + count+" simboliu";
        symbolCountTextView.setText(result);
        showDialog("Simbolių skaičius",result);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.finishWork) {
            finish();
            return true;
        } else if (id == R.id.timeDifference) {
            showTimeDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimeDialog(){

        LocalTime currentTime = LocalTime.now();
        int hour = currentTime.getHour();
        int minute = currentTime.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calculateTimeDifference(hourOfDay, minute);
                    }
                }, hour, minute, true // 24
        );
        timePickerDialog.show();
    }
    private void calculateTimeDifference(int hour, int minute) {
        LocalTime currentTime = LocalTime.now();

        LocalTime selectedTime = LocalTime.of(hour, minute);

        long difference = ChronoUnit.MINUTES.between(currentTime, selectedTime);

        String result = "Skirtumas tarp dabar ir nurodyto laiko yra " + Math.abs(difference) + " minutės";

        timeDifferenceTextView.setText(result);
        showDialog("Skirtumas minutėmis",result);
    }
    private void showDialog(String title, String body){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("Ok", null);
        builder.show();
    }
}