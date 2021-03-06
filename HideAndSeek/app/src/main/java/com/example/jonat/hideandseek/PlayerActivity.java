package com.example.jonat.hideandseek;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class PlayerActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        // code from Googlehttp://developer.android.com/guide/topics/location/strategies.html
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                // Called when a new location is found by the network location provider.
                String locStr = String.format("%s %f:%f (%f meters)", loc.getProvider(),
                        loc.getLatitude(), loc.getLongitude(), loc.getAccuracy());
                TextView tvLoc = (TextView) findViewById(R.id.textView1);
                tvLoc.setText(locStr);
                Log.v("Gibbons", locStr);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v("Gibbons", "location onStatusChanged() called");
            }

            public void onProviderEnabled(String provider) {
                Log.v("Gibbons", "location onProviderEnabled() called");
            }

            public void onProviderDisabled(String provider) {
                Log.v("Gibbons", "location onProviderDisabled() called");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        Log.v("Gibbons", "setting location updates from network provider");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Log.v("Gibbons","setting location updates from GPS provider");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);









    }
}
