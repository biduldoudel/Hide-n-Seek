package com.example.jonat.hideandseek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterNewPlayer extends AppCompatActivity {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference databaseReference = databaseGame.getReference("games");
    private static final DatabaseReference gameRef = databaseReference.push();


    Switch switchTeam;
    Switch switchRole;
    TextView teamText;
    TextView roleText;
    EditText editTextUsername;

    String gameId;
    private String playerId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_player);

        Bundle extras = getIntent().getExtras();
        gameId = extras.getString("gameId");



        switchTeam = findViewById(R.id.switchTeam);
        switchRole = findViewById(R.id.switchRole);
        teamText = findViewById(R.id.TextViewTeam);
        roleText = findViewById(R.id.TextViewRole);
        editTextUsername = findViewById(R.id.editTextUsername);

        switchTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTeam.isChecked()) {
                    teamText.setText("Survival");
                } else {
                    teamText.setText("Zombie");
                }
            }
        });

        switchRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchRole.isChecked()) {
                    roleText.setText("Master");
                } else {
                    roleText.setText("Player");
                }
            }
        });


        Button buttonNewGame = findViewById(R.id.buttonStart);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPlayerToFirebaseDB();

                if (switchRole.isChecked()) {
                    // Go to the master activity (Map...)
                    Intent intent = new Intent(RegisterNewPlayer.this,PlayerActivity.class);
                    startActivity(intent);
                } else {
                    // Go to the player activity
                    Intent intent = new Intent(RegisterNewPlayer.this,PlayerActivity.class);
                    startActivity(intent);
                }
            }

        });


    }

    private void addPlayerToFirebaseDB() {
        String username = editTextUsername.getText().toString();
        String team = teamText.getText().toString();
        String role = roleText.getText().toString();

        String playerId = databaseReference.child(gameId).child("players").push().getKey(); // We get a unique key for a game !
        Player myPlayer = new Player(playerId, username, team, role);// We create a new game (Class Game with the number of player - add location too...)
        databaseReference.child(gameId).child("players").child(playerId).setValue(myPlayer); // Save to Firebase !
    }
}
