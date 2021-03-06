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
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class RunnerPlayer extends AppCompatActivity implements LocationListener {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private String role;
    private String team;
    private String username;
    private String gameId;
    private String command;
    private TextView command1;
    private TextView command2;
    private ValueEventListener valueListener;
    private int onTargetKey;
    private Date prevDate;
    private Double score = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_player);

        //sendWearStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission("android" + ""
                + ".permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") ==
                        PackageManager.PERMISSION_DENIED || checkSelfPermission("android" + "" +
                ".permission.INTERNET") == PackageManager.PERMISSION_DENIED)) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android"
                    + ".permission.ACCESS_COARSE_LOCATION", "android.permission.INTERNET"}, 0);
        } else {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        final Intent intent = getIntent();

        role = intent.getExtras().getString("role");
        team = intent.getExtras().getString("team");
        gameId = intent.getExtras().getString("gameId");
        username = intent.getExtras().getString("username");

        command1 = findViewById(R.id.command1);
        command2 = findViewById(R.id.command2);


        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (command1.getText() != dataSnapshot.child(gameId).child("players").child(username).child("command").getValue(String.class)) {
                    command2.setText(command1.getText());
                    command = dataSnapshot.child(gameId).child("players").child(username).child("command").getValue(String.class);
                    command1.setText(command);
/*
                    Intent intentWear = new Intent(RunnerPlayer.this, WearService.class);
                    intentWear.setAction(WearService.ACTION_SEND.MESSAGE.name());
                    intentWear.putExtra(WearService.MESSAGE, command);
                    intentWear.putExtra(WearService.PATH, BuildConfig.W_example_path_text);
                    startService(intentWear);*/
                }

                if (dataSnapshot.child(gameId).child("gameStatus").getValue(String.class).equals("InProgress") && team.equals("Survivor")){
                    for (final DataSnapshot target : dataSnapshot.child(gameId).child("targets").getChildren()) {
                        double targetLatitude = target.child("latitude").getValue(Double.class);
                        double targetLongitude = target.child("longitude").getValue(Double.class);
                        double d = coordinatesDistance(latitude, longitude, targetLatitude, targetLongitude);
                        if (d > 25 && onTargetKey == Integer.parseInt(target.getKey())) {
                            onTargetKey = 0;
                        } else if (d < 25 && onTargetKey == Integer.parseInt(target.getKey())) {
                            Date currentDate = Calendar.getInstance().getTime();
                            long dt = currentDate.getTime() - prevDate.getTime();
                            score = score + dt/1000.0;
                            gamesRef.child(gameId).child("survivorsScore").setValue(score);
                            prevDate = currentDate;
                        } else if (d < 25 && onTargetKey != Integer.parseInt(target.getKey())) {
                            onTargetKey = Integer.parseInt(target.getKey());
                            prevDate = Calendar.getInstance().getTime();
                        }

                    }
                } else if (dataSnapshot.child(gameId).child("gameStatus").getValue(String.class).equals("InProgress") && team.equals("Zombie")){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    private void sendWearStart() {
        Intent intent = new Intent(RunnerPlayer.this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_mainactivity);
        startService(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
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

}
