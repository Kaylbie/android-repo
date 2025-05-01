package com.example.android04ld;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.MotionEvent;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.GestureDetector;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private WebView webView;
    private SharedPreferences preferences;
    private static final String LAST_URL = "last_url";
    private boolean exitWarning = false;
    private Button button;
    private GestureDetector gestureDetector;
    private Handler backPressHandler;
    private Runnable resetExitWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        webView = findViewById(R.id.webView);
        button = findViewById(R.id.button);

        preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String lastUrl = preferences.getString(LAST_URL, null);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                editText.setText("https://www.");
                return true;
            }
        });
        editText.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        if (lastUrl != null) {
            editText.setText(lastUrl);
            loadWebPage(lastUrl);

        }

        button.setOnClickListener(v -> {
            String url = editText.getText().toString();
            loadWebPage(url);
            preferences.edit().putString(LAST_URL, url).apply();
        });

        webView.setWebViewClient(new WebViewClient());


        backPressHandler = new Handler();
        resetExitWarning = () -> exitWarning = false;

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    if (exitWarning) {
                        finish();
                    } else {
                        exitWarning = true;
                        Toast.makeText(MainActivity.this, "Kitą kartą paspaudus ATGAL bus uždarytas naršyklės langas", Toast.LENGTH_SHORT).show();

                        backPressHandler.postDelayed(resetExitWarning, 2000);
                    }
                }
            }
        });
    }
    private void loadWebPage(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        webView.loadUrl(url);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        backPressHandler.removeCallbacks(resetExitWarning);
    }

}

