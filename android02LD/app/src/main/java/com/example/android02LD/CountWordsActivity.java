package com.example.android02LD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CountWordsActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;
    private String textToCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);

        button = findViewById(R.id.button22);
        textView = findViewById(R.id.editText22);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("text_to_count")) {
            String textToCount = intent.getStringExtra("text_to_count");
            textView.setText(textToCount);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resultString = textView.getText().toString();
                int wordInt = resultString.trim().split("\\s+").length;
                String wordCountText = "Sakinyje '" + resultString + "' yra " + wordInt + " žodžiai";

                Intent resultIntent = new Intent();
                resultIntent.putExtra("word_count_text", wordCountText);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        int wordCount = textToCount != null ? textToCount.trim().split("\\s+").length : 0;
        String wordCountText = "Sakinyje '" + textToCount + "' yra " + wordCount + " žodžiai";
        Intent resultIntent = new Intent();
        resultIntent.putExtra("word_count_text", wordCountText);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
