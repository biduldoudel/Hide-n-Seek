package com.example.jonat.hideandseek;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MasterPlayer extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");
    private static DatabaseReference playersRef;


    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private boolean marekersInitiated = false;
    private List<Marker> markers= new ArrayList<>();
    private String role;
    private String team;
    private String gameId;
    private String username;
    private boolean mapReady=false;
    private String selectedPlayer;
    private Button forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_player);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission("android" + ""
                + ".permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") ==
                        PackageManager.PERMISSION_DENIED || checkSelfPermission("android" + "" +
                ".permission.INTERNET") == PackageManager.PERMISSION_DENIED)) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android"
                    + ".permission.ACCESS_COARSE_LOCATION", "android.permission.INTERNET"}, 0);
        }

        Intent intent = getIntent();

        role = intent.getExtras().getString("role");
        team = intent.getExtras().getString("team");
        gameId = intent.getExtras().getString("gameId");
        username = intent.getExtras().getString("username");

        playersRef = gamesRef.child(gameId).child("players");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        playersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(marekersInitiated == false && mapReady){
                    for (final DataSnapshot player : dataSnapshot.getChildren()) {
                        String username = player.getKey();
                        double latitude = player.child("latitude").getValue(Double.class);
                        double longitude =player.child("longitude").getValue(Double.class);
                        LatLng playerPosition = new LatLng(latitude, longitude);
                        markers.add(mMap.addMarker(new MarkerOptions().position(playerPosition).title(username)));
                        markers.get(markers.size()-1).setTag(username);
                        marekersInitiated = true;
                    }
                }
                else
                for (final DataSnapshot player : dataSnapshot.getChildren()) {
                    String tempUsername = player.getKey();
                    double latitude = player.child("latitude").getValue(Double.class);
                    double longitude =player.child("longitude").getValue(Double.class);
                    LatLng playerPosition = new LatLng(latitude, longitude);

                    for(Marker marker : markers){
                            if(marker.getTag().equals(tempUsername)){
                                marker.setPosition(playerPosition);
                                break;
                            }
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playersRef.child(selectedPlayer).child("command").setValue("Forward");
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mapReady = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        //mMap.clear();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        LatLng currentLocation = new LatLng(latitude, longitude);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        //mMap.addMarker(new MarkerOptions().position(currentLocation).title("test"));
        //mMap.addCircle(new CircleOptions().center(currentLocation).radius(1000));
        longitude = 6.931933;
        latitude = 46.992979;
        LatLng currentLocation2 = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(currentLocation2).title("test"));
        gamesRef.child(gameId).child("players").child(username).child("longitude").setValue(location.getLongitude());
        gamesRef.child(gameId).child("players").child(username).child("latitude").setValue(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedPlayer = marker.getTag().toString();
        return false;
    }
}
