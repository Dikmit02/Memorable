package com.example.diksh.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    LocationManager loactionmanager;
    LocationListener locationListener;


    private GoogleMap mMap;


    public void centremaponlocation(Location location,String title){

        LatLng userlocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        if(title!="your location") {
            mMap.addMarker(new MarkerOptions().position(userlocation).title(title));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,2));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                loactionmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = loactionmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null) {

                    centremaponlocation(lastKnownLocation, "Your location");
                }

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent=getIntent();
        mMap.setOnMapLongClickListener(this);
        if(intent.getIntExtra("placenumber",0)==0) {
            loactionmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centremaponlocation(location, "your location");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (Build.VERSION.SDK_INT < 23) {


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                loactionmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    loactionmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 12000, 0, locationListener);
                    Location lastknownlocation = loactionmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centremaponlocation(lastknownlocation, "your location");


                }

            }

        }else{
            //mMap.clear();
            Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.location.get(intent.getIntExtra("placenumber",0)).latitude);
            placeLocation.setLongitude(MainActivity.location.get(intent.getIntExtra("placenumber",0)).longitude);
            centremaponlocation(placeLocation,MainActivity.memorablelist.get(intent.getIntExtra("placenumber",0)));

        }


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
          String ans="Could Not Find Address";

        Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
        try {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList!=null&&addressList.size()>0){
                ans="";
                Log.i("placeinfo",addressList.get(0).toString());
                if(addressList.get(0).getAddressLine(0)!=null){


                    ans+=addressList.get(0).getAddressLine(0);
                    mMap.addMarker(new MarkerOptions().position(latLng).title(ans));

                    Log.i("Dikshaaa",ans);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainActivity.memorablelist.add(ans);
        MainActivity.location.add(latLng);
        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.diksh.memorableplaces",Context.MODE_PRIVATE);
       try {
           ArrayList<String> latitudes = new ArrayList<>();
           ArrayList<String> longitudes = new ArrayList<>();
           for (LatLng coordinates : MainActivity.location) {
               latitudes.add(Double.toString(coordinates.latitude));
               longitudes.add(Double.toString(coordinates.longitude));

           }
           sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.memorablelist)).apply();
           sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitudes)).apply();
           sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longitudes)).apply();
       }
       catch(IOException e){
           e.printStackTrace();

        }




    }
}
