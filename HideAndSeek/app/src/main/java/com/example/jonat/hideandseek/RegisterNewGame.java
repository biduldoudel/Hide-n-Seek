package com.example.jonat.hideandseek;

import android.content.Intent;
import android.support.annotation.NonNull;
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
    private static final DatabaseReference databaseReference = databaseGame.getReference("games");
    private static final DatabaseReference gameRef = databaseReference.push();

    private static final DatabaseReference gameNumbersField = databaseGame.getReference("gameNumbers");
    private static final int REGISTER_PROFILE = 1;

    Game myGame;
    String gameCode;
    SeekBar seekBarPlayerNumber;
    TextView textViewPlayerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_game);


        displaySeekBar();


        Button buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGameToFirebaseDB();
                Intent intent = new Intent(RegisterNewGame.this, JoinNewGame.class);
                intent.putExtra("gameCode", gameCode);
                intent.putExtra("RegisterNewGame", true);
                startActivity(intent);
            }

        });




    }


    private void addGameToFirebaseDB() {
        int playerNumber = Integer.parseInt(textViewPlayerNumber.getText().toString()); // get the player number from the textView
        gameCode = getGameCode();
        String gameId = databaseReference.push().getKey(); // We get a unique key for a game !
        myGame = new Game(gameId, playerNumber, gameCode);// We create a new game (Class Game with the number of player - add location too...)
        databaseReference.child(gameId).setValue(myGame); // Save to Firebase !
    }


    private String getGameCode() {
        Random rand = new Random();
        int randNumber = rand.nextInt(999999);
        return Integer.toString(randNumber);
    }


    private void displaySeekBar() {
        seekBarPlayerNumber = findViewById(R.id.seekBarPlayerNumber);
        textViewPlayerNumber = findViewById(R.id.textViewPlayerNumber);

        seekBarPlayerNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the number of player with the seek bar
                textViewPlayerNumber.setText(Integer.toString(progress));
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



