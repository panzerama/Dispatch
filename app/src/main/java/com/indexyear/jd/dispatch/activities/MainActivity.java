package com.indexyear.jd.dispatch.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.crisis.CrisisParcel;
import com.indexyear.jd.dispatch.data.user.IUserEventListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.data.user.UserParcel;
import com.indexyear.jd.dispatch.models.Crisis;
import com.indexyear.jd.dispatch.models.User;

import java.util.List;
import java.util.Locale;

import static com.indexyear.jd.dispatch.R.id.map;
import static com.indexyear.jd.dispatch.R.id.spinner;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        //ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private String[] menuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DatabaseReference mDatabase; //Firebase reference
    private Spinner statusSpinner;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    private FloatingActionButton fab;


    // JDP - User info
    // mUser is regularly updated on database update by the implementation of IUserEventListener
    // We base our identification of the user on userID, populated by a call to the auth instance.
    private String userID;
    private User mUser;
    private UserManager mUserManager;
    private IUserEventListener mUserEventListener;

    //Strings for crisis is for testing purposes entered by LJS 10/29/17
    private String crisisID;
    private String crisisAddress;

    //LatLng for testing purposes
    //private LatLng crisisPinStart = new LatLng(47, -122);
    //private LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();

    //For location
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private LatLng userLocation;
    private LocationManager lm;
    private LocationListener locationListener;
    private DatabaseReference locations;

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

    private int MY_LOCATION_REQUEST_CODE;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Firebase Reference
        locations = FirebaseDatabase.getInstance().getReference("Locations");

        // Obtain Auth instance for logging and database access
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserParcel uParcel = getIntent().getParcelableExtra("user");
        mUser = uParcel.getUser();

        Log.d(TAG, " onCreate mUser userID " + mUser.getUserID());
        Log.d(TAG, " onCreate mAuth userID " + mAuth.getCurrentUser().getUid());

        setUserManagerAndListener();
        updateWithUserSettings();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        this.context = getApplicationContext();

        // Obtain last known location and camera position from saved instance state.
//        if (savedInstanceState != null) {
//            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
//        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // End Init

        // figure out which way to handle the incoming intent
        String incomingIntentPurpose = getIntent().getStringExtra("intent_purpose");

        if (incomingIntentPurpose != null && incomingIntentPurpose.equals("crisis_map_update")) {

            //if we have a Crisis with the intent get the Crisis Object
            CrisisParcel acceptedCrisisEvent = getIntent().getParcelableExtra("crisis");
            Crisis intentCrisis = acceptedCrisisEvent.getCrisis();

            //pass itself to it's own helper methods to get the lat and lng state assigned
            intentCrisis.GetLatLng(intentCrisis);

            //set the LatLng of the Crisis
            LatLng addressPosition;
            addressPosition = new LatLng(intentCrisis.getLatitude(), intentCrisis.getLongitude());

            //Use the new LatLng to place a pin on the map.
            PlacePinAndPositionCamera(addressPosition);

        } else {
            // set it up as you would normally, with the current location of the team
            // being set as map marker
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions();
            } else {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null){ location = new Location(LocationManager.GPS_PROVIDER); location.setLatitude(47.7394422); location.setLongitude(-122.345282);}
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermissions() {
        int hasLocationPermissionsGranted = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermissionsGranted != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            mMap.clear();
                            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mLastLocation = location;

                            //Write User Location to Database
                            String userID = getAuthUserID();
                            if (userID != null) {
                                mDatabase.child("employees").child(userID).child("latitude").setValue(userLocation.latitude);
                                mDatabase.child("employees").child(userID).child("longitude").setValue(userLocation.longitude);
                            } else {
                                Toast.makeText(getApplicationContext(), "Current user not recognized. Try reauthenticating.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Permissions Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

//    public void setCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        mLastLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            //Update to Firebase
//            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
//                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                            String.valueOf(mLastLocation.getLatitude()),
//                            String.valueOf(mLastLocation.getLongitude())));
//
//        } else {
//            //Toast.makeText(this, "Couldn't get location.", Toast.LENGTH_SHORT).show();
//            Log.d("TEST", " Couldn't load location");
//        }
//    }

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
                Crisis inputCrisis = Crisis.createFromAddress(crisisAddress);

                Intent i = new Intent(context, DispatchTeamActivity.class);
                i.putExtra("crisis", new CrisisParcel(inputCrisis));
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

        // TODO: 11/11/17 JD set string constants for all of these
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // gets the current status and positions the spinner accordingly
        String currentUserStatus = (mUser != null) ? mUser.getCurrentStatus() : "Offline";
        int spinnerPosition = adapter.getPosition(currentUserStatus);
        statusSpinner.setSelection(spinnerPosition);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = statusSpinner.getSelectedItem().toString();

                // stopping point 11/18/2017 - trigger the save through manageUsers

                mUserManager.setUserStatus(userID, text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private String getAuthUserID() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions();
                return;
            } else {
                mMap.setMyLocationEnabled(true);
                LatLng userLocationLatLng = new LatLng(userLocation.latitude, userLocation.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationLatLng, 13));
                //PositionCameraOverUserLocation(userLocation);
                buildGoogleApiClient();
                String userID = getAuthUserID();
                if (userID != null) {
                    mDatabase.child("employees").child(userID).child("latitude").setValue(userLocation.latitude);
                    mDatabase.child("employees").child(userID).child("longitude").setValue(userLocation.longitude);
                } else {
                    Toast.makeText(getApplicationContext(), "Current user not recognized. Try reauthenticating.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        //trying to place the marker on my house using the AlertDialog.
        //mMap.addMarker(new MarkerOptions().position(crisisPinStart).title("Marker"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(crisisPinStart));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(crisisPinStart, 10));
        //PositionCameraOverUserLocation(userLocation);

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

//    public void GetLatLng(final Crisis mCrisis) {
//
//        String crisisAddress = mCrisis.getCrisisAddress();
//
//        crisisAddress = ConvertAddressToJSON(crisisAddress);
//
//        RequestQueue mRequestQueue;
//
//        // Instantiate the cache
//        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//
//        // Set up the network to use HttpURLConnection as the HTTP client.
//        Network network = new BasicNetwork(new HurlStack());
//
//        // Instantiate the RequestQueue with the cache and network.
//        mRequestQueue = new RequestQueue(cache, network);
//
//        // Start the queue
//        mRequestQueue.start();
//        Log.d(TAG, getResources().getString(R.string.google_geocoding_key));
//        Log.d(TAG, crisisAddress);
//        String urlForGoogleMaps = "https://maps.googleapis.com/maps/api/geocode/json?address=" + crisisAddress +
//                "&key=" + getResources().getString(R.string.google_geocoding_key);
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, urlForGoogleMaps, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, "Response: " + response.toString());
//                        LatLng addressPosition;
//                        double lat = -122;
//                        double lng = 47;
//
//                        try {
//                            lat = response.getJSONArray("results").getJSONObject(0)
//                                    .getJSONObject("geometry").getJSONObject("location")
//                                    .getDouble("lat");
//                            lng = response.getJSONArray("results").getJSONObject(0)
//                                    .getJSONObject("geometry").getJSONObject("location")
//                                    .getDouble("lng");
//                            mCrisis.setLatitude(lat);
//                            mCrisis.setLongitude(lng);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        addressPosition = new LatLng(lat, lng);
//
//                        PlacePinAndPositionCamera(addressPosition);
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//
//        // Add the request to the RequestQueue.
//        mRequestQueue.add(jsObjRequest);
//
//
//    }

//    //Making the address given parsable by HTTP
//    public String ConvertAddressToJSON(String address) {
//
//        address.replace(' ', '+');
//
//        return address;
//    }

    public void PlacePinAndPositionCamera(LatLng addressPosition) {

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(addressPosition);
//        mMap.addMarker(markerOptions
//                .title("Crisis Location").icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressPosition, 12));
//
//        latLngBounds.include(addressPosition);
//
//        LatLngBounds bounds = latLngBounds.build();
//
//        int padding = 150; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//        mMap.animateCamera(cu);
    }

    public void PositionCameraOverUserLocation(LatLng addressPosition) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(addressPosition)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    protected synchronized void buildGoogleApiClient() {
        // Use the GoogleApiClient.Builder class to create an instance of the
        // Google Play Services API client//
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        // Connect to Google Play Services, by calling the connect() method//
        mGoogleApiClient.connect();
    }

    private boolean writeTeamLocationBasedOnUser() {

        return false;
    }

    private void setUserManagerAndListener(){
        mUserManager = new UserManager();

        /*mUserEventListener = new IUserEventListener() {
            @Override
            public void onUserCreated(User newUser) {
                String mAuthID = mAuth.getCurrentUser().getUid();
                String newUserString = newUser.getUserID();
                if ((mUser != null) && (newUser != null) && (newUserString.equals(mAuthID))){
                    mUser = newUser;
                    Log.d(TAG, " onusercreated, mUser assigned, mUser uid " + mUser.getUserID());
                    updateWithUserSettings();
                }
            }

            @Override
            public void onUserRemoved(User removedUser) {
                //do nothing for now
            }

            @Override
            public void onUserUpdated(User updatedUser) {
                if (updatedUser.getUserID().equals(mAuth.getCurrentUser().getUid())) {
                    Log.d(TAG, " onuserupdated fired");
                    if ((mUser != null) && (updatedUser.getUserID().equals(mAuth.getCurrentUser().getUid()))){
                        mUser.updateUser(updatedUser);
                        updateWithUserSettings();
                    }
                }
            }
        };

        mUserManager.addNewListener(mUserEventListener);*/
    }

    /**
     * Setting the FAB use on user update guarantees that we have a user in hand before attempting.
     */
        private void updateWithUserSettings() {
        String currentUserRole = "MCT";
        if (mUser != null){
            Log.d(TAG, "Update user with settings " + mUser.getCurrentRole());
            currentUserRole = mUser.getCurrentRole();
        } else {
            Log.d(TAG, "Update user with settings mUser null.");
        }

        // If the current user is dispatch, then this should be create address dialog.
        if ("Dispatcher".equalsIgnoreCase(currentUserRole))
        {
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CreateAddressDialog();
                }
            });
        }
        // If the current user is Team, this should offer a message dialog
        else {
            Toast MCTtoast = Toast.makeText(this, "This is not dispatch just a Team user.", Toast.LENGTH_LONG);
            MCTtoast.show();
        }
    }
}