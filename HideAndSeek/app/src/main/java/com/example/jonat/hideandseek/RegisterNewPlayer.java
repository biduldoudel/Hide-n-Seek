package com.example.jonat.hideandseek;

import android.app.AutomaticZenRule;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    private TextView editTextCodeNumber;
    private String gameCode;
    private boolean foundGame;
    private Button buttonStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_player);

        buttonStart = findViewById(R.id.buttonStart);

        Bundle extras = getIntent().getExtras();
        gameId = extras.getString("gameId");


        // If we are the creator (from RegisterNewGame Activity and not from MainActivity)
        if (extras.getBoolean("RegisterNewGame")) {
            gameCode = extras.getString("gameCode");


            editTextCodeNumber = findViewById(R.id.editTextCodeNumber);
            editTextCodeNumber.setText(gameCode);
            editTextCodeNumber.setEnabled(false);
/*
            Button buttonJoin = findViewById(R.id.buttonJoin);
            buttonJoin.setVisibility(View.INVISIBLE);
*/
            TextView textButton = findViewById(R.id.textView);
            textButton.setText("Please share this number with the players !");
            Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();

            //getGameData();
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


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameCode = editTextCodeNumber.getText().toString();
                getGameData();
                addPlayerToFirebaseDB();
                if (switchRole.isChecked()) {
                    // Go to the master activity (Map...)
                    Intent intent = new Intent(RegisterNewPlayer.this, RulesRecall.class);
                    startActivity(intent);
                } else {
                    // Go to the player activity
                    Intent intent = new Intent(RegisterNewPlayer.this, RulesRecall.class);
                    startActivity(intent);
                }
            }

        });


    }
    private void getGameData() {

        gamesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gameCodeDB;
                String gameStatusDB;

                for (final DataSnapshot game : dataSnapshot.getChildren()) {
                    foundGame = false;
                    gameCodeDB = game.child("GameCode").getValue(String.class);
                    //gameStatusDB = game.child("gameStatus").getValue(String.class);

                    if (gameCodeDB.equals(gameCode) /*&& gameStatusDB.equals("Waiting")*/) {
                        gameId = game.getKey();
                        buttonStart.setEnabled(false);
                        editTextCodeNumber.setEnabled(false);
                        Toast.makeText(getApplicationContext(), gameId, Toast.LENGTH_LONG).show();
                        foundGame = true;
                        break;
                    }
                }

                /*if (foundGame){

                    // Get the player number !
                    int playerNumber = dataSnapshot.child(gameId).child("playerNumber").getValue(Integer.class);
                    int instantPlayerNumber = dataSnapshot.child(gameId).child("instantPlayerNumber").getValue(Integer.class);

                    if(flagOnce){
                        int newPlayerNumber = instantPlayerNumber+1;
                        databaseReference.child(gameId).child("instantPlayerNumber").setValue(newPlayerNumber);
                        flagOnce = false;
                    }

                    TextViewpeopleEnrolled = findViewById(R.id.textViewPeopleEnrolled);
                    TextViewpeopleEnrolled.setText(Integer.toString(instantPlayerNumber) + " / "  + Integer.toString(playerNumber) + " peoples enrolled !");

                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setMax(playerNumber+1);
                    progressBar.setProgress(instantPlayerNumber+1);

                    Toast.makeText(getApplicationContext(), "Player added : " + Integer.toString(instantPlayerNumber + 1), Toast.LENGTH_LONG).show();

                    foundGame = false;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Game not found !", Toast.LENGTH_LONG).show();
                }*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }


    private void addPlayerToFirebaseDB() {
    Player player = new Player("test","test","test","test");
   // gamesRef.child(gameId).setValue(player);
    }


}
