package com.indexyear.jd.dispatch.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.data.user.IGetUserListener;
import com.indexyear.jd.dispatch.data.user.IUserEventListener;
import com.indexyear.jd.dispatch.data.user.UserManager;
import com.indexyear.jd.dispatch.models.User;

import android.Manifest;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private LocationManager lm;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String LOGSERVICE = "#######";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private LatLng userLocation;

    private FirebaseAuth mAuth;
    private UserManager mUserManager;
    private DatabaseReference mDatabase; //Firebase reference
    private User mUser;

    private IUserEventListener mUserEventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        mAuth = FirebaseAuth.getInstance();

        mUserManager = new UserManager();
        setUserEventListener();
        mUserManager.getUser(mAuth.getCurrentUser().getUid(), new IGetUserListener() {
            @Override
            public void onGetSingleUser(User retrievedUser) {
                mUser = retrievedUser;
            }

            @Override
            public void onFailedSingleUser() {
            }
        });

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Log.i(LOGSERVICE, "onCreate");
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGSERVICE, "onStartCommand");

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOGSERVICE, "onConnected" + bundle);



        mDatabase = FirebaseDatabase.getInstance().getReference();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOGSERVICE, "Permission not granted");
            return;
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null){ location = new Location(LocationManager.GPS_PROVIDER); location.setLatitude(0); location.setLongitude(0);}
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if (mLastLocation != null) {
        //Log.i(LOGSERVICE, "lat " + mLastLocation.getLatitude());
        //Log.i(LOGSERVICE, "lng " + mLastLocation.getLongitude());

        startLocationUpdates();
    }
    private void startLocationUpdates() {
        initLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOGSERVICE, "Permission not granted.");
            return;
        } else {
            LatLng userLocationLatLng = new LatLng(userLocation.latitude, userLocation.longitude);
            buildGoogleApiClient();

            if (mUser.getUserID() != null) {
                mDatabase.child("users").child(mUser.getUserID()).child("latitude").setValue(userLocation.latitude);
                mDatabase.child("users").child(mUser.getUserID()).child("longitude").setValue(userLocation.longitude);
            } else {
                Toast.makeText(getApplicationContext(), "Current user not recognized. Try reauthenticating.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void setUserEventListener(){
        mUserEventListener = new IUserEventListener() {
            @Override
            public void onUserCreated(User newUser) {
                if (mUser == null) { mUser = newUser; }
            }

            @Override
            public void onUserRemoved(User removedUser) {
                //do nothing
            }

            @Override
            public void onUserUpdated(User updatedUser) {
                if (mUser == null) { mUser = updatedUser; }
                else if (updatedUser.getUserID().equals(mUser.getUserID())){
                    mUser.updateUser(updatedUser);
                }
            }
        };
        mUserManager.addNewListener(mUserEventListener);
    }
}