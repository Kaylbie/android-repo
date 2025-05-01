package com.example.cloudnd;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String BASE_URL_KEY = "base_url";

    private HashMap<Circle, CircleData> circleLocationMap = new HashMap<>();
    private List<CircleData> allLocations = new ArrayList<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            moveCameraToDefaultLocation();
            fetchLocations();

            mMap.setOnCameraIdleListener(() -> loadVisibleCircles());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    private void moveCameraToDefaultLocation() {
        LatLng defaultLocation = new LatLng(54.687157, 25.279652);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 16));
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void fetchLocations() {
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String BASE_URL = sharedPreferences.getString(BASE_URL_KEY, null);

        if (BASE_URL == null || BASE_URL.isEmpty()) {
            Toast.makeText(getContext(), "Base URL is not set!", Toast.LENGTH_LONG).show();
            return;
        }

        String url = BASE_URL + "/api/v1/stops";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        allLocations.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject location = response.getJSONObject(i);
                            double latitude = location.getDouble("stop_lat");
                            double longitude = location.getDouble("stop_lon");
                            String description = location.getString("stop_name");
                            String id = location.getString("stop_id");
                            LatLng position = new LatLng(latitude, longitude);

                            allLocations.add(new CircleData(id, description, position));
                        }

                        loadVisibleCircles();
                        Toast.makeText(getContext(), "Loaded successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }

    private void loadVisibleCircles() {
        if (mMap == null) return;

        mMap.clear();
        circleLocationMap.clear();

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        for (CircleData location : allLocations) {
            if (bounds.contains(location.position)) {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(location.position)
                        .radius(30)
                        .strokeWidth(2f)
                        .strokeColor(Color.RED)
                        .fillColor(0x55FF0000)
                        .clickable(true));

                circleLocationMap.put(circle, location);
            }
        }

        mMap.setOnCircleClickListener(circle -> {
            CircleData data = circleLocationMap.get(circle);
            if (data != null) {
                StopsBottomSheetDialog bottomSheetDialog = StopsBottomSheetDialog.newInstance(data.id, data.name);
                bottomSheetDialog.show(getParentFragmentManager(), "StopsBottomSheetDialog");
            }
        });
    }

}