package com.indexyear.jd.dispatch.data.crisis;

import android.content.Context;
import android.util.Log;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indexyear.jd.dispatch.R;
import com.indexyear.jd.dispatch.models.Crisis;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class CrisisManager {
    private static final String TAG = "CrisisManager";

    private Crisis mCrisis;
    private DatabaseReference mDatabase; //Firebase reference
    private List<ICrisisEventListener> mListeners;
    private IGetLatLngListener getLatLngListener;

    public CrisisManager(){
        mListeners = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("crisis").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisCreated(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (mListeners.isEmpty()) { return; }
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisUpdated(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (mListeners.isEmpty()) { return; }
                for (ICrisisEventListener mListener: mListeners) {
                    mListener.onCrisisRemoved(new CrisisFirebaseMapper().fromDataSnapshot(dataSnapshot));
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Crisis createNewCrisis(String crisisAddress){
        String newKey = UUID.randomUUID().toString(); // TODO: 11/30/17 JD update to reflect new method of assigning id from push() 
        Crisis newCrisis = new Crisis(newKey, crisisAddress, "Unspecified", "open");

        DatabaseReference newCrisisNode = mDatabase.child("crisis").child(newCrisis.getCrisisID());
        newCrisisNode.updateChildren(newCrisis.toMap());

        return newCrisis;

    }

    public void addCrisisToDatabase(Crisis inputCrisis){
        DatabaseReference crisisNode = mDatabase.child("crisis");
        DatabaseReference newCrisisNode = crisisNode.push();
        inputCrisis.setCrisisID(newCrisisNode.getKey());

        newCrisisNode.child(newCrisisNode.getKey()).updateChildren(inputCrisis.toMap());
    }

    //GetAddress of crisis based on ID
    public String getCrisisAddress(DataSnapshot dataSnapshot, String crisisID) {

        for (DataSnapshot addressDataSnapshot : dataSnapshot.getChildren()) {
            String addressCheck = String.valueOf(addressDataSnapshot.child("address").getValue());
            if (crisisID.equals(addressDataSnapshot.child("crisisID").getValue())) {
               return addressCheck;
            }
        }
        return "address not found";
    }

    //set time automatically, to be called from MapActivity listener
    public void setArrivalTime(String crisisID){
        String currentDateTimeString = "" + Calendar.DATE + Calendar.getInstance().getTime();
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(currentDateTimeString);
    }

    //set time in case of Listener failure,
    public void setArrivalTime(String crisisID, String time) {
        mDatabase.child("crisis").child(crisisID).child("arrivalTime").setValue(time);
    }

    public void setCrisisEventListener(ICrisisEventListener crisisEventListener){
        mListeners.add(crisisEventListener);
    }

    public void GetLatLng(Context incomingContext, Crisis crisisToLocate, IGetLatLngListener incomingLatLngListener) {

        // 11/29/17 JD: register the observer
        getLatLngListener = incomingLatLngListener;
        mCrisis = crisisToLocate;

        String googleKey = incomingContext.getResources().getString(R.string.google_geocoding_key);

        String crisisAddress = ConvertAddressToJSON(crisisToLocate.getCrisisAddress());

        RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(incomingContext.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();


        String urlForGoogleMaps = "https://maps.googleapis.com/maps/api/geocode/json?address=" + crisisAddress +
                "&key=" + googleKey;

        Log.d(TAG, urlForGoogleMaps);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlForGoogleMaps, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Crisis successfulCrisis = mCrisis;
                        double lat = -122;
                        double lng = 47;

                        try {
                            lat = response.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            Log.d(TAG, response.getJSONArray("results").toString());
                            lng = response.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            successfulCrisis.setLatitude(lat);
                            successfulCrisis.setLongitude(lng);
                            // crisisEventListener observes the success of json request
                            // TODO: 12/1/17 JD failure test: what happens if the request is returned without info 
                            getLatLngListener.onCrisisGetLatLng(successfulCrisis);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // Add the request to the RequestQueue.
        mRequestQueue.add(jsObjRequest);

        //TODO: update crisis on Firebase, or is that handled elsewhere?

    }

    //Helper Method to GetLatLng
    //Making the address given parsable by HTTP
    private String ConvertAddressToJSON(String address) {
        return address.replace(' ', '+');
    }


}
