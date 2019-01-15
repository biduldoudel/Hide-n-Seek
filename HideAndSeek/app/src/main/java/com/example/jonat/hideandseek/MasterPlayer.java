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
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MasterPlayer extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");
    private static DatabaseReference playersRef;

    Marker currentShown;
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
    private String selectedPlayer = "";
    private Button forwardButton;
    private String command;
    private boolean mapInitialized = false;
    Circle gameZone;
    private String selectedPlayerTeam = "";
    List<Double> targetLat = new ArrayList<>();
    List<Double> targetLong = new ArrayList<>();
    private Double totScore = 0.0;

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
        } else {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        Intent intent = getIntent();

        role = intent.getExtras().getString("role");
        team = intent.getExtras().getString("team");
        gameId = intent.getExtras().getString("gameId");
        username = intent.getExtras().getString("username");

        playersRef = gamesRef.child(gameId).child("players");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (marekersInitialized == false && mapReady) {
                    for (final DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren()) {
                        String username = player.getKey();
                        double latitude = player.child("latitude").getValue(Double.class);
                        double longitude = player.child("longitude").getValue(Double.class);
                        String playerRole = player.child("role").getValue(String.class);
                        LatLng playerPosition = new LatLng(latitude, longitude);

                        if (playerRole.equals("Runner")) {
                            markers.add(mMap.addMarker(new MarkerOptions().position(playerPosition).title(username).anchor(0.5f, 0.5f)));
                            markers.get(markers.size() - 1).setTag(username);
                            markers.get(markers.size() - 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.survivor_icon_small));
                            switch (player.child("team").getValue(String.class)) {
                                case "Survivor":
                                    markers.get(markers.size() - 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.survivor_icon_small));
                                    break;
                                case "Zombie":
                                    markers.get(markers.size() - 1).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.zombie_icon_small));
                                    break;
                            }
                        }
                    }
                    marekersInitialized = true;
                } else if (marekersInitialized == true && mapReady && mapInitialized)
                    for (final DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren()) {
                        String tempUsername = player.getKey();
                        double latitude = player.child("latitude").getValue(Double.class);
                        double longitude = player.child("longitude").getValue(Double.class);
                        LatLng playerPosition = new LatLng(latitude, longitude);
                        boolean playerAlive = player.child("alive").getValue(Boolean.class);

                        for (Marker marker : markers) {
                            if (marker.getTag().equals(tempUsername)) {
                                marker.setPosition(playerPosition);
                                if (!playerAlive)
                                    marker.setVisible(false);
                                else
                                    marker.setVisible(true);

                                break;
                            }
                        }
                        if (player.child("team").getValue(String.class).equals("Survivor") && mapInitialized && team.equals("Survivor")) {
                            totScore += dataSnapshot.child(gameId).child("players").child(player.getKey()).child("score").getValue(Double.class);
                            for (Circle circle : circles) {
                                circle.setStrokeColor(Color.BLUE);
                            }
                            for (final DataSnapshot target : dataSnapshot.child(gameId).child("targets").getChildren()) {
                                if (target.child("latitude").exists() && target.child("longitude").exists()) {
                                    double targetLatitude = target.child("latitude").getValue(Double.class);
                                    double targetLongitude = target.child("longitude").getValue(Double.class);
                                    if (coordinatesDistance(latitude, longitude, targetLatitude, targetLongitude) < 25) {
                                        circles.get(Integer.parseInt(target.getKey()) - 1).setStrokeColor(Color.RED);
                                    }
                                }
                            }
                        }
                    }

                    gamesRef.child(gameId).child("totSurvivorsScore").setValue(totScore);
                    totScore = 0.0;

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
                        }
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

                        if (team.equals("Survivor")) {
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
                            gamesRef.child(gameId).child("gameStatus").setValue("InProgress");

                        }
                        mapInitialized = true;
                        gamesRef.child(gameId).child("startTime").setValue(Calendar.getInstance().getTime().getTime());
                    }

                }

                if (!selectedPlayer.equals("")) {
                    selectedPlayerTeam = dataSnapshot.child(gameId).child("players").child(selectedPlayer).child("team").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.forwardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command.equals("Go ")) {
                    command += "forward";
                    if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                        playersRef.child(selectedPlayer).child("command").setValue(command);
                    command = "";
                }
            }
        });


        findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command.equals("Go ")) {
                    command += "left";
                    if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                        playersRef.child(selectedPlayer).child("command").setValue(command);
                    command = "";
                }
            }
        });


        findViewById(R.id.rigthButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command.equals("Go ")) {
                    command += "right";
                    if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                        playersRef.child(selectedPlayer).child("command").setValue(command);
                    command = "";
                }
            }
        });


        findViewById(R.id.backwardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command.equals("Go ")) {
                    command += "back";
                    if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                        playersRef.child(selectedPlayer).child("command").setValue(command);
                    command = "";
                }
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

                if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                    playersRef.child(selectedPlayer).child("command").setValue("Stay there");
            }
        });


        findViewById(R.id.carefulButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectedPlayer.equals("") && selectedPlayerTeam.equals(team))
                    playersRef.child(selectedPlayer).child("command").setValue("Be careful!");
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mapReady = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        //mMap.clear();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        gamesRef.child(gameId).child("players").child(username).child("longitude").setValue(longitude);
        gamesRef.child(gameId).child("players").child(username).child("latitude").setValue(latitude);
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

        if (marker.equals(currentShown)) {
            marker.hideInfoWindow();
            selectedPlayer = "";
            currentShown = null;
        } else {
            marker.showInfoWindow();
            currentShown = marker;
        }
        return true;

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentShown != null) {
            currentShown.hideInfoWindow();
            selectedPlayer = "";
            currentShown = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
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
