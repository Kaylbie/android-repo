package com.example.android05ld;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_list_container, new ListFragment())
                    .commit();
        }
    }

    @Override
    public void onItemSelected(String selectedText) {
        Fragment fragment;

        if (selectedText.toLowerCase().contains("a")) {
            fragment = FragmentWithA.newInstance(selectedText);
        } else {
            fragment = FragmentWithoutA.newInstance(selectedText);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_details_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}