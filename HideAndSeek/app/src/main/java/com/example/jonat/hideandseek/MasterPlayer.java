package com.example.jonat.hideandseek;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MasterPlayer extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");
    private static DatabaseReference playersRef;


    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private boolean marekersInitialized = false;
    private List<Marker> markers = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private String role;
    private String team;
    private String gameId;
    private String username;
    private boolean mapReady = false;
    private String selectedPlayer;
    private Button forwardButton;
    private String command;
    private boolean mapInitialized = false;
    Circle gameZone;

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

        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mapInitialized == false && mapReady) {
                    double zombieMasterLat = 0;
                    double zombieMasterLong = 0;
                    double survivorMasterLat = 0;
                    double survivorMasterLong = 0;
                    for (final DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren()) {
                        if (player.child("role").getValue(String.class).equals("Master")) {
                            switch (player.child("team").getValue(String.class)) {
                                case "Zombie":
                                    zombieMasterLat = player.child("latitude").getValue(Double.class);
                                    zombieMasterLong = player.child("longitude").getValue(Double.class);
                                    break;
                                case "Survivor":
                                    survivorMasterLat = player.child("latitude").getValue(Double.class);
                                    survivorMasterLong = player.child("longitude").getValue(Double.class);
                                    break;
                            }
                            if (zombieMasterLat != 0 && zombieMasterLong != 0 && survivorMasterLat != 0 && survivorMasterLong != 0) {
                                double mediumLat = (zombieMasterLat + survivorMasterLat) / 2;
                                double mediumLong = (survivorMasterLong + zombieMasterLong) / 2;
                                gameZone = mMap.addCircle(new CircleOptions().center(new LatLng(mediumLat, mediumLong)).radius(400).strokeColor(Color.BLACK));
                                /*double radius = gameZone.getRadius();
                                double scale = radius/500;
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mediumLat,mediumLong),(float)(16 - Math.log(scale) / Math.log(2))));*/
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(new LatLng(mediumLat + 0.0035973, mediumLong));
                                builder.include(new LatLng(mediumLat - 0.0035973, mediumLong));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 5));

                                if (team.equals("Survivor"))
                                    for (int i = 1; i <= dataSnapshot.child(gameId).child("nSurvivors").getValue(Integer.class); i++) {

                                        double randomLat = mediumLat + ThreadLocalRandom.current().nextDouble(-0.0033725, 0.00337251);
                                        double randomLong = mediumLong + ThreadLocalRandom.current().nextDouble(-1 * getLongitudeRange(mediumLat), getLongitudeRange(mediumLat));

                                        while (coordinatesDistance(randomLat, randomLong, mediumLat, mediumLong) > 375) {
                                            randomLat = mediumLat + ThreadLocalRandom.current().nextDouble(-0.0033725, 0.00337251);
                                            randomLong = mediumLong + ThreadLocalRandom.current().nextDouble(-1 * getLongitudeRange(mediumLat), getLongitudeRange(mediumLat));

                                        }

                                        circles.add(mMap.addCircle(new CircleOptions().center(new LatLng(randomLat, randomLong)).radius(25).strokeColor(Color.BLUE).strokeWidth(5)));
                                        gamesRef.child(gameId).child("targets").child(Integer.toString(i)).child("latitude").setValue(randomLat);
                                        gamesRef.child(gameId).child("targets").child(Integer.toString(i)).child("longitude").setValue(randomLong);
                                    }
                                mapInitialized = true;
                            }
                        }
                    }

                }
                if (marekersInitialized == false && mapReady) {
                    for (final DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren()) {
                        String username = player.getKey();
                        double latitude = player.child("latitude").getValue(Double.class);
                        double longitude = player.child("longitude").getValue(Double.class);
                        LatLng playerPosition = new LatLng(latitude, longitude);
                        markers.add(mMap.addMarker(new MarkerOptions().position(playerPosition).title(username).anchor(0.5f, 0.5f)));
                        markers.get(markers.size() - 1).setTag(username);
                        markers.get(markers.size() - 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.survivor_icon_small));
                    }
                    marekersInitialized = true;
                } else
                    for (final DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren()) {
                        String tempUsername = player.getKey();
                        double latitude = player.child("latitude").getValue(Double.class);
                        double longitude = player.child("longitude").getValue(Double.class);
                        LatLng playerPosition = new LatLng(latitude, longitude);

                        for (Marker marker : markers) {
                            if (marker.getTag().equals(tempUsername)) {
                                marker.setPosition(playerPosition);
                                break;
                            }
                        }

                        for (final DataSnapshot target : dataSnapshot.child(gameId).child("targets").getChildren()){
                            double targetLatitude = target.child("latitude").getValue(Double.class);
                            double targetLongitude = target.child("longitude").getValue(Double.class);
                            if(coordinatesDistance(latitude, longitude, targetLatitude, targetLongitude) < 25){
                                circles.get(Integer.parseInt(target.getKey())-1).setStrokeColor(Color.RED);
                            }
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.forwardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command += "forward";

                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue(command);
            }
        });


        findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command += "left";
                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue(command);
            }
        });


        findViewById(R.id.rigthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command += "right";

                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue(command);
            }
        });


        findViewById(R.id.backwardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command += "back";

                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue(command);
            }
        });


        findViewById(R.id.runButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command = "";
                command += "Go ";
                //playersRef.child(selectedPlayer).child("command").setValue("Run");
            }
        });


        findViewById(R.id.stayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue("Stay there");
            }
        });


        findViewById(R.id.carefulButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlayer!=null)
                playersRef.child(selectedPlayer).child("command").setValue("Be careful!");
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

    public double coordinatesDistance(double lat1, double long1, double lat2, double long2) {
        double d;
        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);

        double a = Math.pow(Math.sin((lat1 - lat2) / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((long1 - long2) / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        d = Math.abs(6371000 * c);
        return d;
    }

    public double getLongitudeRange(double latitude) {
        double range;
        double R = 6371000;
        double dRadLat = Math.PI / 180 * Math.cos(Math.toRadians(latitude)) * R;
        range = Math.abs(375 / dRadLat);

        return range;
    }
}
