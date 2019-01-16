package com.example.jonat.hideandseek;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import java.util.Random;

public class RegisterNewGame extends AppCompatActivity {

    private static final FirebaseDatabase databaseGame = FirebaseDatabase.getInstance();
    private static final DatabaseReference gamesGetRef = databaseGame.getReference("games");
    private static DatabaseReference gamesRef = null;

    private static final DatabaseReference gameNumbersField = databaseGame.getReference("gameNumbers");
    private static final int REGISTER_PROFILE = 1;

    Game myGame;
    String gameCode;
    SeekBar seekBarPlayerNumber;
    TextView textViewPlayerNumber;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_game);


        displaySeekBar();

        //Create listener on firebase
        gamesGetRef.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (gamesRef != null) {
                    boolean gameCodeFound;

                    //Get random game code and check if it is not already used
                    do {
                        gameCodeFound = true;
                        gameCode = getGameCode();
                        for (DataSnapshot game : dataSnapshot.getChildren()) {
                            String s = game.child("gameCode").getValue(String.class);
                                if (s.equals(gameCode))
                                    gameCodeFound = false;
                        }
                    } while (!gameCodeFound);

                    //Update firebase
                    gamesGetRef.removeEventListener(valueEventListener);
                    gamesRef.child("gameCode").setValue(gameCode);


                    //Go to register new player
                    Intent intent = new Intent(RegisterNewGame.this, RegisterNewPlayer.class);
                    intent.putExtra("gameCode", gameCode);
                    intent.putExtra("registerNewGame", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Creates game on firebase when clicked
        Button buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gamesRef == null) {
                    addGameToFirebaseDB();
                }
            }

        });


    }


    private void addGameToFirebaseDB() {

        //Create an instance of game with unique key using the available fields for game
        gamesRef = gamesGetRef.push();
        Game game = new Game(4, "", seekBarPlayerNumber.getProgress() + 4);
        gamesRef.setValue(game);
    }


    private String getGameCode() {

        //Get random number 0-999999 check if it has 6 digits then return it
        Random rand = new Random();
        int randNumber;
        do {
            randNumber = rand.nextInt(999999);
        } while (String.valueOf(randNumber).length() != 6);
        return Integer.toString(randNumber);
    }


    private void displaySeekBar() {

        //Creates seekbar, to get a minimum of 4 players and update the displayed n players
        seekBarPlayerNumber = findViewById(R.id.seekBarPlayerNumber);
        textViewPlayerNumber = findViewById(R.id.textViewPlayerNumber);

        textViewPlayerNumber.setText(Integer.toString(seekBarPlayerNumber.getProgress() + 4));
        seekBarPlayerNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the number of player with the seek bar
                textViewPlayerNumber.setText(Integer.toString(progress + 4));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}



