package com.theneem.barterbuddy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.media.Rating;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by deval on 3/21/2017.
 */

public class MapSaintActivity extends BaseAppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleMap.OnMarkerClickListener,
        LocationListener {


    private GoogleMap mMap;
    SharedPreferences MyPref;

    private static String ServerURL = null;
    private static String UrlGet = null; //  ServerURL + "/Saint/GetSaintByID/";
    private static String UrlAddSaintReview = null; // ServerURL + "/Saint/AddSaint/";



    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    public static String stringSaint = null;
    public static float currentSaintLat = 0;
    public static float currentSaintLng = 0;
    public static String currentSaintName = null;
    public static int currentSaintID;
    public static LatLng currentLocation;
    public static LatLng selectedLocation;
    public static boolean isLoaded = false;
    public static String currentpublicID = null;
    public static String currentRating = null;




    public ArrayList<String > alSaint = new ArrayList<String>();
    public ArrayList<String > alSaintID = new ArrayList<String>();
    public ArrayList<String> alSaintDesc  = new ArrayList<String>();
    public ArrayList<String> alSaintImage = new ArrayList<String>();
    public ArrayList<String> alSaintRating = new ArrayList<String>();


    ListView list;


    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsaint);

        BaseonCreate();

        addListenerOnRatingBar();



        //load header
        // ---------------------------------------------------------

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //"/Saint/GetSaintByID/";

        ServerURL = getString(R.string.ServerURL);
        UrlGet = ServerURL + "/getsaint.php?saintid=";

        UrlAddSaintReview = ServerURL + "/addsaintreview.php";


        // Load current Data
        //------------------------------------------------------------
        //GetSaintWithinXKm("1 Km");

        // Set Fragment for Map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Handle button goto direction
        Button btnDriveTo = (Button) findViewById(R.id.btnDriveTo);
        btnDriveTo.setOnClickListener(this);

        //Handle button for submit new temple
        Button btnSubmitSaint = (Button) findViewById(R.id.btnSubmitSaint);
        btnSubmitSaint.setOnClickListener(this);

        Button btnAddBack = (Button) findViewById(R.id.btnBackSubmit);
        btnAddBack.setOnClickListener(this);


        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);



        // Handle spinner for Distance withing
        // -----------------------------------------------------------

        Spinner spnLocale = (Spinner) findViewById(R.id.aryKmrange);

        spnLocale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                MyPref = getApplication().getSharedPreferences("com.theneem.findrligion.AppSettings", MODE_PRIVATE);

                GetSaintWithinXKm(adapterView.getItemAtPosition(i).toString().replace(" Km", ""));

                //Toast.makeText(getApplication(), "item selected ", Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }


    /**
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
        //LatLng sydney = new LatLng(currentTempleLat,currentTempleLng);
        //mMap.addMarker(new MarkerOptions().position(sydney).title(currentTempleName)).showInfoWindow();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMarkerClickListener(this);


    }


    //Build google apoi client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void GetSaintWithinXKm(String XKm) {

        //Toast.makeText(this, "inside GetSaintWithinXKm" + XKm, Toast.LENGTH_LONG).show();


        try {
            //String UrlGetByKm = "http://172.168.0.30/Temple/GetTempleByKM/1?ReligionId="+MyPref.getString ("ReligionId",null )+"&CurrentLang="+currentLocation.latitude+"&CurrentLat="+currentLocation.longitude+"&WithinXKm="+XKm;
            String UrlGetByKm = ServerURL + "/getsaintwithinxkm.php?ReligionId=" + MyPref.getString("ReligionId", null) + "&CurrentLang=22.4168315&CurrentLat=71.319661&WithinXKm=1000";
            //Toast.  makeText(this, UrlGetByKm, Toast.LENGTH_LONG).show();


            ShowList();


            StringRequest strReq = new StringRequest(Request.Method.GET,
                    UrlGetByKm, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        //Toast.makeText(getApplication(), "inside respose", Toast.LENGTH_LONG).show();

                        stringSaint = response.toString();

                        mMap.clear();
                        alSaint.clear();
                        alSaintDesc.clear();
                        alSaintID.clear();
                        alSaintImage.clear();
                        alSaintRating.clear();

                        // substring added to remove un seen characters being return by mysql service
                        JSONObject info = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}") + 1 ));

                        //JSONArray rootArray = new JSONArray(stringTemple);
                        JSONArray rootArray = info.optJSONArray("info");    //   JSONArray(stringTemple);


                        for (int i = 0; i < rootArray.length(); i++) {

                            JSONObject root = rootArray.getJSONObject(i);

                            currentSaintID = Integer.parseInt(root.getString("SaintId"));
                            currentSaintName = root.getString("SaintName");


                            alSaint.add(currentSaintName);
                            alSaintID.add(root.getString("SaintId"));
                            alSaintDesc.add(root.getString("SaintSynopsis"));
                            alSaintImage.add(root.getString("SaintSynopsis"));
                            alSaintRating.add(root.getString("rating"));



                            currentSaintLat = (float) root.getDouble("SaintLat");
                            currentSaintLng = (float) root.getDouble("SaintLag");


                            // Add a marker in current saint location  and move the camera
                            LatLng saint = new LatLng(currentSaintLat, currentSaintLng);

                            Marker mkr = mMap.addMarker(new MarkerOptions().position(saint)
                                    .title(currentSaintName));
                            mkr.showInfoWindow();
                            mkr.setTag(currentSaintID);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(saint));

                        }



                        String[] stTemple = new String[alSaint.size()];
                        stTemple = alSaint.toArray(stTemple);

                        String[] stTempleDesc = new String[alSaintDesc.size()];
                        stTempleDesc = alSaintDesc.toArray(stTempleDesc);

                        String[] stImage  = new String[alSaintImage.size()];
                        stImage = alSaintImage.toArray(stImage);


                        String[] stSaintRating = new String[alSaintRating.size()];
                        stSaintRating = alSaintRating.toArray(stSaintRating);

                        ImageListAdapter adapter=new ImageListAdapter(MapSaintActivity.this, stTemple,stTempleDesc, stImage,stSaintRating);

                        list=(ListView) findViewById(R.id.list);
                        //list.setVisibility(View.VISIBLE);

                        ShowList();
                        list.setAdapter(adapter);

                        //LinearLayout parentLayout = (LinearLayout) findViewById(R.id.ldetail);
                        //parentLayout.setVisibility(View.GONE);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                // TODO Auto-generated method stub
                                String[] st = new String[alSaintID.size()];
                                st = alSaintID.toArray(st);


                                String Slecteditem= st[+position];
                                //Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                                GetSaint(st[+position]);
                                //LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.lview);

                                //  parentLayout.setVisibility(View.GONE);



                                //LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.ldetail);
                                //parentLayout.setVisibility(View.VISIBLE);


                            }
                        });

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        //Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(this, "inside temple" + error.getMessage(), Toast.LENGTH_LONG).show();

                    error.printStackTrace();
                    // pDialog.hide();
                }
            });

            strReq.setRetryPolicy(new DefaultRetryPolicy(500000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(strReq);


            //Toast.makeText(getActivity(), "after temple" , Toast.LENGTH_LONG).show();

            //return stringTemple;

        } catch (Exception ex) {
            // Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

            // return null;
        }

    }


    public void GetSaint(String SaintId) {

        String URLWithSaintID;

        URLWithSaintID = UrlGet + SaintId;
        currentSaintID = Integer.parseInt(SaintId) ;

        //Toast.makeText(this, "inside GetTemple with id " + URLWithSaintID, Toast.LENGTH_LONG).show();


        try {

            ShowDetail();


            StringRequest strReq = new StringRequest(Request.Method.GET,
                    URLWithSaintID, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        stringSaint = response.toString();

                        JSONObject rootobj = new JSONObject(response.substring(response.indexOf("{"),response.lastIndexOf("}") + 1 ));

                        //JSONArray rootArray = new JSONArray(stringTemple);
                        JSONArray rootArray = rootobj.optJSONArray("info");


                        JSONObject root = rootArray.getJSONObject(0);


                        TextView txtSaintName = (TextView) findViewById(R.id.saintname);
                        txtSaintName.setText(root.getString("SaintName"));
                        currentSaintName = root.getString("SaintName");

                        TextView txtSaintContactNo = (TextView) findViewById(R.id.saintcontactno);
                        txtSaintContactNo.setText(root.getString("SaintContactNo"));

                        TextView txtSaintCurrentLocation = (TextView) findViewById(R.id.saintcurrentlocation);
                        txtSaintCurrentLocation.setText(root.getString("SaintCurrentLocation"));

                        TextView txtSaintSynopsis = (TextView) findViewById(R.id.saintsynopsis);
                        txtSaintSynopsis.setText(root.getString("SaintSynopsis"));


                        RatingBar rBar = (RatingBar) findViewById(R.id.ratingBar) ;
                        rBar.setRating((float ) root.getDouble("rating"));

                        currentSaintLat = (float) root.getDouble("SaintLat");
                        currentSaintLng = (float) root.getDouble("SaintLag");

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        //Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(this, "inside temple" + error.getMessage(), Toast.LENGTH_LONG).show();

                    error.printStackTrace();
                    // pDialog.hide();
                }
            });

            strReq.setRetryPolicy(new DefaultRetryPolicy(500000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(strReq);



        } catch (Exception ex) {
            // Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

            // return null;
        }

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
    protected void onResume() {
        super.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

            }
        }

    }




    @Override
    protected void onStart() {
        super.onStart();
        //ShowList();

        ShowList();

        Spinner spnLocale = (Spinner) findViewById(R.id.aryKmrange);
        spnLocale.setSelection(1);


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
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        if(!isLoaded )
        {
            GetSaintWithinXKm("5");
            ShowList();
            isLoaded=true;
        }



    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Toast.makeText(getApplication(), "inside market click " + marker.getTag(), Toast.LENGTH_LONG).show();

        //if (marker.getTitle().equals("Vartej Jain Tirth"))
        {
            //handle click here
            //Toast.makeText(getApplication(), "inside market click " + marker.getTitle(), Toast.LENGTH_LONG).show();

            GetSaint(marker.getTag().toString());

        }

        return true;
    }








    public void AddSaintReview() {



        SetAppString("Lastpage", "saintreview");


        if(AccessToken.getCurrentAccessToken() == null)
        {

            Intent intentSubmit = new Intent(this, facebooklogin.class);
            startActivity(intentSubmit);

        }
        else
        {

        // GET ALL THE ENTERED TEXT
        currentpublicID = GetAppString("profileid");


        RatingBar rbar = (RatingBar) findViewById(R.id.ratingBar) ;
        currentRating = String.valueOf(rbar.getRating())  ;


        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlAddSaintReview,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplication(),"Insite add saintreview  response" + response.toString(), Toast.LENGTH_LONG).show();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplication(),"Add saintreview response error" + error.toString(),Toast.LENGTH_LONG).show();



                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("profileid", currentpublicID);
                    params.put("rating", currentRating );
                    params.put("Saintid", Integer.toString(currentSaintID));
                    return params;
                }

            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(500000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        } catch (Exception ex) {
            //Toast.makeText(this, "inside error" + ex.getMessage(), Toast.LENGTH_LONG).show();

            // return null;
        }

        } // else

    }



    // ALL EVENTS DEFINATION
    public void ShowList()
    {
        list=(ListView) findViewById(R.id.list);
        list.setVisibility(View.VISIBLE);


        Button btnAdd = (Button) findViewById(R.id.btnSubmitSaint);
        btnAdd.setVisibility(View.VISIBLE);

        Button btnBack = (Button) findViewById(R.id.btnBackSubmit);
        btnBack.setVisibility(View.VISIBLE);

        Button btnAddBack = (Button) findViewById(R.id.btnBack);
        btnAddBack.setVisibility(View.GONE);




        Button btnDrive = (Button) findViewById(R.id.btnDriveTo);
        btnDrive.setVisibility(View.GONE);


        LinearLayout lbdrive =(LinearLayout) findViewById(R.id.llbacdrivefooter);
        lbdrive.setVisibility(View.GONE);



        LinearLayout lbsub =(LinearLayout) findViewById(R.id.llbacsubfooter);
        lbsub.setVisibility(View.VISIBLE);


        LinearLayout lname =(LinearLayout) findViewById(R.id.lvName);
        lname.setVisibility(View.GONE);


        LinearLayout lcont =(LinearLayout) findViewById(R.id.lvContact);
        lcont.setVisibility(View.GONE);


        LinearLayout ladd =(LinearLayout) findViewById(R.id.lvaddress);
        ladd .setVisibility(View.GONE);



        LinearLayout lsyno =(LinearLayout) findViewById(R.id.lvsyno);
        lsyno.setVisibility(View.GONE);



        LinearLayout rbar =(LinearLayout) findViewById(R.id.rBar);
        rbar.setVisibility(View.GONE);


    }



    public void ShowDetail()
    {

        list=(ListView) findViewById(R.id.list);
        list.setVisibility(View.GONE);


        Button btnAdd = (Button) findViewById(R.id.btnSubmitSaint);
        btnAdd.setVisibility(View.GONE);

        Button btnAddBack = (Button) findViewById(R.id.btnBackSubmit);
        btnAddBack.setVisibility(View.GONE);

        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);



        Button btnDrive = (Button) findViewById(R.id.btnDriveTo);
        btnDrive.setVisibility(View.VISIBLE);

        LinearLayout lbdrive =(LinearLayout) findViewById(R.id.llbacdrivefooter);
        lbdrive.setVisibility(View.VISIBLE);


        LinearLayout lbsub =(LinearLayout) findViewById(R.id.llbacsubfooter);
        lbsub.setVisibility(View.GONE);



        LinearLayout lname =(LinearLayout) findViewById(R.id.lvName);
        lname.setVisibility(View.VISIBLE);


        LinearLayout lcont =(LinearLayout) findViewById(R.id.lvContact);
        lcont.setVisibility(View.VISIBLE);


        LinearLayout ladd =(LinearLayout) findViewById(R.id.lvaddress);
        ladd .setVisibility(View.VISIBLE);



        LinearLayout lsyno =(LinearLayout) findViewById(R.id.lvsyno);
        lsyno.setVisibility(View.VISIBLE);


        LinearLayout rbar =(LinearLayout) findViewById(R.id.rBar);
        rbar.setVisibility(View.VISIBLE);



    }


    //
    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.btnDriveTo:
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", currentLocation.latitude, currentLocation.longitude, "My Location", currentSaintLat, currentSaintLng, currentSaintName);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                break;
            case R.id.btnSubmitSaint:

                SetAppString("Lastpage", "saint");


                if(AccessToken.getCurrentAccessToken() != null)
                {
                    Intent intentSubmit = new Intent(this, SubmitSaintActivity.class);
                    startActivity(intentSubmit);
                    //Toast.makeText(getApplication(), "inside button for submit temple click ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intentSubmit = new Intent(this, facebooklogin.class);
                    startActivity(intentSubmit);
                }

                //Toast.makeText(getApplication(), "inside button for submit temple click ", Toast.LENGTH_LONG).show();
                break;
            case R.id.btnBack:


                ShowList();

                //Toast.makeText(getApplication(), "inside button for submit temple click ", Toast.LENGTH_LONG).show();

                break;


            case R.id.btnBackSubmit:
                Intent intentSubmit = new Intent(this, MainActivity.class);
                startActivity(intentSubmit);
                break;


        }


        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        //this.startActivity(intent);

    }




    public void addListenerOnRatingBar() {

        RatingBar ratingBar;

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
//        stars.getDrawable(0).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        stars.getDrawable(0).setColorFilter(Color.rgb(255,128,0), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.rgb(0,0,0), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.rgb(109,138,0), PorterDuff.Mode.SRC_ATOP);


        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                //Toast.makeText(this, String.valueOf(rating), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,String.valueOf(rating),Toast.LENGTH_SHORT).show();

                //lets  first check about the login
AddSaintReview();

            }
        });
    }





}
