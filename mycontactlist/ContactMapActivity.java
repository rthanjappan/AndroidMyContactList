package com.example.mycontactlist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import android.location.Location;
//import com.google.android.gms.location.location;
//import com.google.android.gms.location.LocationRequest;

public class ContactMapActivity
        extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener
        {

    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    TextView textDirection;
    TextView textProximity;
    private Sensor proximitySensor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);

        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity.this);
            ds.open();
            if(extras !=null){
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));
                Toast.makeText(this, "CurrentContact retrieved successfully!!!" +
                                " ID: "+
                                currentContact.getContactID()+
                                " Name : " +currentContact.getContactName(),
                        Toast.LENGTH_LONG).show();
            }
            else {
                contacts = ds.getContacts("contactname", "ASC");
                Toast.makeText(this, "Contact(s) retrieved successfully!!! Number of Contacts"+
                                contacts.size(),
                        Toast.LENGTH_LONG).show();
            }
            ds.close();

        }
        catch (Exception e) {
            Toast.makeText(this, "Contact(s) could not be retrieved.",
                    Toast.LENGTH_LONG).show();

        }

        initListButton();
        initSettingsButton();

        //initGetLocationButton();
        initMapButton();
        initMapTypeButton();



//        initListButton();
//        initSettingsButton();
//
//        //initGetLocationButton();
//        initMapButton();
//        initMapTypeButton();

//        ImageButton ibMap=(ImageButton)findViewById(R.id.imageButtonMap);
//        ibMap.setEnabled(false);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createLocationRequest();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(mySensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, magnetometer, SensorManager. SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "Sensors not found",Toast.LENGTH_LONG).show();
        }
        textDirection = (TextView) findViewById(R.id.textHeading);
        textProximity=(TextView) findViewById(R.id.textProximity);
        proximitySensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//        if (proximitySensor != null ) {
//            sensorManager.registerListener(mySensorEventListener, proximitySensor,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//
//        } else {
//            Toast.makeText(this, "proximitySensor not found",Toast.LENGTH_LONG).show();
//        }
    }
    protected void onResume() {
        super.onResume();
//        sensorManager.registerListener((SensorEventListener) this, proximitySensor,
//                SensorManager.SENSOR_DELAY_NORMAL);
        if (proximitySensor != null ) {
            sensorManager.registerListener(mySensorEventListener, proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            Toast.makeText(this, "proximitySensor not found",Toast.LENGTH_LONG).show();
        }
    }
            private SensorEventListener mySensorEventListener = new SensorEventListener() {

                public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

                float[] accelerometerValues;
                float[] magneticValues;
                float[] proximityValues;

                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                        accelerometerValues = event.values;
                    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                        magneticValues = event.values;
                    if (accelerometerValues!= null && magneticValues!= null) {
                        float R[] = new float[9];
                        float I[] = new float[9];
                        boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticValues);
                        if (success) {
                            float orientation[] = new float[3];
                            SensorManager.getOrientation(R, orientation);

                            float azimut = (float) Math.toDegrees(orientation[0]);
                            if (azimut < 0.0f) { azimut+=360.0f;}
                            String direction;
//                            NW=  292.5-337.5  N=337.5-22.5
//                            NE=22.5-67.5      E=67.5-112.5
//                            SE=112.5-157.5    S=157.5-202.5
//                            SW=202.5-247.5    W=247.5-292.5
                            if (azimut >= 337.5 || azimut < 22.5) { direction = "N"; }
                            else if (azimut >= 292.5 && azimut < 337.5) { direction = "NW"; }
                            else if (azimut >= 247.5 && azimut < 292.5) { direction = "W"; }
                            else if (azimut >= 202.5 && azimut < 247.5) { direction = "SW"; }
                            else if (azimut >= 157.5 && azimut < 202.5) { direction = "S"; }
                            else if (azimut >= 112.5 && azimut < 157.5) { direction = "SE"; }
                            else if (azimut >= 67.5&& azimut < 112.5) { direction = "E"; }
                            else { direction = "NE"; }
//                            if (azimut >= 315 || azimut < 45) { direction = "N"; }
//                            else if (azimut >= 225 && azimut < 315) { direction = "W"; }
//                            else if (azimut >= 135 && azimut < 225) { direction = "S"; }
//                            else { direction = "E"; }
                            textDirection.setText(direction);
                        }
                    }
                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY)
                        proximityValues = event.values;
                    float maxRange = proximitySensor.getMaximumRange();
                    if(maxRange == event.values[0]) {
                        // Do something when something is far away.
                        textProximity.setText("far");
                    }
                    else {
                        // Do something when something is near.
                        textProximity.setText("near");
                    }
//                    if (proximityValues[0] == 0) {
//                        textProximity.setText("near");
//                    } else {
//                        textProximity.setText("far");
//                    }
                }
            };
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //mLocationRequest=LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED)
                {
                    return  ;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, (LocationListener) this);
            }

    @Override
    public void onConnectionSuspended(int i) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                (LocationListener) this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return  ;
                }
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, (LocationListener) this);
            }

    @Override
    public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                Point size = new Point();
                WindowManager w = getWindowManager();
                //1
                w.getDefaultDisplay().getSize(size);
                int measuredWidth = size.x;
                int measuredHeight = size.y;

                if (contacts.size()>0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int i=0; i<contacts.size(); i++) {
                        currentContact = contacts.get(i);

                        Geocoder geo = new Geocoder(this);
                        //List<Address> addresses = null;
                        List<Address> addresses = new ArrayList<Address>();

                        String address = currentContact.getStreetAddress() + ", " +
                                currentContact.getCity() + ", " +
                                currentContact.getState() + " " +
                                currentContact.getZipCode()+", USA";
//                        Toast.makeText(this, "Contacts " +i+
//                                        " : address: "+ address
//                                        ,
//                                Toast.LENGTH_LONG).show();
                        try {
                            addresses = geo.getFromLocationName(address, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0) {
                            LatLng point = new LatLng(addresses.get(0).getLatitude(),
                                    addresses.get(0).getLongitude());
//                            Toast.makeText(this, "Contacts :" +i+
//                                            " address: "+ address +
//                                                    " Latitude : "+addresses.get(0).getLatitude()+
//                                                    " Longitude : "+addresses.get(0).getLongitude()
//                                    ,
//                                    Toast.LENGTH_LONG).show();
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    ContactMapActivity.this).create();
                            alertDialog.setTitle("Latitude and Longitude ");

                            alertDialog.setMessage("Contact : "+(i+1)+" address: "+ address+
                                    " \n\nLatitude : "+addresses.get(0).getLatitude()
                                    +" \nLongitude : "+addresses.get(0).getLongitude());

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //finish();
                                            dialog.cancel();
                                        } });
                            alertDialog.show();

                            builder.include(point);

                            gMap.addMarker(new MarkerOptions().position(point).
                                    title(currentContact.getContactName()).
                                    snippet(address));
                        }
                    }
                        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                                measuredWidth, measuredHeight, 450));

                }
                else {
                    if (currentContact != null) {
                        Geocoder geo = new Geocoder(this);
                        //List<Address> addresses = null;
                        List<Address> addresses = new ArrayList<Address>();

                        String address = currentContact.getStreetAddress() + ", " +
                                currentContact.getCity() + ", " +
                                currentContact.getState() + " " +
                                currentContact.getZipCode()+", USA";
                        Toast.makeText(this, "CurrentContact " +
                                        " address: "+ address
                                ,
                                Toast.LENGTH_LONG).show();
                        try {
                            addresses = geo.getFromLocationName(address, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0) {
                            LatLng point = new LatLng(addresses.get(0).getLatitude(),
                                    addresses.get(0).getLongitude());
//                            Toast.makeText(this, "CurrentContact :" +
//                                            " address: "+ address +
//                                                    " Latitude : "+addresses.get(0).getLatitude()+
//                                                    " Longitude : "+addresses.get(0).getLongitude()
//                                    ,
//                                    Toast.LENGTH_LONG).show();
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    ContactMapActivity.this).create();
                            alertDialog.setTitle("Latitude and Longitude ");

                            alertDialog.setMessage("CurrentContact : address: "+ address+
                                            " \nLatitude : "+addresses.get(0).getLatitude()
                                            +" \nLongitude : "+addresses.get(0).getLongitude());

                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        } });
                            alertDialog.show();
                            gMap.addMarker(new MarkerOptions().position(point).
                                    title(currentContact.getContactName()).snippet(address));
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));

                        }
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                ContactMapActivity.this).create();
                        alertDialog.setTitle("No Data");
                        alertDialog.setMessage("No data is available for the mapping function.");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            } });
                        alertDialog.show();
                    }
                }
                try {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(ContactMapActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                     ContactMapActivity.this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                                Snackbar.make(
                                        findViewById(R.id.activity_contact_map),
                                        "MyContactList requires this permission to locate your contacts",
                                        Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        ActivityCompat.requestPermissions(ContactMapActivity.this,
                                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                PERMISSION_REQUEST_LOCATION);
                                    }
                                })
                                        .show();

                            } else {
                                ActivityCompat.requestPermissions(
                                        ContactMapActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        } else {
                            startLocationUpdates();
                        }
                    } else {
                        startLocationUpdates();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(),
                            "Error requesting permission",
                            Toast.LENGTH_LONG).show();
                }

            }
    @Override
    public void onLocationChanged( Location location) {
        Toast.makeText(getBaseContext(),
                "onLocationChanged  Lat: "+location.getLatitude()+
                        " Long: "+location.getLongitude()+
                        " Accuracy:  "+ location.getAccuracy(),
                Toast.LENGTH_LONG).show();

    }
            //p.139 Listing 7.13
    private void startLocationUpdates() {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( getBaseContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return  ;
                }

                gMap.setMyLocationEnabled(true);
            }
    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactMapActivity.this,
                        ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
        ibMap.setEnabled(false);
//        ibMap.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(ContactMapActivity.this,
//                        ContactMapActivity.class);
//                if(currentContact.getContactID()==-1){
//                    Toast.makeText(getBaseContext(),
//                            "Contact must be saved before it can be mapped",
//                            Toast.LENGTH_LONG).show();
//
//                }
//                else{
//                    intent.putExtra("contactid",currentContact.getContactID());
//                }
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactMapActivity.this,
                        ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
    private void initMapTypeButton() {
        final Button satelitebtn = (Button) findViewById(R.id.buttonMapType);
        satelitebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String currentSetting = satelitebtn.getText().toString();
                if (currentSetting.equalsIgnoreCase("Satellite View")) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    satelitebtn.setText("Normal View");
                }
                else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satelitebtn.setText("Satellite View");
                }
            }
        });
    }


}
