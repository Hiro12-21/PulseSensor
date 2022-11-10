package com.example.proj1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mypackage.R;

public class MapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // to initialise variable for Map
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //to assign variable for maps
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        //initialise fused location
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        if (supportMapFragment == null) {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.google_map, supportMapFragment).commit();
        }

        this.getCurrentLocation();

    }


    private void getCurrentLocation() {
        //initialise task Location
        Task<Location> task;
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            int[] grantResults = new int[]{ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)};
            ActivityCompat.requestPermissions(getActivity(),
                    permission , 44);
            this.onRequestPermissionsResult(44,permission, grantResults);
        } else {
            task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // when success
                    if (location != null) {
                        //sync map
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                //initialise
                                LatLng latLng = new LatLng(location.getLatitude()
                                        , location.getLongitude());
                                //create market options
                                MarkerOptions options = new MarkerOptions().position(latLng)
                                        .title("I am Here");
                                // zoom map
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                // add marker on map
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (permission.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                //when permission granted
                // call method
                this.getCurrentLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permission, grantResults);
        }
    }}