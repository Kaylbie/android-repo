package com.example.cloudnd;

import com.google.android.gms.maps.model.LatLng;

class CircleData {
    String id;
    String name;
    LatLng position; // Store stop coordinates

    public CircleData(String id, String name, LatLng position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
}
