package com.indexyear.jd.dispatch.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.data.ManageCrisis;
import com.indexyear.jd.dispatch.data.ManageUsers;

import java.io.IOException;
import java.util.List;

import static com.indexyear.jd.dispatch.R.id.spinner;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.Active;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.Dispatched;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.NotSet;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.OffDuty;
import static com.indexyear.jd.dispatch.activities.MainActivity.UserStatus.OnBreak;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    //todo jd fix level of zoom on default view
    private static final String TAG = "MainActivity";
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
    ;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        DispatchAlertDialog();
    }

    public void DispatchAlertDialog() {
        //For Testing added by Luke
        ManageCrisis newCrisis = new ManageCrisis();
        crisisID = "testCrisis";
        crisisAddress = "8507 18TH AVE NW Seattle WA 98117";
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
                        crisisPin = getLocationFromAddress(MainActivity.this, crisisAddress);
                        if (crisisPin != null) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(crisisPin);
                            mMap.addMarker(markerOptions
                                    .title("Crisis Location").icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(crisisPin, 12));

                        } else {
                            Log.d(TAG, "Made it through the AlertDialog but the crisisPin " +
                                    "Address is null.");
                        }

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
        int id = item.getItemId();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, id, duration);
        toast.show();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, id, duration);
        toast.show();

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
        Geocoder coder= new Geocoder(context);
        List<Address> addresses;
        try {
            addresses = coder.getFromLocationName(address, 5);
            if (addresses == null) {
            }
            Address location = addresses.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.i("Lat",""+lat);
            Log.i("Lng",""+lng);
            return new LatLng(lat,lng);
        }
        catch (IOException e) {
            e.printStackTrace();
        } // end catch
         // end if
      return null;
    }



}
