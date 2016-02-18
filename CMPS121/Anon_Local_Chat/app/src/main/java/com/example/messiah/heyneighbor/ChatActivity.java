package com.example.messiah.heyneighbor;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ChatActivity extends Activity {

    LocationListener locationListener;
    LocationManager locationManager;

    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean location_found = false;

    final String BASE_URL_GET = "https://luca-teaching.appspot.com/localmessages/default/get_messages?";
    final String BASE_URL_POST = "https://luca-teaching.appspot.com/localmessages/default/post_message?";

    float latitude;
    float longitude;

    int backButtonCount = 0;

    Context context;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        context = getApplicationContext();

        // used for volley
        requestQueue = Volley.newRequestQueue(this);

        TextView action_bar = (TextView) findViewById(R.id.chat_action_bar);
        action_bar.setText("Chatting as " + MainActivity.nickname.toUpperCase());

        if(!location_found) Toast.makeText(context, "Finding location... please wait", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /**
         * Listens to the location, and gets the most precise recent location.
         */
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                if(!location_found) {
                    location_found = true;
                    Toast.makeText(context, "Location Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backButtonCount = 0;
            startActivity(intent);
        } else {
            Toast.makeText(context, "Press the back button again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }

    public void sendMessage(View view) {
        checkLocationStatus();
        if(!gps_enabled || !network_enabled || !location_found) return;

        EditText messageToSend = (EditText) findViewById(R.id.message_to_send);
        String message = messageToSend.getText().toString();

        String message_id = "";
        Random rnd = new Random();
        String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int n = 0; n < 10; ++n) message_id += randomLetters.charAt(rnd.nextInt(randomLetters.length()));

        String URL_POST = BASE_URL_POST + "lat=" + latitude + "&lng=" + longitude + "&user_id=1&nickname=" + MainActivity.nickname + "&message=" + message + "&message_id=" + message_id;

        URL_POST = URL_POST.replaceAll(" ", "%20");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL_POST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // checks if there is a response from the server
                        try {
                            // checks for  if the status of the server is error, if error, exit
                            String responseCheck = response.getString("result");
                            if (responseCheck.equals("error")) {
                                Toast.makeText(context, "Status(1): Status: error, please try again", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(context, "Status(0): Message Sent", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // if no response, toasts a message to user
                            Toast.makeText(context, "Status(1): Error 500, Server Failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Status(1): Failed to get data, try again", Toast.LENGTH_SHORT).show();
                    }
                });

        // requests data
        requestQueue.add(jsonObjectRequest);

        if(message != "") {
            messageToSend.setText("");
        }
    }

    public void checkLocationStatus() {
        try { gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch(Exception ex) {}
        try { network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch(Exception ex) {}

        if(!gps_enabled) Toast.makeText(context, "GPS not enabled", Toast.LENGTH_SHORT).show();
        if(!network_enabled) Toast.makeText(context, "Network not enabled", Toast.LENGTH_SHORT).show();
    }

    public void refreshChat(View view) {
        checkLocationStatus();
        if(!gps_enabled || !network_enabled || !location_found) return;

        String URL_GET = BASE_URL_GET + "lat=" + latitude + "&lng=" + longitude;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(URL_GET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // checks if there is a response from the server
                        try {
                            // checks for  if the status of the server is error, if error, exit
                            String responseCheck = response.getString("result");
                            if (responseCheck.equals("error")) {
                                Toast.makeText(context, "Status(1): Status: error, please try again", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // if no error, then connect and grab data
                            Toast.makeText(context, "Status(0): Connected, getting data", Toast.LENGTH_SHORT).show();
                            // conditions object
                            JSONArray result_list = response.getJSONArray("result_list");

                            for(int i = result_list.length()-1; i >= 0; --i) {
                                System.out.println(result_list.getJSONObject(i));
                            }

                            // tells the user complete
                            Toast.makeText(context, "Status(0): Data request complete", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            // if no response, toasts a message to user
                            Toast.makeText(context, "Status(1): Error 500, Server Failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Status(1): Failed to get data, try again", Toast.LENGTH_SHORT).show();
                    }
                });

        // requests data
        requestQueue.add(jsonObjectRequest);
    }
}