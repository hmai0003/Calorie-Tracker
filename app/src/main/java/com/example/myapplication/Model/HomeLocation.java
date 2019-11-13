package com.example.myapplication.Model;


import com.google.android.gms.maps.model.LatLng;

public class HomeLocation {

    protected LatLng homeLatLng;

    public LatLng getHomeLatLng() {
        return homeLatLng;
    }

    public void setHomeLatLng(LatLng homeLatLng) {
        this.homeLatLng = homeLatLng;
    }

}