package com.example.mapsactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    EditText edtsource,edtdest;
    TextView textView;
    String stype;
    double lat1= 0,long1 =0,lat2= 0,long2= 0;
    int flag =0;
    //public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtsource = findViewById(R.id.edtSource);
        edtdest = findViewById(R.id.edtDestination);
        textView = findViewById(R.id.text_View);

        //checkLocationPermission();
        requestLocationPermission();


        //Initialize Places
        Places.initialize(getApplicationContext(),"AIzaSyD1xNi3-i_y0B4bSSEO1PP1xAPUJGPXfbs");

        //
        edtsource.setFocusable(false);
        edtsource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                stype ="source";
                //Intialize Place Field List
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(MainActivity.this);
                //start activity Results
                startActivityForResult(intent,100);
            }
        });

        edtdest.setFocusable(false);
        edtdest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stype = "Destination";
                //Intialize Place Field List
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(MainActivity.this);
                //start activity Results
                startActivityForResult(intent,100);
            }
        });

        textView.setText("0.0 Kilometers");
    }




    /*public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("R.string.ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                       // locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check the Condition
        if(requestCode==100 && resultCode== RESULT_OK)
        {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //check type condition
            if(stype.equals("source"))
            {
                //increase the value of flag
                flag++;
                edtsource.setText(place.getAddress());
                //get Longitude and Lattitude
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng:","");
                sSource = sSource.replace("(","");
                sSource =sSource.replace(")","");
                String[] split = sSource.split(",");
                lat1= Double.parseDouble(split[0]);
                long1 =Double.parseDouble(split[1]);

            }
            else {
                //when type is destination
                flag++;
                edtdest.setText(place.getAddress());
                String sDestination = String.valueOf(place.getLatLng());
                sDestination = sDestination.replaceAll("lat/lng:","");
                sDestination = sDestination.replace("(","");
                sDestination =sDestination.replace(")","");
                String[] split = sDestination.split(",");
                lat2= Double.parseDouble(split[0]);
                long2 =Double.parseDouble(split[1]);
            }
            if(flag>=2)
            {
                //Calculate Distance
                distance(lat1,long1,lat2,long2);
            }
        }
        else  if (requestCode== AutocompleteActivity.RESULT_ERROR)
        {
            //when error
            //Initialize Status
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void distance(double lat1, double long1, double lat2, double long2)
    {
        //calclulate longitude difference
        double longDiff = long1-long2;
        //
        double distance = Math.sin(degrad(lat1))
                *Math.sin(degrad(lat2))
                +Math.cos(degrad(lat1))
                *Math.cos(degrad(lat2))
                *Math.cos(degrad(longDiff));
        distance =Math.acos(distance);
        //conver distance to radian to degree
        distance = rad2deg(distance);
        //Distance in miles
        distance =distance*60*1.1515;
        //distance in km
        distance = distance *1.609344;

        //set value to the textview
        textView.setText(String.format(Locale.US,"%2f Kilometers",distance));



    }

    private double rad2deg(double distance)
    {
            return(distance*180.0/Math.PI);
    }

    //Convert Degree to Radition
    private double degrad(double lat1) {
        return (lat1*Math.PI/180.0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

}