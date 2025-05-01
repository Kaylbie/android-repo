package com.example.kontrolinis1;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button selectButton;
    private Button numberButton;
    private Button smsButton;
    private ListView listView;
    private Spinner spinner;
    private ArrayList<String> productList = new ArrayList<>();
    private ArrayAdapter<String> listViewAdapter;
    private String phoneNumber = "";

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

        spinner = findViewById(R.id.spinner);
        listView = findViewById(R.id.listView);
        selectButton = findViewById(R.id.selectButton);
        numberButton = findViewById(R.id.numberButton);
        smsButton = findViewById(R.id.smsButton);


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.products, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listView.setAdapter(listViewAdapter);


        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);

        selectButton.setOnClickListener(v -> {
            String selectedProduct = spinner.getSelectedItem().toString();
            productList.add(selectedProduct);
            listViewAdapter.notifyDataSetChanged();
            Toast.makeText(this, "pridėtas ", Toast.LENGTH_SHORT).show();
        });

        numberButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivityForResult(intent, 1);
        });

        smsButton.setOnClickListener(v -> {
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "neivestas numeris", Toast.LENGTH_SHORT).show();
            } else {
                String message = "produktai: " + String.join(", ", productList);
                smsIntent.setData(android.net.Uri.parse("smsto:" + phoneNumber));
                smsIntent.putExtra("sms_body", message);
                startActivity(smsIntent);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            phoneNumber = data.getStringExtra("phoneNumber");
            Toast.makeText(this, "numeris atnaujintas: " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

}