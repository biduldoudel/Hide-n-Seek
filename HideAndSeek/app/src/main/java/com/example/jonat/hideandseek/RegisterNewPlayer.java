package com.example.jonat.hideandseek;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterNewPlayer extends AppCompatActivity {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesRef = databaseGame.getReference("games");


    Switch switchTeam;
    Switch switchRole;
    TextView teamText;
    TextView roleText;
    EditText editTextUsername;

    String gameId;
    private String playerId;
    private String username;
    private String role;
    private String team;
    private TextView editTextCodeNumber;
    private String gameCode;
    private boolean foundGame;
    private Button buttonReady;
    private int nReadyPlayers;
    private boolean playerRegistered;
    private int nExpectedPlayers;
    private ValueEventListener valueListener;
    private boolean masterZombieRegistered;
    private boolean masterSurvivorRegistered;
    private int nZombies;
    private int nSurvivors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_player);

        buttonReady = findViewById(R.id.buttonReady);
        editTextCodeNumber = findViewById(R.id.editTextCodeNumber);
        editTextUsername = findViewById(R.id.editTextUsername);

        Bundle extras = getIntent().getExtras();
        gameId = extras.getString("gameId");


        // If we are the creator (from RegisterNewGame Activity and not from MainActivity)
        if (extras.getBoolean("registerNewGame")) {
            gameCode = extras.getString("gameCode");

            editTextCodeNumber.setText(gameCode);
            editTextCodeNumber.setEnabled(false);

            TextView textButton = findViewById(R.id.textView);
            textButton.setText("Please share this number with the other players !");
            Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
        }

        switchTeam = findViewById(R.id.switchTeam);
        switchRole = findViewById(R.id.switchRole);
        teamText = findViewById(R.id.TextViewTeam);
        roleText = findViewById(R.id.TextViewRole);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextCodeNumber = findViewById(R.id.editTextCodeNumber);


        switchTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchTeam.isChecked()) {
                    teamText.setText("Survivor");
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
                    roleText.setText("Runner");
                }
            }
        });


        buttonReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCode = editTextCodeNumber.getText().toString();
                username = editTextUsername.getText().toString();
                team = teamText.getText().toString();
                role = roleText.getText().toString();

                updateDatabase();


            }

        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueListener != null)
            gamesRef.removeEventListener(valueListener);
    }

    private void updateDatabase() {

        gamesRef.addValueEventListener(valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameCodeDB;
                String gameStatusDB;
                if (foundGame == false)
                    for (final DataSnapshot game : dataSnapshot.getChildren()) {
                        foundGame = false;
                        gameCodeDB = game.child("gameCode").getValue(String.class);
                        gameStatusDB = game.child("gameStatus").getValue(String.class);

                        if (gameCodeDB.equals(gameCode) /*&& gameStatusDB.equals("Waiting")*/) {
                            gameId = game.getKey();

                            //Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
                            foundGame = true;
                            break;
                        }
                    }
                if (foundGame == true) {

                    gameCodeDB = dataSnapshot.child(gameId).child("gameCode").getValue(String.class);
                    gameStatusDB = dataSnapshot.child(gameId).child("gameStatus").getValue(String.class);
                    nReadyPlayers = dataSnapshot.child(gameId).child("nReadyPlayers").getValue(Integer.class);
                    nExpectedPlayers = dataSnapshot.child(gameId).child("nExpectedPlayers").getValue(Integer.class);
                    nZombies = dataSnapshot.child(gameId).child("nZombies").getValue(Integer.class);

                    nSurvivors = dataSnapshot.child(gameId).child("nSurvivors").getValue(Integer.class);

                    masterSurvivorRegistered = dataSnapshot.child(gameId).child("masterSurvivorRegistered").getValue(Boolean.class);
                    masterZombieRegistered = dataSnapshot.child(gameId).child("masterZombieRegistered").getValue(Boolean.class);


                    if (!playerRegistered && gameStatusDB.equals("WaitingForPlayers")) {
                        if (nReadyPlayers >= nExpectedPlayers) {
                            Toast.makeText(getApplicationContext(), "This game seems to be full", Toast.LENGTH_LONG).show();
                        } else {
                            boolean usernameTaken = false;
                            for (DataSnapshot player : dataSnapshot.child(gameId).child("players").getChildren())
                            {
                                if(player.getKey().equals(username))
                                    usernameTaken = true;
                            }
                            if(usernameTaken)
                                Toast.makeText(getApplicationContext(), "Username taken, please chose another one", Toast.LENGTH_LONG).show();
                            else if(role.equals("Master") && masterZombieRegistered && masterSurvivorRegistered){
                                Toast.makeText(getApplicationContext(), "You need to chose a runner role", Toast.LENGTH_LONG).show();
                            }
                            else if(role.equals("Master") && team.equals("Survivor") && masterSurvivorRegistered){
                                Toast.makeText(getApplicationContext(), "Master survivor is already registered, please chose another role", Toast.LENGTH_LONG).show();
                            }
                            else if(role.equals("Master") && team.equals("Zombie")  && masterZombieRegistered){
                                Toast.makeText(getApplicationContext(), "Master zombie is already registered, please chose another role", Toast.LENGTH_LONG).show();
                            }
                            else if ((nExpectedPlayers - nReadyPlayers) == 2 && !masterSurvivorRegistered && !masterZombieRegistered && !role.equals("Master")) {
                                Toast.makeText(getApplicationContext(), "You need to chose a master role", Toast.LENGTH_LONG).show();
                            } else if ((nExpectedPlayers - nReadyPlayers) == 1 && !masterSurvivorRegistered && (!role.equals("Master") || !team.equals("Survivor"))) {
                                Toast.makeText(getApplicationContext(), "You need to chose the survivor master role", Toast.LENGTH_LONG).show();
                            } else if ((nExpectedPlayers - nReadyPlayers) == 1 && !masterZombieRegistered && (!role.equals("Master") || !team.equals("Zombie"))) {
                                Toast.makeText(getApplicationContext(), "You need to chose the zombie master role", Toast.LENGTH_LONG).show();
                            } else {
                                addPlayerToFirebaseDB();
                                nReadyPlayers++;
                                gamesRef.child(gameId).child("nReadyPlayers").setValue(nReadyPlayers);
                                playerRegistered = true;
                                editTextCodeNumber.setEnabled(false);
                                editTextUsername.setEnabled(false);
                                buttonReady.setEnabled(false);
                                buttonReady.setText("Waiting for players");
                                editTextCodeNumber.setEnabled(false);
                                if (nReadyPlayers == nExpectedPlayers) {
                                    gamesRef.child(gameId).child("gameStatus").setValue("RulesRecall");
                                    gameStatusDB = "RulesRecall";
                                }
                            }

                        }
                    }
                    if (playerRegistered && gameStatusDB.equals("RulesRecall")) {
                        // Go to the player activity
                        gamesRef.child(gameId).child("nReadyPlayers").setValue(0);
                        Intent intent = new Intent(RegisterNewPlayer.this, RulesRecall.class);
                        intent.putExtra("role", role);
                        intent.putExtra("team", team);
                        intent.putExtra("gameId", gameId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }


    private void addPlayerToFirebaseDB() {
        Player player = new Player(username, team, role);
        gamesRef.child(gameId).child("players").child(username).setValue(player);
        if (role.equals("Master")) {
            switch (team) {
                case "Zombie":
                    gamesRef.child(gameId).child("masterZombieRegistered").setValue(true);
                    break;
                case "Survivor":
                    gamesRef.child(gameId).child("masterSurvivorRegistered").setValue(true);
                    break;
            }
        } else if (role.equals("Runner")) {
            switch (team) {
                case "Zombie":
                    gamesRef.child(gameId).child("nZombies").setValue(nZombies + 1);
                    break;
                case "Survivor":
                    gamesRef.child(gameId).child("nSurvivors").setValue(nSurvivors + 1);
            }

        }

    }
}
