package com.example.android05ld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentWithoutA extends Fragment {

    private static final String ARG_TEXT = "text";

    public static FragmentWithoutA newInstance(String text) {
        FragmentWithoutA fragment = new FragmentWithoutA();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_without_a, container, false);

        String text = getArguments().getString(ARG_TEXT);
        int length = text.length();
        int vowels = countVowels(text);
        int uppercase = countUppercase(text);
        int lowercase = countLowercase(text);

        TextView textView = view.findViewById(R.id.textViewWithoutA);
        textView.setText("Ilgis: " + length + ", Balsiai: " + vowels + ", Didžiosios: " + uppercase + ", Mažosios: " + lowercase);

        return view;
    }

    private int countVowels(String text) {
        int count = 0;
        for (char c : text.toLowerCase().toCharArray()) {
            if ("aeiouąėį".contains(String.valueOf(c))) count++;
        }
        return count;
    }

    private int countUppercase(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) count++;
        }
        return count;
    }

    private int countLowercase(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLowerCase(c)) count++;
        }
        return count;
    }
}
