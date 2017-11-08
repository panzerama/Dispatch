package com.indexyear.jd.dispatch.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.ManageCrisis;
import com.indexyear.jd.dispatch.data.ManageUsers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.indexyear.jd.dispatch.R.id.spinner;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.Active;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.Dispatched;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.NotSet;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.OffDuty;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.OnBreak;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //todo jd fix level of zoom on default view
    private static final String TAG = "MainActivity";
    private static final float MAP_ZOOM_LEVEL = 0.5f;

    private String[] menuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DatabaseReference mDatabase; //Firebase reference
    private Spinner statusSpinner;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    private String userID;
    private UserStatus currentStatus;

    //Strings for crisis is for testing purposes entered by LJS 10/29/17
    private String crisisID;
    private String crisisAddress;
    //LatLng for testing purposes
    private LatLng crisisPin = new LatLng(47, -122);

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    boolean mRequestingLocationUpdates;


    // Retrieving User UID for database calls and logging
    private FirebaseAuth mAuth;

    // For managing Firebase analytics logging
    private FirebaseAnalytics mAnalytics;

    // For location tracking and map updates
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static Context context;
    private Address mCurrentLocation;
    private String mLastUpdateTime;
    private HashMap mCoordinate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAddressDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Obtain last known location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Obtain Auth instance for logging and database access
        mAuth = FirebaseAuth.getInstance();

        //For Testing
        ManageUsers newUser = new ManageUsers();
        newUser.AddNewEmployee("kbullard", "Kari", "Bullard", "541-335-9392");
        userID = "kbullard";




        //Register data listeners
        ref = database.getReference("team-orange-20666/employees/" + userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TO DO
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TO DO
            }
        });
        // This triggers the Alert Dialog. It is currently set to a static address - JD and Luke
        DispatchAlertDialog();

    }

    public void DispatchAlertDialog() {
        //For Testing added by Luke
        Log.d(TAG, "In DispatchAlertDialog");
        ManageCrisis newCrisis = new ManageCrisis();
        crisisID = "testCrisis";
        crisisAddress = "8507 18TH AVE NW, Seattle, WA 98117";
        //This adds a crisis to the JSON Database
        newCrisis.CreateNewCrisis(crisisID, crisisAddress);

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Crisis Alert");
        alertDialog.setMessage("Go to this address? \n " + crisisAddress);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //put your call to the Map activity here maybe?
                        // need the LatLang to give it
                        GetLatLng(crisisAddress);

                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void CreateAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incident Address : ");

        //address input
        final AppCompatEditText input = new AppCompatEditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Calculate Travel Times", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                crisisAddress = input.getText().toString();
                Intent i = new Intent(context, DispatchTeamActivity.class);
                i.putExtra("crisisAddress", crisisAddress);
                startActivity(i);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(spinner);
        statusSpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        statusSpinner.setSelection(0);
        ManageUsers newUser = new ManageUsers();
        newUser.SetUserStatus(userID, Active);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = statusSpinner.getSelectedItem().toString();
                ManageUsers user = new ManageUsers();
                user.SetUserStatus(userID, getSpinnerValueAsEnum(text));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: 11/7/2017 Kari / Mahillet Set UserLocation object data
        Location mCurrentLocation = location;
//        DateFormat dateFormat = new DateFormat("yyyy/MM/dd HH:mm:ss");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date date = new Date();
//        String mLastUpdateTime = dateFormat.format(date).toString();

        saveToFirebase(mCurrentLocation);

        //Retrieve saved locations and draw as marker on map
       //drawLocations();

        // Update UI to draw bread crumb with the latest bus location.
        mMap.clear();

        
        LatLng mLatlng = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        MarkerOptions mMarkerOption = new MarkerOptions()
                .position(mLatlng)
                .title(mLastUpdateTime).icon(BitmapDescriptorFactory.fromResource(R.drawable.code_the_road_small));

        Marker mMarker = mMap.addMarker(mMarkerOption);

    }

    private static String GetCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        return userID;
    }

    private void saveToFirebase(Location userLocation) {

//        Map mLocations = new HashMap();
//        //Log.d("location check" , "Check Save firebase");
//        //mLocations.put("timestamp", mLastUpdateTime);
//        Map  mCoordinate = new HashMap();
//    // LatLng busLocation = new LatLng(37.783879,-122.401254);
//        mCoordinate.put("latitude", mCurrentLocation.getLatitude());
//       mCoordinate.put("longitude", mCurrentLocation.getLongitude());
//      ;
       // mCoordinate.put("latitude",37.783879);
       // mCoordinate.put("longitude", -122.401254);
        //mLocations.put("location", mCoordinate);
        String userID = GetCurrentUser();
        if(userID != null){
            DatabaseReference employeeReference = database.getReference("team-orange-20666/employees/" + userID);
            employeeReference.child("userLocation").setValue(userLocation);
        } else {
            Toast toast = Toast.makeText(context, "Unable to find user ID.", Toast.LENGTH_LONG);
            toast.show();
        }
        ref.child("employees").setValue(userLocation);
    }


    private void drawLocations(Location userLocation) {
        // Get only latest logged locations - since 'START' button clicked
//        Query queryRef = ref.orderByChild("timestamp").startAt(userLocation.getTime());
//        // Add listener for a child added at the data at this location
//       queryRef.addChildEventListener(new ChildEventListener() {
//           LatLngBounds bounds;
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//           @Override
//           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//               Map  data = (Map ) dataSnapshot.getValue();
//                String timestamp = (String) data.get("timestamp");
//                // Get recorded latitude and longitude
//
//        Map  mCoordinate = (HashMap)data.get("location");
//               double latitude = (double) (mCoordinate.get("latitude"));
//                double longitude = (double) (mCoordinate.get("longitude"));
//
//               // Create LatLng for each locations
//               LatLng mLatlng = new LatLng(latitude, longitude);
//                // Make sure the map boundary contains the location
//               builder.include(mLatlng);
//                bounds = builder.build();
//
//                // Add a marker for each logged location
//               MarkerOptions mMarkerOption = new MarkerOptions().position(mLatlng)
//                       .title(timestamp).icon(BitmapDescriptorFactory.fromResource(R.drawable.measle_blue));
//               Marker mMarker = mMap.addMarker(mMarkerOption);
//
//           markerList.add(mMarker);
//
//                // Zoom map to the boundary that contains every logged location
//                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ZOOM_LEVEL));
           // }

        //});
    }

    public enum UserStatus {
        Active, OffDuty, OnBreak, Dispatched, NotSet
    }

    private UserStatus getSpinnerValueAsEnum(String value) {
        switch (value) {
            case "Active":
                return Active;
            case "Off-Duty":
                return OffDuty;
            case "On-Break":
                return OnBreak;
            case "Dispatched":
                return Dispatched;
            default:
                return NotSet;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        Context context = getApplicationContext();
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, id, duration);
//        toast.show();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.nav_message:
                Intent intent = new Intent(this, MessengerActivity.class);
                this.startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, ": onmapready");

        //trying to place the marker on my house using the AlertDialog.
        mMap.addMarker(new MarkerOptions().position(crisisPin).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(crisisPin));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crisisPin, 10));

    }


    // returns a LatLng object from an address given
    public LatLng getLocationFromAddress(Context context, String address) {
        Log.d(TAG, "In getLocationFromAddress");
        LatLng latLng = null;
        Log.d(TAG, "getLocationFromAddress, made new latlng");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Log.d(TAG, "getLocationFromAddress, made new geocoder");
        List<Address> geoResults = null;
        Log.d(TAG, "getLocationFromAddress, made list.");

        try {
            geoResults = geocoder.getFromLocationName(address, 1);
            String addressFromCoder = geoResults.get(0).toString();
            Log.d(TAG, addressFromCoder);
            while (geoResults.size() == 0) {
                geoResults = geocoder.getFromLocationName(address, 1);


            }
            if (geoResults.size() > 0) {
                Address addr = geoResults.get(0);
                latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        //latLng = new LatLng(47.6909439,-122.3823438);
        Log.d(TAG, "Returning a LatLng value");
        return latLng; //LatLng value of address

    }

    public void GetLatLng(String crisisAddress) {

        crisisAddress = ConvertAddressToJSON(crisisAddress);

        RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();
        Log.d(TAG, getResources().getString(R.string.google_geocoding_key));
        String urlForGoogleMaps = "https://maps.googleapis.com/maps/api/geocode/json?address=" + crisisAddress +
                "&key=" + getResources().getString(R.string.google_geocoding_key);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlForGoogleMaps, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        LatLng addressPosition;
                        double lat = -122;
                        double lng = 47;


                        try {
                            lat = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            lng = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        addressPosition = new LatLng(lat, lng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(addressPosition);
                        mMap.addMarker(markerOptions
                                .title("Crisis UserLocation").icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressPosition, 12));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add the request to the RequestQueue.
        mRequestQueue.add(jsObjRequest);


    }

    public String ConvertAddressToJSON(String address) {

        address.replace(' ', '+');

        return address;
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()),
                MAP_ZOOM_LEVEL));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static final long UPDATE_INTERVAL_IN_MS = 120000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MS =
            UPDATE_INTERVAL_IN_MS / 4;
    protected LocationRequest mLocationRequest;


    private void startLogging() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


}