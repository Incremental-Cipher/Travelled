package com.example.test3;


import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String city = "The sea";

    public static final String FILE_NAME = "markers.txt";
    private File file;
    private FileOutputStream fileOutputStream;
    private FileInputStream fileInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Creates a new file at the system default location
        file = new File(this.getFilesDir(), FILE_NAME);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // default camera view
        LatLng defaultCamera = new LatLng(35, 26);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultCamera, 0f));

        // read from file and re-add markers
        int length = (int)file.length();
        byte[] bytes = new byte[length];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            fileInputStream.close();

            String data = new String(bytes);
            String[] parts = data.split("\\+");

            for (int i = 0; i < parts.length; i++) {
                String[] slice = (parts[i].split("\\|"));
                mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(slice[1]), Double.parseDouble(slice[2])))
                    .title(slice[0]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @SuppressLint("WorldReadableFiles")
            @Override
            public void onMapClick(LatLng point) {

                // request url
                String path = "https://maps.googleapis.com/maps/api/geocode/json";
                String latlng = "?latlng=" + point.latitude + "," + point.longitude;
                String result_type = "&result_type=administrative_area_level_1";
                String google_maps_key = "&key=AIzaSyBCuPT6Ls6K83vZRGJpArYCrlM141jdmBo";
                String url = path + latlng + result_type + google_maps_key;

                // Formulate request and handle response
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    city = response.getJSONArray("results")
                                            .getJSONObject(0)
                                            .getString("formatted_address");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("REVERSE_GEO_ERR", error.toString());
                            }
                        });

                // Instantiate Singleton instance if not null, else, return instance
                MySingleton.getInstance(getApplicationContext()).getRequestQueue();
                // Add request to RequestQueue
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

                // Add marker to the map with the city name
                MarkerOptions newMarker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title(city);
                mMap.addMarker(newMarker);

                String[] marker = {city + "|" + point.latitude + "|" + point.longitude + "+"};

                // write to file
                try {
                    fileOutputStream = new FileOutputStream(file, true);
                    for (String s : marker) {
                        fileOutputStream.write(s.getBytes());
                    }
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }

}