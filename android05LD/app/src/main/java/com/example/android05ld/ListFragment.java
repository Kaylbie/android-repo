package com.example.android05ld;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListFragment extends Fragment {

    private OnItemSelectedListener callback;

    public interface OnItemSelectedListener {
        void onItemSelected(String selectedText);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            callback = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        String[] items = {
                "Tekstas su A raidėmis",
                "Be ieškomo simbolio",
                "Čia yra ieškomas simbolis",
                "Nieko nebus",
                "Rasi ko ieškai",
                "Dar vienas tekstas"
        };

        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent, View view1, int position, long id) -> {
            if (callback != null) {
                callback.onItemSelected(items[position]);
            }
        });

        return view;
    }
}