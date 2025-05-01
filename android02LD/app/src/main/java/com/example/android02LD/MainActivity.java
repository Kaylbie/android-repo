package com.example.android02LD;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button firstButton, secondButton, thirdButton;
    private TextView textView;
    private String enteredText;
    private String wordCountResult;

    private final ActivityResultLauncher<Intent> enterTextLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    enteredText = result.getData().getStringExtra("result_text");
                    textView.setText(enteredText);
                }
            });

    private final ActivityResultLauncher<Intent> countWordsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    wordCountResult = result.getData().getStringExtra("word_count_text");
                    textView.setText(wordCountResult);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstButton = findViewById(R.id.button1);
        secondButton = findViewById(R.id.button2);
        thirdButton = findViewById(R.id.button3);
        textView = findViewById(R.id.textView1);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
            String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            if (receivedText != null) {
                enteredText = receivedText;
                textView.setText(enteredText);
            }
        }

        firstButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EnterTextActivity.class);
            intent.putExtra("initial_text", enteredText);
            enterTextLauncher.launch(intent);
        });

        secondButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CountWordsActivity.class);
            intent.putExtra("text_to_count", enteredText);
            countWordsLauncher.launch(intent);
        });


        thirdButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String textToShare = wordCountResult != null ? wordCountResult : enteredText;
            sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            sendIntent.setType("text/plain");


            startActivity(Intent.createChooser(sendIntent, null));
        });
    }
}
