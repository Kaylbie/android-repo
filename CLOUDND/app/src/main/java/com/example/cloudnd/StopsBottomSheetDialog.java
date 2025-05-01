package com.example.cloudnd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.FragmentTransaction;

public class StopsBottomSheetDialog extends BottomSheetDialogFragment {

    private String stopId;
    private String stopName;
    private TextView stopNameTextView;
    private ProgressBar progressBar;

    public static StopsBottomSheetDialog newInstance(String stopId, String stopName) {
        StopsBottomSheetDialog fragment = new StopsBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("stop_id", stopId);
        args.putString("stop_name", stopName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stopId = getArguments().getString("stop_id");
            stopName = getArguments().getString("stop_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        stopNameTextView = view.findViewById(R.id.stopNameTextView);
        stopNameTextView.setText("Stotelė: "+stopName);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        view.post(() -> {
            ItemFragment itemFragment = ItemFragment.newInstance(stopId);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.listOfStopsFragment, itemFragment);
            transaction.commit();
        });

        return view;
    }
    public void hideLoadingIndicator() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}

