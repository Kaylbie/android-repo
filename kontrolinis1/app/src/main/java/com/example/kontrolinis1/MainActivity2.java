package com.example.kontrolinis1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    private String phoneNumber = "";
    private EditText phoneNumberEditText;
    private Button saveButton;
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        saveButton = findViewById(R.id.saveButton);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneNumberEditText.setText(phoneNumber);

        for (int i = 0; i <= 9; i++) {
            int resID = getResources().getIdentifier("button" + i, "id", getPackageName());
            findViewById(resID).setOnClickListener(v -> {
                phoneNumber += ((Button) v).getText().toString();
                phoneNumberEditText.setText(phoneNumber);
            });
        }
        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("phoneNumber", phoneNumber);
            setResult(RESULT_OK, intent);
            finish();
        });



    }
}