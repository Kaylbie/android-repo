package com.example.android05ld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentWithA extends Fragment {

    private static final String ARG_TEXT = "text";

    public static FragmentWithA newInstance(String text) {
        FragmentWithA fragment = new FragmentWithA();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_with_a, container, false);

        String text = getArguments().getString(ARG_TEXT);
        int countA = countA(text);

        TextView textView = view.findViewById(R.id.textViewA);
        textView.setText("Šiame tekste yra " + countA + " simboliai A(a).");

        return view;
    }

    private int countA(String text) {
        int count = 0;
        for (char c : text.toLowerCase().toCharArray()) {
            if (c == 'a') count++;
        }
        return count;
    }
}