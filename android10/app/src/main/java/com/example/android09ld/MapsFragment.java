package com.example.android09ld;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private EditText inputLatitude, inputLongitude, inputAddress;
    private Button addByCoordinatesButton, addByAddressButton, fetchLocationsButton;

    private static final String BASE_URL = "http://10.0.2.2/locations/";

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            LatLng defaultLocation = new LatLng(0, 0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2));

            fetchLocations();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        inputLatitude = view.findViewById(R.id.inputLatitude);
//        inputLongitude = view.findViewById(R.id.inputLongitude);
//        inputAddress = view.findViewById(R.id.inputAddress);
//        addByCoordinatesButton = view.findViewById(R.id.addByCoordinatesButton);
//        addByAddressButton = view.findViewById(R.id.addByAddressButton);
//        fetchLocationsButton = view.findViewById(R.id.fetchLocationsButton);

        addByCoordinatesButton.setOnClickListener(v -> addLocationByCoordinates());
        addByAddressButton.setOnClickListener(v -> addLocationByAddress());
        fetchLocationsButton.setOnClickListener(v -> fetchLocations());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void addLocationByCoordinates() {
        String latitudeStr = inputLatitude.getText().toString();
        String longitudeStr = inputLongitude.getText().toString();

        if (latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(getContext(), "latitude and longitude not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude, longitude;
        try {
            latitude = Double.parseDouble(latitudeStr);
            longitude = Double.parseDouble(longitudeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid coordinates", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng position = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(position).title("Custom Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));

        sendLocationToServer(latitude, longitude, "Custom Location");
    }

    private void addLocationByAddress() {
        String addressInput = inputAddress.getText().toString();
        if (addressInput.isEmpty()) {
            Toast.makeText(getContext(), "adreso ivedimas", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressInput, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                LatLng position = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(position).title(addressInput));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));

                sendLocationToServer(latitude, longitude, addressInput);
            } else {
                Toast.makeText(getContext(), "adresas nerastas", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLocationToServer(double latitude, double longitude, String description) {
        String url = BASE_URL + "add_location.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(getContext(), "prideta sekmingai", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getContext(), "klaida: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("description", description);
                return params;
            }
        };
        queue.add(request);
    }

    private void fetchLocations() {
        mMap.clear();

        String url = BASE_URL + "get_locations.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject location = response.getJSONObject(i);
                            double latitude = location.getDouble("latitude");
                            double longitude = location.getDouble("longitude");
                            String description = location.getString("description");

                            LatLng position = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(position).title(description));
                        }

                        Toast.makeText(getContext(), "vietos uzkrautos sekmingai", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "klaida", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }
}
