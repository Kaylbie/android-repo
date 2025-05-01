package com.example.android03LD;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private AutoCompleteTextView departmentAutoComplete;
    private RatingBar ratingBar;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private Spinner citySpinner;
    private Switch registerSwitch;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nameEditText = findViewById(R.id.nameEditText);
        departmentAutoComplete = findViewById(R.id.departmentAutoComplete);
        ratingBar = findViewById(R.id.ratingBar);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        datePicker = findViewById(R.id.datePicker);
        citySpinner = findViewById(R.id.citySpinner);
        registerSwitch = findViewById(R.id.registerSwitch);
        saveButton = findViewById(R.id.saveButton);

        String[] departments = getResources().getStringArray(R.array.departments);
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departments);
        departmentAutoComplete.setAdapter(departmentAdapter);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);


        saveButton.setOnClickListener(v -> showDataSummarySnackbar());

        departmentAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    String str = (String) parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), "Pasirinkta: "+str,   Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }

            }
        });

    }

    private void showDataSummarySnackbar() {
        String name = nameEditText.getText().toString();
        String department = departmentAutoComplete.getText().toString();
        int rating = ratingBar.getProgress();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String city = citySpinner.getSelectedItem().toString();
        boolean isRegistered = registerSwitch.isChecked();

        if(isRegistered){
            String summary = "Pavadinimas: " + name + "; Sudėtingumas: " + rating +
                    "; Laikas: " + String.format("%02d:%02d", hour, minute) +
                    ", data: " + year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) +
                    "; Padalinys: " + department + "; Miestas: " + city;

            Snackbar snackbar = Snackbar.make(findViewById(R.id.saveButton), summary, 6000);
            snackbar.setAction("Patvirtinu, informacija teisinga", view -> saveDataToInternalStorage(name, summary));


            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event != DISMISS_EVENT_ACTION) {
                        Toast.makeText(MainActivity.this, "Laikas baigėsi, bandykite dar kartą išsaugoti duomenis", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            snackbar.show();
        }

    }


    private void saveDataToInternalStorage(String name, String data) {
        String fileName = name.replace(" ", "") + ".txt";

        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes());
            Toast.makeText(this, "Duomenys įrašyti sėkmingai:\n" + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Klaida įrašant duomenis", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}