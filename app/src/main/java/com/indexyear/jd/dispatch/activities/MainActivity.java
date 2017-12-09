package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.crisis.CrisisManager;
import com.indexyear.jd.dispatch.data.crisis.CrisisParcel;
import com.indexyear.jd.dispatch.data.crisis.IGetLatLngListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.models.Crisis;
import com.indexyear.jd.dispatch.models.User;

import java.util.List;
import java.util.Locale;

import static com.indexyear.jd.dispatch.R.id.map;
import static com.indexyear.jd.dispatch.R.id.spinner;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //FOR LAYOUT
    private static final String TAG = "MainActivity";
    private String[] menuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Spinner statusSpinner;
    private FloatingActionButton fab;

    //FOR APPLICATION STATE
    private User mUser;
    private UserManager mUserManager;

    //FOR LISTENERS
    private Context context;

    //FOR MAP
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private float DEFAULT_ZOOM;
    private LatLng mDefaultLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private LatLng mCurrentLocation;
    private LatLng mCameraPosition;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GeoDataClient mGeoDataClient;
    private LocationRequest mLocationRequest;

    //FOR CRISIS
    private String crisisAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mUserManager = new UserManager();
        determineIntent();
        createAddressDialogButton();
        context = getApplicationContext();

        DEFAULT_ZOOM = 13;
        mDefaultLocation = new LatLng(0, 0);
    }

    private void createAddressDialogButton() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser != null) {
                    if (mUser.getCurrentRole().equalsIgnoreCase("Dispatcher")) {
                        CreateAddressDialog();
                    }
                }
            }
        });
        if (mUser != null) {
            if (mUser.getCurrentRole().equalsIgnoreCase("Mobile Crisis Team")) {
                fab.setVisibility(View.GONE);
            }
        }
    }

    private boolean determineIntent() {
        // figure out which way to handle the incoming intent
        String incomingIntentPurpose = getIntent().getStringExtra("intent_purpose");

        if (incomingIntentPurpose != null) {

            switch (incomingIntentPurpose) {
                case "crisis_map_update":
                    IGetLatLngListener getLatLngListener = new IGetLatLngListener() {
                        @Override
                        public void onCrisisGetLatLng(Crisis locationUpdatedCrisis) {
                            //set the LatLng of the Crisis
                            LatLng addressPosition;
                            addressPosition = new LatLng(locationUpdatedCrisis.getLatitude(), locationUpdatedCrisis.getLongitude());
                            //Use the new LatLng to place a pin on the map.
                            PlacePinAndPositionCamera(addressPosition);
                        }
                    };
                    //if we have a Crisis with the intent get the Crisis Object
                    CrisisParcel acceptedCrisisEvent = getIntent().getParcelableExtra("crisis");
                    CrisisManager acceptedCrisisManager = new CrisisManager();
                    Crisis intentCrisis = acceptedCrisisEvent.getCrisis();
                    //pass itself to it's own helper methods to get the lat and lng state assigned
                    acceptedCrisisManager.GetLatLng(context, intentCrisis, getLatLngListener);
                    break;
                case "passing_user":
                    mUser = getIntent().getParcelableExtra("user");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                    updateLocationUI();
                }
            }
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
        }
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Intent intent = new Intent(this, LocationServices.class);
                startService(intent);
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void CreateAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.crisis_dialog_title)
                .setMessage(R.string.crisis_dialog_message);

        //address input
        final AppCompatEditText input = new AppCompatEditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.crisis_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                crisisAddress = input.getText().toString();
                IGetLatLngListener latLngListener = new IGetLatLngListener() {
                    @Override
                    public void onCrisisGetLatLng(final Crisis locationUpdatedCrisis) {

                        LatLng identifiedLatLng = new LatLng(locationUpdatedCrisis.getLatitude(), locationUpdatedCrisis.getLongitude());
                        PlacePinAndPositionCamera(identifiedLatLng);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 10 seconds
                                confirmAddressDialog(locationUpdatedCrisis);
                            }
                        }, 2000);
                    }
                };
                // TODO: 12/2/17 JD Maybe pass the crisis manager so i'm not covering my own code again 
                CrisisManager inputCrisisManager = new CrisisManager();
                Crisis crisisToLocate = Crisis.createFromAddress(crisisAddress);
                crisisToLocate.setTeamName("unset");
                crisisToLocate.setStatus("unset");
                inputCrisisManager.GetLatLng(context, crisisToLocate, latLngListener);
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

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.status_spinner_items));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // gets the current status and positions the spinner accordingly
        if (mUser != null) {
            int spinnerPosition = adapter.getPosition(mUser.getCurrentStatus());
            statusSpinner.setSelection(spinnerPosition);
        }

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = statusSpinner.getSelectedItem().toString();
                if (mUser != null) {
                    mUserManager.setUserStatus(mUser.getUserID(), text);
                    mUser.setCurrentStatus(text);
                }
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

    //TODO: Luke 12/9/17 - DELETE this method.
    // I think it's leftover from trying to get Geocoder to work
    // returns a LatLng object from an address given
    public LatLng getLocationFromAddress(Context context, String address) {
        LatLng latLng = null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> geoResults = null;

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

        return latLng; //LatLng value of address
    }

    public void PlacePinAndPositionCamera(LatLng addressPosition) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(addressPosition);
        mMap.addMarker(markerOptions
                .title("Crisis Location").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressPosition, 12));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(addressPosition);

        LatLngBounds bounds = builder.build();

        int padding = 150; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

    //TODO: Revise or DELETE this method.
    //Luke - 12/9/17 - This functionality is handled elsewhere
    //this is probably left over from originally creating the map.
    public void PositionCameraOverUserLocation(LatLng addressPosition) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(addressPosition)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    /**
     * Setting the FAB use on user update guarantees that we have a user in hand before attempting.
     */
    //TODO: Revise or DELETE this method.
    // Luke - 12/9/17 This functionality is handled in the CreateAddressDialogButton()
    private void createAddressInput() {

        // If the current user is dispatch, then this should be create address dialog.
        if (mUser.getCurrentRole().equals("Mobile Crisis Team")) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Luke - 12/9/17 - This method builds the dialog where the Dispatcher confirms that the address
    // they supplied is actually in the correct location on the google map.
    private void confirmAddressDialog(final Crisis confirmedCrisis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_crisis_address_title)
                .setMessage(R.string.confirm_crisis_address_message);

        builder.setPositiveButton(R.string.crisis_dialog_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //create new dialog, is this the right place?
                CrisisManager successfulLatLngCrisisManager = new CrisisManager();
                successfulLatLngCrisisManager.addCrisisToDatabase(confirmedCrisis);
                Intent dispatchActivity = new Intent(context, DispatchTeamActivity.class);
                dispatchActivity.putExtra("user", mUser);
                dispatchActivity.putExtra("intent_purpose", "passing_user");
                dispatchActivity.putExtra("crisis", new CrisisParcel(confirmedCrisis));
                //put extra user
                startActivity(dispatchActivity);
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
}