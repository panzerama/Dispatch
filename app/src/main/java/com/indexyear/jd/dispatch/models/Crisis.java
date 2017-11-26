package com.indexyear.jd.dispatch.models;

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
import com.indexyear.jd.dispatch.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.facebook.FacebookSdk.getCacheDir;

public class Crisis {

    private String crisisID;
    private String crisisAddress;
    private String teamName;
    private String status;
    private double latitude;
    private double longitude;

    /*
    private Date crisisDate;
    private Calendar timeCallReceived;
    private Calendar timeArrived;
    private Boolean policeOnScene;
    private ReferringAgency referringAgency;
    private ReferralReason referralReason;
    private String callBackNumber;
    private String dispatchComments;
    private Team responseTeam;
    private List<User> responseTeamMembers;
    */

    public static Crisis createFromAddress(String address) {
        return new Crisis(address);
    }

    public Crisis(String crisisID, String crisisAddress, String teamName, String status) {
        this.crisisID = crisisID;
        this.crisisAddress = crisisAddress;
        this.teamName = teamName;
        this.status = status;
    }

    private Crisis(String crisisAddress){
        this.crisisAddress = crisisAddress;
        this.crisisID = UUID.randomUUID().toString();
        this.status = "open";
        this.teamName = "unset";
    }

    /*
    private enum ReferringAgency {
        DMHP, FIRE, POLICE, CC
    }

    private enum ReferralReason {
        MH, CD, MHCD, OTHER
    }
    */

    public String getCrisisID() { return crisisID; }

    public void setCrisisID(String crisisID) { this.crisisID = crisisID; }

    public String getCrisisAddress() {
        return crisisAddress;
    }

    public void setCrisisAddress(String crisisAddress) {
        this.crisisAddress = crisisAddress;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Map toMap(){
        Map<String, String> outputMap = new HashMap<String, String>();

        outputMap.put("crisisID", crisisID);
        outputMap.put("crisisAddress", crisisAddress);
        outputMap.put("teamName", teamName);
        outputMap.put("status", status);
        outputMap.put("latitude", "" + latitude);
        outputMap.put("longitude", "" + longitude);
        return outputMap;
    }

    public void GetLatLng(final Crisis mCrisis) {

        String crisisAddress = mCrisis.getCrisisAddress();
        String googleKey = "" + R.string.google_geocoding_key;

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


        String urlForGoogleMaps = "https://maps.googleapis.com/maps/api/geocode/json?address=" + crisisAddress +
                "&key=" + googleKey;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlForGoogleMaps, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        double lat = -122;
                        double lng = 47;

                        try {
                            lat = response.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            lng = response.getJSONArray("results").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            mCrisis.setLatitude(lat);
                            mCrisis.setLongitude(lng);
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

        address.replace(' ', '+');

        return address;
    }

    /*
    public Date getCrisisDate() {
        return crisisDate;
    }

    public void setCrisisDate(Date crisisDate) {
        this.crisisDate = crisisDate;
    }

    public Calendar getTimeCallReceived() {
        return timeCallReceived;
    }

    public void setTimeCallReceived(Calendar timeCallReceived) {
        this.timeCallReceived = timeCallReceived;
    }

    public Calendar getTimeArrived() { return timeArrived; }

    public void setTimeArrived(Calendar timeArrived) {
        this.timeArrived = timeArrived;
    }

    public Boolean getPoliceOnScene() {
        return policeOnScene;
    }

    public void setPoliceOnScene(Boolean policeOnScene) {
        this.policeOnScene = policeOnScene;
    }

    public ReferringAgency getReferringAgency() {
        return referringAgency;
    }

    public void setReferringAgency(ReferringAgency referringAgency) {
        this.referringAgency = referringAgency;
    }

    public ReferralReason getReferralReason() {
        return referralReason;
    }

    public void setReferralReason(ReferralReason referralReason) {
        this.referralReason = referralReason;
    }

    public String getCallBackNumber() {
        return callBackNumber;
    }

    public void setCallBackNumber(String callBackNumber) {
        this.callBackNumber = callBackNumber;
    }

    public String getDispatchComments() {
        return dispatchComments;
    }

    public void setDispatchComments(String dispatchComments) {
        this.dispatchComments = dispatchComments;
    }

    public Team getResponseTeam() {
        return responseTeam;
    }

    public void setResponseTeam(Team responseTeam) {
        this.responseTeam = responseTeam;
    }

    public List<User> getResponseTeamMembers() {
        return responseTeamMembers;
    }

    public void setResponseTeamMembers(List<User> responseTeamMembers) {
        this.responseTeamMembers = responseTeamMembers;
    }
    */
}
