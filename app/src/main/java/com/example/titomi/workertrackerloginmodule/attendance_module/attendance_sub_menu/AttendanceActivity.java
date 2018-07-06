package com.example.titomi.workertrackerloginmodule.attendance_module.attendance_sub_menu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.HttpParse;
import com.example.titomi.workertrackerloginmodule.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.titomi.workertrackerloginmodule.R.drawable.clock_in_red;

public class AttendanceActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Spinner spinner;
    EditText notes, institution;
    Button clock_in, clock_out;
    Toolbar toolbar;
    TextClock textClock;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mGoogleMap;
    private ProgressDialog progressDialog;
    private HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpUrl = "";
    String finalResult;
    Date inputDate;
    private static final String KEY_BUTTON_CLICK = "button_clicked";
    Boolean buttonClicked;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        spinner = findViewById(R.id.spinner);
        institution = findViewById(R.id.atted_institution);
        notes = findViewById(R.id.atted_comments);
        clock_in = findViewById(R.id.clock_in);
        clock_out = findViewById(R.id.clock_out);
        toolbar = findViewById(R.id.toolbar);
        textClock = findViewById(R.id.clock_time);

        final String userFirstName = getIntent().getStringExtra("UserFirstName");
        final String userId = getIntent().getStringExtra("UserId");
        final String userLastName = getIntent().getStringExtra("UserLastName");
        final String userEmail = getIntent().getStringExtra("UserEmail");

        institution.setText(userFirstName);



//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Attendance");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setTitleTextColor(Color.WHITE);

        SimpleDateFormat fmt = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fmt = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            try {
                inputDate = fmt.parse("10-22-2011 01:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

// Create the MySQL datetime string
        String dateString = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateString = fmt.format(inputDate);
        }


        clock_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                insertClockTime(startTimeHolder,clockLngHolder,clockLatHolder,clockCommentHolder);
                clock_in.setBackgroundColor(clock_in_red);


                Toast.makeText(AttendanceActivity.this, "You've clocked in", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }


    public void insertClockTime (final String S_startTime, final String S_clockLng, final String S_clockLat, final String S_clockComment){
        class insertClockTimeClass extends AsyncTask<String, Void, String>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = progressDialog.show(AttendanceActivity.this,"Loading",null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();
                Toast.makeText(AttendanceActivity.this, httpResponseMsg, Toast.LENGTH_SHORT).show();
                clock_out.setEnabled(false);
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("StartTime", params[0]);
                hashMap.put("ClockLng", params[1]);
                hashMap.put("ClockLat", params[2]);
                hashMap.put("ClockComment", params[3]);

                finalResult = httpParse.postRequest(hashMap, HttpUrl);
                return finalResult;
            }
        }

        insertClockTimeClass insertClockTimeClass = new insertClockTimeClass();
        insertClockTimeClass.execute(S_startTime, S_clockLng, S_clockLat, S_clockComment);
    }


/*    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildingGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            buildingGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildingGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        double longitude = 0;
        double latitude = 0;
        if (null != locations && null != providerList && providerList.size() > 0) {
            longitude = locations.getLongitude();
            latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
// Here we are finding , whatever we want our marker to show when clicked
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
//move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
//this code stops location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        outState.putBoolean(KEY_BUTTON_CLICK, buttonClicked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
// Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted. Do the
// contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildingGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
