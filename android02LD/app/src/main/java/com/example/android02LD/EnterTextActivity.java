package com.example.android02LD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EnterTextActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        editText = findViewById(R.id.editText1);
        button = findViewById(R.id.button4);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("initial_text")) {
            String initialText = intent.getStringExtra("initial_text");
            editText.setText(initialText);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultString = editText.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result_text", resultString);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        String resultText = editText.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result_text", resultText);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
