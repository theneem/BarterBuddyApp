package com.theneem.barterbuddy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by be_Lu on 3/21/2017.
 */

public class SubmitTempleActivity extends BaseAppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnMarkerClickListener,
        LocationListener {


    private GoogleMap mMap;
    SharedPreferences MyPref;
    private static final String ServerURL =  "http://relisant.com/app";
    private static final String UrlAddTemple = ServerURL + "/addtemple.php";


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;


    public static String stringTemple = null;

    public static int currentReligionId;
    public static int currentTempleId;
    public static String currentTempleName = null;
    public static String currentTempleOfficeNo = null;
    public static String currentTempleOfficeAddress = null;
    public static String currentTempleSynopsis = null;
    public static float currentTempleLat = 0;
    public static float currentTempleLng = 0;
    public static String currentpublicID = null;


    public static LatLng currentLocation;
    public static LatLng selectedLocation;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.temple_submit_layout);

        BaseonCreate();

        //load header
        // ---------------------------------------------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // Load current Data
        //------------------------------------------------------------
        // GetTemple("1");

        MyPref = getApplication().getSharedPreferences("com.theneem.findrligion.AppSettings", MODE_PRIVATE);


        currentReligionId = Integer.parseInt(MyPref.getString("ReligionId", null));


        // Set Fragment for Map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmitTempleData);
        btnSubmit.setOnClickListener(this);


        // event listener for Radio button for select map or manual entry
        //RadioGroup rg  = (RadioGroup) findViewById(R.id.radioGroup);
        // rg.setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);




        final RadioGroup radio = (RadioGroup) findViewById(R.id.radioGroup);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);

                // Add logic here
                LinearLayout rtManualEntry = (LinearLayout) findViewById(R.id.layoutForEntry);
                LinearLayout rtMapEntry = (LinearLayout) findViewById(R.id.layoutForMapSelection);


                switch (index) {
                    case 0: // first button
                        rtMapEntry.setVisibility(View.VISIBLE);
                        rtManualEntry.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Selected button number " + index, 500).show();
                        break;
                    case 1: // secondbutton
                        rtMapEntry.setVisibility(View.GONE);
                        rtManualEntry.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "Selected button number " + index, 500).show();
                        break;
                }
            }
        });

    }


    // CUSTOME METHODS


    public void AddTemple() {

        // GET ALL THE ENTERED TEXT
        EditText tTempleName = (EditText) findViewById(R.id.txtTempleName);
        currentTempleName = tTempleName.getText().toString().trim();

        EditText ttempleofficeno = (EditText) findViewById(R.id.txttempleofficeno);
        currentTempleOfficeNo = ttempleofficeno.getText().toString().trim();

        EditText ttempleofficeaddress = (EditText) findViewById(R.id.txttempleofficeaddress);
        currentTempleOfficeAddress = ttempleofficeaddress.getText().toString().trim();


        EditText ttemplesynopsis = (EditText) findViewById(R.id.txttemplesynopsis);
        currentTempleSynopsis = ttemplesynopsis.getText().toString().trim();

        EditText ttempleLattitude = (EditText) findViewById(R.id.txttempleLattitude);
        currentTempleLat = Float.parseFloat(ttempleLattitude.getText().toString().trim());


        EditText ttempleLongitude = (EditText) findViewById(R.id.txttempleLongitude);
        currentTempleLng = Float.parseFloat(ttempleLongitude.getText().toString().trim());


        currentpublicID = GetAppString("profileid");

        //URLWithTempleID = UrlAddTemple;
        //Toast.makeText(this, "inside AddTemple with id " + URLWithTempleID, Toast.LENGTH_LONG).show();

        try {


            //////////////////////

            StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlAddTemple,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             ////Toast.makeText(getApplication(),"Insite add temple response" + response.toString(), Toast.LENGTH_LONG).show();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getApplication(),"add temple response error" + error.toString(),Toast.LENGTH_LONG).show();


                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("inReligionId", Integer.toString(currentReligionId));
                    params.put("inTempleName", currentTempleName);
                    params.put("inTempleOfficeNo", currentTempleOfficeNo);
                    params.put("inTempleOfficeAddress", currentTempleOfficeAddress);
                    params.put("inTempleSynopsis", currentTempleSynopsis);
                    params.put("inCurrentLat", Float.toString(currentTempleLng));
                    params.put("inCurrentLang", Float.toString(currentTempleLat));
                    params.put("inCurrentpublicID", currentpublicID);

                    return params;
                }

            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        } catch (Exception ex) {
            // Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

            // return null;
        }

    }

    /**
     * Map readyness
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(currentTempleLat, currentTempleLng);
        //mMap.addMarker(new MarkerOptions().position(sydney).title(currentTempleName)).showInfoWindow();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMarkerClickListener(this);


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


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
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                   // Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onLocationChanged(Location location) {


        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLocation = latLng;


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        EditText ttempleLattitude = (EditText) findViewById(R.id.txttempleLattitude);
        ttempleLattitude.setText(Double.toString(currentLocation.latitude));
        //currentTempleLat = Float.parseFloat(ttempleLattitude.getText().toString().trim());


        EditText ttempleLongitude = (EditText) findViewById(R.id.txttempleLongitude);

        ttempleLongitude.setText(Double.toString(currentLocation.longitude));
        //currentTempleLng = Float.parseFloat(ttempleLongitude.getText().toString().trim());


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Toast.makeText(getApplication(), "inside market click " + marker.getTag(), Toast.LENGTH_LONG).show();

        //if (marker.getTitle().equals("Vartej Jain Tirth"))
        {
            //handle click here
            // Toast.makeText(getApplication(), "inside market click " + marker.getTitle(), Toast.LENGTH_LONG).show();

            //GetTemple(marker.getTag().toString());

        }

        return true;
    }

    // ALL EVENTS DEFINATION
    @Override
    public void onClick(View view) {

        //Toast.makeText(this, "inside click of button submit ", Toast.LENGTH_LONG).show();
        switch (view.getId()) {


            case R.id.btnBack:

                Intent intentSubmit = new Intent(this, MapActivity.class);
                startActivity(intentSubmit);
                break;

            case R.id.btnSubmitTempleData:

                //Toast.makeText(this, "before calling add temple  ", Toast.LENGTH_LONG).show();

                AddTemple();
                //Toast.makeText(this, "after calling add temple  ", Toast.LENGTH_LONG).show();


                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("New Temple Submitted Successfully.");
                builder1.setCancelable(true);
                builder1.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                // MOVE BACK TO MAP ACTIVITY SCREEN -- NEED TO SHOW ALERT MESSAGE IN BETWEEN
                                Intent intentSubmit = new Intent(getApplication(), MapActivity.class);
                                startActivity(intentSubmit);
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();






                break;

        }
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        //this.startActivity(intent);

    }


    private void addListenerRadioButton() {


        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

        // rg.setOnClickListener(this);
        int selectedButton = rg.getCheckedRadioButtonId();
        RadioButton rdOne = (RadioButton) findViewById(selectedButton);

        LinearLayout rtManualEntry = (LinearLayout) findViewById(R.id.layoutForEntry);
        LinearLayout rtMapEntry = (LinearLayout) findViewById(R.id.layoutForMapSelection);

        //Toast.makeText(this, rdOne.getText(), Toast.LENGTH_LONG).show();


        if (rdOne.getText() == "Use Map") {
            rtMapEntry.setVisibility(View.VISIBLE);
            rtManualEntry.setVisibility(View.GONE);
        } else if (rdOne.getText() == "Manual") {
            rtMapEntry.setVisibility(View.GONE);
            rtManualEntry.setVisibility(View.VISIBLE);
            //set current lang lat

            EditText ttempleLattitude = (EditText) findViewById(R.id.txttempleLattitude);
            ttempleLattitude.setText(Double.toString( currentLocation.latitude));


            EditText ttempleLongitude = (EditText) findViewById(R.id.txttempleLongitude);
            ttempleLongitude.setText(Double.toString(currentLocation.longitude));

        }


    }


}
